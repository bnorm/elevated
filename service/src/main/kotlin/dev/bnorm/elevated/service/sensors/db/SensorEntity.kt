package dev.bnorm.elevated.service.sensors.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(SensorEntity.COLLECTION_NAME)
class SensorEntity(
    val name: String,
    val deviceId: String,
) {
    @Id
    lateinit var id: String

    companion object {
        const val COLLECTION_NAME = "sensors"
    }
}
