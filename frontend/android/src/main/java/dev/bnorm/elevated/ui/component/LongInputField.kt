package dev.bnorm.elevated.ui.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun LongInputField(
    value: Long,
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