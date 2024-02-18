package dev.bnorm.elevated.service.pumps

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.pumps.Pump
import dev.bnorm.elevated.model.pumps.PumpCreateRequest
import dev.bnorm.elevated.model.pumps.PumpId
import dev.bnorm.elevated.model.pumps.PumpUpdateRequest
import dev.bnorm.elevated.service.pumps.db.PumpEntity
import dev.bnorm.elevated.service.pumps.db.PumpRepository
import dev.bnorm.elevated.service.pumps.db.PumpUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class PumpService(
    private val pumpRepository: PumpRepository,
) {
    suspend fun createPump(request: PumpCreateRequest): Pump {
        return pumpRepository.create(request.toEntity()).toDto()
    }

    suspend fun updatePump(pumpId: PumpId, request: PumpUpdateRequest): Pump? {
        return pumpRepository.update(pumpId, request.toUpdate())?.toDto()
    }

    suspend fun deletePump(pumpId: PumpId) {
        pumpRepository.delete(pumpId)
    }

    fun getPumps(): Flow<Pump> {
        return pumpRepository.getAll().map { it.toDto() }
    }

    suspend fun getPump(pumpId: PumpId): Pump? {
        return pumpRepository.getById(pumpId)?.toDto()
    }

    fun getPumpsByDeviceId(deviceId: DeviceId): Flow<Pump> {
        return pumpRepository.getByDeviceId(deviceId).map { it.toDto() }
    }

    private fun PumpEntity.toDto(): Pump {
        return Pump(
            id = id,
            name = name,
            flowRate = flowRate,
            content = content,
        )
    }

    private fun PumpCreateRequest.toEntity(): PumpEntity {
        return PumpEntity(
            name = name,
            deviceId = deviceId,
            flowRate = flowRate,
            content = content,
        )
    }

    private fun PumpUpdateRequest.toUpdate(): PumpUpdate {
        return PumpUpdate(
            name = name,
            deviceId = deviceId?.value,
            flowRate = flowRate,
            content = content,
        )
    }
}
