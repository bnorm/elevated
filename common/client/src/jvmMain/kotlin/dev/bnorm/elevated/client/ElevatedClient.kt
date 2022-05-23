package dev.bnorm.elevated.client

import dev.bnorm.elevated.model.auth.AuthenticatedDevice
import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.model.devices.*
import dev.bnorm.elevated.model.sensors.Sensor
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.model.sensors.SensorReadingPrototype
import dev.bnorm.elevated.model.users.UserLoginRequest
import kotlinx.datetime.Instant
import retrofit2.http.*

interface ElevatedClient {

    // Users

    @POST("/api/v1/users/login")
    suspend fun login(@Body request: UserLoginRequest): AuthenticatedUser

    @GET("/api/v1/users/current")
    suspend fun getCurrentUser(): AuthenticatedUser

    // Sensors

    @GET("/api/v1/sensors")
    suspend fun getSensors(): List<Sensor>

    // Readings

    @POST("/api/v1/sensors/{sensorId}/readings/record")
    suspend fun recordSensorReading(
        @Path("sensorId") sensorId: SensorId,
        @Body sensorReadingPrototype: SensorReadingPrototype,
    ): SensorReading

    @GET("/api/v1/sensors/{sensorId}/readings")
    suspend fun getSensorReadings(
        @Path("sensorId") sensorId: SensorId,
        @Query("startTime") startTime: Instant? = null,
        @Query("endTime") endTime: Instant? = null,
    ): List<SensorReading>

    @GET("/api/v1/sensors/{sensorId}/readings/latest")
    suspend fun getLatestSensorReadings(
        @Path("sensorId") sensorId: SensorId,
        @Query("count") count: Int? = null,
    ): List<SensorReading>

    // Devices

    @POST("/api/v1/devices/login")
    suspend fun loginDevice(
        @Body request: DeviceLoginRequest
    ): AuthenticatedDevice

    @GET("/api/v1/devices")
    suspend fun getDevices(): List<Device>

    @GET("/api/v1/devices/{deviceId}")
    suspend fun getDevice(
        @Path("deviceId") deviceId: DeviceId,
    ): Device

    // Actions

    @GET("/api/v1/devices/{deviceId}/actions")
    suspend fun getDeviceActions(
        @Path("deviceId") deviceId: DeviceId,
        @Query("submittedAfter") submittedAfter: Instant,
    ): List<DeviceAction>

    @POST("/api/v1/devices/{deviceId}/actions")
    suspend fun submitDeviceAction(
        @Path("deviceId") deviceId: DeviceId,
        @Body request: DeviceActionPrototype
    ): DeviceAction

    @PUT("/api/v1/devices/{deviceId}/actions/{actionId}/complete")
    suspend fun completeDeviceAction(
        @Path("deviceId") deviceId: DeviceId,
        @Path("actionId") actionId: DeviceActionId,
    ): DeviceAction
}
