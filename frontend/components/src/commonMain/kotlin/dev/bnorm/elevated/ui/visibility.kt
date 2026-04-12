package dev.bnorm.elevated.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable

@Composable
@NonRestartableComposable
expect fun LaunchedVisible(block: suspend () -> Unit)
