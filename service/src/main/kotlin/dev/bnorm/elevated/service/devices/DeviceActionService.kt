package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.model.devices.DeviceAction
import dev.bnorm.elevated.model.devices.DeviceActionArguments
import dev.bnorm.elevated.model.devices.DeviceActionId
import dev.bnorm.elevated.model.devices.DeviceActionPrototype
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import dev.bnorm.elevated.service.devices.db.DeviceActionArgumentsEntity
import dev.bnorm.elevated.service.devices.db.DeviceActionEntity
import dev.bnorm.elevated.service.devices.db.DeviceActionRepository
import dev.bnorm.elevated.service.devices.db.PumpDispenseArgumentsEntity
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class DeviceActionService(
    private val deviceActionRepository: DeviceActionRepository,
    private val deviceService: DeviceService,
) {
    // TODO Replace with a mongo change stream?
    private val deviceActions = MutableSharedFlow<DeviceAction>(
        replay = 0,
        extraBufferCapacity = Channel.UNLIMITED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    suspend fun submitDeviceAction(
        deviceId: DeviceId,
        prototype: DeviceActionPrototype,
    ): DeviceAction {
        val action = deviceActionRepository.insert(prototype.toEntity(deviceId)).toDto()
        deviceActions.emit(action)
        return action
    }

    suspend fun getAction(deviceId: DeviceId, deviceActionId: DeviceActionId): DeviceAction? {
        return deviceActionRepository.findById(deviceId, deviceActionId)?.toDto()
    }

    suspend fun completeAction(deviceId: DeviceId, deviceActionId: DeviceActionId): DeviceAction? {
        val timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val device = deviceService.getDeviceById(deviceId)
        if (device != null) {
            val action = deviceActionRepository.complete(deviceId, deviceActionId, timestamp)
            if (action != null) {
                val lastActionTime = device.lastActionTime
                if (lastActionTime == null || lastActionTime.toJavaInstant() < timestamp) {
                    deviceService.updateDevice(deviceId, timestamp)
                }
                return action.toDto()
            }
        }
        return null
    }

    fun watchActions(deviceId: DeviceId): Flow<DeviceAction> {
        return deviceActions.filter { it.deviceId == deviceId }
    }

    fun getActions(deviceId: DeviceId, submittedAfter: Instant, limit: Int?): Flow<DeviceAction> {
        return deviceActionRepository.findByDeviceId(deviceId, submittedAfter, limit).map { it.toDto() }
    }

    fun getLatestActions(deviceId: DeviceId, limit: Int): Flow<DeviceAction> {
        return deviceActionRepository.findLatestByDeviceId(deviceId, limit).map { it.toDto() }
    }

    suspend fun deleteAction(deviceId: DeviceId, deviceActionId: DeviceActionId) {
        deviceActionRepository.deleteById(deviceId, deviceActionId)
    }

    private fun DeviceActionEntity.toDto(): DeviceAction {
        return DeviceAction(
            id = id,
            deviceId = deviceId,
            submitted = submitted.toKotlinInstant(),
            completed = completed?.toKotlinInstant(),
            args = args.toDto()
        )
    }

    private fun DeviceActionArgumentsEntity.toDto(): DeviceActionArguments {
        return when (this) {
            is PumpDispenseArgumentsEntity -> PumpDispenseArguments(
                pump = pump,
                pumpId = pumpId,
                amount = amount,
            )
        }
    }

    private fun DeviceActionPrototype.toEntity(deviceId: DeviceId): DeviceActionEntity {
        return DeviceActionEntity(
            deviceId = deviceId,
            submitted = Instant.now().truncatedTo(ChronoUnit.MILLIS),
            completed = null,
            args = args.toEntity(),
        )
    }

    private fun DeviceActionArguments.toEntity(): DeviceActionArgumentsEntity {
        return when (this) {
            is PumpDispenseArguments -> PumpDispenseArgumentsEntity(
                pump = pump,
                pumpId = pumpId,
                amount = amount,
            )
        }
    }
}
