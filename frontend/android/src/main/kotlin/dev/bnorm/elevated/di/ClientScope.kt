package dev.bnorm.elevated.di

import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.client.HttpElevatedClient
import dev.bnorm.elevated.client.TokenStore
import dev.bnorm.elevated.state.auth.UserSession
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

@ContributesTo(AppScope::class)
interface ClientModule {
    @SingleIn(AppScope::class)
    @Provides
    fun elevatedClient(httpClient: HttpClient, store: TokenStore): ElevatedClient = HttpElevatedClient(
        hostUrl = Url("https://elevated.bnorm.dev"),
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
