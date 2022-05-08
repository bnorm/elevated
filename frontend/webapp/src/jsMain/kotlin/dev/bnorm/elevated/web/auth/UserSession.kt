package dev.bnorm.elevated.web.auth

import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.model.users.UserLoginRequest
import dev.bnorm.elevated.web.api.apiUrl
import dev.bnorm.elevated.web.api.appendPath
import dev.bnorm.elevated.web.api.clearAuthorization
import dev.bnorm.elevated.web.api.client
import dev.bnorm.elevated.web.api.setAuthorization
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserSession {
    private val mutableState = MutableStateFlow<UserState>(UserState.Authenticating)
    val state = mutableState.asStateFlow()

    private fun setState(user: AuthenticatedUser) {
        setAuthorization(user.token)
        mutableState.value = UserState.Authenticated(user)
    }

    suspend fun refresh(): AuthenticatedUser {
        return client.get<AuthenticatedUser>(apiUrl.appendPath("users/current"))
            .also { setState(it) }
    }

    suspend fun login(email: Email, password: Password): AuthenticatedUser {
        return client.post<AuthenticatedUser>(apiUrl.appendPath("users/login")) {
            contentType(ContentType.Application.Json)
            body = UserLoginRequest(email, password)
        }.also { setState(it) }
    }

    fun logout() {
        clearAuthorization()
        mutableState.value = UserState.Unauthenticated
    }
}
