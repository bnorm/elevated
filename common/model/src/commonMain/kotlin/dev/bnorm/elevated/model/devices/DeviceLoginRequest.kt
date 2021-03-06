package dev.bnorm.elevated.model.devices

import dev.bnorm.elevated.model.auth.Password
import kotlinx.serialization.Serializable

@Serializable
data class DeviceLoginRequest(
    val id: DeviceId,
    val key: Password,
)
