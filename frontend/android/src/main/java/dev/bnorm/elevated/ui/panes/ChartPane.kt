package dev.bnorm.elevated.ui.panes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.bnorm.elevated.state.NetworkResult
import dev.bnorm.elevated.state.graph.SensorGraph
import dev.bnorm.elevated.state.graph.SensorGraphState
import dev.bnorm.elevated.ui.component.LongInputField
import dev.bnorm.elevated.ui.component.SensorReadingGraph
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant

@Composable
fun ChartPane(
    state: SensorGraphState,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    var selectedTimestamp by remember { mutableStateOf<Instant?>(null) }
    val phReadings by state.phReadings.collectAsState()
    val ecReadings by state.ecReadings.collectAsState()

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                delay(1.minutes)
                state.refresh()
            }
        }
    }

    @Composable
    fun Chart(name: String, result: NetworkResult<SensorGraph>) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 8.dp),
        ) {
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
    }

    Column {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LongInputField(
                value = state.duration.inWholeHours,
                onValueChange = { state.duration = it.hours },
                label = { Text("Hours") },
            )
            Button(
                modifier = Modifier.padding(start = 16.dp),
                onClick = { state.refresh() },
            ) {
                Text("Refresh")
            }
        }

        Chart("pH", phReadings)
        Chart("EC", ecReadings)
    }
}
