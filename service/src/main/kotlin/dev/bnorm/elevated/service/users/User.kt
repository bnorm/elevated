package dev.bnorm.elevated.service.users

import dev.bnorm.elevated.service.auth.Role
import kotlinx.serialization.Serializable

@Serializable
class User(
    val id: UserId,
    val email: Email,
    val name: String,
    val role: Role,
)
