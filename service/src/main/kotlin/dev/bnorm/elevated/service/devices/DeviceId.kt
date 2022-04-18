package dev.bnorm.elevated.service.devices

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class DeviceId(
    val value: String,
)
