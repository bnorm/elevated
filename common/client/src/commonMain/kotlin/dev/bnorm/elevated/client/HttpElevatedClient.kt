package dev.bnorm.elevated.client

import dev.bnorm.elevated.model.auth.AuthenticatedDevice
import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.model.charts.Chart
import dev.bnorm.elevated.model.devices.*
import dev.bnorm.elevated.model.sensors.Sensor
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.model.sensors.SensorReadingPrototype
import dev.bnorm.elevated.model.users.UserLoginRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class HttpElevatedClient(
    private val hostUrl: Url,
    baseHttpClient: HttpClient,
    private val tokenStore: TokenStore,
    private val json: Json = DefaultJson,
) : ElevatedClient {
    private val httpClient = baseHttpClient.config {
        install(WebSockets)

        install(ContentNegotiation) {
            json(json)
        }

        expectSuccess = true
        install(DefaultRequest) {
            tokenStore.authorization?.let { headers[HttpHeaders.Authorization] = it }
        }

        install(HttpCallValidator) {
            handleResponseExceptionWithRequest { exception, request ->
                val clientException = exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                if (clientException.response.status == HttpStatusCode.Unauthorized) {
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

    suspend fun getActionQueue(deviceId: DeviceId): Flow<DeviceAction> {
        return channelFlow {
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

                incoming.consumeAsFlow()
                    .filterIsInstance<Frame.Text>()
                    .collect {
                        val frame = it.readText()
                        val action = json.decodeFromString(DeviceAction.serializer(), frame)
                        channel.send(action)
                    }
            }
        }
    }

    // Charts

    override suspend fun getCharts(): List<Chart> =
        httpClient.get(apiPath("charts")).body()
}
