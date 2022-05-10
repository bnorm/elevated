package dev.bnorm.elevated.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.web.auth.UserSession
import dev.bnorm.elevated.web.components.state.DevicePaneState
import dev.bnorm.elevated.web.components.state.SensorReadingState
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
    val devicePaneState = remember { DevicePaneState() }

    Div {
        Text("Hello, ${user.user.name}")
    }
    Div {
        MDCButton(
            text = "Logout",
            attrs = {
                onClick { UserSession.logout() }
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

        val phReadingState by devicePaneState.phReadings.collectAsState(SensorReadingState.Loading)
        when (val state = phReadingState) {
            is SensorReadingState.Loaded -> {
                SensorChart(
                    readings = state.readings,
                    selectedTimestamp = selectedTimestamp,
                    onSelectedTimestamp = { selectedTimestamp = it },
                )
            }
            else -> Unit
        }

        val ecReadingState by devicePaneState.ecReadings.collectAsState(SensorReadingState.Loading)
        when (val state = ecReadingState) {
            is SensorReadingState.Loaded -> {
                SensorChart(
                    readings = state.readings,
                    selectedTimestamp = selectedTimestamp,
                    onSelectedTimestamp = { selectedTimestamp = it },
                )
            }
            else -> Unit
        }
    }
}

fun toNearestReading(readings: List<SensorReading>, timestamp: Instant): SensorReading {
    val index = readings.binarySearchBy(timestamp) { it.timestamp }
    val insertIndex = -(index + 1)
    val prev = insertIndex - 1
    val next = insertIndex

    val reading = when {
        index >= 0 -> readings[index]
        prev < 0 -> readings[next]
        next >= readings.size -> readings[prev]
        else -> {
            val prevReading = readings[prev]
            val nextReading = readings[next]
            if (timestamp - prevReading.timestamp < nextReading.timestamp - timestamp) {
                prevReading
            } else {
                nextReading
            }
        }
    }

    return reading
}
