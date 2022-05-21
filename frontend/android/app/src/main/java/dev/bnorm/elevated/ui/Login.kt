package dev.bnorm.elevated.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.state.UserSession
import kotlinx.coroutines.launch

@Composable
fun Login(userSession: UserSession) {
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    fun login() {
        scope.launch {
            runCatching {
                userSession.login(Email(email), Password(password))
            }.onFailure {
                error = "Unable to login: ${it.message}"
            }
            email = ""
            password = ""
        }
    }

    Column(
//        attrs = {
//            style {
//                width(400.px)
//                property("margin", "20px auto")
//            }
//        }
    ) {
        val errorMessage = error
        if (errorMessage != null) {
            // TODO style red or something
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
//                style { width(300.px) }
//                type(InputType.Email)
//                required(true)
//                onInput { email = it.value }
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
//                style { width(300.px) }
//                required(true)
//                onInput { password = it.value }
//            },
        )
        Button(
            enabled = email.isNotEmpty() && password.isNotEmpty(),
            onClick = {
                login()
            },
        ) {
            Text(text = "Login")
        }
    }
}