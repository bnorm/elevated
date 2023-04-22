package dev.bnorm.elevated.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import dev.bnorm.elevated.inject.Inject
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.state.auth.UserSession
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class LoginScreen @Inject constructor(
    private val userSession: UserSession,
) {
    @Composable
    fun Render() {
        var error by rememberSaveable { mutableStateOf<String?>(null) }
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var passwordVisible by rememberSaveable { mutableStateOf(false) }

        val scope = rememberCoroutineScope()
        fun login() {
            scope.launch {
                try {
                    userSession.login(Email(email), Password(password))
                } catch (t: Throwable) {
                    if (t is CancellationException) throw t
                    error = "Unable to login: ${t.message}"
                }
                email = ""
                password = ""
            }
        }

        Column {
            val errorMessage = error
            if (errorMessage != null) {
                Text(errorMessage)
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                isError = email.isEmpty(),
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Email,
                ),
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                isError = password.isEmpty(),
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Password,
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                }
            )

            Button(
                enabled = email.isNotEmpty() && password.isNotEmpty(),
                onClick = { login() },
            ) {
                Text(text = "Login")
            }
        }
    }
}
