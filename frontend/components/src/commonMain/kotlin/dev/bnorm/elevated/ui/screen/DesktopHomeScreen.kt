package dev.bnorm.elevated.ui.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.bnorm.elevated.state.auth.UserSession
import dev.bnorm.elevated.ui.screen.tab.TabScreen
import dev.zacsweers.metro.Inject

@Inject
class DesktopHomeScreen(
    private val userSession: UserSession,
    tabs: Set<TabScreen>,
) : HomeScreen {
    private val tabs = tabs.sortedBy { it.index }

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
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { userSession.logout() }) {
                    Text("Logout")
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
                    composable(tab.route) { tab.Render() }
                }
            }
        }
    }
}
