package dev.bnorm.elevated.ui

import android.annotation.SuppressLint
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.state.auth.UserSession
import dev.bnorm.elevated.state.auth.UserState
import dev.bnorm.elevated.ui.panes.Home
import dev.bnorm.elevated.ui.panes.Login
import dev.bnorm.elevated.ui.theme.ElevatedTheme
import javax.inject.Inject

class MainComponent @Inject constructor(
    private val client: ElevatedClient,
    private val userSession: UserSession,
) {
    @SuppressLint("ComposableNaming")
    @Composable
    fun render() {
        val userState by userSession.state.collectAsState()
        LaunchedEffect(Unit) { runCatching { userSession.refresh() } }

        ElevatedTheme {
            Surface {
                when (userState) {
                    is UserState.Authenticating -> Unit
                    is UserState.Unauthenticated -> Login(userSession)
                    is UserState.Authenticated -> Home(client)
                }
            }
        }
    }
}
