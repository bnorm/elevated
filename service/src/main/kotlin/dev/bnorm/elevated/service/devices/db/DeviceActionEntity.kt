package dev.bnorm.elevated.service.devices.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(DeviceActionEntity.COLLECTION_NAME)
class DeviceActionEntity(
    val deviceId: String,
    val submitted: Instant,
    val completed: Instant?,
    val args: DeviceActionArgumentsEntity,
) {
    @Id
    lateinit var id: String

    companion object {
        const val COLLECTION_NAME = "deviceActions"
    }
}
