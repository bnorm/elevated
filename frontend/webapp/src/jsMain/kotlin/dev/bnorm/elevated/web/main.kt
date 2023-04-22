package dev.bnorm.elevated.web

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import dev.bnorm.elevated.state.auth.UserState
import dev.bnorm.elevated.ui.screen.LoginScreen
import dev.bnorm.elevated.web.api.userSession
import dev.bnorm.elevated.web.components.Routes
import org.jetbrains.skiko.wasm.onWasmReady

private val loginScreen = LoginScreen(userSession)

fun main() {
    onWasmReady {
        Window {
            val state by userSession.state.collectAsState()
            LaunchedEffect(Unit) {
                runCatching { userSession.refresh() }
                    .onFailure { it.printStackTrace() }
            }

            when (val actual = state) {
                is UserState.Authenticating -> Unit
                is UserState.Unauthenticated -> loginScreen.Render()
                is UserState.Authenticated -> Routes(actual.user)
            }
        }
    }
}
