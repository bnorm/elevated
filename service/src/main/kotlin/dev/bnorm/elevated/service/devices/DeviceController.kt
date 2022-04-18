package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.service.auth.AuthenticatedDevice
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
@RequestMapping("/api/v1/devices")
class DeviceController(
    private val deviceService: DeviceService,
) {
    @PreAuthorize("hasAuthority('DEVICES_WRITE')")
    @PostMapping
    suspend fun createDevice(@RequestBody prototype: DevicePrototype): Device {
        return deviceService.createDevice(prototype)
    }

    @PostMapping("/login")
    suspend fun authenticateDevice(@RequestBody request: DeviceLoginRequest): AuthenticatedDevice {
        return deviceService.authenticateDevice(request)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    }

    @PreAuthorize("hasAuthority('DEVICES_READ')")
    @GetMapping
    fun getAllDevices(): Flow<Device> {
        return deviceService.getAllDevices()
    }

    @PreAuthorize("hasAuthority('DEVICES_READ')")
    @GetMapping("/{id}")
    suspend fun getDeviceById(@PathVariable id: String): Device {
        return deviceService.getDeviceById(DeviceId(id))
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}
