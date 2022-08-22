package dev.bnorm.elevated.state.auth

import dev.bnorm.elevated.model.auth.AuthenticatedUser

sealed class UserState {
    object Unauthenticated : UserState()
    object Authenticating : UserState()

    class Authenticated(
        val user: AuthenticatedUser,
    ) : UserState()
}
