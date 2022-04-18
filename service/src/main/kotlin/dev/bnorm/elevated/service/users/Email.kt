package dev.bnorm.elevated.service.users

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Email(
    val value: String,
)
