package dev.bnorm.elevated.service.users

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class UserId(
    val value: String
)
