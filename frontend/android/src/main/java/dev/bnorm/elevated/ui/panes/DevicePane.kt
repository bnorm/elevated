@file:OptIn(ExperimentalMaterialApi::class)

package dev.bnorm.elevated.ui.panes

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.model.devices.Device
import dev.bnorm.elevated.model.devices.DeviceAction
import dev.bnorm.elevated.model.devices.DeviceStatus
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours

data class DeviceSummary(
    val device: Device,
    val readings: Map<SensorId, SensorReading>,
    val actions: List<DeviceAction>,
)

@Composable
fun DevicePane(client: ElevatedClient) {
    var devices by remember { mutableStateOf<List<DeviceSummary>?>(null) }
    LaunchedEffect(Unit) {
        devices = client.getDeviceSummary()
    }

    LazyColumn {
        items(devices.orEmpty(), key = { it.device.id.value }) {
            DeviceSummaryCard(it)
        }
    }
}

@Composable
fun DeviceSummaryCard(summary: DeviceSummary) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(if (expanded) 180f else 0f) // Rotation State

    Card(
        elevation = 12.dp,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = if (expanded) 1.dp else 2.dp,
            color = when (summary.device.status) {
                DeviceStatus.Online -> Color.Green
                DeviceStatus.Offline -> Color.Red
            }
        ),
        modifier = Modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 400,
                    easing = LinearOutSlowInEasing,
                )
            )
            .padding(8.dp),
        onClick = { expanded = !expanded },
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = summary.device.name,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(.9f),
                )
                IconButton(
                    modifier = Modifier.weight(.1f).rotate(rotationState),
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Drop Down Arrow",
                    )
                }
            }

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

            if (expanded) {
                Divider(modifier = Modifier.padding(2.dp))
                Text("Device Actions")
                for (action in summary.actions.sortedByDescending { it.submitted }) {
                    Text(buildString {
                        append(action.submitted.toLocalDateTime(TimeZone.currentSystemDefault()))
                        append(" ")
                        append(action.args)
                        action.completed?.let { completed ->
                            append(" completed at ")
                            append(completed.toLocalDateTime(TimeZone.currentSystemDefault()))
                        }
                    })
                }
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
    val actions = devices
        .map { async { it.id to getDeviceActions(it.id, submittedAfter = Clock.System.now() - 48.hours) } }
        .awaitAll()
        .toMap()


    devices.map { device ->
        DeviceSummary(
            device = device,
            readings = device.sensors.mapNotNull { readings[it.id] }.associateBy { it.sensorId },
            actions = actions[device.id] ?: emptyList()
        )
    }
}
