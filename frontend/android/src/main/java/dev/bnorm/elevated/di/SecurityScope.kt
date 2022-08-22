package dev.bnorm.elevated.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module

abstract class SecurityScope private constructor()

@Module
@ContributesTo(SecurityScope::class)
class HttpSecurityModule {
}
