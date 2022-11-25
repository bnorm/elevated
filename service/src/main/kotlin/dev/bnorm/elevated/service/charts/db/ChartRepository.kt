package dev.bnorm.elevated.service.charts.db

import dev.bnorm.elevated.model.charts.ChartId
import dev.bnorm.elevated.service.mongo.ensureIndex
import dev.bnorm.elevated.service.mongo.Update
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findAndModify
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Repository

@Repository
class ChartRepository(
    private val mongo: ReactiveMongoOperations,
) {
    @PostConstruct
    fun setup(): Unit = runBlocking {
        val indexOps = mongo.indexOps(ChartEntity.COLLECTION_NAME)
        indexOps.ensureIndex {
            on(ChartEntity::name.toDotPath(), Sort.Direction.ASC)
            unique()
            background()
        }
    }

    suspend fun insert(chartEntity: ChartEntity): ChartEntity {
        return mongo.insert(chartEntity).awaitSingle()
    }

    suspend fun delete(chartId: ChartId) {
        val criteria = ChartEntity::_id isEqualTo chartId
        val query = Query(criteria)
        mongo.remove<ChartEntity>(query).awaitSingle()
    }

    fun findAll(): Flow<ChartEntity> {
        return mongo.findAll<ChartEntity>().asFlow()
    }

    suspend fun findById(chartId: ChartId): ChartEntity? {
        val criteria = ChartEntity::_id isEqualTo chartId
        val query = Query(criteria)
        return mongo.findOne<ChartEntity>(query).awaitSingleOrNull()
    }

    suspend fun modify(chartId: ChartId, chartUpdate: ChartUpdate): ChartEntity? {
        val criteria = ChartEntity::_id isEqualTo chartId
        val query = Query(criteria)
        val update = Update {
            patch(ChartEntity::name, chartUpdate.name)
            patch(ChartEntity::targetPhLow, chartUpdate.targetPhLow)
            patch(ChartEntity::targetPhHigh, chartUpdate.targetPhHigh)
            patch(ChartEntity::targetEcLow, chartUpdate.targetEcLow)
            patch(ChartEntity::targetEcHigh, chartUpdate.targetEcHigh)
            patch(ChartEntity::microMl, chartUpdate.microMl)
            patch(ChartEntity::groMl, chartUpdate.groMl)
            patch(ChartEntity::bloomMl, chartUpdate.bloomMl)
        }
        val options = FindAndModifyOptions.options().returnNew(true)
        return mongo.findAndModify<ChartEntity>(query, update, options).awaitSingleOrNull()
    }
}
