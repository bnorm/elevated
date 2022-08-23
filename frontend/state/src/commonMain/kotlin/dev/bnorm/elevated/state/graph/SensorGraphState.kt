package dev.bnorm.elevated.state.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.state.NetworkResult
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class SensorGraphState(
    private val client: ElevatedClient,
) {
    private var clock: Instant by mutableStateOf(Clock.System.now())

    fun refresh() {
        clock = Clock.System.now()
    }

    var duration: Duration by mutableStateOf(2.hours)
    val phReadings = readingsFlow(SensorId("6278048e770bd023d5d971ea"))
    val ecReadings = readingsFlow(SensorId("6278049d770bd023d5d971eb"))

    private fun readingsFlow(sensorId: SensorId) = snapshotFlow { clock - duration }
        .debounce(100)
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
