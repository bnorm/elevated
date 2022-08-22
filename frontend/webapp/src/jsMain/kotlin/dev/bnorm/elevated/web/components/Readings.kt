package dev.bnorm.elevated.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.state.graph.SensorGraph
import kotlinx.datetime.Instant
import org.jetbrains.compose.web.attributes.height
import org.jetbrains.compose.web.attributes.width
import org.jetbrains.compose.web.dom.Canvas
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import kotlin.math.PI

@Composable
fun SensorChart(
    graph: SensorGraph,
    width: Int = 500,
    height: Int = 500,
    selectedTimestamp: Instant? = null,
    onSelectedTimestamp: (Instant?) -> Unit = {},
) {
    fun SensorReading.toX(): Double = with(graph) { toX(width.toDouble()) }
    fun SensorReading.toY(): Double = with(graph) { toY(height.toDouble()) }

    fun CanvasRenderingContext2D.drawPath(graph: SensorGraph) {
        beginPath()

        var previous: SensorReading? = null
        var previousX: Double? = null
        for (reading in graph.readings.sortedBy { it.timestamp }) {
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

    val selectedReading = remember(graph, selectedTimestamp) {
        if (selectedTimestamp != null) graph.toNearestReading(selectedTimestamp) else null
    }

    Canvas(
        attrs = {
            width(width)
            height(height)

            onMouseMove {
                val selectedValue = graph.minX + (it.offsetX / width) * graph.spanX
                val timestamp = Instant.fromEpochSeconds(selectedValue.toLong())
                onSelectedTimestamp(timestamp)
            }
            onMouseDown {
                val selectedValue = graph.minX + (it.offsetX / width) * graph.spanX
                val timestamp = Instant.fromEpochSeconds(selectedValue.toLong())
                onSelectedTimestamp(timestamp)
            }
            onMouseUp {
                onSelectedTimestamp(null)
            }
            onMouseLeave {
                onSelectedTimestamp(null)
            }

            onTouchMove {
                val touch = it.touches[0]!!
                val offsetX = (touch.pageX - (touch.target as HTMLElement).offsetLeft).toDouble()
                val selectedValue = graph.minX + (offsetX / width) * graph.spanX
                val timestamp = Instant.fromEpochSeconds(selectedValue.toLong())
                onSelectedTimestamp(timestamp)
            }
            onTouchEnd {
                onSelectedTimestamp(null)
            }
        }
    ) {
        DisposableEffect(graph, selectedReading) {
            val ctx = scopeElement.getContext("2d") as? CanvasRenderingContext2D
            val reading = selectedReading

            if (ctx != null) {
                ctx.clearRect(0.0, 0.0, width.toDouble(), height.toDouble())
                ctx.drawPath(graph)

                ctx.withStyle(
                    font = "14px sanserif"
                ) {
                    fillText("${graph.maxY}", 0.0, 14.0)
                    fillText("${graph.minY}", 0.0, height.toDouble())
                }

                ctx.withStyle(
                    font = "14px sanserif"
                ) {
                    fillText("${(reading ?: graph.readings.lastOrNull())?.value ?: -1.0}", 0.0, 28.0)
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
