package dev.bnorm.elevated.service.devices.db

import dev.bnorm.elevated.model.devices.DeviceActionId
import dev.bnorm.elevated.model.devices.DeviceId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(DeviceActionEntity.COLLECTION_NAME)
class DeviceActionEntity(
    val deviceId: DeviceId,
    val submitted: Instant,
    val completed: Instant?,
    val args: DeviceActionArgumentsEntity,
) {
    @Id
    lateinit var id: DeviceActionId

    companion object {
        const val COLLECTION_NAME = "deviceActions"
    }
}
