package dev.bnorm.elevated.model.charts

import kotlinx.serialization.Serializable

@Serializable
class Chart(
    val id: ChartId,
    val name: String,
    val targetEcLow: Long,
    val targetEcHigh: Long,
    val microMl: Double,
    val groMl: Double,
    val bloomMl: Double,
)
