package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.client.HttpElevatedClient
import dev.bnorm.elevated.client.TokenStore
import dev.bnorm.elevated.log.getLogger
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.devices.Device
import dev.bnorm.elevated.model.devices.DeviceAction
import dev.bnorm.elevated.model.devices.DeviceActionId
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.DeviceLoginRequest
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.model.sensors.SensorReadingPrototype
import io.ktor.client.HttpClient
import io.ktor.http.Url
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

// TODO replace with something from :common:client
class ElevatedClient(
    httpClient: HttpClient,
    private val deviceId: DeviceId,
    private val deviceKey: Password,
) {
    companion object {
        private val log = getLogger<ElevatedClient>()
    }

    private val tokenStore = TokenStore.Memory()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val elevatedClient = HttpElevatedClient(Url("https://elevated.bnorm.dev"), httpClient, tokenStore, json)

    suspend fun authenticate(): Device {
        val authenticatedDevice = elevatedClient.loginDevice(
            DeviceLoginRequest(
                id = deviceId,
                key = deviceKey,
            )
        )
        tokenStore.setAuthorization(authenticatedDevice.token)
        return authenticatedDevice.device
    }

    suspend fun getDevice(): Device {
        return elevatedClient.getDevice(deviceId)
    }

    suspend fun completeDeviceAction(deviceActionId: DeviceActionId): DeviceAction {
        return elevatedClient.completeDeviceAction(deviceId, deviceActionId)
    }

    suspend fun getActionQueue(): Flow<DeviceAction> {
        return flow {
            while (true) {
                try {
                    val device = getDevice()
                    log.info { "Connecting to server for device=$device" }
                    emitAll(
                        elevatedClient.connectDeviceActions(device.id)
                            .onEach { log.info { "Received : action=$it" } }
                    )
                    log.info { "Disconnected from server" }
                } catch (t: Throwable) {
                    if (t is CancellationException) throw t
                    log.warn(t) { "Error in WebSocket" }
                }

                delay(15.seconds)
            }
        }
    }

    suspend fun recordSensorReading(
        sensorId: SensorId,
        value: Double,
        timestamp: Instant = Clock.System.now(),
    ): SensorReading {
        return elevatedClient.recordSensorReading(
            sensorId,
            SensorReadingPrototype(
                value = value,
                timestamp = timestamp
            )
        )
    }
}
