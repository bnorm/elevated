package dev.bnorm.elevated.service.pumps.db

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.pumps.PumpId
import dev.bnorm.elevated.service.mongo.createIndex
import dev.bnorm.elevated.service.mongo.Update
import jakarta.annotation.PostConstruct
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
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findAndModify
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Repository

@Repository
class PumpRepository(
    private val mongo: ReactiveMongoOperations,
) {
    @PostConstruct
    fun setup(): Unit = runBlocking {
        val indexOps = mongo.indexOps(PumpEntity.COLLECTION_NAME)
        indexOps.createIndex {
            on(PumpEntity::deviceId.toDotPath(), Sort.Direction.ASC)
        }
    }

    suspend fun create(pumpEntity: PumpEntity): PumpEntity {
        return mongo.insert(pumpEntity).awaitSingle()
    }

    suspend fun update(pumpId: PumpId, pumpUpdate: PumpUpdate): PumpEntity? {
        val criteria = PumpEntity::_id isEqualTo pumpId
        val query = Query(criteria)
        val update = Update {
            patch(PumpEntity::name, pumpUpdate.name)
            patch(PumpEntity::deviceId, pumpUpdate.deviceId)
            patch(PumpEntity::flowRate, pumpUpdate.flowRate)
            patch(PumpEntity::content, pumpUpdate.content)
        }
        val options = FindAndModifyOptions.options().returnNew(true)
        return mongo.findAndModify<PumpEntity>(query, update, options).awaitSingleOrNull()
    }

    suspend fun delete(pumpId: PumpId) {
        val criteria = PumpEntity::_id isEqualTo pumpId
        val query = Query(criteria)
        mongo.remove<PumpEntity>(query).awaitSingle()
    }

    fun getAll(): Flow<PumpEntity> {
        return mongo.findAll<PumpEntity>().asFlow()
    }

    fun getByDeviceId(deviceId: DeviceId): Flow<PumpEntity> {
        val criteria = PumpEntity::deviceId isEqualTo deviceId
        val query = Query(criteria)
        return mongo.find<PumpEntity>(query).asFlow()
    }

    suspend fun getById(pumpId: PumpId): PumpEntity? {
        val criteria = PumpEntity::_id isEqualTo pumpId
        val query = Query(criteria)
        return mongo.findOne<PumpEntity>(query).awaitSingleOrNull()
    }
}
