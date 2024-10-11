package dev.bnorm.elevated.web

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.CanvasBasedWindow
import dev.bnorm.elevated.state.auth.UserState
import dev.bnorm.elevated.ui.screen.LoginScreen
import dev.bnorm.elevated.web.api.userSession
import dev.bnorm.elevated.web.components.Navigation

private val loginScreen = LoginScreen(userSession)

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow {
        val state by userSession.state.collectAsState()
        LaunchedEffect(Unit) {
            runCatching { userSession.refresh() }
                .onFailure { it.printStackTrace() }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            when (state) {
                is UserState.Authenticating -> Unit
                is UserState.Unauthenticated -> loginScreen.Render()
                is UserState.Authenticated -> Navigation()
            }
        }
    }
}
