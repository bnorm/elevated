package dev.bnorm.elevated.state.sensor

import dev.bnorm.elevated.model.sensors.Sensor
import dev.bnorm.elevated.state.NetworkResult
import kotlinx.coroutines.flow.Flow

data class SensorModel(
    val sensor: Sensor,
    val readings: Flow<NetworkResult<SensorGraph>>,
)
