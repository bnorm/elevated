package dev.bnorm.elevated.di

import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.client.HttpElevatedClient
import dev.bnorm.elevated.client.TokenStore
import dev.bnorm.elevated.state.auth.UserSession
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.http.URLBuilder
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.browser.window

@ContributesTo(AppScope::class)
interface ClientModule {
    @SingleIn(AppScope::class)
    @Provides
    fun elevatedClient(httpClient: HttpClient, store: TokenStore): ElevatedClient = HttpElevatedClient(
        hostUrl = URLBuilder(window.location.toString()).apply {
            // Sanitize other URL properties
            user = null
            password = null
            fragment = ""
            parameters.clear()
            trailingQuery = false
        }.build(),
        baseHttpClient = httpClient,
        tokenStore = store,
        json = DefaultJson,
    )

    @SingleIn(AppScope::class)
    @Provides
    fun userSession(client: ElevatedClient, store: TokenStore): UserSession = UserSession(
        client = client,
        store = store,
    )
}
