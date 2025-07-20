package dev.bnorm.elevated.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.bnorm.elevated.state.auth.UserSession
import dev.bnorm.elevated.state.auth.UserState
import dev.bnorm.elevated.ui.screen.HomeScreen
import dev.bnorm.elevated.ui.screen.LoginScreen
import dev.bnorm.elevated.ui.screen.Screen
import dev.bnorm.elevated.ui.theme.ElevatedTheme
import dev.zacsweers.metro.Inject

@Inject
class MainScreen(
    private val userSession: UserSession,
    private val homeScreen: HomeScreen,
    private val loginScreen: LoginScreen,
) : Screen {
    @Composable
    override fun Render() {
        val userState by userSession.state.collectAsState()
        LaunchedEffect(Unit) { runCatching { userSession.refresh() } }

        ElevatedTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                when (userState) {
                    is UserState.Authenticating -> Unit
                    is UserState.Unauthenticated -> loginScreen.Render()
                    is UserState.Authenticated -> homeScreen.Render()
                }
            }
        }
    }
}
