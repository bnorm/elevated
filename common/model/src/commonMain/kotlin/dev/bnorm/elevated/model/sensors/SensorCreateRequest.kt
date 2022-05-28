package dev.bnorm.elevated.model.sensors

import dev.bnorm.elevated.model.devices.DeviceId
import kotlinx.serialization.Serializable

@Serializable
data class SensorCreateRequest(
    val name: String,
    val type: SensorType,
    val deviceId: DeviceId,
)
