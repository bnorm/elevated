package dev.bnorm.elevated.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.bnorm.elevated.client.TokenStore

abstract class SecurityScope private constructor()

@Module
@ContributesTo(SecurityScope::class)
class HttpSecurityModule(
    private val tokenStore: TokenStore,
) {
    @Provides
    fun tokenStore(): TokenStore = tokenStore
}
