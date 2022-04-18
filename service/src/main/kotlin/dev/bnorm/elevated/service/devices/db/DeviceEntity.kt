package dev.bnorm.elevated.service.devices.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("devices")
class DeviceEntity(
    val name: String,
    val keyHash: String,
) {
    @Id
    lateinit var id: String
}
