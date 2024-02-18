package dev.bnorm.elevated.service.charts.db

import dev.bnorm.elevated.model.pumps.PumpContent

class ChartUpdate(
    val name: String? = null,
    val targetPhLow: Double? = null,
    val targetPhHigh: Double? = null,
    val targetEcLow: Double? = null,
    val targetEcHigh: Double? = null,
    val microMl: Double? = null,
    val groMl: Double? = null,
    val bloomMl: Double? = null,
    val bounds: List<ChartEntity.Bound>? = null,
    val amounts: Map<PumpContent, Double>? = null,
)
