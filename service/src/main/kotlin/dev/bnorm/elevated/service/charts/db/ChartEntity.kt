package dev.bnorm.elevated.service.charts.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(ChartEntity.COLLECTION_NAME)
class ChartEntity(
    val name: String,
    val targetEcLow: Long,
    val targetEcHigh: Long,
    val microMl: Double,
    val groMl: Double,
    val bloomMl: Double,
) {
    @Id
    lateinit var id: String

    companion object {
        const val COLLECTION_NAME = "charts"
    }
}
