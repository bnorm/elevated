package dev.bnorm.elevated.model.sensors

import dev.bnorm.elevated.model.devices.DeviceId
import kotlinx.serialization.Serializable

@Serializable
class SensorPrototype(
    val name: String,
    val deviceId: DeviceId,
)
