package dev.bnorm.elevated.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.document
import org.w3c.dom.Document
import org.w3c.dom.events.Event
import org.w3c.dom.get

private val Document.visibilityState: String get() = this["visibilityState"].toString()

@Composable
actual fun LaunchedVisible(block: suspend () -> Unit) {
    var visibility by remember { mutableStateOf(document.visibilityState) }

    DisposableEffect(block) {
        val callback: (Event) -> Unit = {
            visibility = document.visibilityState
        }

        document.addEventListener("visibilitychange", callback)
        onDispose { document.removeEventListener("visibilitychange", callback) }
    }

    LaunchedEffect(visibility) {
        if (visibility == "visible") block()
    }
}
