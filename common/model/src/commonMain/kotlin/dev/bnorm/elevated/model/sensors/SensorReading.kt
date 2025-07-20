package dev.bnorm.elevated.model.sensors

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SensorReading(
    val sensorId: SensorId,
    val value: Double,
    val timestamp: Instant,
)
