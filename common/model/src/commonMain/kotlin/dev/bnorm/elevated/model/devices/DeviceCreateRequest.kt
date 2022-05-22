package dev.bnorm.elevated.model.devices

import dev.bnorm.elevated.model.auth.Password
import kotlinx.serialization.Serializable

@Serializable
data class DeviceCreateRequest(
    val name: String,
    val key: Password,
)
