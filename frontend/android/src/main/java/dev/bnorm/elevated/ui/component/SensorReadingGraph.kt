package dev.bnorm.elevated.ui.component

import android.graphics.Typeface
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.state.graph.SensorGraph
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SensorReadingGraph(
    graph: SensorGraph,
    modifier: Modifier = Modifier,
    selectedTimestamp: Instant? = null,
    onSelectedTimestamp: (Instant?) -> Unit = {},
) {
    var size by remember { mutableStateOf(Size(1f, 1f)) }
    val path by derivedStateOf { graph.calculateRawPath(size) }

    fun SensorReading.toX(): Float = with(graph) { toX(size.width.toDouble()).toFloat() }
    fun SensorReading.toY(): Float = with(graph) { toY(size.height.toDouble()).toFloat() }

    // TODO SampledReadings

    val selectedReading = remember(graph, selectedTimestamp) {
        if (selectedTimestamp != null) graph.toNearestReading(selectedTimestamp) else null
    }

    Column(modifier = modifier) {
        Row {
            val reading = selectedReading ?: graph.readings.lastOrNull()
            if (reading != null) {
                Text("${reading.value} at ${reading.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())}")
            }
        }
        Canvas(
            modifier = Modifier.fillMaxSize()
                .pointerInteropFilter {
                    val timestamp = when (it.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            val selectedValue = graph.minX + (it.x / size.width) * graph.spanX
                            Instant.fromEpochSeconds(selectedValue.toLong())
                        }
                        else -> null
                    }
                    onSelectedTimestamp(timestamp)
                    true
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

            drawIntoCanvas {
                val paint = Paint().asFrameworkPaint()
                paint.apply {
                    color = Color(40, 193, 218).toArgb()
                    isAntiAlias = true
                    textSize = 24f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                it.nativeCanvas.drawText("${graph.maxY}", 0.0f, 10.0f, paint)
                it.nativeCanvas.drawText("${graph.minY}", 0.0f, size.height, paint)
            }
        }
    }
}

fun SensorGraph.calculateRawPath(size: Size): Path {
    val path = Path()
    var previous: SensorReading? = null
    var previousX: Float? = null
    for (reading in readings.sortedBy { it.timestamp }) {
        val x = reading.toX(size.width.toDouble()).toFloat()
        val y = reading.toY(size.height.toDouble()).toFloat()
        if (previous == null) {
            path.moveTo(x, y)
        } else if (previousX != x) {
            path.lineTo(x, y)
        }
        previousX = x
        previous = reading
    }
    return path
}
