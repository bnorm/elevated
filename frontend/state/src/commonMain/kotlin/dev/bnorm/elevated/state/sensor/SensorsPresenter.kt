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
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import dev.bnorm.elevated.model.sensors.MeasurementType
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.state.NetworkResult
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.withContext

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
    val duration = remember { mutableStateOf(2.hours) }
    var after by remember { mutableStateOf(Clock.System.now() - duration.value) }
    LaunchedEffect(duration) {
        snapshotFlow { duration.value }
            .debounce(200)
            .collect { after = Clock.System.now() - it }
    }

    // Load sensors and readings based on (debounced) clock and duration.
    var graphs by remember { NetworkResult.stateOf<List<SensorGraph>>() }
    LaunchedEffect(after) {
        withContext(Dispatchers.Default) {
            graphs = NetworkResult.of { getSensorGraphs(client, after) }
        }
    }

    // Processes incoming events.
    LaunchedEffect(events) {
        events.collect {
            when (it) {
                SensorViewEvent.Refresh -> {
                    val now = Clock.System.now()
                    after = now - duration.value
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

private suspend fun getSensorGraphs(
    client: ElevatedClient,
    after: Instant
): List<SensorGraph> {
    return coroutineScope {
        val devices = async {
            client.getDevices().associateBy { it.id }
        }

        val bounds = async {
            buildMap {
                for (device in devices.await().values) {
                    val bounds = device.chart?.bounds?.associateBy { it.type }.orEmpty()
                    for (sensor in device.sensors) {
                        put(sensor.id, bounds[sensor.type])
                    }
                }
            }
        }

        val actions = async {
            val pumps = client.getPumps().associateBy { it.id }
            devices.await().values
                .asyncMap { client.getDeviceActions(it.id, after) }
                .flatten()
                .groupBy {
                    when (val args = it.args) {
                        is PumpDispenseArguments -> {
                            val pump = pumps[args.pumpId]
                            pump?.content?.type
                        }
                    }
                }
                .mapValues { (_, actions) -> actions.mapNotNull { it.completed } }
        }

        client.getSensors().asyncMap { sensor ->
            val averagedReadings = client.getSensorReadings(sensor.id, after)
                .sortedBy { it.timestamp }
                .map {
                    when (sensor.type) {
                        MeasurementType.TMP -> it.copy(value = it.value * 9.0 / 5.0 + 32.0)
                        else -> it
                    }
                }
                .simpleMovingAverage()

            SensorGraph.create(
                sensor = sensor,
                bound = bounds.await()[sensor.id],
                readings = averagedReadings,
                actions = actions.await()[sensor.type].orEmpty(),
            )
        }
    }
}

private fun List<SensorReading>.simpleMovingAverage(size: Int = 9): List<SensorReading> {
    val middle = size / 2
    return windowed(size) { window ->
        val average = window.sumOf { it.value } / size
        window[middle].copy(value = average)
    }
}
