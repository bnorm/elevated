package dev.bnorm.elevated.service

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.HandshakeInfo
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

abstract class CoroutineWebSocketHandler : WebSocketHandler {
    companion object {
        private val log = LoggerFactory.getLogger(CoroutineWebSocketHandler::class.java)
    }

    override fun handle(webSocketSession: WebSocketSession): Mono<Void> {
        return mono {
            try {
                val sendChannel = Channel<String>()

                launch {
                    val messages = sendChannel.consumeAsFlow()
                        .map { webSocketSession.textMessage(it) }
                        .asFlux()
                    webSocketSession.send(messages).awaitSingleOrNull()
                }

                val receiveChannel = webSocketSession.receive()
                    .map { it.payloadAsText } // this must be before asFlow() - byte buffer dereferenced on discard?
                    .asFlow()
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
        sendChannel: SendChannel<String>,
        receiveChannel: ReceiveChannel<String>,
    )
}

fun CloseStatus.withNullableReason(reason: String?): CloseStatus {
    reason ?: return this
    return withReason(reason)
}
