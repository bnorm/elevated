package dev.bnorm.elevated.service.devices.db

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.DeviceStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findAndModify
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class DeviceRepository(
    private val mongo: ReactiveMongoOperations,
) {
    suspend fun insert(deviceEntity: DeviceEntity): DeviceEntity {
        return mongo.insert(deviceEntity).awaitSingle()
    }

    suspend fun delete(deviceId: DeviceId) {
        val query = Query(
            DeviceEntity::id isEqualTo deviceId.value,
        )
        mongo.remove<DeviceEntity>(query).awaitSingle()
    }

    fun findAll(): Flow<DeviceEntity> {
        return mongo.findAll<DeviceEntity>().asFlow()
    }

    suspend fun findById(id: DeviceId): DeviceEntity? {
        val query = Query(
            DeviceEntity::id isEqualTo id.value,
        )
        return mongo.findOne<DeviceEntity>(query).awaitSingleOrNull()
    }

    suspend fun modify(id: DeviceId, timestamp: Instant): DeviceEntity? {
        val query = Query(
            DeviceEntity::id isEqualTo id.value,
        )
        val update = Update()
            .set(DeviceEntity::lastActionTime.name, timestamp)
        val options = FindAndModifyOptions.options()
            .returnNew(true)
        return mongo.findAndModify<DeviceEntity>(query, update, options)
            .awaitSingleOrNull()
    }

    suspend fun modify(id: DeviceId, status: DeviceStatus): DeviceEntity? {
        val query = Query(
            DeviceEntity::id isEqualTo id.value,
        )
        val update = Update()
            .set(DeviceEntity::status.name, status)
        val options = FindAndModifyOptions.options()
            .returnNew(true)
        return mongo.findAndModify<DeviceEntity>(query, update, options)
            .awaitSingleOrNull()
    }
}
