package dev.bnorm.elevated.di

import android.app.Application
import dev.bnorm.elevated.ElevatedApplication
import dev.bnorm.elevated.client.TokenStore
import dev.bnorm.elevated.ui.MainScreen
import dev.bnorm.elevated.work.ElevatedWorkerFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
interface ElevatedAppGraph {
    val workerFactory: ElevatedWorkerFactory

    val mainScreen: MainScreen

    @DependencyGraph.Factory
    interface Factory {
        fun create(@Provides tokenStore: TokenStore): ElevatedAppGraph
    }
}

val Application.graph: ElevatedAppGraph
    get() = (this as ElevatedApplication).graph
