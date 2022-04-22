package dev.bnorm.elevated.service.devices

import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/api/v1/devices/{deviceId}/actions")
class DeviceActionController(
    private val deviceActionService: DeviceActionService,
) {
    @PreAuthorize("hasAuthority('DEVICES_WRITE')")
    @PostMapping
    suspend fun submitDeviceAction(
        @PathVariable deviceId: String,
        @RequestBody prototype: DeviceActionPrototype,
    ): DeviceAction {
        return deviceActionService.submitDeviceAction(DeviceId(deviceId), prototype)
    }

    @PreAuthorize("hasAuthority('DEVICES_READ')")
    @GetMapping
    suspend fun getDeviceAction(
        @PathVariable deviceId: String,
        @RequestParam(required = true) submittedAfter: Instant,
        @RequestParam limit: Int?,
    ): Flow<DeviceAction> {
        require(limit == null || limit > 0)
        return deviceActionService.getActions(DeviceId(deviceId), submittedAfter, limit)
    }

    @PreAuthorize("hasAuthority('DEVICES_WRITE')")
    @PutMapping("{actionId}/complete")
    suspend fun getDeviceAction(
        @PathVariable deviceId: String,
        @PathVariable actionId: String,
    ): DeviceAction? {
        return deviceActionService.complete(DeviceId(deviceId), DeviceActionId(actionId))
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}
