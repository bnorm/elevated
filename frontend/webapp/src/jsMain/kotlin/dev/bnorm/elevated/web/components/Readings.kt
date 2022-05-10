package dev.bnorm.elevated.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.bnorm.elevated.model.sensors.SensorReading
import kotlinx.datetime.Instant
import org.jetbrains.compose.web.attributes.height
import org.jetbrains.compose.web.attributes.width
import org.jetbrains.compose.web.dom.Canvas
import org.w3c.dom.CanvasRenderingContext2D
import kotlin.math.PI

@Composable
fun SensorChart(
    readings: List<SensorReading>,
    selectedTimestamp: Instant? = null,
    width: Int = 300,
    height: Int = 300,
    onSelectedTimestamp: (Instant?) -> Unit = {},
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

    val selectedReading by derivedStateOf {
        if (selectedTimestamp != null) toNearestReading(readings, selectedTimestamp) else null
    }

    Canvas(
        attrs = {
            width(width)
            height(height)

            onMouseMove {
                val selectedValue = chartSizing.minX + (it.offsetX / width) * chartSizing.spanX
                val timestamp = Instant.fromEpochSeconds(selectedValue.toLong())
                onSelectedTimestamp(timestamp)
            }
            onMouseLeave {
                onSelectedTimestamp(null)
            }
        }
    ) {
        DisposableEffect(readings, selectedReading) {
            val ctx = scopeElement.getContext("2d") as? CanvasRenderingContext2D
            val reading = selectedReading

            if (ctx != null) {
                ctx.clearRect(0.0, 0.0, width.toDouble(), height.toDouble())
                ctx.drawPath(readings)

                ctx.withStyle(
                    font = "14px sanserif"
                ) {
                    fillText("${chartSizing.maxY}", 0.0, 14.0)
                    fillText("${chartSizing.minY}", 0.0, height.toDouble())
                }

                ctx.withStyle(
                    font = "14px sanserif"
                ) {
                    fillText("${(reading ?: readings.last()).value}", 0.0, 28.0)
                }

                if (reading != null) {
                    val x = reading.toX()
                    val y = reading.toY()

                    ctx.withStyle(
                        strokeStyle = "#28C1DA",
                        lineDash = arrayOf(20.0, 20.0),
                    ) {
                        beginPath()
                        moveTo(x, 0.0)
                        lineTo(x, height.toDouble())
                        stroke()
                    }

                    ctx.withStyle(
                        fillStyle = "#28C1DA",
                    ) {
                        beginPath()
                        arc(x, y, 3.0, 0.0, 2.0 * PI)
                        fill()
                    }
                }
            }

            onDispose {}
        }
    }
}

fun CanvasRenderingContext2D.withStyle(
    font: String? = null,
    lineDash: Array<Double>? = null,
    strokeStyle: String? = null,
    fillStyle: String? = null,
    block: CanvasRenderingContext2D.() -> Unit,
) {
    save()

    if (font != null) this.font = font
    if (lineDash != null) setLineDash(lineDash)
    if (strokeStyle != null) this.strokeStyle = strokeStyle
    if (fillStyle != null) this.fillStyle = fillStyle

    try {
        block()
    } finally {
        restore()
    }
}
