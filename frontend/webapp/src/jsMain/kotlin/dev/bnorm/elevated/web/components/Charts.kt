package dev.bnorm.elevated.web.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.bnorm.elevated.model.charts.Chart
import dev.bnorm.elevated.web.api.client
import kotlinx.coroutines.launch

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

        Button(
            onClick = {
                scope.launch { getCharts() }
            },
        ) {
            Text("Refresh")
        }

        for (chart in charts) {
            Text(chart.name)
        }
    }
}
