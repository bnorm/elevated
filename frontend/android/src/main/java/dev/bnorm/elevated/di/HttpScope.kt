package dev.bnorm.elevated.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import dev.bnorm.elevated.client.TokenStore
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import javax.inject.Singleton

abstract class HttpScope private constructor()

@Module
@ContributesTo(HttpScope::class)
class HttpModule {
    @Singleton
    @Provides
    fun okHttpClient(interceptors: Set<@JvmSuppressWildcards Interceptor>): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                for (interceptor in interceptors) {
                    addInterceptor(interceptor)
                }
            }
            .build()
    }

    @Singleton
    @Provides
    fun httpClient(okHttpClient: OkHttpClient, tokenStore: TokenStore): HttpClient {
        return HttpClient(OkHttp.create {
            preconfigured = okHttpClient
        })
    }

    @Singleton
    @Provides
    @IntoSet
    fun loggingInterceptor(): Interceptor {
        return Interceptor {
            it.proceed(it.request())
        }
    }
}
