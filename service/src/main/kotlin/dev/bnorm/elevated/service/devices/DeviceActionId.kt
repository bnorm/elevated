package dev.bnorm.elevated.service.devices

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class DeviceActionId(
    val value: String,
)
