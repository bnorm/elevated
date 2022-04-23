package dev.bnorm.elevated.model.users

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class Email(
    val value: String,
)
