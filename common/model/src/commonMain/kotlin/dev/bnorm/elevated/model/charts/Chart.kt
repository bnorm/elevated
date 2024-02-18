package dev.bnorm.elevated.model.charts

import dev.bnorm.elevated.model.pumps.PumpContent
import dev.bnorm.elevated.model.sensors.SensorType
import kotlinx.serialization.Serializable

@Serializable
data class Chart(
    val id: ChartId,
    val name: String,
    @Deprecated(message = "replace with `bounds`")
    val targetPhLow: Double? = null,
    @Deprecated(message = "replace with `bounds`")
    val targetPhHigh: Double? = null,
    @Deprecated(message = "replace with `bounds`")
    val targetEcLow: Double? = null,
    @Deprecated(message = "replace with `bounds`")
    val targetEcHigh: Double? = null,
    @Deprecated(message = "replace with `dispense`")
    val microMl: Double? = null,
    @Deprecated(message = "replace with `dispense`")
    val groMl: Double? = null,
    @Deprecated(message = "replace with `dispense`")
    val bloomMl: Double? = null,
    val bounds: List<Bound>? = null,
    val amounts: Map<PumpContent, Double>? = null,
) {
    @Serializable
    data class Bound(
        val type: SensorType,
        val low: Double,
        val high: Double,
    )
}
