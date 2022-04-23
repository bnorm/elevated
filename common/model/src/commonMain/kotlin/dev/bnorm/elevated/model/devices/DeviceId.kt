package dev.bnorm.elevated.model.devices

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class DeviceId(
    val value: String,
)
