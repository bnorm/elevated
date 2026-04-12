package dev.bnorm.elevated.desktop

import androidx.compose.ui.window.singleWindowApplication
import dev.bnorm.elevated.client.FileTokenStore
import dev.zacsweers.metro.createGraphFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.http.URLBuilder

fun main() {
    val graph = createGraphFactory<ElevatedGraph.Factory>().create(
        hostUrl = URLBuilder("https://elevated.bnorm.dev").build(),
        httpClient = HttpClient(OkHttp),
        tokenStore = FileTokenStore.DEFAULT,
    )
    singleWindowApplication {
        graph.mainScreen.Render()
    }
}
