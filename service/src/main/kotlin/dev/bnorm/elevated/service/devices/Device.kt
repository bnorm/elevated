package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.service.sensors.InstantSerializer
import dev.bnorm.elevated.service.sensors.Sensor
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
class Device(
    val id: DeviceId,
    val name: String,
    val sensors: List<Sensor>,
    @Serializable(with = InstantSerializer::class) val lastActionTime: Instant? = null,
)
