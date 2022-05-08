package dev.bnorm.elevated.web

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.bnorm.elevated.web.auth.UserSession
import dev.bnorm.elevated.web.auth.UserState
import dev.bnorm.elevated.web.components.Login
import dev.bnorm.elevated.web.components.Routes
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        val state by UserSession.state.collectAsState()
        LaunchedEffect(Unit) { runCatching { UserSession.refresh() } }

        when (val actual = state) {
            is UserState.Authenticating -> Unit
            is UserState.Unauthenticated -> Login()
            is UserState.Authenticated -> Routes(actual.user)
        }
    }
}
