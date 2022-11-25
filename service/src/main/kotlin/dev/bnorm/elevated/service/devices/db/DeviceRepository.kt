package dev.bnorm.elevated.service.devices.db

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.service.mongo.Update
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
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Repository

@Repository
class DeviceRepository(
    private val mongo: ReactiveMongoOperations,
) {
    suspend fun insert(deviceEntity: DeviceEntity): DeviceEntity {
        return mongo.insert(deviceEntity).awaitSingle()
    }

    suspend fun delete(deviceId: DeviceId) {
        val criteria = DeviceEntity::_id isEqualTo deviceId
        val query = Query(criteria)
        mongo.remove<DeviceEntity>(query).awaitSingle()
    }

    fun findAll(): Flow<DeviceEntity> {
        return mongo.findAll<DeviceEntity>().asFlow()
    }

    suspend fun findById(deviceId: DeviceId): DeviceEntity? {
        val criteria = DeviceEntity::_id isEqualTo deviceId
        val query = Query(criteria)
        return mongo.findOne<DeviceEntity>(query).awaitSingleOrNull()
    }

    suspend fun modify(deviceId: DeviceId, deviceUpdate: DeviceUpdate): DeviceEntity? {
        val criteria = DeviceEntity::_id isEqualTo deviceId
        val query = Query(criteria)
        val update = Update {
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
