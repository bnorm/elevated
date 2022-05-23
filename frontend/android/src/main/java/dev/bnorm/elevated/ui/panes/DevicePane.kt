package dev.bnorm.elevated.ui.panes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.model.devices.Device
import dev.bnorm.elevated.model.devices.DeviceStatus
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class DeviceSummary(
    val device: Device,
    val readings: Map<SensorId, SensorReading>
)

@Composable
fun DevicePane(client: ElevatedClient) {
    var devices by remember { mutableStateOf<List<DeviceSummary>?>(null) }
    LaunchedEffect(Unit) {
        devices = client.getDeviceSummary()
    }

    Column(
    ) {
        devices?.forEach { summary ->
            DeviceSummaryCard(summary)
        }
    }
}

@Composable
fun DeviceSummaryCard(summary: DeviceSummary) {
    val status = summary.device.status
    Card(
        elevation = 12.dp,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 2.dp,
            color = when (status) {
                DeviceStatus.Online -> Color.Green
                DeviceStatus.Offline -> Color.Red
            }
        ),
        modifier = Modifier.padding(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(text = summary.device.name)

            Divider(modifier = Modifier.padding(2.dp))
            for (sensor in summary.device.sensors) {
                val reading = summary.readings[sensor.id]
                if (reading != null) {
                    val timestamp = reading.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                    Text("Sensor ${sensor.name} : ${reading.value} at $timestamp")
                } else {
                    Text("Sensor ${sensor.name}")
                }
            }

            val chart = summary.device.chart
            if (chart != null) {
                Divider(modifier = Modifier.padding(2.dp))
                Text(text = "Chart : ${chart.name}")
                Text(text = "   Target EC : ${chart.targetEcLow} to ${chart.targetEcHigh}")
                Text(text = "   Micro : ${chart.microMl} mL")
                Text(text = "   Gro : ${chart.groMl} mL")
                Text(text = "   Bloom : ${chart.bloomMl} mL")
            }
        }
    }
}

private suspend fun ElevatedClient.getDeviceSummary(): List<DeviceSummary> = coroutineScope {
    val devices = getDevices()
    val readings = devices
        .flatMap { it.sensors }.map { it.id }.distinct()
        .map { async { getLatestSensorReadings(it, count = 1).singleOrNull() } }.awaitAll()
        .filterNotNull().associateBy { it.sensorId }

    devices.map { device ->
        DeviceSummary(
            device = device,
            readings = device.sensors.mapNotNull { readings[it.id] }.associateBy { it.sensorId }
        )
    }
}
