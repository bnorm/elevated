package dev.bnorm.elevated.service.sensors.db

import dev.bnorm.elevated.model.sensors.MeasurementType

class SensorUpdate(
    val name: String? = null,
    val type: MeasurementType? = null,
    val deviceId: String? = null,
)
