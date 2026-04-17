package dev.bnorm.elevated.desktop

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import dev.bnorm.elevated.client.FileTokenStore
import dev.zacsweers.metro.createGraphFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.http.URLBuilder

fun main() {
    application {
        val graph = createGraphFactory<ElevatedGraph.Factory>().create(
            hostUrl = URLBuilder("https://elevated.bnorm.dev").build(),
            httpClient = HttpClient(OkHttp),
            tokenStore = FileTokenStore.DEFAULT,
            viewModelCoroutineScope = rememberCoroutineScope(),
        )

        val windowState = WindowState()
        Window(onCloseRequest = ::exitApplication, title = "Elevated", state = windowState) {
            graph.mainScreen.Render()

            MenuBar {
                Menu("Emulate") {
                    val insets = window.insets
                        ?.let { DpSize((it.left + it.right).dp, (it.top + it.bottom).dp) }
                        ?: DpSize.Zero

                    Item("Pixel 9 Pro - Vertical") {
                        val targetSize = insets + with(Density(2.25f)) {
                            Size(960f, 1881f).toDpSize()
                        }

                        windowState.placement = WindowPlacement.Floating
                        windowState.size = targetSize
                    }

                    Item("Pixel 9 Pro - Horizontal") {
                        val targetSize = insets + with(Density(2.25f)) {
                            Size(2034f, 906f).toDpSize()
                        }

                        windowState.placement = WindowPlacement.Floating
                        windowState.size = targetSize
                    }
                }
            }
        }
    }
}
