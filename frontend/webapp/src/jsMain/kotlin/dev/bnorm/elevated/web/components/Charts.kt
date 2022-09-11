package dev.bnorm.elevated.web.components

import androidx.compose.runtime.*
import dev.bnorm.elevated.model.charts.Chart
import dev.bnorm.elevated.web.api.client
import dev.petuska.kmdc.button.MDCButton
import dev.petuska.kmdc.button.MDCButtonType
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Text

@Composable
fun Charts() {
    val scope = rememberCoroutineScope()

    val charts = remember { mutableStateListOf<Chart>() }

    suspend fun getCharts() = runCatching {
        val newCharts = client.getCharts()
        charts.clear()
        charts.addAll(newCharts)
    }

    LaunchedEffect(Unit) { getCharts() }

    Column {
        Text("Available Charts")

        MDCButton(
            text = "Refresh",
            type = MDCButtonType.Raised,
            attrs = {
                onClick { scope.launch { getCharts() } }
            }
        )

        for (chart in charts) {
            Text(chart.name)
        }
    }
}
