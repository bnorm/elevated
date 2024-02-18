package dev.bnorm.elevated.model.charts

import dev.bnorm.elevated.model.pumps.PumpContent
import kotlinx.serialization.Serializable

@Serializable
data class ChartCreateRequest(
    val name: String,
    val targetPhLow: Double? = null,
    val targetPhHigh: Double? = null,
    val targetEcLow: Double? = null,
    val targetEcHigh: Double? = null,
    val microMl: Double? = null,
    val groMl: Double? = null,
    val bloomMl: Double? = null,
    val bounds: List<Chart.Bound>,
    val amounts: Map<PumpContent, Double>,
)
