package dev.bnorm.elevated.web.api

import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.client.StorageTokenStore
import dev.bnorm.elevated.state.auth.UserSession
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import kotlinx.browser.localStorage
import kotlinx.browser.window

val tokenStore = StorageTokenStore(localStorage)

val httpClient = HttpClient(Js) {
    install(WebSockets)

    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }

    install(DefaultRequest) {
        tokenStore.authorization?.let { headers[HttpHeaders.Authorization] = it }
    }

    install(HttpCallValidator) {
        handleResponseException { exception ->
            val clientException = exception as? ClientRequestException ?: return@handleResponseException
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
