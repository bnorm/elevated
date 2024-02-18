package dev.bnorm.elevated.client

import dev.bnorm.elevated.model.auth.AuthenticatedDevice
import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.model.charts.Chart
import dev.bnorm.elevated.model.devices.Device
import dev.bnorm.elevated.model.devices.DeviceAction
import dev.bnorm.elevated.model.devices.DeviceActionId
import dev.bnorm.elevated.model.devices.DeviceActionPrototype
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.DeviceLoginRequest
import dev.bnorm.elevated.model.notifications.Notification
import dev.bnorm.elevated.model.notifications.NotificationId
import dev.bnorm.elevated.model.pumps.Pump
import dev.bnorm.elevated.model.sensors.Sensor
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.model.sensors.SensorReadingPrototype
import dev.bnorm.elevated.model.users.UserId
import dev.bnorm.elevated.model.users.UserLoginRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class HttpElevatedClient(
    private val hostUrl: Url,
    baseHttpClient: HttpClient,
    private val tokenStore: TokenStore,
    private val json: Json = DefaultJson,
) : ElevatedClient {
    private val httpClient = baseHttpClient.config {
        install(WebSockets) {
            pingInterval = 30_000
        }

        install(ContentNegotiation) {
            json(json)
        }

        expectSuccess = true
        install(DefaultRequest) {
            tokenStore.authorization?.let { headers[HttpHeaders.Authorization] = it }
        }

        install(HttpCallValidator) {
            handleResponseExceptionWithRequest { cause, _ ->
                if (cause is ClientRequestException && cause.response.status == HttpStatusCode.Unauthorized) {
                    tokenStore.authorization = null
                }
            }
        }
    }

    private val apiUrl = URLBuilder().apply {
        takeFrom(hostUrl)
        path("api", "v1")
    }.build()

    private fun apiPath(vararg path: String) = URLBuilder(apiUrl).appendPathSegments(path.toList()).build()

    override suspend fun login(request: UserLoginRequest): AuthenticatedUser {
        return httpClient.post(apiPath("users/login")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getCurrentUser(): AuthenticatedUser {
        return httpClient.get(apiPath("users/current")).body()
    }

    // Pumps

    override suspend fun getPumps(): List<Pump> {
        return httpClient.get(apiPath("pumps")).body()
    }

    // Sensors

    override suspend fun getSensors(): List<Sensor> {
        return httpClient.get(apiPath("sensors")).body()
    }

    // Readings

    override suspend fun recordSensorReading(
        sensorId: SensorId,
        sensorReadingPrototype: SensorReadingPrototype,
    ): SensorReading {
        return httpClient.post(apiPath("sensors/${sensorId.value}/readings/record")) {
            contentType(ContentType.Application.Json)
            setBody(sensorReadingPrototype)
        }.body()
    }

    override suspend fun getSensorReadings(
        sensorId: SensorId,
        startTime: Instant?,
        endTime: Instant?,
    ): List<SensorReading> {
        return httpClient.get(apiPath("sensors/${sensorId.value}/readings")) {
            if (startTime != null) parameter("startTime", startTime.toString())
            if (endTime != null) parameter("endTime", endTime.toString())
        }.body()
    }

    override suspend fun getLatestSensorReadings(
        sensorId: SensorId,
        count: Int?,
    ): List<SensorReading> {
        return httpClient.get(apiPath("sensors/${sensorId.value}/readings/latest")) {
            if (count != null) parameter("count", count.toString())
        }.body()
    }

    // Devices

    override suspend fun loginDevice(request: DeviceLoginRequest): AuthenticatedDevice {
        return httpClient.post(apiPath("devices/login")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getDevices(): List<Device> {
        return httpClient.get(apiPath("devices")).body()
    }

    override suspend fun getDevice(deviceId: DeviceId): Device {
        return httpClient.get(apiPath("devices/${deviceId.value}")).body()
    }

    // Actions

    override suspend fun getDeviceActions(deviceId: DeviceId, submittedAfter: Instant): List<DeviceAction> {
        return httpClient.get(apiPath("devices/${deviceId.value}/actions")) {
            parameter("submittedAfter", submittedAfter.toString())
        }.body()
    }

    override suspend fun submitDeviceAction(deviceId: DeviceId, request: DeviceActionPrototype): DeviceAction {
        return httpClient.post(apiPath("devices/${deviceId.value}/actions")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun completeDeviceAction(deviceId: DeviceId, actionId: DeviceActionId): DeviceAction {
        return httpClient.put(apiPath("devices/${deviceId.value}/actions/${actionId.value}/complete")).body()
    }

    override suspend fun connectDeviceActions(deviceId: DeviceId): Flow<DeviceAction> {
        return channelFlow {
            val device = getDevice(deviceId)

            httpClient.webSocket(
                request = {
                    method = HttpMethod.Get
                    url {
                        takeFrom(hostUrl)
                        path("api", "v1", "devices", deviceId.value, "connect")
                        protocol = if (hostUrl.protocol == URLProtocol.HTTPS) URLProtocol.WSS else URLProtocol.WS
                    }
                }
            ) {
                val authorization = tokenStore.authorization
                if (authorization != null) {
                    outgoing.send(Frame.Text(authorization.substringAfter(' ')))
                }

                val pending = getDeviceActions(device.id, device.lastActionTime ?: Instant.DISTANT_PAST)
                val pendingIds = pending.map { it.id }.toSet()

                incoming.consumeAsFlow()
                    .filterIsInstance<Frame.Text>()
                    .map { json.decodeFromString(DeviceAction.serializer(), it.readText()) }
                    .filter { it.id !in pendingIds }
                    .onStart { emitAll(pending.asFlow()) }
                    .filter { it.completed == null }
                    .collect { channel.send(it) }
            }
        }
    }

    // Charts

    override suspend fun getCharts(): List<Chart> {
        return httpClient.get(apiPath("charts")).body()
    }

    // Notifications

    override suspend fun getNotifications(userId: UserId): List<Notification> {
        return httpClient.get(apiPath("users/${userId.value}/notifications")).body()
    }

    override suspend fun acknowledgeNotification(userId: UserId, notificationId: NotificationId): Notification {
        return httpClient.put(apiPath("users/${userId.value}/notifications/${notificationId.value}/acknowledge")).body()
    }
}
