package dev.bnorm.elevated.state.sensor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import dev.bnorm.elevated.asyncMap
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.state.NetworkResult
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

sealed class SensorViewEvent {
    data object Refresh : SensorViewEvent()
    data class SetDuration(val duration: Duration) : SensorViewEvent()
}

data class SensorModel(
    val duration: Duration,
    // TODO unwrap network result into model state?
    val graphs: NetworkResult<List<SensorGraph>>,
)

@Composable
fun SensorPresenter(
    client: ElevatedClient,
    events: Flow<SensorViewEvent>,
): SensorModel {
    val clock = remember { mutableStateOf(Clock.System.now()) }
    val duration = remember { mutableStateOf(2.hours) }
    var after by remember { mutableStateOf(clock.value - duration.value) }
    LaunchedEffect(clock, duration) {
        snapshotFlow { clock.value - duration.value }
            .debounce(200)
            .collect { after = it }
    }


    // Load sensors and readings based on (debounced) clock and duration.
    var graphs by remember { NetworkResult.stateOf<List<SensorGraph>>() }
    LaunchedEffect(after) {
        withContext(Dispatchers.Default) {
            graphs = NetworkResult.of {
                client.getSensors().asyncMap { sensor ->
                    val averagedReadings = client.getSensorReadings(sensor.id, after)
                        .sortedBy { it.timestamp }
                        .simpleMovingAverage()

                    SensorGraph.create(sensor, averagedReadings)
                }
            }
        }
    }

    // Processes incoming events.
    LaunchedEffect(events) {
        events.collect {
            when (it) {
                SensorViewEvent.Refresh -> {
                    clock.value = Clock.System.now()
                }

                is SensorViewEvent.SetDuration -> {
                    duration.value = it.duration
                }
            }
        }
    }

    return SensorModel(
        duration = duration.value,
        graphs = graphs,
    )
}

private fun List<SensorReading>.simpleMovingAverage(size: Int = 9): List<SensorReading> {
    val middle = size / 2
    return windowed(size) { window ->
        val average = window.sumOf { it.value } / size
        window[middle].copy(value = average)
    }
}
