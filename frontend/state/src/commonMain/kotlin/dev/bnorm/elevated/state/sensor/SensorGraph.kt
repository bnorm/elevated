package dev.bnorm.elevated.state.sensor

import dev.bnorm.elevated.model.sensors.Sensor
import dev.bnorm.elevated.model.sensors.SensorReading
import kotlin.time.Instant

class SensorGraph(
    val sensor: Sensor,
    val readings: List<SensorReading>,
    val minX: Double,
    val maxX: Double,
    val minY: Double,
    val maxY: Double,
) {
    val spanX = maxX - minX
    val spanY = maxY - minY

    fun toNearestReading(timestamp: Instant): SensorReading {
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

    fun SensorReading.toX(width: Double): Double {
        return (width * (timestamp.epochSeconds.toDouble() - minX) / spanX)
    }

    fun SensorReading.toY(height: Double): Double {
        return (height * (maxY - value) / spanY)
    }

    companion object {
        fun create(
            sensor: Sensor,
            readings: List<SensorReading>
        ): SensorGraph {
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
            return SensorGraph(
                sensor = sensor,
                readings = readings,
                minX = minTimestamp.toDouble(),
                maxX = maxTimestamp.toDouble(),
                minY = (minReading - padding),
                maxY = (maxReading + padding),
            )
        }
    }
}
