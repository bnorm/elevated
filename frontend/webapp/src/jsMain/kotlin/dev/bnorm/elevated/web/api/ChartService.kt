package dev.bnorm.elevated.web.api

import dev.bnorm.elevated.model.charts.Chart
import io.ktor.client.request.get

object ChartService {
    suspend fun getCharts(): List<Chart> =
        client.get(apiUrl.appendPath("charts"))
}
