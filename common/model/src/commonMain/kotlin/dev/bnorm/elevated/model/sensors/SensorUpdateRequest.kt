package dev.bnorm.elevated.model.sensors

import dev.bnorm.elevated.model.devices.DeviceId
import kotlinx.serialization.Serializable

@Serializable
data class SensorUpdateRequest(
    val name: String? = null,
    val type: SensorType? = null,
    val deviceId: DeviceId? = null,
)
