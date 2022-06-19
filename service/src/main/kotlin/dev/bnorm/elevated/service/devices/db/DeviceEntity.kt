package dev.bnorm.elevated.service.devices.db

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.DeviceStatus
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(DeviceEntity.COLLECTION_NAME)
class DeviceEntity(
    val name: String,
    val keyHash: String,
    val status: DeviceStatus = DeviceStatus.Offline,
    val lastActionTime: Instant? = null,
    val chartId: String? = null,
) {
    @Id
    lateinit var _id: ObjectId
    val id: DeviceId get() = DeviceId(_id.toHexString())

    companion object {
        const val COLLECTION_NAME = "devices"
    }
}
