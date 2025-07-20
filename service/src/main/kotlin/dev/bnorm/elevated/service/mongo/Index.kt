package dev.bnorm.elevated.service.mongo

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations

suspend fun ReactiveIndexOperations.createIndex(builder: Index.() -> Unit) {
    createIndex(Index().apply(builder)).awaitSingleOrNull()
}
