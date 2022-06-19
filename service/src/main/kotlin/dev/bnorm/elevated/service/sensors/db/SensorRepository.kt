package dev.bnorm.elevated.service.sensors.db

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.service.mongo.ensureIndex
import dev.bnorm.elevated.service.mongo.patch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.toPath
import org.springframework.stereotype.Repository
import javax.annotation.PostConstruct

@Repository
class SensorRepository(
    private val mongo: ReactiveMongoOperations,
) {
    @PostConstruct
    fun setup(): Unit = runBlocking {
        val indexOps = mongo.indexOps(SensorEntity.COLLECTION_NAME)
        indexOps.ensureIndex {
            on(SensorEntity::deviceId.toPath(), Sort.Direction.ASC)
        }
    }

    suspend fun create(sensorEntity: SensorEntity): SensorEntity {
        return mongo.insert(sensorEntity).awaitSingle()
    }

    suspend fun update(sensorId: SensorId, sensorUpdate: SensorUpdate): SensorEntity? {
        val criteria = SensorEntity::_id isEqualTo sensorId
        val query = Query(criteria)
        val update = Update().apply {
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
