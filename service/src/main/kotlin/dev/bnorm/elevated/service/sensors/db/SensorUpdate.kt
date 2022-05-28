package dev.bnorm.elevated.service.sensors.db

import dev.bnorm.elevated.model.sensors.SensorType

class SensorUpdate(
    val name: String? = null,
    val type: SensorType? = null,
    val deviceId: String? = null,
)
