package dev.bnorm.elevated.service.auth

import dev.bnorm.elevated.service.users.User
import kotlinx.serialization.Serializable

@Serializable
class AuthenticatedUser(
    val token: AuthorizationToken,
    val user: User,
    val authorities: Set<Authority>,
)
