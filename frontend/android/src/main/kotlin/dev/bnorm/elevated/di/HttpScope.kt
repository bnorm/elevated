package dev.bnorm.elevated.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.Interceptor
import okhttp3.OkHttpClient

@ContributesTo(AppScope::class)
interface HttpModule {
    @Multibinds(allowEmpty = true)
    fun okHttpClientInterceptors(): Set<Interceptor>

    @SingleIn(AppScope::class)
    @Provides
    fun okHttpClient(okHttpClientInterceptors: Set<Interceptor>): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                for (interceptor in okHttpClientInterceptors) {
                    addInterceptor(interceptor)
                }
            }
            .build()
    }

    @SingleIn(AppScope::class)
    @Provides
    fun httpClient(okHttpClient: OkHttpClient): HttpClient {
        return HttpClient(OkHttp.create {
            preconfigured = okHttpClient
        })
    }
}
