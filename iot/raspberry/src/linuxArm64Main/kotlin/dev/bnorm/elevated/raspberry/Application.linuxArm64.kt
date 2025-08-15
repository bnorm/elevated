package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.devices.DeviceId
import io.ktor.client.HttpClient
import io.ktor.client.engine.curl.Curl
import kotlinx.cinterop.toKString
import platform.posix.getenv

actual fun createApplication(): Application {
    // TODO enable Kotlin coroutines debugging
    //  - blocked by https://github.com/Kotlin/kotlinx.coroutines/issues/4339

    val elevatedClient = ElevatedClient(
        httpClient = HttpClient(Curl.create {
            // https://youtrack.jetbrains.com/issue/KTOR-8339
            caPath = "/etc/ssl/certs"
        }),
        deviceId = DeviceId("62780348770bd023d5d971e9"),
        deviceKey = Password(getenv("DEVICE_KEY")!!.toKString())
    )

    val pumpService = PumpService()
    val sensorService = SensorService()
    val sensorReadingService = SensorReadingService(sensorService, elevatedClient)

    return DefaultApplication(elevatedClient, pumpService, sensorReadingService)
}
