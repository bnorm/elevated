package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.client.TokenStore
import dev.bnorm.elevated.client.createElevatedClient
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.devices.*
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.model.sensors.SensorReadingPrototype
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import okhttp3.*
import okio.ByteString
import org.slf4j.LoggerFactory
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.seconds

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

    private val client = createElevatedClient(tokenStore)

    suspend fun authenticate(): Device {
        val authenticatedDevice = client.loginDevice(
            DeviceLoginRequest(
                id = DEVICE_ID,
                key = DEVICE_KEY,
            )
        )
        tokenStore.setAuthorization(authenticatedDevice.token)
        return authenticatedDevice.device
    }

    suspend fun getDevice(): Device {
        return client.getDevice(DEVICE_ID)
    }

    suspend fun getDeviceActions(submittedAfter: Instant): List<DeviceAction> {
        return client.getDeviceActions(DEVICE_ID, submittedAfter)
    }

    suspend fun completeDeviceAction(deviceActionId: DeviceActionId): DeviceAction {
        return client.completeDeviceAction(DEVICE_ID, deviceActionId)
    }

    suspend fun getActionQueue(): Flow<DeviceAction> {
        return channelFlow {
            while (isActive) {
                try {
                    val device = getDevice()
                    log.info("Connecting to server for device={}", device)

                    val flow = this.channel
                    val request = Request.Builder()
                        .url("https://elevated.bnorm.dev/api/v1/devices/${DEVICE_ID.value}/connect")
                        .build()

                    val incoming = Channel<String>(capacity = Channel.UNLIMITED)
                    val webSocket = okHttpClient.newWebSocket(request, incoming)

                    val authorization = tokenStore.authorization
                    if (authorization != null) {
                        webSocket.send(authorization.substringAfter(' '))
                    }

                    val pending = getDeviceActions(device.lastActionTime ?: Instant.DISTANT_PAST)
                    val actionIds = pending.map { it.id }.toSet()
                    log.info("Existing actions={}", pending)

                    incoming.consumeAsFlow()
                        .onEach { log.info("Received : frame.text={}", it) }
                        .map { json.decodeFromString(DeviceAction.serializer(), it) }
                        .filter { it.id !in actionIds }
                        .onStart { emitAll(pending.asFlow()) }
                        .filter { it.completed == null }
                        .collect {
                            log.info("Received : action={}", it)
                            flow.send(it)
                        }

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
        return client.recordSensorReading(
            sensorId,
            SensorReadingPrototype(
                value = value,
                timestamp = timestamp
            )
        )
    }

    private suspend fun OkHttpClient.newWebSocket(request: Request, incoming: SendChannel<String>): WebSocket {
        return suspendCancellableCoroutine {
            val webSocket = newWebSocket(
                request,
                object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        if (it.isActive) it.resume(webSocket)
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        incoming.close(t)
                        if (it.isActive) it.resumeWithException(t)
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        incoming.close()
                    }

                    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                        incoming.close()
                    }

                    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                        incoming.trySendBlocking(bytes.hex())
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        incoming.trySendBlocking(text)
                    }
                },
            )

            it.invokeOnCancellation { webSocket.cancel() }
        }
    }
}
