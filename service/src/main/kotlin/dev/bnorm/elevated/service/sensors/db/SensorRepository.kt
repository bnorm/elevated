package dev.bnorm.elevated.service.sensors.db

import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.service.mongo.ensureIndex
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Query
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

    suspend fun insert(sensorEntity: SensorEntity): SensorEntity {
        return mongo.insert(sensorEntity).awaitSingle()
    }

    suspend fun delete(sensorId: SensorId) {
        val criteria = SensorEntity::id isEqualTo sensorId.value
        val query = Query(criteria)
        mongo.remove<SensorEntity>(query).awaitSingle()
    }

    fun findAll(): Flow<SensorEntity> {
        return mongo.findAll<SensorEntity>().asFlow()
    }

    fun findByDeviceId(deviceId: DeviceId): Flow<SensorEntity> {
        val criteria = SensorEntity::deviceId isEqualTo deviceId.value
        val query = Query(criteria)
        return mongo.find<SensorEntity>(query).asFlow()
    }

    suspend fun findBySensorId(sensorId: SensorId): SensorEntity? {
        val criteria = SensorEntity::id isEqualTo sensorId.value
        val query = Query(criteria)
        return mongo.findOne<SensorEntity>(query).awaitSingleOrNull()
    }
}
