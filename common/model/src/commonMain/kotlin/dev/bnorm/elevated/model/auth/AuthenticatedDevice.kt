package dev.bnorm.elevated.model.auth

import dev.bnorm.elevated.model.devices.Device
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatedDevice(
    val token: AuthorizationToken,
    val device: Device,
)
