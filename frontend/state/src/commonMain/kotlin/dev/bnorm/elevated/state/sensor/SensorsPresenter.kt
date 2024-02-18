package dev.bnorm.elevated.state.sensor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.inject.Inject
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.state.NetworkResult
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class SensorsPresenter @Inject constructor(
    private val client: ElevatedClient,
) {
    private var clock: Instant by mutableStateOf(Clock.System.now())
    var duration: Duration by mutableStateOf(2.hours)
    private var sensors by mutableStateOf<List<SensorModel>?>(null)

    fun refresh() {
        clock = Clock.System.now()
    }

    @Composable
    fun present(): List<SensorModel>? {
        LaunchedEffect(Unit) {
            sensors = client.getSensorModels()
        }

        return sensors
    }

    private suspend fun ElevatedClient.getSensorModels(): List<SensorModel> = coroutineScope {
        val sensors = getSensors()
        sensors.map { sensor ->
            SensorModel(
                sensor = sensor,
                // TODO this is probably REALLY ugly flow/state management
                readings = readingsFlow(sensor.id)
            )
        }
    }

    private fun readingsFlow(sensorId: SensorId) = snapshotFlow { clock - duration }
        .debounce(200)
        .map { after ->
            runCatching {
                val readings = client.getSensorReadings(sensorId, after)
                val averagedReadings = readings.sortedBy { it.timestamp }.simpleMovingAverage()
                NetworkResult.Loaded(SensorGraph.create(averagedReadings))
            }.getOrElse { NetworkResult.Error(it) }
        }
}

private fun List<SensorReading>.simpleMovingAverage(size: Int = 9): List<SensorReading> {
    val middle = size / 2
    return windowed(size) { window ->
        val average = window.sumOf { it.value } / size
        window[middle].copy(value = average)
    }
}
