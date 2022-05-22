package dev.bnorm.elevated.model.charts

import kotlinx.serialization.Serializable

@Serializable
data class ChartPatchRequest(
    val name: String? = null,
    val targetEcLow: Long? = null,
    val targetEcHigh: Long? = null,
    val microMl: Double? = null,
    val groMl: Double? = null,
    val bloomMl: Double? = null,
)
