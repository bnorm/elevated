package dev.bnorm.elevated.service.charts.db

import dev.bnorm.elevated.model.charts.ChartId
import dev.bnorm.elevated.service.mongo.patch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
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

    suspend fun modify(id: ChartId, chartUpdate: ChartUpdate): ChartEntity? {
        val query = Query(
            ChartEntity::id isEqualTo id.value,
        )
        val update = Update().apply {
            patch(ChartEntity::name, chartUpdate.name)
            patch(ChartEntity::targetEcLow, chartUpdate.targetEcLow)
            patch(ChartEntity::targetEcHigh, chartUpdate.targetEcHigh)
            patch(ChartEntity::microMl, chartUpdate.microMl)
            patch(ChartEntity::groMl, chartUpdate.groMl)
            patch(ChartEntity::bloomMl, chartUpdate.bloomMl)
        }
        val options = FindAndModifyOptions.options()
            .returnNew(true)
        return mongo.findAndModify<ChartEntity>(query, update, options)
            .awaitSingleOrNull()
    }
}
