package dev.bnorm.elevated.service.auth

import kotlinx.serialization.Serializable

@Serializable
class AuthorizationToken(
    val type: String,
    val value: JwtToken,
)
