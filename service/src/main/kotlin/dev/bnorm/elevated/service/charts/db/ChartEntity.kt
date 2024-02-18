package dev.bnorm.elevated.service.charts.db

import dev.bnorm.elevated.model.charts.ChartId
import dev.bnorm.elevated.model.pumps.PumpContent
import dev.bnorm.elevated.model.sensors.SensorType
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(ChartEntity.COLLECTION_NAME)
class ChartEntity(
    val name: String,
    val targetPhLow: Double? = null,
    val targetPhHigh: Double? = null,
    val targetEcLow: Double? = null,
    val targetEcHigh: Double? = null,
    val microMl: Double? = null,
    val groMl: Double? = null,
    val bloomMl: Double? = null,
    val bounds: List<Bound>? = null,
    val amounts: Map<PumpContent, Double>? = null,
) {
    @Id
    lateinit var _id: ObjectId
    val id: ChartId get() = ChartId(_id.toHexString())

    @Document
    data class Bound(
        val type: SensorType,
        val low: Double,
        val high: Double,
    )

    companion object {
        const val COLLECTION_NAME = "charts"
    }
}
