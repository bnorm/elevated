package dev.bnorm.elevated.model.pumps

import dev.bnorm.elevated.model.sensors.SensorType

// TODO make a configurable entity?
enum class PumpContent(
    val increase: Boolean,
    val type: SensorType,
) {
    GENERAL_HYDROPONICS_PH_UP(increase = true, type = SensorType.PH),
    GENERAL_HYDROPONICS_PH_DOWN(increase = false, type = SensorType.PH),
    GENERAL_HYDROPONICS_MICRO(increase = true, type = SensorType.EC),
    GENERAL_HYDROPONICS_GRO(increase = true, type = SensorType.EC),
    GENERAL_HYDROPONICS_BLOOM(increase = true, type = SensorType.EC),
    ;
}
