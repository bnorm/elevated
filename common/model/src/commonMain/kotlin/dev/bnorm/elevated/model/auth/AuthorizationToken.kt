package dev.bnorm.elevated.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationToken(
    val type: String,
    val value: JwtToken,
)
