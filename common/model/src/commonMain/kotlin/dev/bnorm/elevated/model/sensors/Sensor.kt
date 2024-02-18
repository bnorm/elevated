package dev.bnorm.elevated.model.sensors

import kotlinx.serialization.Serializable

@Serializable
data class Sensor(
    val id: SensorId,
    val name: String,
    val type: MeasurementType? = null,
)
