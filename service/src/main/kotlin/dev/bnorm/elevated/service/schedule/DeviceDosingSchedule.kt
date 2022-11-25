package dev.bnorm.elevated.service.schedule

import dev.bnorm.elevated.model.charts.Chart
import dev.bnorm.elevated.model.devices.Device
import dev.bnorm.elevated.model.devices.DeviceActionPrototype
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.service.ApplicationCoroutineScope
import dev.bnorm.elevated.service.devices.DeviceActionService
import dev.bnorm.elevated.service.devices.DeviceService
import dev.bnorm.elevated.service.sensors.SensorReadingService
import jakarta.annotation.PostConstruct
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
        applicationCoroutineScope.schedule(name = "Dose Active Chart", frequency = 4.hours) {
            // Starts at hour 0 UTC - 6 PM CST
            // 6 PM .. 10 PM .. 2 AM .. 6 AM .. 10 AM .. 2 PM ..
            for (device in deviceService.getAllDevices().toList()) {
                val chart = device.chart ?: continue
                val latestReadings = latestSensorReadings(device)
                dose(device, chart, latestReadings)
            }
        }
    }

    private suspend fun latestSensorReadings(device: Device): Map<SensorId, SensorReading?> = coroutineScope {
        val now = Clock.System.now()
        device.sensors.map {
            async {
                val readings = sensorReadingService.getLatestSensorReading(it.id, count = 1)
                    .filter { (now - it.timestamp).absoluteValue < 5.minutes } // Reading must be within the last 5 minutes
                    .singleOrNull()
                it.id to readings
            }
        }.awaitAll().toMap()
    }

    // TODO remove hardcoded sensor and pump IDs
    private suspend fun dose(device: Device, chart: Chart, readings: Map<SensorId, SensorReading?>) {
        log.debug("Dosing for feedChart={}", chart)
        coroutineScope {
            val ph = readings[SensorId("6278048e770bd023d5d971ea")]
            if (ph != null) {
                launch {
                    log.debug("pH reading={}", ph)
                    if (ph.value > chart.targetPhHigh) {
                        dispense(device, pump = 1, amount = 1.0)
                    } else if (ph.value < chart.targetPhLow) {
                        // TODO emit notification for low pH
                    }
                }
            }

            val ec = readings[SensorId("6278049d770bd023d5d971eb")]
            if (ec != null) {
                launch {
                    log.debug("EC reading={}", ec)
                    if (ec.value < chart.targetEcLow) {
                        dispense(device, pump = 2, amount = chart.microMl / 2)
                        dispense(device, pump = 3, amount = chart.groMl / 2)
                        dispense(device, pump = 4, amount = chart.bloomMl / 2)
                    } else if (ec.value > chart.targetEcHigh) {
                        // TODO emit notification for high EC
                    }
                }
            }
        }
    }

    private suspend fun dispense(device: Device, pump: Int, amount: Double) {
        deviceActionService.submitDeviceAction(device.id, DeviceActionPrototype(PumpDispenseArguments(pump, amount)))
    }
}
