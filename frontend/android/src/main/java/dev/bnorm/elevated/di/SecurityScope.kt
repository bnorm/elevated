package dev.bnorm.elevated.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import dev.bnorm.elevated.client.TokenStore
import okhttp3.Interceptor
import javax.inject.Singleton

abstract class SecurityScope private constructor()

@Module
@ContributesTo(SecurityScope::class)
class HttpSecurityModule {
    @Singleton
    @Provides
    @IntoSet
    fun authorizationInterceptor(tokenStore: TokenStore): Interceptor {
        return Interceptor {
            val authorization = tokenStore.authorization
            val response = it.proceed(
                if (authorization != null) {
                    it.request()
                        .newBuilder()
                        .header("Authorization", authorization)
                        .build()
                } else {
                    it.request()
                }
            )

            if (response.code() == 401) {
                tokenStore.authorization = null
            }

            response
        }
    }
}
