package dev.bnorm.elevated.service.pumps.db

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.pumps.PumpContent
import dev.bnorm.elevated.model.pumps.PumpId
import dev.bnorm.elevated.model.sensors.SensorType
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(PumpEntity.COLLECTION_NAME)
class PumpEntity(
    val name: String,
    val deviceId: DeviceId,
    /** Measured in milliliters / second. */
    val flowRate: Double,
    val content: PumpContent,
) {
    @Id
    lateinit var _id: ObjectId
    val id: PumpId get() = PumpId(_id.toHexString())

    companion object {
        const val COLLECTION_NAME = "pumps"
    }
}
