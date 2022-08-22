package dev.bnorm.elevated.state.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.state.NetworkResult
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class SensorGraphState(
    client: ElevatedClient,
) {
    var duration: Duration by mutableStateOf(2.hours)

    private val phSensorId = SensorId("6278048e770bd023d5d971ea")
    private val ecSensorId = SensorId("6278049d770bd023d5d971eb")

    val phReadings = snapshotFlow { Clock.System.now() - duration }
        .debounce(250)
        .map { after ->
            runCatching {
                val readings = client.getSensorReadings(phSensorId, after)
                NetworkResult.Loaded(SensorGraph.create(readings.sortedBy { it.timestamp }))
            }.getOrElse { NetworkResult.Error(it) }
        }

    val ecReadings = snapshotFlow { Clock.System.now() - duration }
        .debounce(250)
        .map { after ->
            runCatching {
                val readings = client.getSensorReadings(ecSensorId, after)
                NetworkResult.Loaded(SensorGraph.create(readings.sortedBy { it.timestamp }))
            }.getOrElse { NetworkResult.Error(it) }
        }
}
