package dev.bnorm.elevated.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.client.HttpElevatedClient
import dev.bnorm.elevated.client.TokenStore
import dev.bnorm.elevated.state.auth.UserSession
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import javax.inject.Singleton

abstract class ClientScope private constructor()

@Module
@ContributesTo(ClientScope::class)
class ClientModule {
    @Singleton
    @Provides
    fun elevatedClient(httpClient: HttpClient, store: TokenStore): ElevatedClient = HttpElevatedClient(
        hostUrl = Url("https://elevated.bnorm.dev"),
        baseHttpClient = httpClient,
        tokenStore = store,
        json = DefaultJson,
    )

    @Singleton
    @Provides
    fun userSession(client: ElevatedClient, store: TokenStore): UserSession = UserSession(
        client = client,
        store = store,
    )
}
