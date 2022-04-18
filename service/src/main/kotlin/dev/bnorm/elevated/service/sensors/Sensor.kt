package dev.bnorm.elevated.service.sensors

import kotlinx.serialization.Serializable

@Serializable
class Sensor(
    val id: SensorId,
    val name: String,
)
