package dev.bnorm.elevated.service.sensors

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.sensors.Sensor
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorPrototype
import dev.bnorm.elevated.service.sensors.db.SensorEntity
import dev.bnorm.elevated.service.sensors.db.SensorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class SensorService(
    private val sensorRepository: SensorRepository,
) {
    suspend fun createSensor(prototype: SensorPrototype): Sensor {
        return sensorRepository.insert(prototype.toEntity()).toDto()
    }
    suspend fun deleteSensor(sensorId: SensorId) {
        sensorRepository.delete(sensorId)
    }

    fun getAllSensors(): Flow<Sensor> {
        return sensorRepository.findAll().map { it.toDto() }
    }

    fun getSensorByDeviceId(deviceId: DeviceId): Flow<Sensor> {
        return sensorRepository.findByDeviceId(deviceId).map { it.toDto() }
    }

    suspend fun getSensorById(sensorId: SensorId): Sensor? {
        return sensorRepository.findBySensorId(sensorId)?.toDto()
    }

    private fun SensorEntity.toDto(): Sensor {
        return Sensor(
            id = SensorId(id),
            name = name,
        )
    }

    private fun SensorPrototype.toEntity(): SensorEntity {
        return SensorEntity(
            name = name,
            deviceId = deviceId.value,
        )
    }
}
