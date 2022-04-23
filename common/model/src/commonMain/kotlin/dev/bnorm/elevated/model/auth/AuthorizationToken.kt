package dev.bnorm.elevated.model.auth

import kotlinx.serialization.Serializable

@Serializable
class AuthorizationToken(
    val type: String,
    val value: JwtToken,
)
