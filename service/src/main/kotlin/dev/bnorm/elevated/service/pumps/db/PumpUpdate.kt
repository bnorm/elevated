package dev.bnorm.elevated.service.pumps.db

import dev.bnorm.elevated.model.pumps.PumpContent

class PumpUpdate(
    val name: String? = null,
    val deviceId: String? = null,
    /** Measured in milliliters / second. */
    val flowRate: Double? = null,
    val content: PumpContent? = null,
)
