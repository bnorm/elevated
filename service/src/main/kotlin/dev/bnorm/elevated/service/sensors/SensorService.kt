package dev.bnorm.elevated.service.sensors

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.sensors.Sensor
import dev.bnorm.elevated.model.sensors.SensorCreateRequest
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorUpdateRequest
import dev.bnorm.elevated.service.sensors.db.SensorEntity
import dev.bnorm.elevated.service.sensors.db.SensorRepository
import dev.bnorm.elevated.service.sensors.db.SensorUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class SensorService(
    private val sensorRepository: SensorRepository,
) {
    suspend fun createSensor(request: SensorCreateRequest): Sensor {
        return sensorRepository.create(request.toEntity()).toDto()
    }

    suspend fun updateSensor(sensorId: SensorId, request: SensorUpdateRequest): Sensor? {
        return sensorRepository.update(sensorId, request.toUpdate())?.toDto()
    }

    suspend fun deleteSensor(sensorId: SensorId) {
        sensorRepository.delete(sensorId)
    }

    fun getSensors(): Flow<Sensor> {
        return sensorRepository.getAll().map { it.toDto() }
    }

    suspend fun getSensor(sensorId: SensorId): Sensor? {
        return sensorRepository.getById(sensorId)?.toDto()
    }

    fun getSensorsByDeviceId(deviceId: DeviceId): Flow<Sensor> {
        return sensorRepository.getByDeviceId(deviceId).map { it.toDto() }
    }

    private fun SensorEntity.toDto(): Sensor {
        return Sensor(
            id = SensorId(id),
            name = name,
            type = type,
        )
    }

    private fun SensorCreateRequest.toEntity(): SensorEntity {
        return SensorEntity(
            name = name,
            type = type,
            deviceId = deviceId.value,
        )
    }

    private fun SensorUpdateRequest.toUpdate(): SensorUpdate {
        return SensorUpdate(
            name = name,
            type = type,
            deviceId = deviceId?.value,
        )
    }
}
