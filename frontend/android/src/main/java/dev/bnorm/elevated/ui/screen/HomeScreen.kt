package dev.bnorm.elevated.ui.screen

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.delay

class HomeScreen @Inject constructor(
    private val sensorsScreen: SensorsScreen,
    private val pumpsScreen: PumpsScreen,
    private val devicesScreen: DevicesScreen,
) {
    private class Tab(
        val label: String,
        val icon: ImageVector,
        val route: String = label,
        val content: @Composable () -> Unit,
    )

    private val tabs = listOf(
        Tab("Sensors", Icons.Filled.StackedLineChart) {
            sensorsScreen.Render {
                val lifecycleOwner = LocalLifecycleOwner.current
                LaunchedEffect(lifecycleOwner) {
                    lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        while (true) {
                            it.refresh()
                            delay(1.minutes)
                        }
                    }
                }

            }
        },

        Tab("Pumps", Icons.Filled.RotateRight) { pumpsScreen.Render() },
        Tab("Devices", Icons.Filled.AdUnits) { devicesScreen.Render() },
    )

    @Composable
    fun Render() {
        val navController = rememberNavController()
        Scaffold(
            content = {
                Surface(
                    color = MaterialTheme.colors.surface,
                    modifier = Modifier.padding(it)
                ) {
                    NavHost(navController = navController, startDestination = tabs[0].route) {
                        for (tab in tabs) {
                            composable(tab.route) { tab.content() }
                        }
                    }
                }
            },
            bottomBar = {
                BottomNavigation {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    for (tab in tabs) {
                        BottomNavigationItem(
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
            }
        )
    }
}
