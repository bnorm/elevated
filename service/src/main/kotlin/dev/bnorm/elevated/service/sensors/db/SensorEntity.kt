package dev.bnorm.elevated.service.sensors.db

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(SensorEntity.COLLECTION_NAME)
class SensorEntity(
    val name: String,
    val type: SensorType? = null,
    val deviceId: DeviceId,
) {
    @Id
    lateinit var id: SensorId

    companion object {
        const val COLLECTION_NAME = "sensors"
    }
}
