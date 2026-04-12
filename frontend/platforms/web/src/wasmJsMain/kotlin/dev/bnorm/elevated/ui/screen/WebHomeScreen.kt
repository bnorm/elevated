package dev.bnorm.elevated.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bnorm.elevated.state.auth.UserSession
import dev.bnorm.elevated.ui.screen.tab.TabScreen
import dev.bnorm.elevated.web.router.HashRouter
import dev.bnorm.elevated.web.router.Router
import dev.zacsweers.metro.Inject

@Inject
class WebHomeScreen(
    private val userSession: UserSession,
    tabs: Set<TabScreen>,
) : HomeScreen {
    private val tabs = tabs.sortedBy { it.index }

    @Composable
    override fun Render() {
        HashRouter(initRoute = tabs[0].route) {
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
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                for (tab in tabs) {
                                    NavButton(to = tab.route, colors = navButtonColors) {
                                        Icon(tab.icon, contentDescription = null)
                                        Text(tab.label)
                                    }
                                }
                            }
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { userSession.logout() }, colors = navButtonColors) {
                                Text("Logout")
                            }
                        }
                    }
                }
            ) {
                for (tab in tabs) {
                    route(tab.route) { tab.Render() }
                }
                noMatch { Text("Default page") }
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
