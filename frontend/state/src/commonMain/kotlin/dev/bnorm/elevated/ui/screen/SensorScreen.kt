package dev.bnorm.elevated.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bnorm.elevated.state.NetworkResult
import dev.bnorm.elevated.state.sensor.SensorGraph
import dev.bnorm.elevated.ui.LaunchedVisible
import dev.bnorm.elevated.ui.component.DurationInputField
import dev.bnorm.elevated.ui.component.SensorReadingGraph
import dev.zacsweers.metro.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.Instant
import kotlinx.coroutines.delay

@Inject
class SensorScreen(
    private val viewModel: SensorViewModel
) : Screen {
    @Composable
    override fun Render() {
        val model by viewModel.models.collectAsState()
        var selectedTimestamp by remember { mutableStateOf<Instant?>(null) }

        LaunchedVisible {
            while (true) {
                viewModel.refresh()
                delay(1.minutes)
            }
        }

        @Composable
        fun Chart(graph: SensorGraph) {
            Column(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp),
            ) {
                Text(text = "Sensor: ${graph.sensor.name}")
                if (graph.readings.isEmpty()) {
                    Text(text = "No ${graph.sensor.name} readings in time range")
                } else {
                    SensorReadingGraph(
                        graph = graph,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        selectedTimestamp = selectedTimestamp,
                        onSelectedTimestamp = { selectedTimestamp = it }
                    )
                }
            }
        }

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(max = 1200.dp),
            ) {
                Row(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DurationInputField(
                        value = model.duration,
                        unit = DurationUnit.HOURS,
                        onValueChange = { viewModel.setDuration(it) },
                        label = { Text("Hours") },
                    )
                    Button(
                        modifier = Modifier.padding(start = 16.dp),
                        onClick = { viewModel.refresh() },
                    ) {
                        Text("Refresh")
                    }
                }

                when (val result = model.graphs) {
                    NetworkResult.Loading -> Text(text = "Loading sensors...")
                    is NetworkResult.Error -> Text(text = "Error loading sensors! ${result.error.message}")
                    is NetworkResult.Loaded -> {
                        for (graph in result.value) {
                            Chart(graph)
                        }
                    }
                }
            }
        }
    }
}
