package dev.bnorm.elevated.raspberry

import com.pi4j.Pi4J
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.devices.DeviceId
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import java.time.Duration
import okhttp3.OkHttpClient

actual fun createApplication(): Application {
    val env = System.getenv()
    System.setProperty("kotlinx.coroutines.debug", "on") // Enable Kotlin coroutines debugging

    if (System.getProperty("os.name") == "Mac OS X") {
        return FakeApplication()
    }

    val elevatedClient = ElevatedClient(
        httpClient = HttpClient(OkHttp.create {
            preconfigured = OkHttpClient.Builder().apply {
                pingInterval(Duration.ofSeconds(30))
            }.build()
        }),
        deviceId = DeviceId("62780348770bd023d5d971e9"),
        deviceKey = Password(env.getValue("DEVICE_KEY"))
    )

    val pi4j = Pi4J.newAutoContext()
    val pumpService = pi4j.PumpService()
    val sensorService = pi4j.SensorService()
    val sensorReadingService = SensorReadingService(sensorService, elevatedClient)

    return DefaultApplication(elevatedClient, pumpService, sensorReadingService)
}

private class FakeApplication : Application {
    override suspend fun run() {
        // TODO connect to localhost
        throw UnsupportedOperationException("Not running on Raspberry PI!")
    }
}
