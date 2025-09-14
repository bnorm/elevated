package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.log.getLogger
import dev.bnorm.elevated.model.devices.DeviceAction
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

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

        private val context = Dispatchers.Default + CoroutineExceptionHandler { _, t ->
            log.warn(t) { "Unhandled exception in worker scope" }
        }
    }

    override suspend fun run() {
        elevatedClient.authenticate()

        supervisorScope {
            schedule(name = "Device Authentication", frequency = 1.hours, context = context) {
                // Refresh authentication every hour
                elevatedClient.authenticate()
            }

            schedule(name = "Record Sensors", frequency = 1.minutes, context = context) {
                sensorReadingService.record()
            }

            launch(context = context) {
                processActions()
            }
        }
    }

    private suspend fun processActions() {
        val log = getLogger("dev.bnorm.elevated.raspberry.Application.actions")
        suspend fun processAction(action: DeviceAction) {
            try {
                log.info { "Received action: $action" }
                when (val args = action.args) {
                    is PumpDispenseArguments -> {
                        pumpService[args.pumpId ?: return]?.dispense(args.amount)
                        elevatedClient.completeDeviceAction(action.id)
                    }
                }
                log.info { "Completed action: $action" }
            } catch (t: Throwable) {
                log.warn(t) { "Exception processing action: $action" }
                throw t
            }
        }

        while (true) {
            supervisorScope {
                launch {
                    try {
                        elevatedClient.getActionQueue()
                            .collect { processAction(it) }
                    } catch (t: Throwable) {
                        log.warn(t) { "Exception processing action queue" }
                        throw t
                    }
                }
            }
        }
    }
}
