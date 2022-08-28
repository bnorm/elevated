package dev.bnorm.elevated.web.api

import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.client.StorageTokenStore
import dev.bnorm.elevated.state.auth.UserSession
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.localStorage
import kotlinx.browser.window

val tokenStore = StorageTokenStore(localStorage)

val httpClient = HttpClient(Js) {
    install(WebSockets)

    install(ContentNegotiation) {
        json()
    }

    install(DefaultRequest) {
        tokenStore.authorization?.let { headers[HttpHeaders.Authorization] = it }
    }

    install(HttpCallValidator) {
        handleResponseExceptionWithRequest { exception, request ->
            val clientException = exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
            if (clientException.response.status == HttpStatusCode.Unauthorized) {
                tokenStore.authorization = null
            }
        }
    }
}

val hostUrl = URLBuilder(window.location.toString()).apply {
    // Sanitize other URL properties
    user = null
    password = null
    fragment = ""
    parameters.clear()
    trailingQuery = false
}.build()

val client = ElevatedClient(httpClient, hostUrl)

val userSession = UserSession(client, tokenStore)
