package dev.bnorm.elevated.service.sensors

import dev.bnorm.elevated.service.devices.DeviceId
import kotlinx.serialization.Serializable

@Serializable
class SensorPrototype(
    val name: String,
    val deviceId: DeviceId,
)
