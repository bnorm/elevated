package dev.bnorm.elevated.service.devices.db

import dev.bnorm.elevated.model.devices.DeviceActionId
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.service.sensors.db.ensureIndex
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findAndModify
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.exists
import org.springframework.data.mongodb.core.query.gt
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.timeseries.Granularity
import org.springframework.stereotype.Repository
import java.time.Instant
import javax.annotation.PostConstruct

@Repository
class DeviceActionRepository(
    private val mongo: ReactiveMongoOperations,
) {
    @PostConstruct
    fun setup(): Unit = runBlocking {
        if (!mongo.collectionExists(DeviceActionEntity.COLLECTION_NAME).awaitSingle()) {
            mongo.createCollection(
                DeviceActionEntity.COLLECTION_NAME, CollectionOptions.empty()
                    .timeSeries(
                        CollectionOptions.TimeSeriesOptions.timeSeries(DeviceActionEntity::submitted.name)
                            .metaField(DeviceActionEntity::deviceId.name)
                            .granularity(Granularity.HOURS)
                    )
            ).awaitSingleOrNull()
        }

        val indexOps = mongo.indexOps(DeviceActionEntity.COLLECTION_NAME)
        indexOps.ensureIndex {
            on(DeviceActionEntity::deviceId.name, Sort.Direction.ASC)
            on(DeviceActionEntity::submitted.name, Sort.Direction.ASC)
        }
    }

    suspend fun insert(deviceActionEntity: DeviceActionEntity): DeviceActionEntity {
        return mongo.insert(deviceActionEntity).awaitSingle()
    }

    suspend fun complete(
        deviceId: DeviceId,
        deviceActionId: DeviceActionId,
        timestamp: Instant,
    ): DeviceActionEntity? {
        val query = Query(
            Criteria().andOperator(
                DeviceActionEntity::id isEqualTo deviceActionId.value,
                DeviceActionEntity::deviceId isEqualTo deviceId.value,
                DeviceActionEntity::completed exists false,
            )
        )
        val update = Update().apply {
            set(DeviceActionEntity::completed.name, timestamp)
        }
        val options = FindAndModifyOptions.options()
            .returnNew(true)
        return mongo.findAndModify<DeviceActionEntity>(query, update, options)
            .awaitSingleOrNull()
    }

    fun findByDeviceId(
        id: DeviceId,
        submittedAfter: Instant,
        limit: Int?,
    ): Flow<DeviceActionEntity> {
        val query = Query(
            Criteria().andOperator(
                DeviceActionEntity::deviceId isEqualTo id.value,
                DeviceActionEntity::submitted gt submittedAfter,
            )
        )
            .with(Sort.by(DeviceActionEntity::submitted.name))
            .apply { if (limit != null) limit(limit) }
        return mongo.find<DeviceActionEntity>(query).asFlow()
    }
}
