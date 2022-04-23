package dev.bnorm.elevated.model.users

import dev.bnorm.elevated.model.auth.Password
import kotlinx.serialization.Serializable

@Serializable
class UserRegisterRequest(
    val email: Email,
    val password: Password,
    val name: String,
)
