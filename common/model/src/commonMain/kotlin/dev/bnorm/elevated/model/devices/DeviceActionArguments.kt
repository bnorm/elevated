package dev.bnorm.elevated.model.devices

import dev.bnorm.elevated.model.pumps.PumpId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class DeviceActionArguments

@Serializable
@SerialName("PUMP_DISPENSE")
data class PumpDispenseArguments(
    val pump: Int? = null,
    val pumpId: PumpId? = null,
    /** Measured in milliliters. */
    val amount: Double,
) : DeviceActionArguments() {
    init {
        require(pump != null || pumpId != null)
    }
}
