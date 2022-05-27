package dev.bnorm.elevated.service.devices.db

import dev.bnorm.elevated.model.devices.DeviceActionId
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.service.mongo.ensureIndex
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.*
import org.springframework.stereotype.Repository
import java.time.Instant
import javax.annotation.PostConstruct

@Repository
class DeviceActionRepository(
    private val mongo: ReactiveMongoOperations,
) {
    @PostConstruct
    fun setup(): Unit = runBlocking {
        val indexOps = mongo.indexOps(DeviceActionEntity.COLLECTION_NAME)
        indexOps.ensureIndex {
            on(DeviceActionEntity::deviceId.toPath(), Sort.Direction.ASC)
            on(DeviceActionEntity::submitted.toPath(), Sort.Direction.ASC)
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
            DeviceActionEntity::id isEqualTo deviceActionId.value,
            DeviceActionEntity::deviceId isEqualTo deviceId.value,
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
            DeviceActionEntity::id isEqualTo deviceActionId.value,
            DeviceActionEntity::deviceId isEqualTo deviceId.value,
            DeviceActionEntity::completed exists false,
        )
        val query = Query(criteria)
        val update = Update().apply {
            set(DeviceActionEntity::completed.toPath(), timestamp)
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
            DeviceActionEntity::deviceId isEqualTo id.value,
            DeviceActionEntity::submitted gt submittedAfter,
        )
        val query = Query(criteria)
            .with(Sort.by(DeviceActionEntity::submitted.toPath()))
            .apply { if (limit != null) limit(limit) }
        return mongo.find<DeviceActionEntity>(query).asFlow()
    }

    fun findLatestByDeviceId(
        id: DeviceId,
        limit: Int,
    ): Flow<DeviceActionEntity> {
        val criteria = DeviceActionEntity::deviceId isEqualTo id.value
        val query = Query(criteria)
            .with(Sort.by(DeviceActionEntity::submitted.toPath()).descending())
            .limit(limit)
        return mongo.find<DeviceActionEntity>(query).asFlow()
    }

    suspend fun deleteById(
        deviceId: DeviceId,
        deviceActionId: DeviceActionId,
    ) {
        val criteria = Criteria().andOperator(
            DeviceActionEntity::id isEqualTo deviceActionId.value,
            DeviceActionEntity::deviceId isEqualTo deviceId.value,
        )
        val query = Query(criteria)
        mongo.remove<DeviceActionEntity>(query).awaitSingleOrNull()
    }

    suspend fun deleteByDeviceId(deviceId: DeviceId) {
        val criteria = DeviceActionEntity::deviceId isEqualTo deviceId.value
        val query = Query(criteria)
        mongo.remove<DeviceActionEntity>(query).awaitSingleOrNull()
    }
}
