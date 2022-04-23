package dev.bnorm.elevated.service.users.db

import dev.bnorm.elevated.model.auth.Role
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(UserEntity.COLLECTION_NAME)
class UserEntity(
    val email: String,
    val passwordHash: String,
    val name: String,
    val role: Role,
) {
    @Id
    lateinit var id: String

    companion object {
        const val COLLECTION_NAME = "users"
    }
}
