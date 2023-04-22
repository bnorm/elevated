package dev.bnorm.elevated.web.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.state.graph.SensorGraphPresenter
import dev.bnorm.elevated.ui.screen.SensorsScreen
import dev.bnorm.elevated.web.api.client
import dev.bnorm.elevated.web.router.HashRouter
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.delay

const val ROUTE_HOME = "/home"
const val ROUTE_CHARTS = "/charts"

@Composable
fun Routes(user: AuthenticatedUser) {
    HashRouter(initRoute = ROUTE_HOME) {
        route(ROUTE_HOME) { Home(user) }
        route(ROUTE_CHARTS) { Charts() }
        noMatch {
            Text("Default page")
        }
    }
}

private val sensorsScreen = SensorsScreen(SensorGraphPresenter(client))

@Composable
fun Home(user: AuthenticatedUser) {
    sensorsScreen.Render {
        LaunchedEffect(Unit) {
            while (true) {
                it.refresh()
                delay(1.minutes)
            }
        }
    }
}
