@file:OptIn(ExperimentalTime::class)

package dev.bnorm.elevated.web.api

import dev.bnorm.elevated.model.devices.DeviceAction
import dev.bnorm.elevated.model.devices.DeviceId
import io.ktor.client.features.json.serializer.KotlinxSerializer.Companion.DefaultJson
import io.ktor.client.features.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.takeFrom
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

object DeviceService {
    fun connect(deviceId: DeviceId): Flow<DeviceAction> {
        return channelFlow {
            while (isActive) {
                try {
                    val flow = this.channel
                    client.webSocket({
                        method = HttpMethod.Get
                        url.takeFrom(apiUrl.appendPath("devices/${deviceId.value}/connect"))
                        url.protocol = if (apiUrl.protocol == URLProtocol.HTTPS) URLProtocol.WSS else URLProtocol.WS
                    }) {
                        val authorization = authorization
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
                    t.printStackTrace()
                }

                // Retry to connect forever
                delay(5.seconds)
            }
        }.buffer()
    }
}
