package dev.bnorm.elevated.web.components

import androidx.compose.runtime.Composable
import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.web.auth.UserSession
import material.button.MDCButton
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun Home(user: AuthenticatedUser) {
    Div {
        Text("Hello, ${user.user.name}")
    }
    Div {
        MDCButton(
            text = "Logout",
            attrs = {
                onClick { UserSession.logout() }
            }
        )
    }
}
