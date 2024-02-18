package dev.bnorm.elevated.model.pumps

import dev.bnorm.elevated.model.devices.DeviceId
import kotlinx.serialization.Serializable

@Serializable
data class PumpUpdateRequest(
    val name: String? = null,
    val deviceId: DeviceId? = null,
    /** Measured in milliliters / second. */
    val flowRate: Double? = null,
    val content: PumpContent? = null,
)
