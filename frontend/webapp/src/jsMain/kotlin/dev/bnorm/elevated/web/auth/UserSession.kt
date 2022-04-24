package dev.bnorm.elevated.web.auth

import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.model.users.UserLoginRequest
import dev.bnorm.elevated.web.apiUrl
import dev.bnorm.elevated.web.appendPath
import dev.bnorm.elevated.web.clearAuthorization
import dev.bnorm.elevated.web.client
import dev.bnorm.elevated.web.setAuthorization
import io.ktor.client.features.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserSession {
    private val mutableState = MutableStateFlow<UserState>(UserState.Authenticating)
    val state = mutableState.asStateFlow()

    private suspend fun getAuthenticatedUser(call: suspend () -> AuthenticatedUser): AuthenticatedUser? {
        try {
            val user = call()

            setAuthorization(user.token)
            mutableState.value = UserState.Authenticated(user)
            return user
        } catch (t: ResponseException) {
            if (t.response.status != HttpStatusCode.Unauthorized) {
                throw t
            }

            logout()
            return null
        }
    }

    suspend fun refresh(): AuthenticatedUser? {
        return getAuthenticatedUser {
            client.get(apiUrl.appendPath("users/current"))
        }
    }

    suspend fun login(email: Email, password: Password): AuthenticatedUser? {
        return getAuthenticatedUser {
            client.post(apiUrl.appendPath("users/login")) {
                contentType(ContentType.Application.Json)
                body = UserLoginRequest(email, password)
            }
        }
    }

    fun logout() {
        clearAuthorization()
        mutableState.value = UserState.Unauthenticated
    }
}
