package dev.bnorm.elevated.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import dev.bnorm.elevated.client.StorageTokenStore
import dev.bnorm.elevated.di.ElevatedGraph
import dev.zacsweers.metro.createGraphFactory
import kotlinx.browser.localStorage

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val graph = createGraphFactory<ElevatedGraph.Factory>()
        .create(StorageTokenStore(localStorage))
    CanvasBasedWindow {
        graph.mainScreen.Render()
    }
}
