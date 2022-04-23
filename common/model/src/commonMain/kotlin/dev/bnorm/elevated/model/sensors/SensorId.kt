package dev.bnorm.elevated.model.sensors

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class SensorId(
    val value: String,
)
