package dev.bnorm.elevated.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.state.NetworkResult
import dev.bnorm.elevated.state.graph.SensorGraphState
import dev.bnorm.elevated.web.api.client
import dev.bnorm.elevated.web.api.userSession
import dev.bnorm.elevated.web.router.HashRouter
import dev.bnorm.elevated.web.router.Router
import kotlinx.datetime.Instant
import material.button.MDCButton
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

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

@Composable
fun Home(user: AuthenticatedUser) {
    val devicePaneState = remember { SensorGraphState(client, ) }

    Div {
        Text("Hello, ${user.user.name}")
    }
    Div {
        MDCButton(
            text = "Logout",
            attrs = {
                onClick { userSession.logout() }
            }
        )
    }
    Div {
        val router = Router.current
        MDCButton(
            text = "Charts",
            attrs = {
                onClick { router.navigate(ROUTE_CHARTS) }
            }
        )
    }
    Div {
        DurationInputField(
            value = devicePaneState.duration,
            onValueChange = { devicePaneState.duration = it },
            label = "Duration",
        )
    }
    Div {
        var selectedTimestamp by remember { mutableStateOf<Instant?>(null) }

        val phReadingState by devicePaneState.phReadings.collectAsState(NetworkResult.Loading)
        when (val state = phReadingState) {
            is NetworkResult.Loaded -> {
                SensorChart(
                    graph = state.value,
                    selectedTimestamp = selectedTimestamp,
                    onSelectedTimestamp = { selectedTimestamp = it },
                )
            }
            else -> Unit
        }

        val ecReadingState by devicePaneState.ecReadings.collectAsState(NetworkResult.Loading)
        when (val state = ecReadingState) {
            is NetworkResult.Loaded -> {
                SensorChart(
                    graph = state.value,
                    selectedTimestamp = selectedTimestamp,
                    onSelectedTimestamp = { selectedTimestamp = it },
                )
            }
            else -> Unit
        }
    }
}
