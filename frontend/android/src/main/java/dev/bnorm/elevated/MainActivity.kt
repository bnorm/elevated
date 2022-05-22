package dev.bnorm.elevated

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.client.createElevatedClient
import dev.bnorm.elevated.state.SparedPreferenceTokenStore
import dev.bnorm.elevated.state.UserSession
import dev.bnorm.elevated.state.UserState
import dev.bnorm.elevated.ui.panes.Home
import dev.bnorm.elevated.ui.panes.Login
import dev.bnorm.elevated.ui.theme.ElevatedTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val store = SparedPreferenceTokenStore(getSharedPreferences("KEYS", Context.MODE_PRIVATE))

        val apiClient = createElevatedClient(store)

        setContent {
            MainContent(apiClient, UserSession(apiClient, store))
        }
    }
}

@Composable
fun MainContent(client: ElevatedClient, userSession: UserSession) {
    val userState by userSession.state.collectAsState()
    LaunchedEffect(Unit) { runCatching { userSession.refresh() } }

    ElevatedTheme {
        when (userState) {
            is UserState.Authenticating -> Unit
            is UserState.Unauthenticated -> Login(userSession)
            is UserState.Authenticated -> Home(client)
        }
    }
}
