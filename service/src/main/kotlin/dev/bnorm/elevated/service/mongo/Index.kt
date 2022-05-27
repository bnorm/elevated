package dev.bnorm.elevated.service.mongo

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations

suspend fun ReactiveIndexOperations.ensureIndex(builder: Index.() -> Unit) {
    ensureIndex(Index().apply(builder)).awaitSingleOrNull()
}
