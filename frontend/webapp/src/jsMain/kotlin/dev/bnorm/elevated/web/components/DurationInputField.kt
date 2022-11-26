package dev.bnorm.elevated.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.petuska.kmdc.textfield.MDCTextField
import dev.petuska.kmdc.textfield.MDCTextFieldType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width

@Composable
fun DurationInputField(
    value: Long, // Duration, TODO https://issuetracker.google.com/issues/251430194
    onValueChange: (Duration) -> Unit,
    label: String,
) {
    var text by remember { mutableStateOf(value.toString()) }
    MDCTextField(
        value = text,
        type = MDCTextFieldType.Outlined,
        label = label,
        attrs = {
            style { width(300.px) }
            type(InputType.Number)
            required()
            onInput {
                text = it.value
                val duration = it.value.toIntOrNull()
                if (duration != null) onValueChange(duration.hours)
            }
        },
    )
}
