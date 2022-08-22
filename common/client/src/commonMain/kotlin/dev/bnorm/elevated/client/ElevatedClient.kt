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
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Instant

class ElevatedClient(
    private val httpClient: HttpClient,
    private val hostUrl: Url,
) {
    private val apiUrl = URLBuilder().apply {
        takeFrom(hostUrl)
        path("api", "v1")
    }.build()

    private fun Url.appendPath(vararg path: String) = when (encodedPath) {
        "/" -> copy(encodedPath = path.joinToString("/", prefix = "/"))
        else -> copy(encodedPath = "$encodedPath${path.joinToString("/", prefix = "/")}")
    }

    suspend fun login(request: UserLoginRequest): AuthenticatedUser {
        return httpClient.post(apiUrl.appendPath("users/login")) {
            contentType(ContentType.Application.Json)
            body = request
        }
    }

    suspend fun getCurrentUser(): AuthenticatedUser {
        return httpClient.get(apiUrl.appendPath("users/current"))
    }

    // Sensors

    suspend fun getSensors(): List<Sensor> {
        return httpClient.get(apiUrl.appendPath("sensors"))
    }

    // Readings

    suspend fun recordSensorReading(
        sensorId: SensorId,
        sensorReadingPrototype: SensorReadingPrototype,
    ): SensorReading {
        return httpClient.post(apiUrl.appendPath("sensors/${sensorId.value}/readings/record")) {
            contentType(ContentType.Application.Json)
            body = sensorReadingPrototype
        }
    }

    suspend fun getSensorReadings(
        sensorId: SensorId,
        startTime: Instant? = null,
        endTime: Instant? = null,
    ): List<SensorReading> {
        return httpClient.get(apiUrl.appendPath("sensors/${sensorId.value}/readings")) {
            if (startTime != null) parameter("startTime", startTime.toString())
            if (endTime != null) parameter("endTime", endTime.toString())
        }
    }

    suspend fun getLatestSensorReadings(
        sensorId: SensorId,
        count: Int? = null,
    ): List<SensorReading> {
        return httpClient.get(apiUrl.appendPath("sensors/${sensorId.value}/readings/latest")) {
            if (count != null) parameter("count", count.toString())
        }
    }

    // Devices

    suspend fun loginDevice(request: DeviceLoginRequest): AuthenticatedDevice {
        return httpClient.post(apiUrl.appendPath("devices/login")) {
            contentType(ContentType.Application.Json)
            body = request
        }
    }

    suspend fun getDevices(): List<Device> {
        return httpClient.get(apiUrl.appendPath("devices"))
    }

    suspend fun getDevice(deviceId: DeviceId): Device {
        return httpClient.get(apiUrl.appendPath("devices/${deviceId.value}"))
    }

    // Actions

    suspend fun getDeviceActions(deviceId: DeviceId, submittedAfter: Instant): List<DeviceAction> {
        return httpClient.get(apiUrl.appendPath("devices/${deviceId.value}/actions")) {
            parameter("submittedAfter", submittedAfter.toString())
        }
    }

    suspend fun submitDeviceAction(deviceId: DeviceId, request: DeviceActionPrototype): DeviceAction {
        return httpClient.post(apiUrl.appendPath("devices/${deviceId.value}/actions")) {
            contentType(ContentType.Application.Json)
            body = request
        }
    }

    suspend fun completeDeviceAction(deviceId: DeviceId, actionId: DeviceActionId): DeviceAction {
        return httpClient.put(apiUrl.appendPath("devices/${deviceId.value}/actions/${actionId.value}/complete"))
    }

    // Charts

    suspend fun getCharts(): List<Chart> =
        httpClient.get(apiUrl.appendPath("charts"))
}
