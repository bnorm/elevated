package dev.bnorm.elevated.di

import android.app.Activity
import dev.bnorm.elevated.ElevatedApplication
import dev.bnorm.elevated.client.TokenStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.MembersInjector
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

@DependencyGraph(AppScope::class)
interface ElevatedApplicationComponent {
    fun inject(application: ElevatedApplication)

    @Multibinds
    fun activityInjectors(): Map<KClass<*>, MembersInjector<Activity>>

    @DependencyGraph.Factory
    interface Factory {
        fun create(@Provides tokenStore: TokenStore): ElevatedApplicationComponent
    }
}
