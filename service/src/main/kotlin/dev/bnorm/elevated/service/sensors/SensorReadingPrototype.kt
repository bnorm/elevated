package dev.bnorm.elevated.service.sensors

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
class SensorReadingPrototype(
    val value: Double,
    @Serializable(with = InstantSerializer::class) val timestamp: Instant,
)
