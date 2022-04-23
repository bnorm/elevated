package dev.bnorm.elevated.model.auth

import dev.bnorm.elevated.model.users.User
import kotlinx.serialization.Serializable

@Serializable
class AuthenticatedUser(
    val token: AuthorizationToken,
    val user: User,
    val authorities: Set<Authority>,
)
