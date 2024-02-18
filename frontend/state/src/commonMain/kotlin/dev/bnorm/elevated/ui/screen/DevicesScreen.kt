package dev.bnorm.elevated.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bnorm.elevated.inject.Inject
import dev.bnorm.elevated.model.devices.DeviceStatus
import dev.bnorm.elevated.state.device.DeviceModel
import dev.bnorm.elevated.state.device.DevicesPresenter
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DevicesScreen @Inject constructor(
    private val presenter: DevicesPresenter,
) {
    @Composable
    fun Render() {
        val devices = presenter.present()
        LazyColumn {
            items(devices.orEmpty(), key = { it.device.id.value }) {
                DeviceSummaryCard(it)
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun DeviceSummaryCard(summary: DeviceModel) {
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
                    Text(text = "Bounds :")
                    for (bound in chart.bounds.orEmpty()) {
                        Text(text = "   ${bound.type} : ${bound.low} to ${bound.high}")
                    }
                    Text(text = "Amounts :")
                    for ((content, amount) in chart.amounts.orEmpty()) {
                        Text(text = "   ${content.displayName} : $amount mL")
                    }
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
}
