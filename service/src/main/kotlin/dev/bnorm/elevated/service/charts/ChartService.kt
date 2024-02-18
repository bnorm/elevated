package dev.bnorm.elevated.service.charts

import dev.bnorm.elevated.model.charts.Chart
import dev.bnorm.elevated.model.charts.ChartCreateRequest
import dev.bnorm.elevated.model.charts.ChartId
import dev.bnorm.elevated.model.charts.ChartPatchRequest
import dev.bnorm.elevated.service.charts.db.ChartEntity
import dev.bnorm.elevated.service.charts.db.ChartRepository
import dev.bnorm.elevated.service.charts.db.ChartUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class ChartService(
    private val chartRepository: ChartRepository,
) {
    suspend fun createChart(prototype: ChartCreateRequest): Chart {
        return chartRepository.insert(prototype.toEntity()).toDto()
    }

    suspend fun deleteChart(chartId: ChartId) {
        chartRepository.delete(chartId)
    }

    fun getAllCharts(): Flow<Chart> {
        return chartRepository.findAll().map { it.toDto() }
    }

    suspend fun getChartById(chartId: ChartId): Chart? {
        return chartRepository.findById(chartId)?.toDto()
    }

    suspend fun patchChartById(chartId: ChartId, request: ChartPatchRequest): Chart? {
        return chartRepository.modify(chartId, request.toUpdate())?.toDto()
    }

    private fun ChartEntity.toDto(): Chart {
        return Chart(
            id = id,
            name = name,
            targetPhLow = targetPhLow,
            targetPhHigh = targetPhHigh,
            targetEcLow = targetEcLow,
            targetEcHigh = targetEcHigh,
            microMl = microMl,
            groMl = groMl,
            bloomMl = bloomMl,
            bounds = bounds?.map { it.toDto() },
            amounts = amounts,
        )
    }

    private fun ChartEntity.Bound.toDto(): Chart.Bound {
        return Chart.Bound(
            type = type,
            low = low,
            high = high,
        )
    }

    private fun ChartCreateRequest.toEntity(): ChartEntity {
        return ChartEntity(
            name = name,
            targetPhLow = targetPhLow,
            targetPhHigh = targetPhHigh,
            targetEcLow = targetEcLow,
            targetEcHigh = targetEcHigh,
            microMl = microMl,
            groMl = groMl,
            bloomMl = bloomMl,
            bounds = bounds.map { it.toEntity() },
            amounts = amounts,
        )
    }

    private fun Chart.Bound.toEntity(): ChartEntity.Bound {
        return ChartEntity.Bound(
            type = type,
            low = low,
            high = high,
        )
    }

    private fun ChartPatchRequest.toUpdate(): ChartUpdate {
        return ChartUpdate(
            name = name,
            targetPhLow = targetPhLow,
            targetPhHigh = targetPhHigh,
            targetEcLow = targetEcLow,
            targetEcHigh = targetEcHigh,
            microMl = microMl,
            groMl = groMl,
            bloomMl = bloomMl,
            bounds = bounds?.map { it.toEntity() },
            amounts = amounts,
        )
    }
}
