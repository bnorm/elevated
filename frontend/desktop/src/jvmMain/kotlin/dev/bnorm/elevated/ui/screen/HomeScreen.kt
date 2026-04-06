package dev.bnorm.elevated.ui.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.bnorm.elevated.icons.AdUnits
import dev.bnorm.elevated.icons.RotateRight
import dev.bnorm.elevated.icons.StackedLineChart
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@Inject
class HomeScreen(
    private val sensorScreen: SensorScreen,
    private val pumpsScreen: PumpsScreen,
    private val deviceScreen: DeviceScreen,
) : Screen {
    private class Tab(
        val label: String,
        val icon: ImageVector,
        val route: String = label,
        val content: @Composable () -> Unit,
    )

    private val tabs = listOf(
        Tab("Sensors", Icons.Filled.StackedLineChart) { sensorScreen.Render() },
        Tab("Pumps", Icons.Filled.RotateRight) { pumpsScreen.Render() },
        Tab("Devices", Icons.Filled.AdUnits) { deviceScreen.Render() },
    )

    @Composable
    override fun Render() {
        val navController = rememberNavController()
        Row {
            NavigationRail {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                for (tab in tabs) {
                    NavigationRailItem(
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(tab.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
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

            NavHost(
                navController = navController,
                startDestination = tabs[0].route,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                contentAlignment = Alignment.TopCenter,
            ) {
                for (tab in tabs) {
                    composable(tab.route) { tab.content() }
                }
            }
        }
    }
}
