package dev.bnorm.elevated.ui.panes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bnorm.elevated.ElevatedClient
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.ui.component.LongInputField
import dev.bnorm.elevated.ui.component.SensorReadingChart
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

sealed class SensorReadingResult {
    object Loading : SensorReadingResult()
    class Error(val exception: Throwable) : SensorReadingResult()
    class Loaded(val readings: List<SensorReading>) : SensorReadingResult()
}

class ChartPaneState(
    client: ElevatedClient,
) {
    var duration: Duration by mutableStateOf(2.hours)

    val phReadings = snapshotFlow { Clock.System.now() - duration }
        .debounce(500)
        .map { after ->
            runCatching {
                val readings = client.getSensorReadings("6278048e770bd023d5d971ea", after)
                SensorReadingResult.Loaded(readings.sortedBy { it.timestamp })
            }.getOrElse { SensorReadingResult.Error(it) }
        }

    val ecReadings = snapshotFlow { Clock.System.now() - duration }
        .debounce(500)
        .map { after ->
            runCatching {
                val readings = client.getSensorReadings("6278049d770bd023d5d971eb", after)
                SensorReadingResult.Loaded(readings.sortedBy { it.timestamp })
            }.getOrElse { SensorReadingResult.Error(it) }
        }
}

@Composable
fun ChartPane(state: ChartPaneState) {
    val phReadings by state.phReadings.collectAsState(SensorReadingResult.Loading)
    val ecReadings by state.ecReadings.collectAsState(SensorReadingResult.Loading)

    @Composable
    fun Chart(name: String, result: SensorReadingResult) {
        Text(text = "$name Sensor")
        when (result) {
            SensorReadingResult.Loading -> Text(text = "Loading sensor $name readings...")
            is SensorReadingResult.Error -> Text(text = "Error loading sensor $name readings! ${result.exception.message}")
            is SensorReadingResult.Loaded ->
                SensorReadingChart(
                    readings = result.readings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
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