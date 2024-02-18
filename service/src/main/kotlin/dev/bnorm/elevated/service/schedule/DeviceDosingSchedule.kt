package dev.bnorm.elevated.service.schedule

import dev.bnorm.elevated.model.charts.Chart
import dev.bnorm.elevated.model.devices.Device
import dev.bnorm.elevated.model.devices.DeviceActionPrototype
import dev.bnorm.elevated.model.devices.DeviceStatus
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import dev.bnorm.elevated.model.pumps.PumpId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.service.ApplicationCoroutineScope
import dev.bnorm.elevated.service.devices.DeviceActionService
import dev.bnorm.elevated.service.devices.DeviceService
import dev.bnorm.elevated.service.sensors.SensorReadingService
import jakarta.annotation.PostConstruct
import java.time.Instant
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DeviceDosingSchedule(
    private val applicationCoroutineScope: ApplicationCoroutineScope,
    private val deviceService: DeviceService,
    private val deviceActionService: DeviceActionService,
    private val sensorReadingService: SensorReadingService,
) {
    companion object {
        private val log = LoggerFactory.getLogger(DeviceDosingSchedule::class.java)
    }

    @PostConstruct
    fun schedule() {
        // TODO chart should determine schedule
        applicationCoroutineScope.schedule(name = "Dose Active Chart", frequency = 4.hours) {
            // Starts at hour 0 UTC - 6 PM CST
            // 6 PM .. 10 PM .. 2 AM .. 6 AM .. 10 AM .. 2 PM ..
            for (device in deviceService.getAllDevices().toList()) {
                val chart = device.chart ?: continue

                // Check that the device doesn't still have pending actions.
                val lastActionTime = device.lastActionTime?.toJavaInstant() ?: Instant.MIN
                if (deviceActionService.getActions(device.id, lastActionTime, limit = 1).toList().isNotEmpty()) continue

                val latestReadings = latestSensorReadings(device)
                dose(device, chart, latestReadings)
            }
        }
    }

    private suspend fun latestSensorReadings(device: Device): List<SensorReading> {
        val now = Clock.System.now()
        return device.sensors.asyncMap { sensor ->
            sensorReadingService.getLatestSensorReading(sensor.id, count = 1)
                    .filter { (now - it.timestamp).absoluteValue < 5.minutes } // Reading must be within the last 5 minutes
                    .singleOrNull()
        }.filterNotNull()
    }

    private suspend fun dose(device: Device, chart: Chart, readings: List<SensorReading>) {
        log.debug("Dosing for feedChart={}", chart)

        val boundsByType = chart.bounds.orEmpty().associateBy { it.type }
        val amounts = chart.amounts.orEmpty()

        // TODO protect against multiple sensors of the same type => unique DB index?

        coroutineScope {
            for (reading in readings) {
                val sensor = device.sensors.singleOrNull { it.id == reading.sensorId } ?: continue // TODO error?
                val bound = boundsByType[sensor.type] ?: continue // TODO error?

                launch {
                    log.debug("bound={} reading={}", bound, reading)
                    val pumps = when {
                        reading.value < bound.low -> device.pumps.filter { it.content.type == bound.type && it.content.increase }
                        reading.value > bound.high -> device.pumps.filter { it.content.type == bound.type && !it.content.increase }
                        else -> return@launch // No change needed
                    }

                    for (pump in pumps) {
                        val amount = amounts[pump.content] ?: continue // TODO error?
                        dispense(device, pump.id, amount)
                    }
                }
            }
        }
    }

    private suspend fun dispense(device: Device, pumpId: PumpId, amount: Double) {
        deviceActionService.submitDeviceAction(
            deviceId = device.id,
            prototype = DeviceActionPrototype(
                args = PumpDispenseArguments(
                    pumpId = pumpId,
                    amount = amount,
                )
            )
        )
    }
}

suspend fun <T, R> Iterable<T>.asyncMap(transform: suspend (T) -> R): List<R> {
    val upstream = this
    return coroutineScope {
        upstream.map { async { transform(it) } }.awaitAll()
    }
}
