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
import kotlinx.datetime.Instant

interface ElevatedClient {
    suspend fun login(request: UserLoginRequest): AuthenticatedUser

    suspend fun getCurrentUser(): AuthenticatedUser

    suspend fun getSensors(): List<Sensor>

    suspend fun recordSensorReading(
        sensorId: SensorId,
        sensorReadingPrototype: SensorReadingPrototype,
    ): SensorReading

    suspend fun getSensorReadings(
        sensorId: SensorId,
        startTime: Instant? = null,
        endTime: Instant? = null,
    ): List<SensorReading>

    suspend fun getLatestSensorReadings(
        sensorId: SensorId,
        count: Int? = null,
    ): List<SensorReading>

    suspend fun loginDevice(request: DeviceLoginRequest): AuthenticatedDevice

    suspend fun getDevices(): List<Device>

    suspend fun getDevice(deviceId: DeviceId): Device

    suspend fun getDeviceActions(deviceId: DeviceId, submittedAfter: Instant): List<DeviceAction>

    suspend fun submitDeviceAction(deviceId: DeviceId, request: DeviceActionPrototype): DeviceAction

    suspend fun completeDeviceAction(deviceId: DeviceId, actionId: DeviceActionId): DeviceAction

    suspend fun getCharts(): List<Chart>
}
