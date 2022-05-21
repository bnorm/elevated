package dev.bnorm.elevated

import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.model.sensors.Sensor
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.model.devices.*
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

    @GET("/api/v1/sensors/{sensorId}/readings")
    suspend fun getSensorReadings(
        @Path("sensorId") sensorId: String,
        @Query("startTime") startTime: Instant? = null,
        @Query("endTime") endTime: Instant? = null,
    ): List<SensorReading>

    // Devices

    @POST("/api/v1/devices/{deviceId}/actions")
    suspend fun submitDeviceAction(
        @Path("deviceId") deviceId: String,
        @Body request: DeviceActionPrototype
    ): DeviceAction
}

fun MockElevatedService(): ElevatedClient = object : ElevatedClient {
    override suspend fun login(request: UserLoginRequest): Nothing =
        throw NotImplementedError()

    override suspend fun getCurrentUser(): Nothing = throw NotImplementedError()

    override suspend fun getSensors(): Nothing = throw NotImplementedError()

    override suspend fun getSensorReadings(
        sensorId: String,
        startTime: Instant?,
        endTime: Instant?
    ): Nothing = throw NotImplementedError()

    override suspend fun submitDeviceAction(
        deviceId: String,
        request: DeviceActionPrototype
    ): Nothing = throw NotImplementedError()
}