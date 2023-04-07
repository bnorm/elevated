package dev.bnorm.elevated.service

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.web.reactive.socket.*
import reactor.core.publisher.Mono
import java.time.Duration

sealed class Frame {
    class Text(val data: String) : Frame()
    class Binary(val data: ByteArray) : Frame()
    object Ping : Frame()
    object Pong : Frame()
}

abstract class CoroutineWebSocketHandler(
    private val pingInterval: Duration = Duration.ofSeconds(30),
) : WebSocketHandler {
    companion object {
        private val log = LoggerFactory.getLogger(CoroutineWebSocketHandler::class.java)
    }

    override fun handle(webSocketSession: WebSocketSession): Mono<Void> {
        return mono {
            try {
                val sendChannel = Channel<Frame>()

                launch {
                    while (isActive) {
                        delay(pingInterval.toMillis())
                        sendChannel.send(Frame.Ping)
                    }
                }

                launch {
                    webSocketSession.send(
                        sendChannel.consumeAsFlow()
                            .map { frame ->
                                when (frame) {
                                    is Frame.Text -> webSocketSession.textMessage(frame.data)
                                    is Frame.Binary -> webSocketSession.binaryMessage { factory ->
                                        factory.wrap(frame.data)
                                    }
                                    Frame.Ping -> webSocketSession.pingMessage { it.allocateBuffer(0) }
                                    Frame.Pong -> webSocketSession.pongMessage { it.allocateBuffer(0) }
                                }
                            }.asFlux()
                    ).awaitSingleOrNull()
                }

                val receiveChannel = webSocketSession.receive()
                    .map {
                        // this must be before asFlow() - byte buffer dereferenced on discard?
                        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
                        when (it.type) {
                            WebSocketMessage.Type.TEXT -> Frame.Text(it.payloadAsText)
                            WebSocketMessage.Type.BINARY -> Frame.Binary(it.payload.toByteArray())
                            WebSocketMessage.Type.PING -> Frame.Ping
                            WebSocketMessage.Type.PONG -> Frame.Pong
                        }
                    }
                    .asFlow()
                    .transform { frame ->
                        when (frame) {
                            is Frame.Text -> emit(frame)
                            is Frame.Binary -> emit(frame)
                            Frame.Ping -> sendChannel.send(Frame.Pong)
                            Frame.Pong -> Unit
                        }
                    }
                    .produceIn(this)

                handle(webSocketSession.handshakeInfo, sendChannel, receiveChannel)
                webSocketSession.close(CloseStatus.NORMAL).awaitSingleOrNull()

                null
            } catch (t: CancellationException) {
                throw t
            } catch (t: Throwable) {
                log.warn("marker=WebSocket.Error", t)
                webSocketSession.close(CloseStatus.SERVER_ERROR.withNullableReason(t.message)).awaitSingleOrNull()
                throw t
            }
        }
    }

    abstract suspend fun handle(
        info: HandshakeInfo,
        sendChannel: SendChannel<Frame>,
        receiveChannel: ReceiveChannel<Frame>,
    )
}

private fun DataBuffer.toByteArray(): ByteArray {
    val buffer = ByteArray(readableByteCount())
    read(buffer)
    return buffer
}

fun CloseStatus.withNullableReason(reason: String?): CloseStatus {
    reason ?: return this
    return withReason(reason)
}
