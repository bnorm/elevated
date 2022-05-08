package dev.bnorm.elevated.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import dev.bnorm.elevated.model.sensors.SensorReading
import org.jetbrains.compose.web.attributes.height
import org.jetbrains.compose.web.attributes.width
import org.jetbrains.compose.web.dom.Canvas
import org.w3c.dom.CanvasLineJoin
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.ROUND

@Composable
fun SensorChart(
    readings: List<SensorReading>,
    width: Int = 300,
    height: Int = 300,
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

    fun SensorReading.toX(): Double {
        return width.toDouble() * (timestamp.epochSeconds.toDouble() - chartSizing.minX) / chartSizing.spanX
    }

    fun SensorReading.toY(): Double {
        return height.toDouble() * (chartSizing.maxY - value) / chartSizing.spanY
    }

    fun CanvasRenderingContext2D.drawPath(readings: List<SensorReading>) {
        beginPath()
        lineJoin = CanvasLineJoin.ROUND
        strokeStyle = "black"

        var previous: SensorReading? = null
        var previousX: Double? = null
        for (reading in readings.sortedBy { it.timestamp }) {
            val x = reading.toX()
            if (previous == null) {
                moveTo(x, reading.toY())
            } else if (previousX != x) {
                lineTo(x, reading.toY())
            }
            previousX = x
            previous = reading
        }
        stroke()
    }

    Canvas(
        attrs = {
            width(width)
            height(height)
        }
    ) {
        DisposableEffect(readings) {
            val ctx = scopeElement.getContext("2d") as? CanvasRenderingContext2D

            if (ctx != null) {
                ctx.clearRect(0.0, 0.0, width.toDouble(), height.toDouble())
                ctx.drawPath(readings)

                // TODO add graph markers (min, max, etc)
                // TODO add cursor selection
            }

            onDispose {}
        }
    }
}
