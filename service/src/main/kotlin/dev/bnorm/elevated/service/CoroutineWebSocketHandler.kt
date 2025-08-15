package dev.bnorm.elevated.service

import java.time.Duration
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.HandshakeInfo
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

sealed class Frame {
    class Text(val data: String) : Frame() {
        override fun toString(): String {
            return "Frame.Text(data=$data}"
        }
    }

    class Binary(val data: ByteArray) : Frame() {
        override fun toString(): String {
            return "Frame.Binary(data=${data.toHexString()})"
        }
    }

    class Ping(val data: ByteArray) : Frame() {
        override fun toString(): String {
            return "Frame.Ping(data=${data.toHexString()})"
        }
    }

    class Pong(val data: ByteArray) : Frame() {
        override fun toString(): String {
            return "Frame.Pong(data=${data.toHexString()})"
        }
    }
}

abstract class CoroutineWebSocketHandler(
    private val pingInterval: Duration = Duration.ofSeconds(30),
) : WebSocketHandler {
    companion object {
        private val log = LoggerFactory.getLogger(CoroutineWebSocketHandler::class.java)
    }

    override fun handle(webSocketSession: WebSocketSession): Mono<Void> {
        val context = MDC.getCopyOfContextMap() ?: mutableMapOf()
        val xRequestId = webSocketSession.handshakeInfo.headers.getFirst("X-Request-ID") ?: Random.randomRequestId()
        context["request.id"] = xRequestId

        return mono {
            try {
                withContext(MDCContext(context)) {
                    val sendChannel = Channel<Frame>()
                    val pongResponse = Channel<Frame.Pong>(Channel.UNLIMITED)

                    val pinger = launch {
                        while (isActive) {
                            delay(pingInterval.toMillis())
                            val now = Clock.System.now().toString()
                            log.debug { "marker=WebSocket.PingPong ping=$now" }
                            sendChannel.send(Frame.Ping(now.encodeToByteArray()))
                            val pong = withTimeoutOrNull(5.seconds) { pongResponse.receive() }
                            val response = pong?.data?.decodeToString()
                            log.debug { "marker=WebSocket.PingPong pong=$response" }
                            require(response == now)
                        }
                    }

                    val sender = launch {
                        webSocketSession.send(
                            sendChannel.consumeAsFlow()
                                .onEach { log.debug { "marker=WebSocket.Send message=\"$it\"" } }
                                .map { frame ->
                                    when (frame) {
                                        is Frame.Text -> webSocketSession.textMessage(frame.data)
                                        is Frame.Binary -> webSocketSession.binaryMessage { it.wrap(frame.data) }
                                        is Frame.Ping -> webSocketSession.pingMessage { it.wrap(frame.data) }
                                        is Frame.Pong -> webSocketSession.pongMessage { it.wrap(frame.data) }
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
                                WebSocketMessage.Type.PING -> Frame.Ping(it.payload.toByteArray())
                                WebSocketMessage.Type.PONG -> Frame.Pong(it.payload.toByteArray())
                            }
                        }
                        .asFlow()
                        .onEach { log.debug { "marker=WebSocket.Receive message=\"$it\"" } }
                        .transform { frame ->
                            when (frame) {
                                is Frame.Text -> emit(frame)
                                is Frame.Binary -> emit(frame)
                                is Frame.Ping -> sendChannel.send(Frame.Pong(frame.data))
                                is Frame.Pong -> pongResponse.send(frame)
                            }
                        }
                        .produceIn(this)

                    handle(webSocketSession.handshakeInfo, sendChannel, receiveChannel)
                    pinger.cancelAndJoin()
                    sendChannel.close()
                    sender.cancelAndJoin()
                    webSocketSession.close(CloseStatus.NORMAL).awaitSingleOrNull()
                }

                null
            } catch (t: Throwable) {
                if (t is CancellationException) throw t
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
