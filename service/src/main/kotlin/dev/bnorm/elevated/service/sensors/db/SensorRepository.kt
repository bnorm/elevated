package dev.bnorm.elevated.service.sensors.db

import dev.bnorm.elevated.service.devices.DeviceId
import dev.bnorm.elevated.service.sensors.SensorId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
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
            on(SensorEntity::deviceId.name, Sort.Direction.ASC)
        }
    }

    suspend fun insert(sensorEntity: SensorEntity): SensorEntity {
        return mongo.insert(sensorEntity).awaitSingle()
    }

    fun findAll(): Flow<SensorEntity> {
        return mongo.findAll<SensorEntity>().asFlow()
    }

    fun findByDeviceId(deviceId: DeviceId): Flow<SensorEntity> {
        val query = Query(
            SensorEntity::deviceId isEqualTo deviceId.value,
        )
        return mongo.find<SensorEntity>(query).asFlow()
    }

    suspend fun findBySensorId(sensorId: SensorId): SensorEntity? {
        val query = Query(
            SensorEntity::id isEqualTo sensorId.value,
        )
        return mongo.findOne<SensorEntity>(query).awaitSingleOrNull()
    }
}
