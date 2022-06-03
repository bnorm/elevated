package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.model.devices.DeviceAction
import dev.bnorm.elevated.model.devices.DeviceActionId
import dev.bnorm.elevated.model.devices.DeviceActionPrototype
import dev.bnorm.elevated.model.devices.DeviceId
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/api/v1/devices/{deviceId}/actions")
class DeviceActionController(
    private val deviceActionService: DeviceActionService,
) {
    @PreAuthorize("hasAuthority('ACTIONS_WRITE')")
    @PostMapping
    suspend fun submitDeviceAction(
        @PathVariable deviceId: DeviceId,
        @RequestBody prototype: DeviceActionPrototype,
    ): DeviceAction {
        return deviceActionService.submitDeviceAction(deviceId, prototype)
    }

    @PreAuthorize("hasAuthority('ACTIONS_READ')")
    @GetMapping
    fun getDeviceActions(
        @PathVariable deviceId: DeviceId,
        @RequestParam(required = true) submittedAfter: Instant,
        @RequestParam limit: Int?,
    ): Flow<DeviceAction> {
        require(limit == null || limit > 0)
        return deviceActionService.getActions(deviceId, submittedAfter, limit)
    }

    @PreAuthorize("hasAuthority('ACTIONS_READ')")
    @GetMapping("/latest")
    fun getLatestDeviceActions(
        @PathVariable deviceId: DeviceId,
        @RequestParam count: Int?,
    ): Flow<DeviceAction> {
        if (count != null && count <= 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid count=$count")
        return deviceActionService.getLatestActions(deviceId, count ?: 0)
    }

    @PreAuthorize("hasAuthority('ACTIONS_READ')")
    @GetMapping("{actionId}")
    suspend fun getDeviceAction(
        @PathVariable deviceId: DeviceId,
        @PathVariable actionId: DeviceActionId,
    ): DeviceAction? {
        return deviceActionService.getAction(deviceId, actionId)
    }

    @PreAuthorize("hasAuthority('ACTIONS_WRITE')")
    @PutMapping("{actionId}/complete")
    suspend fun completeDeviceAction(
        @PathVariable deviceId: DeviceId,
        @PathVariable actionId: DeviceActionId,
    ): DeviceAction? {
        return deviceActionService.completeAction(deviceId, actionId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @PreAuthorize("hasAuthority('ACTIONS_ADMIN')")
    @DeleteMapping("{actionId}")
    suspend fun deleteDeviceAction(
        @PathVariable deviceId: DeviceId,
        @PathVariable actionId: DeviceActionId,
    ) {
        deviceActionService.deleteAction(deviceId, actionId)
    }
}
