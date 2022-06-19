package dev.bnorm.elevated.model.charts

import kotlinx.serialization.Serializable

@Serializable
data class ChartCreateRequest(
    val name: String,
    val targetPhLow: Double,
    val targetPhHigh: Double,
    val targetEcLow: Double,
    val targetEcHigh: Double,
    val microMl: Double,
    val groMl: Double,
    val bloomMl: Double,
)
