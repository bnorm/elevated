package dev.bnorm.elevated.raspberry

import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.datetime.Clock
import org.slf4j.LoggerFactory

class SensorReadingService(
    private val sensorService: SensorService,
    private val elevatedClient: ElevatedClient,
) {
    companion object {
        private val log = LoggerFactory.getLogger(SensorReadingService::class.java)
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
                        log.warn("Unable to upload {} sensor reading to elevated.bnorm.dev", type, error)
                    }
                }
            }
        }
    }
}
