package dev.bnorm.elevated.service.sensors.db

import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.service.mongo.ensureIndex
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.CollectionOptions.TimeSeriesOptions.timeSeries
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.*
import org.springframework.data.mongodb.core.timeseries.Granularity
import org.springframework.stereotype.Repository
import java.time.Instant
import javax.annotation.PostConstruct

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
                        timeSeries(SensorReadingEntity::timestamp.toPath())
                            .metaField(SensorReadingEntity::sensorId.toPath())
                            .granularity(Granularity.MINUTES)
                    )
            ).awaitSingleOrNull()
        }

        val indexOps = mongo.indexOps(SensorReadingEntity.COLLECTION_NAME)
        indexOps.ensureIndex {
            on(SensorReadingEntity::sensorId.toPath(), Sort.Direction.ASC)
            on(SensorReadingEntity::timestamp.toPath(), Sort.Direction.ASC)
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
            SensorReadingEntity::sensorId isEqualTo sensorId.value,
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
        val criteria = SensorReadingEntity::sensorId isEqualTo sensorId.value
        val query = Query(criteria)
            .with(Sort.by(SensorReadingEntity::timestamp.toPath()).descending())
            .limit(count)
        return mongo.find<SensorReadingEntity>(query).asFlow()
    }
}

