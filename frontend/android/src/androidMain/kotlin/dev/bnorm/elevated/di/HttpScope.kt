package dev.bnorm.elevated.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.OkHttpClient

abstract class HttpScope private constructor()

@Module
@ContributesTo(HttpScope::class)
abstract class HttpModule {
    @Multibinds
    abstract fun okHttpClientInterceptors(): Set<@JvmSuppressWildcards Interceptor>

    companion object {
        @Singleton
        @Provides
        fun okHttpClient(okHttpClientInterceptors: Set<@JvmSuppressWildcards Interceptor>): OkHttpClient {
            return OkHttpClient.Builder()
                .apply {
                    for (interceptor in okHttpClientInterceptors) {
                        addInterceptor(interceptor)
                    }
                }
                .build()
        }

        @Singleton
        @Provides
        fun httpClient(okHttpClient: OkHttpClient): HttpClient {
            return HttpClient(OkHttp.create {
                preconfigured = okHttpClient
            })
        }
    }
}
