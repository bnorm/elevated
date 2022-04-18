package dev.bnorm.elevated.service.sensors

import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/sensors")
class SensorController(
    private val sensorService: SensorService,
) {
    @PreAuthorize("hasAuthority('SENSORS_WRITE')")
    @PostMapping
    suspend fun createSensor(@RequestBody prototype: SensorPrototype): Sensor {
        return sensorService.createSensor(prototype)
    }

    @PreAuthorize("hasAuthority('SENSORS_READ')")
    @GetMapping
    fun getAllSensors(): Flow<Sensor> {
        return sensorService.getAllSensors()
    }

    @PreAuthorize("hasAuthority('SENSORS_READ')")
    @GetMapping("/{sensorId}")
    suspend fun getSensorById(@PathVariable sensorId: String): Sensor {
        return sensorService.getSensorById(SensorId(sensorId))
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}
