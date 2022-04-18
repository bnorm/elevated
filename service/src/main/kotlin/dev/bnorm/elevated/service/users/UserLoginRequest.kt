package dev.bnorm.elevated.service.users

import dev.bnorm.elevated.service.auth.Password
import kotlinx.serialization.Serializable

@Serializable
class UserLoginRequest(
    val email: Email,
    val password: Password,
)
