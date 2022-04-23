package dev.bnorm.elevated.model.sensors

import kotlinx.serialization.Serializable

@Serializable
class Sensor(
    val id: SensorId,
    val name: String,
)
