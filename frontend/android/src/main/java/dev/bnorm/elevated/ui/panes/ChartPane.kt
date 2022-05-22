package dev.bnorm.elevated.ui.panes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.ui.component.LongInputField
import dev.bnorm.elevated.ui.component.SensorReadingGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

sealed class SensorReadingResult {
    object Loading : SensorReadingResult()
    class Error(val exception: Throwable) : SensorReadingResult()
    class Loaded(val readings: List<SensorReading>) : SensorReadingResult()
}

class ChartPaneState(
    client: ElevatedClient,
    scope: CoroutineScope,
) {
    var duration: Duration by mutableStateOf(2.hours)

    val phReadings = snapshotFlow { Clock.System.now() - duration }
        .debounce(500)
        .map { after ->
            runCatching {
                val readings = client.getSensorReadings(SensorId("6278048e770bd023d5d971ea"), after)
                SensorReadingResult.Loaded(readings.sortedBy { it.timestamp })
            }.getOrElse { SensorReadingResult.Error(it) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = SensorReadingResult.Loading,
        )

    val ecReadings = snapshotFlow { Clock.System.now() - duration }
        .debounce(500)
        .map { after ->
            runCatching {
                val readings = client.getSensorReadings(SensorId("6278049d770bd023d5d971eb"), after)
                SensorReadingResult.Loaded(readings.sortedBy { it.timestamp })
            }.getOrElse { SensorReadingResult.Error(it) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = SensorReadingResult.Loading,
        )
}

@Composable
fun ChartPane(state: ChartPaneState) {
    var selectedTimestamp by remember { mutableStateOf<Instant?>(null) }
    val phReadings by state.phReadings.collectAsState()
    val ecReadings by state.ecReadings.collectAsState()

    @Composable
    fun Chart(name: String, result: SensorReadingResult) {
        Text(text = "$name Sensor")
        when (result) {
            SensorReadingResult.Loading -> Text(text = "Loading sensor $name readings...")
            is SensorReadingResult.Error -> Text(text = "Error loading sensor $name readings! ${result.exception.message}")
            is SensorReadingResult.Loaded -> {
                SensorReadingGraph(
                    readings = result.readings,
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
