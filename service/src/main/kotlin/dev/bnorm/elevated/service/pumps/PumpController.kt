package dev.bnorm.elevated.service.pumps

import dev.bnorm.elevated.model.pumps.Pump
import dev.bnorm.elevated.model.pumps.PumpCreateRequest
import dev.bnorm.elevated.model.pumps.PumpId
import dev.bnorm.elevated.model.pumps.PumpUpdateRequest
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/pumps")
class PumpController(
    private val pumpService: PumpService,
) {
    @PreAuthorize("hasAuthority('PUMPS_WRITE')")
    @PostMapping
    suspend fun createPump(@RequestBody request: PumpCreateRequest): Pump {
        return pumpService.createPump(request)
    }

    @PreAuthorize("hasAuthority('PUMPS_WRITE')")
    @PatchMapping("/{pumpId}")
    suspend fun updatePump(@PathVariable pumpId: PumpId, @RequestBody request: PumpUpdateRequest) {
        pumpService.updatePump(pumpId, request)
    }

    @PreAuthorize("hasAuthority('PUMPS_WRITE')")
    @DeleteMapping("/{pumpId}")
    suspend fun deletePump(@PathVariable pumpId: PumpId) {
        pumpService.deletePump(pumpId)
    }

    @PreAuthorize("hasAuthority('PUMPS_READ')")
    @GetMapping
    fun getPumps(): Flow<Pump> {
        return pumpService.getPumps()
    }

    @PreAuthorize("hasAuthority('PUMPS_READ')")
    @GetMapping("/{pumpId}")
    suspend fun getPumpById(@PathVariable pumpId: PumpId): Pump {
        return pumpService.getPump(pumpId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}
