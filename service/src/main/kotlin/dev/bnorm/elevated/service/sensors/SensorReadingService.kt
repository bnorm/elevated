package dev.bnorm.elevated.service.sensors

import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.model.sensors.SensorReadingPrototype
import dev.bnorm.elevated.service.sensors.db.SensorReadingEntity
import dev.bnorm.elevated.service.sensors.db.SensorReadingRepository
import java.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

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

    fun getLatestSensorReading(
        sensorId: SensorId,
        count: Int = 1,
    ): Flow<SensorReading> {
        return sensorReadingRepository.findLatestBySensorId(sensorId, count).map { it.toDto() }
    }

    private fun SensorReadingEntity.toDto(): SensorReading {
        return SensorReading(
            sensorId = sensorId,
            timestamp = timestamp.toKotlinInstant(),
            value = value,
        )
    }

    private fun SensorReadingPrototype.toEntity(sensorId: SensorId): SensorReadingEntity {
        return SensorReadingEntity(
            sensorId = sensorId,
            timestamp = timestamp.toJavaInstant(),
            value = value,
        )
    }
}
