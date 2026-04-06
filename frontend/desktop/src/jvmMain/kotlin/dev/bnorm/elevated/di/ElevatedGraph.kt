package dev.bnorm.elevated.di

import dev.bnorm.elevated.client.TokenStore
import dev.bnorm.elevated.ui.screen.MainScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
interface ElevatedGraph {
    val mainScreen: MainScreen

    @DependencyGraph.Factory
    interface Factory {
        fun create(@Provides tokenStore: TokenStore): ElevatedGraph
    }
}
