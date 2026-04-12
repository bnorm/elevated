package dev.bnorm.elevated.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.state.sensor.SensorGraph
import kotlin.math.abs
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

private val format = LocalDateTime.Format {
    year(); char('-'); monthNumber(); char('-'); day()
    char(' ')
    hour(); char(':'); minute()
}

@Composable
fun SensorReadingGraph(
    graph: SensorGraph,
    modifier: Modifier = Modifier,
    selectedTimestamp: Instant? = null,
    onSelectedTimestamp: (Instant?) -> Unit = {},
) {
    val primary = MaterialTheme.colors.primary
    val secondary = MaterialTheme.colors.secondary

    var size by remember { mutableStateOf(Size(1f, 1f)) }
    val path by derivedStateOf { graph.calculatePath(size) }

    fun SensorReading.toX(): Float = with(graph) { toX(size.width.toDouble()).toFloat() }
    fun SensorReading.toY(): Float = with(graph) { toY(size.height.toDouble()).toFloat() }
    fun Double.toY(): Float = with(graph) { toY(size.height.toDouble()).toFloat() }

    // TODO SampledReadings

    val selectedReading = remember(graph, selectedTimestamp) {
        if (selectedTimestamp != null) graph.toNearestReading(selectedTimestamp) else null
    }

    Column(modifier = modifier) {
        val reading = selectedReading ?: graph.readings.lastOrNull()
        if (reading != null) {
            Text(text = buildAnnotatedString {
                val timestamp = reading.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                    append(reading.value.format(3))
                }
                append(" at ")
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                    format.formatTo(this, timestamp)
                }
            })
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = graph.maxY.format(3),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = primary,
                modifier = Modifier.align(Alignment.TopEnd),
            )
            Text(
                text = graph.minY.format(3),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = primary,
                modifier = Modifier.align(Alignment.BottomEnd),
            )
            Canvas(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .pointerInput(graph) {
                        val currentContext = currentCoroutineContext()
                        awaitPointerEventScope {
                            while (currentContext.isActive) {
                                val event = awaitPointerEvent()
                                val timestamp = when (event.type) {
                                    PointerEventType.Move, PointerEventType.Enter -> {
                                        val position = event.changes.first().position
                                        val selectedValue = graph.minX + (position.x / size.width) * graph.spanX
                                        Instant.fromEpochSeconds(selectedValue.toLong())
                                    }

                                    else -> null
                                }
                                onSelectedTimestamp(timestamp)
                            }
                        }
                    }
            ) {
                size = this.size

                drawPath(
                    path = path,
                    style = Stroke(width = 3f),
                    color = primary,
                )

                if (selectedReading != null) {
                    val x = selectedReading.toX()
                    val y = selectedReading.toY()

                    drawLine(
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        color = primary,
                        strokeWidth = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
                    )

                    drawCircle(
                        center = Offset(x, y),
                        radius = 8f,
                        color = primary,
                    )
                }

                val bound = graph.bound
                if (bound != null) {
                    val low = bound.low.toY()
                    drawLine(
                        start = Offset(0f, low),
                        end = Offset(size.width, low),
                        color = secondary,
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
                    )

                    val high = bound.high.toY()
                    drawLine(
                        start = Offset(0f, high),
                        end = Offset(size.width, high),
                        color = secondary,
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
                    )
                }
            }
        }
    }
}

private fun Double.format(decimals: Int): String {
    require(decimals >= 0) { "decimals must be non-negative" }
    val integerComponent = this.toLong()
    var decimalComponent = abs(this - integerComponent)
    repeat(decimals) { decimalComponent *= 10 }
    return buildString {
        append(integerComponent)
        append('.')
        append(decimalComponent.roundToLong().toString().padStart(decimals, '0'))
    }
}

fun SensorGraph.calculatePath(size: Size): Path {
    val path = Path()
    var prev: SensorReading? = null
    for (reading in readings.sortedBy { it.timestamp }) {
        val x = reading.toX(size.width.toDouble()).toFloat()
        val y = reading.toY(size.height.toDouble()).toFloat()
        if (prev == null || reading.timestamp - prev.timestamp > 5.minutes) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
        prev = reading
    }
    return path
}
