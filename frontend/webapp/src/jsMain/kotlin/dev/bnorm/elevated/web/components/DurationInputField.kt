package dev.bnorm.elevated.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import material.textfield.MDCTextField
import material.textfield.MDCTextFieldCommonOpts
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Composable
fun DurationInputField(
    value: Duration,
    onValueChange: (Duration) -> Unit,
    label: String,
) {
    var text by remember { mutableStateOf(value.inWholeHours.toString()) }
    MDCTextField(
        value = text,
        opts = {
            this.type = MDCTextFieldCommonOpts.Type.Outlined
            this.label = label
        },
        attrs = {
            style { width(300.px) }
            type(InputType.Number)
            required(true)
            onInput {
                text = it.value
                val duration = it.value.toIntOrNull()
                if (duration != null) onValueChange(duration.hours)
            }
        },
    )
}
