package dev.bnorm.elevated.service.devices.db

import dev.bnorm.elevated.model.devices.DeviceActionId
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.service.mongo.ensureIndex
import dev.bnorm.elevated.service.mongo.Update
import jakarta.annotation.PostConstruct
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findAndModify
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.exists
import org.springframework.data.mongodb.core.query.gt
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Repository

@Repository
class DeviceActionRepository(
    private val mongo: ReactiveMongoOperations,
) {
    @PostConstruct
    fun setup(): Unit = runBlocking {
        val indexOps = mongo.indexOps(DeviceActionEntity.COLLECTION_NAME)
        indexOps.ensureIndex {
            on(DeviceActionEntity::deviceId.toDotPath(), Sort.Direction.ASC)
            on(DeviceActionEntity::submitted.toDotPath(), Sort.Direction.ASC)
        }
    }

    suspend fun insert(deviceActionEntity: DeviceActionEntity): DeviceActionEntity {
        return mongo.insert(deviceActionEntity).awaitSingle()
    }

    suspend fun findById(
        deviceId: DeviceId,
        deviceActionId: DeviceActionId,
    ): DeviceActionEntity? {
        val criteria = Criteria().andOperator(
            DeviceActionEntity::_id isEqualTo deviceActionId,
            DeviceActionEntity::deviceId isEqualTo deviceId,
        )
        val query = Query(criteria)
        return mongo.findOne<DeviceActionEntity>(query).awaitSingleOrNull()
    }

    suspend fun complete(
        deviceId: DeviceId,
        deviceActionId: DeviceActionId,
        timestamp: Instant,
    ): DeviceActionEntity? {
        val criteria = Criteria().andOperator(
            DeviceActionEntity::_id isEqualTo deviceActionId,
            DeviceActionEntity::deviceId isEqualTo deviceId,
            DeviceActionEntity::completed exists false,
        )
        val query = Query(criteria)
        val update = Update {
            set(DeviceActionEntity::completed, timestamp)
        }
        val options = FindAndModifyOptions.options()
            .returnNew(true)
        return mongo.findAndModify<DeviceActionEntity>(query, update, options).awaitSingleOrNull()
    }

    fun findByDeviceId(
        id: DeviceId,
        submittedAfter: Instant,
        limit: Int?,
    ): Flow<DeviceActionEntity> {
        val criteria = Criteria().andOperator(
            DeviceActionEntity::deviceId isEqualTo id,
            DeviceActionEntity::submitted gt submittedAfter,
        )
        val query = Query(criteria)
            .with(Sort.by(DeviceActionEntity::submitted.toDotPath()))
            .apply { if (limit != null) limit(limit) }
        return mongo.find<DeviceActionEntity>(query).asFlow()
    }

    fun findLatestByDeviceId(
        id: DeviceId,
        limit: Int,
    ): Flow<DeviceActionEntity> {
        val criteria = DeviceActionEntity::deviceId isEqualTo id.value
        val query = Query(criteria)
            .with(Sort.by(DeviceActionEntity::submitted.toDotPath()).descending())
            .limit(limit)
        return mongo.find<DeviceActionEntity>(query).asFlow()
    }

    suspend fun deleteById(
        deviceId: DeviceId,
        deviceActionId: DeviceActionId,
    ) {
        val criteria = Criteria().andOperator(
            DeviceActionEntity::_id isEqualTo deviceActionId,
            DeviceActionEntity::deviceId isEqualTo deviceId,
        )
        val query = Query(criteria)
        mongo.remove<DeviceActionEntity>(query).awaitSingleOrNull()
    }

    suspend fun deleteByDeviceId(deviceId: DeviceId) {
        val criteria = DeviceActionEntity::deviceId isEqualTo deviceId
        val query = Query(criteria)
        mongo.remove<DeviceActionEntity>(query).awaitSingleOrNull()
    }
}
