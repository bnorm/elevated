package dev.bnorm.elevated.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable

@Composable
@NonRestartableComposable
actual fun LaunchedVisible(block: suspend () -> Unit) {
    LaunchedEffect(Unit) {
        block()
    }
}
