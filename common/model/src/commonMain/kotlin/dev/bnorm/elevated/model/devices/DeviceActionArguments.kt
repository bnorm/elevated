package dev.bnorm.elevated.model.devices

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class DeviceActionArguments

@Serializable
@SerialName("PUMP_DISPENSE")
class PumpDispenseArguments(
    val pump: Int,
    val amount: Double, // milliliters
) : DeviceActionArguments()
