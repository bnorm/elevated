package dev.bnorm.elevated.model.pumps

import kotlinx.serialization.Serializable

@Serializable
data class Pump(
    val id: PumpId,
    val name: String,
    /** Measured in milliliters / second. */
    val flowRate: Double,
    val content: PumpContent,
)
