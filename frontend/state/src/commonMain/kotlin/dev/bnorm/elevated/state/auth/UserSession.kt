package dev.bnorm.elevated.state.auth

import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.client.TokenStore
import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.model.users.UserLoginRequest
import io.ktor.client.plugins.*
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSession(
    private val client: ElevatedClient,
    private val store: TokenStore,
) {
    private val mutableState = MutableStateFlow<UserState>(UserState.Authenticating)
    val state = mutableState.asStateFlow()

    private fun setAuthorization(user: AuthenticatedUser) {
        store.setAuthorization(user.token)
        mutableState.value = UserState.Authenticated(user)
    }

    suspend fun refresh(): AuthenticatedUser {
        try {
            return client.getCurrentUser()
                .also { setAuthorization(it) }
        } catch (t: ClientRequestException) {
            if (t.response.status == HttpStatusCode.Unauthorized) logout()
            throw t
        }
    }

    suspend fun login(email: Email, password: Password): AuthenticatedUser {
        return client.login(UserLoginRequest(email, password))
            .also { setAuthorization(it) }
    }

    fun logout() {
        store.authorization = null
        mutableState.value = UserState.Unauthenticated
    }
}
