package dev.bnorm.elevated.service.sensors.db

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.sensors.SensorId
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
class SensorRepository(
    private val mongo: ReactiveMongoOperations,
) {
    @PostConstruct
    fun setup(): Unit = runBlocking {
        val indexOps = mongo.indexOps(SensorEntity.COLLECTION_NAME)
        indexOps.createIndex {
            on(SensorEntity::deviceId.toDotPath(), Sort.Direction.ASC)
        }
    }

    suspend fun create(sensorEntity: SensorEntity): SensorEntity {
        return mongo.insert(sensorEntity).awaitSingle()
    }

    suspend fun update(sensorId: SensorId, sensorUpdate: SensorUpdate): SensorEntity? {
        val criteria = SensorEntity::_id isEqualTo sensorId
        val query = Query(criteria)
        val update = Update {
            patch(SensorEntity::name, sensorUpdate.name)
            patch(SensorEntity::type, sensorUpdate.type)
            patch(SensorEntity::deviceId, sensorUpdate.deviceId)
        }
        val options = FindAndModifyOptions.options().returnNew(true)
        return mongo.findAndModify<SensorEntity>(query, update, options).awaitSingleOrNull()
    }

    suspend fun delete(sensorId: SensorId) {
        val criteria = SensorEntity::_id isEqualTo sensorId
        val query = Query(criteria)
        mongo.remove<SensorEntity>(query).awaitSingle()
    }

    fun getAll(): Flow<SensorEntity> {
        return mongo.findAll<SensorEntity>().asFlow()
    }

    fun getByDeviceId(deviceId: DeviceId): Flow<SensorEntity> {
        val criteria = SensorEntity::deviceId isEqualTo deviceId
        val query = Query(criteria)
        return mongo.find<SensorEntity>(query).asFlow()
    }

    suspend fun getById(sensorId: SensorId): SensorEntity? {
        val criteria = SensorEntity::_id isEqualTo sensorId
        val query = Query(criteria)
        return mongo.findOne<SensorEntity>(query).awaitSingleOrNull()
    }
}
