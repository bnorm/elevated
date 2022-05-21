package dev.bnorm.elevated

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.bnorm.elevated.state.KeyStore
import dev.bnorm.elevated.state.UserSession
import dev.bnorm.elevated.state.UserState
import dev.bnorm.elevated.ui.panes.Login
import dev.bnorm.elevated.ui.panes.Home
import dev.bnorm.elevated.ui.theme.ElevatedTheme
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create

val contentType: MediaType = MediaType.get("application/json")

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val store = KeyStore(getSharedPreferences("KEYS", Context.MODE_PRIVATE))

        val httpClient = OkHttpClient.Builder()
            .addInterceptor {
                val authorization = store.authorization
                it.proceed(
                    if (authorization != null) {
                        it.request()
                            .newBuilder()
                            .header("Authorization", authorization)
                            .build()
                    } else {
                        it.request()
                    }
                )
            }
            .build()

        val retrofit = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(HttpUrl.get("https://elevated.bnorm.dev"))
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()


        val apiClient = retrofit.create<ElevatedClient>()

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
