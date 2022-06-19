package dev.bnorm.elevated.model.charts

import kotlinx.serialization.Serializable

@Serializable
data class ChartPatchRequest(
    val name: String? = null,
    val targetPhLow: Double? = null,
    val targetPhHigh: Double? = null,
    val targetEcLow: Double? = null,
    val targetEcHigh: Double? = null,
    val microMl: Double? = null,
    val groMl: Double? = null,
    val bloomMl: Double? = null,
)
