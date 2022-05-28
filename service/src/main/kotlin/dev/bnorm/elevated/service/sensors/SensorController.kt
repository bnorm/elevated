package dev.bnorm.elevated.service.sensors

import dev.bnorm.elevated.model.sensors.Sensor
import dev.bnorm.elevated.model.sensors.SensorCreateRequest
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorUpdateRequest
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/sensors")
class SensorController(
    private val sensorService: SensorService,
) {
    @PreAuthorize("hasAuthority('SENSORS_WRITE')")
    @PostMapping
    suspend fun createSensor(@RequestBody request: SensorCreateRequest): Sensor {
        return sensorService.createSensor(request)
    }

    @PreAuthorize("hasAuthority('SENSORS_WRITE')")
    @PatchMapping("/{sensorId}")
    suspend fun updateSensor(@PathVariable sensorId: String, @RequestBody request: SensorUpdateRequest) {
        sensorService.updateSensor(SensorId(sensorId), request)
    }

    @PreAuthorize("hasAuthority('SENSORS_WRITE')")
    @DeleteMapping("/{sensorId}")
    suspend fun deleteSensor(@PathVariable sensorId: String) {
        sensorService.deleteSensor(SensorId(sensorId))
    }

    @PreAuthorize("hasAuthority('SENSORS_READ')")
    @GetMapping
    fun getSensors(): Flow<Sensor> {
        return sensorService.getSensors()
    }

    @PreAuthorize("hasAuthority('SENSORS_READ')")
    @GetMapping("/{sensorId}")
    suspend fun getSensorById(@PathVariable sensorId: String): Sensor {
        return sensorService.getSensor(SensorId(sensorId))
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}
