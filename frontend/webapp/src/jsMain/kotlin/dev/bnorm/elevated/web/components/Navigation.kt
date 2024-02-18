package dev.bnorm.elevated.web.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdUnits
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bnorm.elevated.web.api.userSession
import dev.bnorm.elevated.web.router.HashRouter
import dev.bnorm.elevated.web.router.Router

const val ROUTE_SENSORS = "/sensors"
const val ROUTE_DEVICES = "/devices"
const val ROUTE_PUMPS = "/pumps"

@Composable
fun Navigation() {
    HashRouter(initRoute = ROUTE_SENSORS) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar {
                    val navButtonColors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary,
                        disabledBackgroundColor = MaterialTheme.colors.surface,
                        disabledContentColor = MaterialTheme.colors.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            NavButton(to = ROUTE_SENSORS, colors = navButtonColors) {
                                Icon(Icons.Filled.StackedLineChart, contentDescription = null)
                                Text("Sensors")
                            }
                            Spacer(Modifier.width(16.dp))
                            NavButton(to = ROUTE_PUMPS, colors = navButtonColors) {
                                Icon(Icons.Filled.RotateRight, contentDescription = null)
                                Text("Pumps")
                            }
                            Spacer(Modifier.width(16.dp))
                            NavButton(to = ROUTE_DEVICES, colors = navButtonColors) {
                                Icon(Icons.Filled.AdUnits, contentDescription = null)
                                Text("Devices")
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = { userSession.logout() }, colors = navButtonColors) {
                                Text("Logout")
                            }
                        }
                    }
                }
            }
        ) {
            route(ROUTE_SENSORS) { WebSensors() }
            route(ROUTE_DEVICES) { WebDevices() }
            route(ROUTE_PUMPS) { WebPumps() }
            noMatch {
                Text("Default page")
            }
        }
    }
}

@Composable
private fun NavButton(
    to: String,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    content: @Composable RowScope.() -> Unit
) {
    val router = Router.current
    TextButton(
        onClick = { router.navigate(to) },
        enabled = if (to == "/") router.currentPath.path != to else !router.currentPath.path.startsWith(to),
        colors = colors,
    ) {
        content()
    }
}
