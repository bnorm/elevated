package dev.bnorm.elevated.service.sensors

import dev.bnorm.elevated.service.sensors.db.SensorReadingEntity
import dev.bnorm.elevated.service.sensors.db.SensorReadingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class SensorReadingService(
    private val sensorReadingRepository: SensorReadingRepository,
) {
    suspend fun createSensorReading(sensorId: SensorId, prototype: SensorReadingPrototype): SensorReading {
        return sensorReadingRepository.insert(prototype.toEntity(sensorId)).toDto()
    }

    fun getSensorReadings(
        sensorId: SensorId,
        startTime: Instant,
        endTime: Instant,
    ): Flow<SensorReading> {
        return sensorReadingRepository.findBySensorId(sensorId, startTime, endTime).map { it.toDto() }
    }

    private fun SensorReadingEntity.toDto(): SensorReading {
        return SensorReading(
            sensorId = SensorId(sensorId),
            timestamp = timestamp,
            value = value,
        )
    }

    private fun SensorReadingPrototype.toEntity(sensorId: SensorId): SensorReadingEntity {
        return SensorReadingEntity(
            sensorId = sensorId.value,
            timestamp = timestamp,
            value = value,
        )
    }
}
