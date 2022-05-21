package dev.bnorm.elevated.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.bnorm.elevated.model.sensors.SensorReading

@Composable
fun SensorReadingChart(
    readings: List<SensorReading>,
    modifier: Modifier = Modifier,
) {
    require(readings.isNotEmpty())
    var reading by remember(key1 = readings) { mutableStateOf(readings.last()) }

    Column(modifier = modifier) {
        Row {
            RollingNumber(reading.value)
            Text(" at ")
            Text(reading.timestamp.toString())
        }
        SensorReadingGraph(
            readings = readings,
            modifier = Modifier.fillMaxSize(),
            selectedReading = { reading = it ?: readings.last() }
        )
    }
}