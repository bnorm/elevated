package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.service.auth.Password
import kotlinx.serialization.Serializable

@Serializable
class DevicePrototype(
    val name: String,
    val key: Password,
)
