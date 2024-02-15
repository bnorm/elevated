package dev.bnorm.elevated.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import dev.bnorm.elevated.inject.Inject
import dev.bnorm.elevated.state.NetworkResult
import dev.bnorm.elevated.state.graph.SensorGraph
import dev.bnorm.elevated.state.graph.SensorGraphPresenter
import dev.bnorm.elevated.ui.component.DurationInputField
import dev.bnorm.elevated.ui.component.SensorReadingGraph
import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.Instant

class SensorsScreen @Inject constructor(
    private val presenter: SensorGraphPresenter,
) {
    @Composable
    fun Render(
        refresher: @Composable (presenter: SensorGraphPresenter) -> Unit = {},
    ) {
        var selectedTimestamp by remember { mutableStateOf<Instant?>(null) }
        val phReadings by presenter.phReadings.collectAsState(NetworkResult.Loading)
        val ecReadings by presenter.ecReadings.collectAsState(NetworkResult.Loading)

        refresher(presenter)

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
                        if (result.value.readings.isEmpty()) {
                            Text(text = "No $name readings in time range")
                        } else {
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
        }

        Column {
            Row(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DurationInputField(
                    value = presenter.duration.inWholeHours,
                    onValueChange = { presenter.duration = it.hours },
                    label = { Text("Hours") },
                )
                Button(
                    modifier = Modifier.padding(start = 16.dp),
                    onClick = { presenter.refresh() },
                ) {
                    Text("Refresh")
                }
            }

            Chart("pH", phReadings)
            Chart("EC", ecReadings)
        }
    }
}
