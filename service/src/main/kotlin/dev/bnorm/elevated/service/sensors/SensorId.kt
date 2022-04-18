package dev.bnorm.elevated.service.sensors

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class SensorId(
    val value: String,
)
