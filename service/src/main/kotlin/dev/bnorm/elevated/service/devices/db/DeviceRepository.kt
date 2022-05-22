package dev.bnorm.elevated.service.devices.db

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.service.mongo.patch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

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

    suspend fun modify(id: DeviceId, deviceUpdate: DeviceUpdate): DeviceEntity? {
        val query = Query(
            DeviceEntity::id isEqualTo id.value,
        )
        val update = Update().apply {
            patch(DeviceEntity::name, deviceUpdate.name)
            patch(DeviceEntity::keyHash, deviceUpdate.keyHash)
            patch(DeviceEntity::status, deviceUpdate.status)
            patch(DeviceEntity::lastActionTime, deviceUpdate.lastActionTime)
            patch(DeviceEntity::chartId, deviceUpdate.chartId?.value)
        }
        val options = FindAndModifyOptions.options()
            .returnNew(true)
        return mongo.findAndModify<DeviceEntity>(query, update, options)
            .awaitSingleOrNull()
    }
}
