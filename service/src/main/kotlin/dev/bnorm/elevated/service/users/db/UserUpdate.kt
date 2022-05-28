package dev.bnorm.elevated.service.users.db

import dev.bnorm.elevated.model.auth.Role

class UserUpdate(
    val email: String? = null,
    val passwordHash: String? = null,
    val name: String? = null,
    val role: Role? = null,
)
