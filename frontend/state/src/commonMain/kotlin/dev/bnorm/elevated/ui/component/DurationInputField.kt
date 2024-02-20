package dev.bnorm.elevated.ui.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun DurationInputField(
    value: Duration,
    unit: DurationUnit,
    onValueChange: (Duration) -> Unit,
    label: @Composable (() -> Unit)? = null,
) {
    var text by remember { mutableStateOf("${value.toLong(unit)}") }
    OutlinedTextField(
        value = text,
        onValueChange = { newValue ->
            text = newValue
            newValue.toLongOrNull()?.let { onValueChange(it.toDuration(unit)) }
        },
        isError = text.isNotEmpty() && text.toLongOrNull() == null,
        label = label,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.Number,
        )
    )
}
