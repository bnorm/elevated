package dev.bnorm.elevated.model.sensors

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
class SensorReadingPrototype(
    val value: Double,
    val timestamp: Instant,
)
