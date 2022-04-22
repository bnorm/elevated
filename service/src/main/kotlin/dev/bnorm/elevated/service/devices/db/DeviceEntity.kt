package dev.bnorm.elevated.service.devices.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("devices")
class DeviceEntity(
    val name: String,
    val keyHash: String,
    val lastActionTime: Instant? = null,
) {
    @Id
    lateinit var id: String
}
