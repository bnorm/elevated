package dev.bnorm.elevated.service.auth

import dev.bnorm.elevated.service.devices.Device
import kotlinx.serialization.Serializable

@Serializable
class AuthenticatedDevice(
    val token: AuthorizationToken,
    val device: Device,
)
