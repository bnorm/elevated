package dev.bnorm.elevated.desktop

import androidx.compose.ui.window.singleWindowApplication
import dev.bnorm.elevated.di.ElevatedGraph
import dev.zacsweers.metro.createGraphFactory

fun main() {
    val graph = createGraphFactory<ElevatedGraph.Factory>()
        .create(FileTokenStore.default())
    singleWindowApplication {
        graph.mainScreen.Render()
    }
}
