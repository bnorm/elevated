package dev.bnorm.elevated.model.pumps

import dev.bnorm.elevated.model.sensors.MeasurementType

// TODO make a configurable entity?
enum class PumpContent(
    val displayName: String,
    val increase: Boolean,
    val type: MeasurementType,
) {
    GENERAL_HYDROPONICS_PH_UP(displayName = "pH Up", increase = true, type = MeasurementType.PH),
    GENERAL_HYDROPONICS_PH_DOWN(displayName = "pH Down", increase = false, type = MeasurementType.PH),
    GENERAL_HYDROPONICS_MICRO(displayName = "Micro", increase = true, type = MeasurementType.EC),
    GENERAL_HYDROPONICS_GRO(displayName = "Gro", increase = true, type = MeasurementType.EC),
    GENERAL_HYDROPONICS_BLOOM(displayName = "Bloom", increase = true, type = MeasurementType.EC),
    ;
}
