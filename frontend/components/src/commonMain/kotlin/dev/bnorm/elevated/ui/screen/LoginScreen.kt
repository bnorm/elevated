package dev.bnorm.elevated.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import dev.bnorm.elevated.icons.Visibility
import dev.bnorm.elevated.icons.VisibilityOff
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.state.auth.UserSession
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@SingleIn(AppScope::class)
@Inject
class LoginScreen(
    private val userSession: UserSession,
) : Screen {
    @Composable
    override fun Render() {
        val focusRequestUsername = remember { FocusRequester() }
        val focusRequestPassword = remember { FocusRequester() }
        val focusRequestLogin = remember { FocusRequester() }

        var error by rememberSaveable { mutableStateOf<String?>(null) }
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var passwordVisible by rememberSaveable { mutableStateOf(false) }

        val scope = rememberCoroutineScope()

        fun isValidLogin() = email.isNotEmpty() && password.isNotEmpty()
        fun login() {
            scope.launch {
                try {
                    userSession.login(Email(email), Password(password))
                } catch (t: Throwable) {
                    error = when (t) {
                        is CancellationException -> throw t

                        is ResponseException if t.response.status == HttpStatusCode.Unauthorized
                            -> "Invalid username or password"

                        else -> "Unable to login: ${t.message}"
                    }
                }
                email = ""
                password = ""
            }
        }

        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val errorMessage = error
                if (errorMessage != null) {
                    Text(errorMessage)
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it.trim() },
                    label = { Text("Email") },
                    isError = email.isEmpty(),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Email,
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequestUsername)
                        .focusProperties {
                            previous = focusRequestLogin
                            next = focusRequestPassword
                        }
                        .onPreviewKeyEvent {
                            when {
                                it.type == KeyEventType.KeyUp && it.key == Key.Enter -> {
                                    if (isValidLogin()) login()
                                    true
                                }

                                else -> false
                            }
                        }
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it.trim() },
                    label = { Text("Password") },
                    isError = password.isEmpty(),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Password,
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description, modifier = Modifier.onPreviewKeyEvent {
                                when {
                                    it.type == KeyEventType.KeyUp && it.key == Key.Enter -> {
                                        passwordVisible = !passwordVisible
                                        true
                                    }

                                    else -> false
                                }
                            })
                        }
                    },
                    modifier = Modifier
                        .focusRequester(focusRequestPassword)
                        .focusProperties {
                            previous = focusRequestUsername
                            next = focusRequestLogin
                        }
                        .onPreviewKeyEvent {
                            when {
                                it.type == KeyEventType.KeyUp && it.key == Key.Enter -> {
                                    if (isValidLogin()) login()
                                    true
                                }

                                else -> false
                            }
                        }
                )

                Button(
                    enabled = isValidLogin(),
                    onClick = { login() },
                    modifier = Modifier
                        .focusRequester(focusRequestLogin)
                        .focusProperties {
                            previous = focusRequestPassword
                            next = focusRequestUsername
                        }
                ) {
                    Text(text = "Login")
                }
            }
        }

        LaunchedEffect(Unit) { focusRequestUsername.requestFocus() }
    }
}
