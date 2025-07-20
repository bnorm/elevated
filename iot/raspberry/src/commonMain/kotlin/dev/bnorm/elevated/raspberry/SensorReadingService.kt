package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.log.getLogger
import kotlin.time.Clock
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class SensorReadingService(
    private val sensorService: SensorService,
    private val elevatedClient: ElevatedClient,
) {
    companion object {
        private val log = getLogger<SensorReadingService>()
    }

    suspend fun record() {
        supervisorScope {
            val timestamp = Clock.System.now()
            for (type in SensorType.values()) {
                launch {
                    val reading = sensorService[type].read()
                    runCatching {
                        elevatedClient.recordSensorReading(type.id, reading, timestamp)
                    }.onFailure { error ->
                        log.warn(error) { "Unable to upload $type sensor reading to elevated.bnorm.dev" }
                    }
                }
            }
        }
    }
}
