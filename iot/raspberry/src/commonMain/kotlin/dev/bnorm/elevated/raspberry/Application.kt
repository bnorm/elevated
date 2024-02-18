package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.log.getLogger
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

expect fun createApplication(): Application

interface Application {
    suspend fun run()
}

class DefaultApplication(
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
                                pumpService[args.pumpId ?: return@collect]?.dispense(args.amount)
                                elevatedClient.completeDeviceAction(it.id)
                            }
                        }
                    }
                }
            }
        }
    }
}
