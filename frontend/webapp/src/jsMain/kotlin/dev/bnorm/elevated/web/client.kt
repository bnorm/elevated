package dev.bnorm.elevated.web

import dev.bnorm.elevated.model.auth.AuthorizationToken
import dev.bnorm.elevated.model.auth.JwtTokenUsage
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import kotlinx.browser.window

private var authorization: String? by window.localStorage

@OptIn(JwtTokenUsage::class)
fun setAuthorization(token: AuthorizationToken) {
    authorization = "${token.type} ${token.value.value}"
}

fun clearAuthorization() {
    authorization = null
}

val client = HttpClient(Js) {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }

    install(DefaultRequest) {
        authorization?.let { headers[HttpHeaders.Authorization] = it }
    }
}

val apiUrl = URLBuilder(window.location.toString()).apply {
    path("api", "v1")

    // Sanitize other URL properties
    user = null
    password = null
    fragment = ""
    parameters.clear()
    trailingQuery = false
}.build()

fun Url.appendPath(vararg path: String) = when (encodedPath) {
    "/" -> copy(encodedPath = path.joinToString("/", prefix = "/"))
    else -> copy(encodedPath = "$encodedPath${path.joinToString("/", prefix = "/")}")
}
