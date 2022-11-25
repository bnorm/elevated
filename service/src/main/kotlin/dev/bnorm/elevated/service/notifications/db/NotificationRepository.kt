package dev.bnorm.elevated.service.notifications.db

import dev.bnorm.elevated.model.notifications.NotificationId
import dev.bnorm.elevated.model.users.UserId
import dev.bnorm.elevated.service.mongo.ensureIndex
import dev.bnorm.elevated.service.mongo.Update
import jakarta.annotation.PostConstruct
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findAllAndRemove
import org.springframework.data.mongodb.core.findAndModify
import org.springframework.data.mongodb.core.findAndRemove
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.exists
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class NotificationRepository(
    private val mongo: ReactiveMongoOperations,
) {
    @PostConstruct
    fun setup(): Unit = runBlocking {
        val indexOps = mongo.indexOps(NotificationEntity.COLLECTION_NAME)
        indexOps.ensureIndex {
            on(NotificationEntity::userId.toDotPath(), Sort.Direction.ASC)
        }
    }

    suspend fun insert(deviceActionEntity: NotificationEntity): NotificationEntity {
        return mongo.insert(deviceActionEntity).awaitSingle()
    }

    suspend fun findById(
        userId: UserId,
        notificationId: NotificationId,
    ): NotificationEntity? {
        val criteria = Criteria().andOperator(
            NotificationEntity::_id isEqualTo notificationId,
            NotificationEntity::userId isEqualTo userId,
        )
        val query = Query(criteria)
        return mongo.findOne<NotificationEntity>(query).awaitSingleOrNull()
    }

    fun findByUserId(
        userId: UserId,
        acknowledged: Boolean,
        limit: Int?,
    ): Flow<NotificationEntity> {
        val criteria = Criteria().andOperator(
            NotificationEntity::userId isEqualTo userId,
            NotificationEntity::acknowledged exists acknowledged,
        )
        val query = Query(criteria).apply {
            with(Sort.by(NotificationEntity::submitted.toDotPath()))
            if (limit != null) limit(limit)
        }
        return mongo.find<NotificationEntity>(query).asFlow()
    }

    suspend fun acknowledge(
        userId: UserId,
        notificationId: NotificationId,
        timestamp: Instant,
    ): NotificationEntity? {
        val criteria = Criteria().andOperator(
            NotificationEntity::_id isEqualTo notificationId,
            NotificationEntity::userId isEqualTo userId,
            NotificationEntity::acknowledged exists false,
        )
        val query = Query(criteria)
        val update = Update {
            set(NotificationEntity::acknowledged, timestamp)
        }
        val options = FindAndModifyOptions.options()
            .returnNew(true)
        return mongo.findAndModify<NotificationEntity>(query, update, options).awaitSingleOrNull()
    }

    suspend fun modify(
        userId: UserId,
        notificationId: NotificationId,
        notificationUpdate: NotificationUpdate,
    ): NotificationEntity? {
        val criteria = Criteria().andOperator(
            NotificationEntity::_id isEqualTo notificationId,
            NotificationEntity::userId isEqualTo userId,
            NotificationEntity::acknowledged exists false,
        )
        val query = Query(criteria)
        val update = Update {
            patch(NotificationEntity::message, notificationUpdate.message)
        }
        val options = FindAndModifyOptions.options()
            .returnNew(true)
        return mongo.findAndModify<NotificationEntity>(query, update, options)
            .awaitSingleOrNull()
    }

    suspend fun deleteById(
        userId: UserId,
        notificationId: NotificationId,
    ): NotificationEntity? {
        val criteria = Criteria().andOperator(
            NotificationEntity::_id isEqualTo notificationId.value,
            NotificationEntity::userId isEqualTo userId,
        )
        val query = Query(criteria)
        return mongo.findAndRemove<NotificationEntity>(query).awaitSingleOrNull()
    }

    suspend fun deleteByUserId(userId: UserId): Flow<NotificationEntity> {
        val criteria = NotificationEntity::userId isEqualTo userId
        val query = Query(criteria)
        return mongo.findAllAndRemove<NotificationEntity>(query).asFlow()
    }
}
