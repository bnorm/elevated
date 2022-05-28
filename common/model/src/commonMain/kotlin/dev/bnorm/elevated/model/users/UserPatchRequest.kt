package dev.bnorm.elevated.model.users

import dev.bnorm.elevated.model.auth.Password
import kotlinx.serialization.Serializable

@Serializable
data class UserPatchRequest(
    val email: Email? = null,
    val password: Password? = null,
    val name: String? = null,
)
