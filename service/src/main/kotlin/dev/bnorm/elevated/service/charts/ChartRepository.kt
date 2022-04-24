package dev.bnorm.elevated.service.charts

import dev.bnorm.elevated.model.charts.ChartId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Repository
import javax.annotation.PostConstruct

@Repository
class ChartRepository(
    private val mongo: ReactiveMongoOperations,
) {
    @PostConstruct
    fun setup(): Unit = runBlocking {
        val indexOps = mongo.indexOps(ChartEntity.COLLECTION_NAME)
        indexOps.ensureIndex(Index(ChartEntity::name.name, Sort.Direction.ASC).unique().background())
            .awaitSingleOrNull()
    }

    suspend fun insert(chartEntity: ChartEntity): ChartEntity {
        return mongo.insert(chartEntity).awaitSingle()
    }

    suspend fun delete(chartId: ChartId) {
        val query = Query(
            ChartEntity::id isEqualTo chartId.value,
        )
        mongo.remove<ChartEntity>(query).awaitSingle()
    }

    fun findAll(): Flow<ChartEntity> {
        return mongo.findAll<ChartEntity>().asFlow()
    }

    suspend fun findById(id: ChartId): ChartEntity? {
        val query = Query(
            ChartEntity::id isEqualTo id.value,
        )
        return mongo.findOne<ChartEntity>(query).awaitSingleOrNull()
    }
}
