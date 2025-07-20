package dev.bnorm.elevated.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.bnorm.elevated.state.auth.UserSession
import dev.bnorm.elevated.state.auth.UserState
import dev.zacsweers.metro.Inject

@Inject
class MainScreen(
    private val userSession: UserSession,
    private val homeScreen: HomeScreen,
    private val loginScreen: LoginScreen,
) : Screen {
    @Composable
    override fun Render() {
        val state by userSession.state.collectAsState()
        LaunchedEffect(Unit) {
            runCatching { userSession.refresh() }
                .onFailure { it.printStackTrace() }
        }

        Box(
            modifier = Modifier.Companion.fillMaxSize(),
        ) {
            when (state) {
                is UserState.Authenticating -> Unit
                is UserState.Unauthenticated -> loginScreen.Render()
                is UserState.Authenticated -> homeScreen.Render()
            }
        }
    }
}
