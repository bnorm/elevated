package dev.bnorm.elevated.state.device

import dev.bnorm.elevated.model.devices.Device
import dev.bnorm.elevated.model.devices.DeviceAction
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading

data class DeviceModel(
    val device: Device,
    val readings: Map<SensorId, SensorReading>,
    val actions: List<DeviceAction>,
)
