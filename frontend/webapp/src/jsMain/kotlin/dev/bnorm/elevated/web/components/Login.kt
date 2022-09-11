package dev.bnorm.elevated.web.components

import androidx.compose.runtime.*
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.web.api.userSession
import dev.petuska.kmdc.button.MDCButton
import dev.petuska.kmdc.textfield.MDCTextField
import dev.petuska.kmdc.textfield.MDCTextFieldType
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.Text

@Composable
fun Login() {
    var error by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    fun validateForm() = email.isNotEmpty() && password.isNotEmpty()

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

    Form(attrs = {
        noValidate()
        onSubmit {
            it.preventDefault()
            login()
        }
    }) {
        Column(
            attrs = {
                style {
                    width(400.px)
                    property("margin", "20px auto")
                }
            }
        ) {
            val errorMessage = error
            if (errorMessage != null) {
                // TODO style red or something
                Text(errorMessage)
            }

            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        justifyContent(JustifyContent.Center)
                        paddingBottom(16.px)
                    }
                }
            ) {
                MDCTextField(
                    value = email,
                    type = MDCTextFieldType.Outlined,
                    label = "Email",
                    attrs = {
                        style { width(300.px) }
                        type(InputType.Email)
                        required(true)
                        onInput { email = it.value }
                    },
                )
            }
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        justifyContent(JustifyContent.Center)
                        paddingBottom(16.px)
                    }
                }
            ) {
                MDCTextField(
                    value = password,
                    type = MDCTextFieldType.Outlined,
                    label = "Password",
                    attrs = {
                        style { width(300.px) }
                        type(InputType.Password)
                        required(true)
                        onInput { password = it.value }
                    },
                )
            }
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        justifyContent(JustifyContent.FlexEnd)
                        width(300.px)
                        property("margin", "auto")
                    }
                }
            ) {
                MDCButton(
                    text = "Login",
                    attrs = {
                        classes("login")
                        type(ButtonType.Submit)
                        if (!validateForm()) disabled()
                    },
                )
            }
        }
    }
}
