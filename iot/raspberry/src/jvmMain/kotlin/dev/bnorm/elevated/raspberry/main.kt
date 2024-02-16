package dev.bnorm.elevated.raspberry

import com.pi4j.Pi4J
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val log = LoggerFactory.getLogger("dev.bnorm.elevated.raspberry")

suspend fun main() {
    System.setProperty("kotlinx.coroutines.debug", "on") // Enable Kotlin coroutines debugging
    val loggingExceptionHandler = CoroutineExceptionHandler { _, t ->
        log.warn("Unhandled exception in worker scope", t)
    }
    withContext(Dispatchers.Default + loggingExceptionHandler) {
        supervisorScope {

            val elevatedClient: ElevatedClient?
            val pumpService: PumpService
            val sensorService: SensorService
            val sensorReadingService: SensorReadingService?

            if (System.getProperty("os.name") == "Mac OS X") {
                // TODO connect to localhost
                log.warn("Not running on Raspberry PI!")
            } else {
                elevatedClient = ElevatedClient()
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
