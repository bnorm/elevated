package dev.bnorm.elevated.raspberry

import com.pi4j.Pi4J
import dev.bnorm.elevated.log.getLogger
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import java.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient

actual fun createApplication(): Application {
    val env = System.getenv()
    System.setProperty("kotlinx.coroutines.debug", "on") // Enable Kotlin coroutines debugging

    if (System.getProperty("os.name") == "Mac OS X") {
        return FakeApplication()
    }

    val okHttpClient = OkHttpClient.Builder()
        .pingInterval(Duration.ofSeconds(30))
        .build()

    val httpClient = HttpClient(OkHttp.create {
        preconfigured = okHttpClient
    })

    val elevatedClient = ElevatedClient(
        httpClient = httpClient,
        deviceId = DeviceId("62780348770bd023d5d971e9"),
        deviceKey = Password(env.getValue("DEVICE_KEY"))
    )

    val pi4j = Pi4J.newAutoContext()
    val pumpService = pi4j.PumpService()
    val sensorService = pi4j.SensorService()
    val sensorReadingService = SensorReadingService(sensorService, elevatedClient)

    return JvmApplication(elevatedClient, pumpService, sensorReadingService)
}

private class FakeApplication : Application {
    override suspend fun run() {
        // TODO connect to localhost
        throw UnsupportedOperationException("Not running on Raspberry PI!")
    }
}

private class JvmApplication(
    private val elevatedClient: ElevatedClient,
    private val pumpService: PumpService,
    private val sensorReadingService: SensorReadingService,
) : Application {
    companion object {
        private val log = getLogger<Application>()
    }

    private val loggingExceptionHandler = CoroutineExceptionHandler { _, t ->
        log.warn(t) { "Unhandled exception in worker scope" }
    }

    override suspend fun run() {
        elevatedClient.authenticate()

        withContext(Dispatchers.Default + loggingExceptionHandler) {
            supervisorScope {
                schedule(name = "Device Authentication", frequency = 1.hours) {
                    // Immediately login and refresh authentication every hour
                    elevatedClient.authenticate()
                }

                schedule(name = "Record Sensors", frequency = 1.minutes) {
                    sensorReadingService.record()
                }

                launch {
                    elevatedClient.getActionQueue().collect {
                        when (val args = it.args) {
                            is PumpDispenseArguments -> {
                                pumpService[args.pump]?.dispense(args.amount)
                                elevatedClient.completeDeviceAction(it.id)
                            }
                        }
                    }
                }
            }
        }
    }
}
