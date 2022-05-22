package dev.bnorm.elevated.model.devices

import kotlinx.serialization.Serializable

@Serializable
data class DeviceActionPrototype(
    val args: DeviceActionArguments,
)
