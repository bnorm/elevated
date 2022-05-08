package dev.bnorm.elevated.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.web.auth.UserSession
import kotlinx.coroutines.launch
import material.button.MDCButton
import material.textfield.MDCTextField
import material.textfield.MDCTextFieldCommonOpts
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.noValidate
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.paddingBottom
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
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
                UserSession.login(Email(email), Password(password))
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
                    opts = {
                        type = MDCTextFieldCommonOpts.Type.Outlined
                        label = "Email"
                    },
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
                    opts = {
                        type = MDCTextFieldCommonOpts.Type.Outlined
                        label = "Password"
                    },
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
