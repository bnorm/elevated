package dev.bnorm.elevated.model.users

import dev.bnorm.elevated.model.auth.Role
import kotlinx.serialization.Serializable

@Serializable
class User(
    val id: UserId,
    val email: Email,
    val name: String,
    val role: Role,
)
