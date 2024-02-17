package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.devices.DeviceId
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.cinterop.toKString
import platform.posix.getenv

actual fun createApplication(): Application {
    // TODO System.setProperty("kotlinx.coroutines.debug", "on") // Enable Kotlin coroutines debugging

    val elevatedClient = ElevatedClient(
        httpClient = HttpClient(CIO),
        deviceId = DeviceId("62780348770bd023d5d971e9"),
        deviceKey = Password(getenv("DEVICE_KEY")!!.toKString())
    )

    val pumpService = PumpService()
    val sensorService = SensorService()
    val sensorReadingService = SensorReadingService(sensorService, elevatedClient)

    return DefaultApplication(elevatedClient, pumpService, sensorReadingService)
}
