package dev.bnorm.elevated.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
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
    @IntoSet
    fun loggingInterceptor(): Interceptor {
        return Interceptor {
            it.proceed(it.request())
        }
    }
}
