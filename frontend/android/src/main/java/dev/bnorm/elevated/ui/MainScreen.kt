package dev.bnorm.elevated.ui

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.bnorm.elevated.state.auth.UserSession
import dev.bnorm.elevated.state.auth.UserState
import dev.bnorm.elevated.ui.screen.HomeScreen
import dev.bnorm.elevated.ui.screen.LoginScreen
import dev.bnorm.elevated.ui.theme.ElevatedTheme
import javax.inject.Inject

class MainScreen @Inject constructor(
    private val userSession: UserSession,
    private val homeScreen: HomeScreen,
    private val loginScreen: LoginScreen,
) {
    @Composable
    fun Render() {
        val userState by userSession.state.collectAsState()
        LaunchedEffect(Unit) { runCatching { userSession.refresh() } }

        ElevatedTheme {
            Surface {
                when (userState) {
                    is UserState.Authenticating -> Unit
                    is UserState.Unauthenticated -> loginScreen.Render()
                    is UserState.Authenticated -> homeScreen.Render()
                }
            }
        }
    }
}
