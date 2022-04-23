package dev.bnorm.elevated.model.sensors

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
class SensorReading(
    val sensorId: SensorId,
    val value: Double,
    val timestamp: Instant,
)
