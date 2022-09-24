package dev.bnorm.elevated.ui.panes

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdUnits
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.state.graph.SensorGraphState
import kotlinx.coroutines.Dispatchers

sealed class Screen(
    val label: String,
    val icon: ImageVector,
    val route: String = label,
) {
    object Charts : Screen("Charts", Icons.Filled.StackedLineChart)
    object Pumps : Screen("Pumps", Icons.Filled.RotateRight)
    object Devices : Screen("Devices", Icons.Filled.AdUnits)

    companion object {
        val screens = listOf(Charts, Pumps, Devices)
    }
}

@Composable
fun Home(client: ElevatedClient) {
    val scope = rememberCoroutineScope { Dispatchers.Default }

    val navController = rememberNavController()

    val chartPaneState = remember(client, scope) { SensorGraphState(client, scope) }

    Scaffold(
        content = {
            Surface(
                color = MaterialTheme.colors.surface,
                modifier = Modifier.padding(it)
            ) {
                NavHost(navController = navController, startDestination = Screen.Charts.route) {
                    composable(Screen.Charts.route) { ChartPane(chartPaneState) }
                    composable(Screen.Pumps.route) { PumpsList(client) }
                    composable(Screen.Devices.route) { DevicePane(client) }
                }
            }
        },
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                for (screen in Screen.screens) {
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    )
}
