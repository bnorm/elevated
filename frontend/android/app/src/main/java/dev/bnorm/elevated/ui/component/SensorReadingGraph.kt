package dev.bnorm.elevated.ui.component

import android.graphics.Typeface
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
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
import kotlinx.datetime.Instant

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SensorReadingGraph(
    readings: List<SensorReading>,
    modifier: Modifier = Modifier,
    selectedReading: (SensorReading?) -> Unit = {},
) {
    data class ChartSizing(
        val minX: Double,
        val maxX: Double,
        val minY: Double,
        val maxY: Double,
    ) {
        val spanX = maxX - minX
        val spanY = maxY - minY
    }

    val chartSizing = remember(key1 = readings) {
        var minTimestamp = Long.MAX_VALUE
        var maxTimestamp = Long.MIN_VALUE
        var minReading = Double.MAX_VALUE
        var maxReading = Double.MIN_VALUE
        for (reading in readings) {
            minTimestamp = minOf(minTimestamp, reading.timestamp.epochSeconds)
            maxTimestamp = maxOf(maxTimestamp, reading.timestamp.epochSeconds)
            minReading = minOf(minReading, reading.value)
            maxReading = maxOf(maxReading, reading.value)
        }
        val padding = 0.2 * maxOf(maxReading - minReading, 1.0)
        ChartSizing(
            minX = minTimestamp.toDouble(),
            maxX = maxTimestamp.toDouble(),
            minY = (minReading - padding),
            maxY = (maxReading + padding),
        )
    }

    var size by remember { mutableStateOf(Size(1f, 1f)) }

    // TODO SampledReadings

    var selectedReading by remember { mutableStateOf<SensorReading?>(null) }

    fun SensorReading.toX(): Float {
        return (size.width.toDouble() * (timestamp.epochSeconds.toDouble() - chartSizing.minX) / chartSizing.spanX).toFloat()
    }

    fun SensorReading.toY(): Float {
        return (size.height.toDouble() * (chartSizing.maxY - value) / chartSizing.spanY).toFloat()
    }

    val path = remember(readings, size) {
        val path = Path()
        var previous: SensorReading? = null
        var previousX: Float? = null
        for (reading in readings.sortedBy { it.timestamp }) {
            val x = reading.toX()
            if (previous == null) {
                path.moveTo(x, reading.toY())
            } else if (previousX != x) {
                path.lineTo(x, reading.toY())
            }
            previousX = x
            previous = reading
        }
        path
    }

    fun toNearestReading(x: Float): SensorReading {
        val selectedValue = chartSizing.minX + (x / size.width) * chartSizing.spanX
        val timestamp = Instant.fromEpochSeconds(selectedValue.toLong())

        val index = readings.binarySearchBy(timestamp) { it.timestamp }
        val insertIndex = -(index + 1)
        val prev = insertIndex - 1
        val next = insertIndex

        val reading = when {
            index >= 0 -> readings[index]
            prev < 0 -> readings[next]
            next >= readings.size -> readings[prev]
            else -> {
                val prevReading = readings[prev]
                val nextReading = readings[next]
                if (timestamp - prevReading.timestamp < nextReading.timestamp - timestamp) {
                    prevReading
                } else {
                    nextReading
                }
            }
        }

        return reading
    }

    Canvas(
        modifier = modifier
            .pointerInteropFilter {
                selectedReading = when (it.action) {
                    MotionEvent.ACTION_DOWN -> toNearestReading(it.x)
                    MotionEvent.ACTION_MOVE -> toNearestReading(it.x)
                    else -> null
                }
                selectedReading(selectedReading)
                true
            }
    ) {
        size = this.size

        drawPath(
            path = path,
            style = Stroke(width = 5.0f),
            brush = SolidColor(Color(40, 193, 218)),
        )

        val reading = selectedReading
        if (reading != null) {
            val x = reading.toX()
            val y = reading.toY()

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
            it.nativeCanvas.drawText("${chartSizing.maxY}", 0.0f, 10.0f, paint)
            it.nativeCanvas.drawText("${chartSizing.minY}", 0.0f, size.height, paint)
        }
    }
}