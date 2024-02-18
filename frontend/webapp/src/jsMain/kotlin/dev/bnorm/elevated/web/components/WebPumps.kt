package dev.bnorm.elevated.web.components

import androidx.compose.runtime.Composable
import dev.bnorm.elevated.ui.screen.PumpsScreen
import dev.bnorm.elevated.web.api.client

private val pumpsScreen = PumpsScreen(client)

@Composable
fun WebPumps() {
    pumpsScreen.Render()
}
