package dev.bnorm.elevated.service.devices.db

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@Document
sealed class DeviceActionArgumentsEntity

@Document
@TypeAlias("PUMP_DISPENSE")
class PumpDispenseArgumentsEntity(
    val pump: Int,
    val amount: Double, // milliliters
) : DeviceActionArgumentsEntity()
