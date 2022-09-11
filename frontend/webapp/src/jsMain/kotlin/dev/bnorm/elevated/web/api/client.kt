package dev.bnorm.elevated.web.api

import dev.bnorm.elevated.client.HttpElevatedClient
import dev.bnorm.elevated.client.StorageTokenStore
import dev.bnorm.elevated.state.auth.UserSession
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.localStorage
import kotlinx.browser.window

val hostUrl = URLBuilder(window.location.toString()).apply {
    // Sanitize other URL properties
    user = null
    password = null
    fragment = ""
    parameters.clear()
    trailingQuery = false
}.build()

val httpClient = HttpClient(Js)

val tokenStore = StorageTokenStore(localStorage)

val client = HttpElevatedClient(hostUrl, httpClient, tokenStore, DefaultJson)

val userSession = UserSession(client, tokenStore)
