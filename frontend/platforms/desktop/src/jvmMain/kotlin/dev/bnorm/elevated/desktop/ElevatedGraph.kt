package dev.bnorm.elevated.desktop

import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.client.HttpElevatedClient
import dev.bnorm.elevated.client.TokenStore
import dev.bnorm.elevated.ui.screen.AndroidHomeScreen
import dev.bnorm.elevated.ui.screen.HomeScreen
import dev.bnorm.elevated.ui.screen.MainScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.coroutines.CoroutineScope

@DependencyGraph(AppScope::class)
interface ElevatedGraph {
    val mainScreen: MainScreen

    @DependencyGraph.Factory
    interface Factory {
        fun create(
            @Provides hostUrl: Url,
            @Provides httpClient: HttpClient,
            @Provides tokenStore: TokenStore,
            @Provides viewModelCoroutineScope: CoroutineScope,
        ): ElevatedGraph
    }

    @Binds
    val AndroidHomeScreen.binds: HomeScreen

    @SingleIn(AppScope::class)
    @Provides
    fun elevatedClient(hostUrl: Url, httpClient: HttpClient, store: TokenStore): ElevatedClient = HttpElevatedClient(
        hostUrl = hostUrl,
        baseHttpClient = httpClient,
        tokenStore = store,
        json = DefaultJson,
    )
}
