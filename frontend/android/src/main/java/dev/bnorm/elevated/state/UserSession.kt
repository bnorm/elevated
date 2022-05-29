package dev.bnorm.elevated.state

import android.util.Log
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.client.TokenStore
import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.model.users.UserLoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import javax.inject.Inject

class UserSession @Inject constructor(
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
        } catch (t: HttpException) {
            if (t.code() == 401) logout()
            else Log.w("UserSession", "Error refreshing user token", t)
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
