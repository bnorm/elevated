package dev.bnorm.elevated.raspberry

import com.pi4j.Pi4J
import dev.bnorm.elevated.log.getLogger
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import java.time.Duration
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import okhttp3.OkHttpClient

private val log = getLogger("dev.bnorm.elevated.raspberry")

private val env = System.getenv()
private val DEVICE_KEY = Password(env.getValue("DEVICE_KEY"))
private val DEVICE_ID = DeviceId("62780348770bd023d5d971e9")

suspend fun main() {
    System.setProperty("kotlinx.coroutines.debug", "on") // Enable Kotlin coroutines debugging
    val loggingExceptionHandler = CoroutineExceptionHandler { _, t ->
        log.warn(t) { "Unhandled exception in worker scope" }
    }
    withContext(Dispatchers.Default + loggingExceptionHandler) {
        supervisorScope {

            val elevatedClient: ElevatedClient?
            val pumpService: PumpService
            val sensorService: SensorService
            val sensorReadingService: SensorReadingService?

            if (System.getProperty("os.name") == "Mac OS X") {
                // TODO connect to localhost
                log.warn { "Not running on Raspberry PI!" }
            } else {

                val okHttpClient = OkHttpClient.Builder()
                    .pingInterval(Duration.ofSeconds(30))
                    .build()

                val httpClient = HttpClient(OkHttp.create {
                    preconfigured = okHttpClient
                })

                elevatedClient = ElevatedClient(httpClient, DEVICE_ID, DEVICE_KEY)
                elevatedClient.authenticate()

                val pi4j = Pi4J.newAutoContext()
                pumpService = pi4j.PumpService()
                sensorService = pi4j.SensorService()

                sensorReadingService = SensorReadingService(sensorService, elevatedClient)

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
