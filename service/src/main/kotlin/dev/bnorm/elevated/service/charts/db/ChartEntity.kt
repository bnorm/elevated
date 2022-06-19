package dev.bnorm.elevated.service.charts.db

import dev.bnorm.elevated.model.charts.ChartId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(ChartEntity.COLLECTION_NAME)
class ChartEntity(
    val name: String,
    val targetPhLow: Double,
    val targetPhHigh: Double,
    val targetEcLow: Double,
    val targetEcHigh: Double,
    val microMl: Double,
    val groMl: Double,
    val bloomMl: Double,
) {
    @Id
    lateinit var id: ChartId

    companion object {
        const val COLLECTION_NAME = "charts"
    }
}
