package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.log.getLogger
import kotlin.time.Clock
import kotlinx.coroutines.delay
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
            for (sensor in sensorService.all) {
                val reading = sensor.read()
                runCatching {
                    elevatedClient.recordSensorReading(sensor.id, reading, timestamp)
                }.onFailure { error ->
                    log.warn(error) { "Unable to upload ${sensor.type} sensor reading to elevated.bnorm.dev" }
                }
                delay(1_000) // Isolate each reading.
            }
        }
    }
}
