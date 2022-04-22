package dev.bnorm.elevated.service.devices

import kotlinx.serialization.Serializable

@Serializable
class DeviceActionPrototype(
    val args: DeviceActionArguments,
)
