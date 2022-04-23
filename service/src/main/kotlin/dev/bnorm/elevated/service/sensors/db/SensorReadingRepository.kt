package dev.bnorm.elevated.service.sensors.db

import dev.bnorm.elevated.model.sensors.SensorId
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
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.gte
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.lt
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
                        timeSeries(SensorReadingEntity::timestamp.name)
                            .metaField(SensorReadingEntity::sensorId.name)
                            .granularity(Granularity.MINUTES)
                    )
            ).awaitSingleOrNull()
        }

        val indexOps = mongo.indexOps(SensorReadingEntity.COLLECTION_NAME)
        indexOps.ensureIndex {
            on(SensorReadingEntity::sensorId.name, Sort.Direction.ASC)
            on(SensorReadingEntity::timestamp.name, Sort.Direction.ASC)
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
        val query = Query(
            Criteria().andOperator(
                SensorReadingEntity::sensorId isEqualTo sensorId.value,
                SensorReadingEntity::timestamp gte startTime,
                SensorReadingEntity::timestamp lt endTime,
            )
        )
        return mongo.find<SensorReadingEntity>(query).asFlow()
    }
}

suspend fun ReactiveIndexOperations.ensureIndex(builder: Index.() -> Unit) {
    ensureIndex(Index().apply(builder)).awaitSingleOrNull()
}
