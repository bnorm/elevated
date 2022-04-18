package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.service.sensors.Sensor
import kotlinx.serialization.Serializable

@Serializable
class Device(
    val id: DeviceId,
    val name: String,
    val sensors: List<Sensor>
)
