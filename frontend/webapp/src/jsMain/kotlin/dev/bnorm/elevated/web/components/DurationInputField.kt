package dev.bnorm.elevated.web.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun DurationInputField(
    value: Long, // Duration, TODO https://issuetracker.google.com/issues/251430194
    onValueChange: (Long) -> Unit,
    label: @Composable (() -> Unit)? = null,
) {
    var text by remember { mutableStateOf("$value") }
    OutlinedTextField(
        value = text,
        onValueChange = { newValue ->
            text = newValue
            newValue.toLongOrNull()?.let { onValueChange(it) }
        },
        isError = text.isNotEmpty() && text.toLongOrNull() == null,
        label = label,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.Number,
        )
    )
}
