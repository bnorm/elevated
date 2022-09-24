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
import dev.bnorm.elevated.model.sensors.Sensor
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.model.sensors.SensorReadingPrototype
import dev.bnorm.elevated.model.users.UserId
import dev.bnorm.elevated.model.users.UserLoginRequest
import kotlinx.coroutines.flow.Flow
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

    suspend fun connectDeviceActions(deviceId: DeviceId): Flow<DeviceAction>

    suspend fun getCharts(): List<Chart>

    // Notifications

    suspend fun getNotifications(userId: UserId): List<Notification>

    suspend fun acknowledgeNotification(userId: UserId, notificationId: NotificationId): Notification
}
