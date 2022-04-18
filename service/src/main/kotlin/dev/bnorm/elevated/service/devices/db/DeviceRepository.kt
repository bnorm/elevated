package dev.bnorm.elevated.service.devices.db

import dev.bnorm.elevated.service.devices.DeviceId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class DeviceRepository(
    private val mongo: ReactiveMongoOperations,
) {
    suspend fun insert(sensorEntity: DeviceEntity): DeviceEntity {
        return mongo.insert(sensorEntity).awaitSingle()
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
}
