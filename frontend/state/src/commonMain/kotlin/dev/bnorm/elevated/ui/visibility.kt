package dev.bnorm.elevated.ui

import androidx.compose.runtime.Composable

@Composable
expect fun LaunchedVisible(block: suspend () -> Unit)
