package dev.bnorm.elevated.web.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.state.NetworkResult
import dev.bnorm.elevated.state.graph.SensorGraphPresenter
import dev.bnorm.elevated.web.api.client
import dev.bnorm.elevated.web.api.userSession
import dev.bnorm.elevated.web.router.HashRouter
import dev.bnorm.elevated.web.router.Router
import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.Instant

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
    val devicePaneState = remember { SensorGraphPresenter(client) }

    Column {
        Text("Hello, ${user.user.name}")
        Button(
            onClick = { userSession.logout() }
//            type = MDCButtonType.Raised,
        ) {
            Text("Logout")
        }

        val router = Router.current
        Button(
            onClick = { router.navigate(ROUTE_CHARTS) }
//            type = MDCButtonType.Raised,
        ) {
            Text("Charts")
        }

        DurationInputField(
            value = devicePaneState.duration.inWholeHours,
            onValueChange = { devicePaneState.duration = it.hours },
            label = { Text("Duration") },
        )

        Row {
            var selectedTimestamp by remember { mutableStateOf<Instant?>(null) }

            val phReadingState by devicePaneState.phReadings.collectAsState(NetworkResult.Loading)
            when (val state = phReadingState) {
                is NetworkResult.Loaded -> {
                    SensorChart(
                        graph = state.value,
                        modifier = Modifier.size(500.dp, 500.dp),
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
                        modifier = Modifier.size(500.dp, 500.dp),
                        selectedTimestamp = selectedTimestamp,
                        onSelectedTimestamp = { selectedTimestamp = it },
                    )
                }

                else -> Unit
            }
        }
    }
}
