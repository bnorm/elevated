package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.model.auth.AuthenticatedDevice
import dev.bnorm.elevated.model.devices.*
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/devices")
class DeviceController(
    private val deviceService: DeviceService,
) {
    @PreAuthorize("hasAuthority('DEVICES_WRITE')")
    @PostMapping
    suspend fun createDevice(@RequestBody prototype: DeviceCreateRequest): Device {
        return deviceService.createDevice(prototype)
    }

    @PreAuthorize("hasAuthority('DEVICES_WRITE')")
    @DeleteMapping("/{deviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteDevice(@PathVariable deviceId: String) {
        deviceService.deleteDevice(DeviceId(deviceId))
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

    @PreAuthorize("hasAuthority('DEVICES_WRITE')")
    @PatchMapping("/{id}")
    suspend fun patchDeviceById(@PathVariable id: String, @RequestBody request: DevicePatchRequest): Device {
        return deviceService.patchDeviceById(DeviceId(id), request)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}
