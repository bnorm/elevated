package dev.bnorm.elevated.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.bnorm.elevated.state.graph.SensorGraphPresenter
import dev.bnorm.elevated.ui.screen.SensorsScreen
import dev.bnorm.elevated.web.api.client
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.delay

private val sensorsScreen = SensorsScreen(SensorGraphPresenter(client))

@Composable
fun WebSensors() {
    sensorsScreen.Render {
        LaunchedEffect(Unit) {
            while (true) {
                it.refresh()
                delay(1.minutes)
            }
        }
    }
}

