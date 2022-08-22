@file:OptIn(ExperimentalTime::class)

package dev.bnorm.elevated.web.api

import dev.bnorm.elevated.model.devices.DeviceAction
import dev.bnorm.elevated.model.devices.DeviceId
import io.ktor.client.features.json.serializer.KotlinxSerializer.Companion.DefaultJson
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

object DeviceService {
    fun connect(deviceId: DeviceId): Flow<DeviceAction> {
        return channelFlow {
            while (isActive) {
                try {
                    val flow = this.channel
                    httpClient.webSocket({
                        method = HttpMethod.Get
                        url {
                            takeFrom(hostUrl)
                            path("api", "v1", "devices", deviceId.value, "connect")
                            protocol = if (hostUrl.protocol == URLProtocol.HTTPS) URLProtocol.WSS else URLProtocol.WS
                        }
                    }) {
                        val authorization = tokenStore.authorization
                        if (authorization != null) {
                            outgoing.send(Frame.Text(authorization.substringAfter(' ')))
                        }

                        incoming.consumeAsFlow()
                            .filterIsInstance<Frame.Text>()
                            .map { it.readText() }
                            .map { DefaultJson.decodeFromString(DeviceAction.serializer(), it) }
                            .collect { flow.send(it) }
                    }
                } catch (t: Throwable) {
                    if (t is CancellationException) throw t
                    t.printStackTrace()
                }

                // Retry to connect forever
                delay(5.seconds)
            }
        }.buffer()
    }
}
