package dev.bnorm.elevated.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.client.TokenStore
import dev.bnorm.elevated.state.auth.UserSession
import io.ktor.client.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

abstract class ClientScope private constructor()

@Module
@ContributesTo(ClientScope::class)
class ClientModule {
    @Singleton
    @Provides
    fun retrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://elevated.bnorm.dev".toHttpUrl())
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Singleton
    @Provides
    fun elevatedClient(httpClient: HttpClient): ElevatedClient = ElevatedClient(
        httpClient = httpClient,
        hostUrl = Url("https://elevated.bnorm.dev")
    )

    @Singleton
    @Provides
    fun userSession(client: ElevatedClient, store: TokenStore): UserSession = UserSession(
        client = client,
        store = store,
    )
}
