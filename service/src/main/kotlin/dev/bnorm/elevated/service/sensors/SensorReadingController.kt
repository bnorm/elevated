package dev.bnorm.elevated.service.sensors

import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.model.sensors.SensorReadingPrototype
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.Instant

@RestController
@RequestMapping("/api/v1/sensors/{sensorId}/readings")
class SensorReadingController(
    private val sensorReadingService: SensorReadingService,
) {
    @PreAuthorize("hasAuthority('READINGS_WRITE')")
    @PostMapping("/record")
    suspend fun recordSensorReading(
        @PathVariable sensorId: String,
        @RequestBody prototype: SensorReadingPrototype
    ): SensorReading {
        return sensorReadingService.createSensorReading(SensorId(sensorId), prototype)
    }

    @PreAuthorize("hasAuthority('READINGS_READ')")
    @GetMapping
    fun getSensorReadings(
        @PathVariable sensorId: String,
        @RequestParam startTime: Instant?,
        @RequestParam endTime: Instant?,
    ): Flow<SensorReading> {
        val endTimeResolved = endTime ?: Instant.now()
        val startTimeResolved = startTime ?: (endTimeResolved - Duration.ofHours(2))
        return sensorReadingService.getSensorReadings(SensorId(sensorId), startTimeResolved, endTimeResolved)
    }

    @PreAuthorize("hasAuthority('READINGS_READ')")
    @GetMapping("/latest")
    fun getLatestSensorReadings(
        @PathVariable sensorId: String,
        @RequestParam count: Int?,
    ): Flow<SensorReading> {
        if (count != null && count <= 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid count=$count")
        return sensorReadingService.getLatestSensorReading(SensorId(sensorId), count ?: 1)
    }
}
