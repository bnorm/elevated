@file:OptIn(FlowPreview::class)

package dev.bnorm.elevated.ui.panes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bnorm.elevated.state.NetworkResult
import dev.bnorm.elevated.state.graph.SensorGraph
import dev.bnorm.elevated.state.graph.SensorGraphState
import dev.bnorm.elevated.ui.component.LongInputField
import dev.bnorm.elevated.ui.component.SensorReadingGraph
import kotlinx.coroutines.FlowPreview
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours

@Composable
fun ChartPane(state: SensorGraphState) {
    var selectedTimestamp by remember { mutableStateOf<Instant?>(null) }
    val phReadings by state.phReadings.collectAsState(NetworkResult.Loading)
    val ecReadings by state.ecReadings.collectAsState(NetworkResult.Loading)

    @Composable
    fun Chart(name: String, result: NetworkResult<SensorGraph>) {
        Text(text = "$name Sensor")
        when (result) {
            NetworkResult.Loading -> Text(text = "Loading sensor $name readings...")
            is NetworkResult.Error -> Text(text = "Error loading sensor $name readings! ${result.error.message}")
            is NetworkResult.Loaded -> {
                SensorReadingGraph(
                    graph = result.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    selectedTimestamp = selectedTimestamp,
                    onSelectedTimestamp = { selectedTimestamp = it }
                )
            }
        }
    }

    Column {
        Row {
            LongInputField(
                value = state.duration.inWholeHours,
                onValueChange = { state.duration = it.hours },
                label = { Text("Hours") },
            )
        }

        Chart("pH", phReadings)
        Chart("EC", ecReadings)
    }
}
