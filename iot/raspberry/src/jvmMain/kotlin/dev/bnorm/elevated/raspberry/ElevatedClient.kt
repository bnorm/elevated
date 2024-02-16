package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.client.HttpElevatedClient
import dev.bnorm.elevated.client.TokenStore
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
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.http.Url
import java.time.Duration
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
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory

// TODO replace with something from :common:client
class ElevatedClient {
    companion object {
        private val log = LoggerFactory.getLogger(ElevatedClient::class.java)

        private val env = System.getenv()
        private val DEVICE_KEY = Password(env.getValue("DEVICE_KEY"))
        private val DEVICE_ID = DeviceId("62780348770bd023d5d971e9")
    }

    private val tokenStore = TokenStore.Memory()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .pingInterval(Duration.ofSeconds(30))
        .build()

    private val httpClient = HttpClient(OkHttp.create {
        preconfigured = okHttpClient
    })

    private val elevatedClient = HttpElevatedClient(Url("https://elevated.bnorm.dev"), httpClient, tokenStore, json)

    suspend fun authenticate(): Device {
        val authenticatedDevice = elevatedClient.loginDevice(
            DeviceLoginRequest(
                id = DEVICE_ID,
                key = DEVICE_KEY,
            )
        )
        tokenStore.setAuthorization(authenticatedDevice.token)
        return authenticatedDevice.device
    }

    suspend fun getDevice(): Device {
        return elevatedClient.getDevice(DEVICE_ID)
    }

    suspend fun completeDeviceAction(deviceActionId: DeviceActionId): DeviceAction {
        return elevatedClient.completeDeviceAction(DEVICE_ID, deviceActionId)
    }

    suspend fun getActionQueue(): Flow<DeviceAction> {
        return flow {
            while (true) {
                try {
                    val device = getDevice()
                    log.info("Connecting to server for device={}", device)
                    emitAll(
                        elevatedClient.connectDeviceActions(device.id)
                            .onEach { log.info("Received : action={}", it) }
                    )
                    log.info("Disconnected from server")
                } catch (t: Throwable) {
                    if (t is CancellationException) throw t
                    log.warn("Error in WebSocket", t)
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
