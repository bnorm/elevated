package dev.bnorm.elevated.service.sensors.db

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.MeasurementType
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(SensorEntity.COLLECTION_NAME)
class SensorEntity(
    val name: String,
    val type: MeasurementType? = null,
    val deviceId: DeviceId,
) {
    @Id
    lateinit var _id: ObjectId
    val id: SensorId get() = SensorId(_id.toHexString())

    companion object {
        const val COLLECTION_NAME = "sensors"
    }
}
