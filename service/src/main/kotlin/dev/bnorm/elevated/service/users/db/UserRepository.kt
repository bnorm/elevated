package dev.bnorm.elevated.service.users.db

import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.model.users.UserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import javax.annotation.PostConstruct

@Repository
class UserRepository(
    private val mongo: ReactiveMongoOperations
) {
    @PostConstruct
    fun setup(): Unit = runBlocking {
        val indexOps = mongo.indexOps(UserEntity.COLLECTION_NAME)
        indexOps.ensureIndex(Index(UserEntity::email.name, Sort.Direction.ASC).unique().background()).awaitSingleOrNull()
    }

    fun getAllUsers(): Flow<UserEntity> {
        return mongo.findAll<UserEntity>().asFlow()
    }

    suspend fun insert(entity: UserEntity): UserEntity {
        return mongo.insert(entity).awaitSingle()
        // TODO wrap duplicate key exceptions
    }

    suspend fun findById(id: UserId): UserEntity? {
        val query = Query(
            UserEntity::id isEqualTo id.value
        )
        return mongo.findOne<UserEntity>(query).awaitSingleOrNull()
    }

    suspend fun findByEmail(email: Email): UserEntity? {
        val query = Query(
            UserEntity::email isEqualTo email.value.lowercase()
        )
        return mongo.findOne<UserEntity>(query).awaitSingleOrNull()
    }
}
