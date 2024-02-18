package dev.bnorm.elevated.model.devices

import dev.bnorm.elevated.model.charts.Chart
import dev.bnorm.elevated.model.pumps.Pump
import dev.bnorm.elevated.model.sensors.Sensor
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val id: DeviceId,
    val name: String,
    val status: DeviceStatus,
    val pumps: List<Pump>,
    val sensors: List<Sensor>,
    val lastActionTime: Instant? = null,
    val chart: Chart? = null,
)
