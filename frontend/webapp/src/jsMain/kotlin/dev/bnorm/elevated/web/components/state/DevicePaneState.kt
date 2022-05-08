package dev.bnorm.elevated.web.components.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.web.api.SensorService
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

sealed class SensorReadingState {
    object Loading : SensorReadingState()
    class Error(val exception: Throwable) : SensorReadingState()
    class Loaded(val readings: List<SensorReading>) : SensorReadingState()
}

class DevicePaneState {
    var duration: Duration by mutableStateOf(2.hours)

    val phReadings = snapshotFlow { Clock.System.now() - duration }
        .debounce(500)
        .map { after ->
            runCatching {
                val readings = SensorService.getSensorReadings(SensorId("6278048e770bd023d5d971ea"), after)
                SensorReadingState.Loaded(readings.sortedBy { it.timestamp })
            }.getOrElse { SensorReadingState.Error(it) }
        }

    val ecReadings = snapshotFlow { Clock.System.now() - duration }
        .debounce(500)
        .map { after ->
            runCatching {
                val readings = SensorService.getSensorReadings(SensorId("6278049d770bd023d5d971eb"), after)
                SensorReadingState.Loaded(readings.sortedBy { it.timestamp })
            }.getOrElse { SensorReadingState.Error(it) }
        }
}
