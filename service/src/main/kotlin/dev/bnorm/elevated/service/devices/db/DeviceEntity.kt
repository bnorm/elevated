package dev.bnorm.elevated.service.devices.db

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.DeviceStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("devices")
class DeviceEntity(
    val name: String,
    val keyHash: String,
    val status: DeviceStatus = DeviceStatus.Offline,
    val lastActionTime: Instant? = null,
    val chartId: String? = null,
) {
    @Id
    lateinit var id: DeviceId
}
