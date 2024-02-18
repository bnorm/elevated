package dev.bnorm.elevated.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.state.sensor.SensorGraph
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun SensorReadingGraph(
    graph: SensorGraph,
    modifier: Modifier = Modifier,
    selectedTimestamp: Instant? = null,
    onSelectedTimestamp: (Instant?) -> Unit = {},
) {
    var size by remember { mutableStateOf(Size(1f, 1f)) }
    val path by derivedStateOf { graph.calculatePath(size) }

    fun SensorReading.toX(): Float = with(graph) { toX(size.width.toDouble()).toFloat() }
    fun SensorReading.toY(): Float = with(graph) { toY(size.height.toDouble()).toFloat() }

    // TODO SampledReadings

    val selectedReading = remember(graph, selectedTimestamp) {
        if (selectedTimestamp != null) graph.toNearestReading(selectedTimestamp) else null
    }

    Column(modifier = modifier) {
        val reading = selectedReading ?: graph.readings.lastOrNull()
        if (reading != null) {
            Text("${reading.value.round(0.001)} at ${reading.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())}")
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "${graph.maxY.round(0.001)}",
                fontWeight = FontWeight.Bold,
                color = Color(40, 193, 218),
                modifier = Modifier.align(Alignment.TopStart),
            )
            Text(
                text = "${graph.minY.round(0.001)}",
                fontWeight = FontWeight.Bold,
                color = Color(40, 193, 218),
                modifier = Modifier.align(Alignment.BottomStart),
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
                    style = Stroke(width = 5.0f),
                    brush = SolidColor(Color(40, 193, 218)),
                )

                if (selectedReading != null) {
                    val x = selectedReading.toX()
                    val y = selectedReading.toY()

                    drawLine(
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        brush = SolidColor(Color(40, 193, 218)),
                        strokeWidth = 5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
                    )

                    drawCircle(
                        center = Offset(x, y),
                        radius = 10f,
                        brush = SolidColor(Color(40, 193, 218)),
                    )
                }
            }
        }
    }
}

private fun Double.round(precision: Double) = (this / precision).roundToLong() * precision

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
