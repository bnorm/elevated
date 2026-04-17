package dev.bnorm.elevated.ui.screen.tab

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.bnorm.elevated.icons.AdUnits
import dev.bnorm.elevated.model.charts.Chart
import dev.bnorm.elevated.model.devices.DeviceAction
import dev.bnorm.elevated.model.devices.DeviceStatus
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import dev.bnorm.elevated.model.pumps.Pump
import dev.bnorm.elevated.model.pumps.PumpId
import dev.bnorm.elevated.state.device.DeviceModel
import dev.bnorm.elevated.ui.LaunchedVisible
import dev.bnorm.elevated.ui.component.format
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

@Inject
@ContributesIntoSet(AppScope::class)
class DeviceScreen(
    private val viewModel: DeviceViewModel
) : TabScreen {
    override val index: Int get() = 2
    override val label: String get() = "Device"
    override val icon: ImageVector get() = Icons.Filled.AdUnits
    override val route: String get() = "/devices"

    private val format = LocalDateTime.Format {
        year(); char('-'); monthNumber(); char('-'); day()
        char(' ')
        hour(); char(':'); minute()
    }

    @Composable
    override fun Render() {
        val model by viewModel.models.collectAsState()

        LaunchedVisible {
            while (true) {
                viewModel.refresh()
                delay(1.minutes)
            }
        }

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize(),
        ) {
            ProvideTextStyle(TextStyle(fontWeight = FontWeight.ExtraLight)) {
                LazyColumn(
                    modifier = Modifier
                        .widthIn(max = 800.dp)
                ) {
                    items(model.devices, key = { it.device.id.value }) {
                        DeviceSummaryCard(it)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun DeviceSummaryCard(summary: DeviceModel.Summary) {
        var expanded by remember { mutableStateOf(false) }

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
                .padding(16.dp),
            onClick = { expanded = !expanded },
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = summary.device.name,
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.weight(1f),
                    )

                    IconButton(
                        onClick = { expanded = !expanded }
                    ) {
                        val rotationState by animateFloatAsState(if (expanded) 180f else 0f) // Rotation State
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Drop Down Arrow",
                            modifier = Modifier.rotate(rotationState),
                        )
                    }
                }

                Divider()
                DeviceSensors(summary, this@DeviceScreen.format)

                val chart = summary.device.chart
                if (chart != null) {
                    Divider()
                    DeviceChart(chart)
                }

                if (expanded) {
                    Divider()
                    DeviceActions(summary, format)
                }
            }
        }
    }
}

@Composable
private fun DeviceSensors(
    summary: DeviceModel.Summary,
    format: DateTimeFormat<LocalDateTime>,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        for (sensor in summary.device.sensors) {
            // TODO align low and high in table format
            Text(text = buildAnnotatedString {
                append("Sensor ")
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(sensor.name)
                }
                val reading = summary.readings[sensor.id]
                if (reading != null) {
                    append(" : ")
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)) {
                        append(reading.value.format(decimals = 2))
                    }
                    append(" at ")
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                        val timestamp = reading.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                        format.formatTo(this, timestamp)
                    }
                }
            })
        }
    }
}

@Composable
private fun DeviceChart(
    chart: Chart,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Text(text = chart.name, style = MaterialTheme.typography.subtitle1)
        Text(text = "Bounds :")
        for (bound in chart.bounds.orEmpty()) {
            // TODO align low and high in table format
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(bound.type.toString())
                    }
                    append(" : ")
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                        append(bound.low.toString())
                    }
                    append(" to ")
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                        append(bound.high.toString())
                    }
                },
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Text(text = "Amounts :")
        for ((content, amount) in chart.amounts.orEmpty()) {
            // TODO align amounts in table format
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(content.displayName)
                    }
                    append(" : ")
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                        append(amount.toString())
                        append(" mL")
                    }
                },
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
private fun DeviceActions(
    summary: DeviceModel.Summary,
    format: DateTimeFormat<LocalDateTime>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val pumps = summary.device.pumps.associateBy { it.id }
        Text("Device Actions", style = MaterialTheme.typography.subtitle1)
        Column {
            for ((index, action) in summary.actions.sortedByDescending { it.submitted }.withIndex()) {
                TimelineDeviceAction(
                    action = action,
                    pumps = pumps,
                    format = format,
                    position = when (index) {
                        0 -> TimelinePosition.First
                        summary.actions.lastIndex -> TimelinePosition.Last
                        else -> TimelinePosition.Middle
                    }
                )
            }

            Canvas(Modifier.height(16.dp).width(32.dp)) {
                drawLine(
                    start = Offset(size.width / 2f, 0f),
                    end = Offset(size.width / 2f, size.height),
                    color = Color.White,
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 8f),
                )
            }
        }
    }
}

private enum class TimelinePosition {
    First,
    Middle,
    Last,
}

@Composable
private fun TimelineDeviceAction(
    action: DeviceAction,
    pumps: Map<PumpId, Pump>,
    format: DateTimeFormat<LocalDateTime>,
    position: TimelinePosition,
) {
    val dateStyle = SpanStyle(fontFamily = FontFamily.Monospace)

    Row(Modifier.height(IntrinsicSize.Min)) {
        Box(modifier = Modifier.fillMaxHeight().width(32.dp)) {
            val lineModifier = when (position) {
                TimelinePosition.First -> Modifier.align(Alignment.BottomCenter).fillMaxHeight(0.5f)
                else -> Modifier.align(Alignment.Center).fillMaxHeight(1f)
            }
            Box(modifier = lineModifier.width(1.dp).background(Color.White))
            Box(modifier = Modifier.align(Alignment.Center).clip(CircleShape).size(8.dp).background(Color.White))
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = buildActionDescription(action, pumps))

            val completed = action.completed?.toLocalDateTime(TimeZone.currentSystemDefault())
            if (completed != null) {
                Text(
                    text = buildAnnotatedString {
                        append("Completed at ")
                        withStyle(dateStyle) {
                            append(format.format(completed))
                        }
                    },
                )
            } else {
                val submitted = action.submitted.toLocalDateTime(TimeZone.currentSystemDefault())
                Text(
                    text = buildAnnotatedString {
                        append("Submitted at ")
                        withStyle(dateStyle) {
                            append(format.format(submitted))
                        }
                    },
                )
            }
        }
    }
}

private fun buildActionDescription(
    action: DeviceAction,
    pumps: Map<PumpId, Pump>,
): AnnotatedString = buildAnnotatedString {
    when (val args = action.args) {
        is PumpDispenseArguments -> {
            val name = when {
                args.pumpId != null -> pumps[args.pumpId]?.name
                args.pump != null -> "Unknown Pump ${args.pump}"
                else -> "Unknown Pump"
            }

            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                append(name)
            }
            append(" dispensed ")
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                    append(args.amount.toString())
                }
                append(" mL")
            }
        }
    }
}
