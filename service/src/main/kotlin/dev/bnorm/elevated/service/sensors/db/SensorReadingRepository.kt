package dev.bnorm.elevated.service.sensors.db

import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.service.mongo.createIndex
import jakarta.annotation.PostConstruct
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.CollectionOptions.TimeSeriesOptions.timeSeries
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.gte
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.lt
import org.springframework.data.mongodb.core.timeseries.Granularity
import org.springframework.stereotype.Repository

@Repository
class SensorReadingRepository(
    private val mongo: ReactiveMongoOperations,
) {
    @PostConstruct
    fun setup(): Unit = runBlocking {
        if (!mongo.collectionExists(SensorReadingEntity.COLLECTION_NAME).awaitSingle()) {
            mongo.createCollection(
                SensorReadingEntity.COLLECTION_NAME, CollectionOptions.empty()
                    .timeSeries(
                        timeSeries(SensorReadingEntity::timestamp.toDotPath())
                            .metaField(SensorReadingEntity::sensorId.toDotPath())
                            .granularity(Granularity.MINUTES)
                    )
            ).awaitSingleOrNull()
        }

        val indexOps = mongo.indexOps(SensorReadingEntity.COLLECTION_NAME)
        indexOps.createIndex {
            on(SensorReadingEntity::sensorId.toDotPath(), Sort.Direction.ASC)
            on(SensorReadingEntity::timestamp.toDotPath(), Sort.Direction.ASC)
        }
    }

    suspend fun insert(sensorReadingEntity: SensorReadingEntity): SensorReadingEntity {
        return mongo.insert(sensorReadingEntity).awaitSingle()
    }

    fun findBySensorId(
        sensorId: SensorId,
        startTime: Instant,
        endTime: Instant,
    ): Flow<SensorReadingEntity> {
        val criteria = Criteria().andOperator(
            Criteria().orOperator(
                SensorReadingEntity::sensorId isEqualTo sensorId,
                SensorReadingEntity::sensorId isEqualTo sensorId.value,
            ),
            SensorReadingEntity::timestamp gte startTime,
            SensorReadingEntity::timestamp lt endTime,
        )
        val query = Query(criteria)
        return mongo.find<SensorReadingEntity>(query).asFlow()
    }

    fun findLatestBySensorId(
        sensorId: SensorId,
        count: Int,
    ): Flow<SensorReadingEntity> {
        val criteria = Criteria().orOperator(
            SensorReadingEntity::sensorId isEqualTo sensorId,
            SensorReadingEntity::sensorId isEqualTo sensorId.value,
        )
        val query = Query(criteria)
            .with(Sort.by(SensorReadingEntity::timestamp.toDotPath()).descending())
            .limit(count)
        return mongo.find<SensorReadingEntity>(query).asFlow()
    }
}

