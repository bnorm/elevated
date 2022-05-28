package dev.bnorm.elevated.service.sensors.db

import dev.bnorm.elevated.model.sensors.SensorType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(SensorEntity.COLLECTION_NAME)
class SensorEntity(
    val name: String,
    val type: SensorType? = null,
    val deviceId: String,
) {
    @Id
    lateinit var id: String

    companion object {
        const val COLLECTION_NAME = "sensors"
    }
}
