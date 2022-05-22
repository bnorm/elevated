package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.model.devices.DeviceAction
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.DeviceStatus
import dev.bnorm.elevated.service.CoroutineWebSocketHandler
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.http.server.PathContainer
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.HandshakeInfo
import org.springframework.web.util.pattern.PathPattern
import org.springframework.web.util.pattern.PathPatternParser

@Component
class DeviceWebSocketHandler(
    private val deviceService: DeviceService,
    private val deviceActionService: DeviceActionService,
    private val reactiveJwtDecoder: ReactiveJwtDecoder,
    private val jwtAuthenticationConverter: ReactiveJwtAuthenticationConverter,
) : CoroutineWebSocketHandler() {
    companion object {
        private val log = LoggerFactory.getLogger(DeviceWebSocketHandler::class.java)
        val pattern: PathPattern = PathPatternParser.defaultInstance.parse("/api/v1/devices/{deviceId}/connect")
    }

    override suspend fun handle(
        info: HandshakeInfo,
        sendChannel: SendChannel<String>,
        receiveChannel: ReceiveChannel<String>,
    ) {
        val match = pattern.matchAndExtract(PathContainer.parsePath(info.uri.path))
        val deviceId = match?.uriVariables?.get("deviceId")?.let { DeviceId(it) }
        if (deviceId != null) {
            // Require the first incoming message to be the JWT token
            val token = receiveChannel.receive()
            jwtAuthenticationConverter.convert(reactiveJwtDecoder.decode(token).awaitSingle())!!.awaitSingle()

            deviceService.setDeviceStatus(deviceId, DeviceStatus.Online)
            log.info("marker=WebSocket.Device deviceId={} message=\"Device is online\"", deviceId.value)
            try {
                coroutineScope {
                    val incoming = launch {
                        receiveChannel.consumeAsFlow()
                            .collect { log.info("marker=WebSocket.DeviceAction.Incoming message=\"{}\"", it) }
                    }
                    launch {
                        deviceActionService.watchActions(deviceId).collect {
                            log.info("marker=WebSocket.DeviceAction.Outgoing action={}", it)
                            sendChannel.send(Json.Default.encodeToString(DeviceAction.serializer(), it))
                        }
                        incoming.cancel()
                        log.info("marker=WebSocket.DeviceAction.Outgoing message=\"Finished sending actions\"")
                    }
                }
            } finally {
                withContext(NonCancellable) {
                    log.info("marker=WebSocket.Device deviceId={} message=\"Device is offline\"", deviceId.value)
                    deviceService.setDeviceStatus(deviceId, DeviceStatus.Offline)
                }
            }
        }
    }
}
