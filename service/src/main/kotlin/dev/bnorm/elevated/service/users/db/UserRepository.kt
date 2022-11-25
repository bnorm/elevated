package dev.bnorm.elevated.service.users.db

import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.model.users.UserId
import dev.bnorm.elevated.service.mongo.ensureIndex
import dev.bnorm.elevated.service.mongo.Update
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
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
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val mongo: ReactiveMongoOperations
) {
    @PostConstruct
    fun setup(): Unit = runBlocking {
        val indexOps = mongo.indexOps(UserEntity.COLLECTION_NAME)

        indexOps.ensureIndex {
            on(UserEntity::email.toDotPath(), Sort.Direction.ASC)
            unique()
            background()
        }
    }

    fun getAllUsers(): Flow<UserEntity> {
        return mongo.findAll<UserEntity>().asFlow()
    }

    suspend fun insert(entity: UserEntity): UserEntity {
        return mongo.insert(entity).awaitSingle()
        // TODO wrap duplicate key exceptions
    }

    suspend fun modify(userId: UserId, userUpdate: UserUpdate): UserEntity? {
        val criteria = UserEntity::_id isEqualTo userId
        val query = Query(criteria)
        val update = Update {
            patch(UserEntity::email, userUpdate.email)
            patch(UserEntity::passwordHash, userUpdate.passwordHash)
            patch(UserEntity::name, userUpdate.name)
            patch(UserEntity::role, userUpdate.role)
        }
        val options = FindAndModifyOptions.options().returnNew(true)
        return mongo.findAndModify<UserEntity>(query, update, options).awaitSingleOrNull()
    }

    suspend fun findById(userId: UserId): UserEntity? {
        val criteria = UserEntity::_id isEqualTo userId
        val query = Query(criteria)
        return mongo.findOne<UserEntity>(query).awaitSingleOrNull()
    }

    suspend fun findByEmail(email: Email): UserEntity? {
        val criteria = UserEntity::email isEqualTo email.value.lowercase()
        val query = Query(criteria)
        return mongo.findOne<UserEntity>(query).awaitSingleOrNull()
    }
}
