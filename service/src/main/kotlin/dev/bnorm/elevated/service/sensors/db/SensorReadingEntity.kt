package dev.bnorm.elevated.service.sensors.db

import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(SensorReadingEntity.COLLECTION_NAME)
class SensorReadingEntity(
    val sensorId: String,
    val timestamp: Instant,
    val value: Double,
) {
    companion object {
        const val COLLECTION_NAME = "sensorReadings"
    }
}
