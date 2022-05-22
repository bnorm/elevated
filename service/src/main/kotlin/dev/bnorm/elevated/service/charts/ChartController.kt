package dev.bnorm.elevated.service.charts

import dev.bnorm.elevated.model.charts.Chart
import dev.bnorm.elevated.model.charts.ChartId
import dev.bnorm.elevated.model.charts.ChartPatchRequest
import dev.bnorm.elevated.model.charts.ChartCreateRequest
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
@RequestMapping("/api/v1/charts")
class ChartController(
    private val chartService: ChartService,
) {
    @PreAuthorize("hasAuthority('CHARTS_WRITE')")
    @PostMapping
    suspend fun createChart(@RequestBody prototype: ChartCreateRequest): Chart {
        return chartService.createChart(prototype)
    }

    @PreAuthorize("hasAuthority('CHARTS_WRITE')")
    @DeleteMapping("/{chartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteChart(@PathVariable chartId: String) {
        chartService.deleteChart(ChartId(chartId))
    }

    @PreAuthorize("hasAuthority('CHARTS_READ')")
    @GetMapping
    fun getAllCharts(): Flow<Chart> {
        return chartService.getAllCharts()
    }

    @PreAuthorize("hasAuthority('CHARTS_READ')")
    @GetMapping("/{id}")
    suspend fun getChartById(@PathVariable id: String): Chart {
        return chartService.getChartById(ChartId(id))
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @PreAuthorize("hasAuthority('CHARTS_WRITE')")
    @PatchMapping("/{id}")
    suspend fun patchChartById(@PathVariable id: String, @RequestBody request: ChartPatchRequest): Chart {
        return chartService.patchChartById(ChartId(id), request)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}
