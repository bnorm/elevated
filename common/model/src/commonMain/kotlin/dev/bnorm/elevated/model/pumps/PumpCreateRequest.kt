package dev.bnorm.elevated.model.pumps

import dev.bnorm.elevated.model.devices.DeviceId
import kotlinx.serialization.Serializable

@Serializable
data class PumpCreateRequest(
    val name: String,
    val deviceId: DeviceId,
    /** Measured in milliliters / second. */
    val flowRate: Double,
    val content: PumpContent,
)
