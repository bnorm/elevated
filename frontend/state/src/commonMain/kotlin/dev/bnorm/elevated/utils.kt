// TODO move to :common:utils?

package dev.bnorm.elevated

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun <T, R> Iterable<T>.asyncMap(transform: suspend (T) -> R): List<R> {
    val upstream = this
    return coroutineScope {
        upstream.map { async { transform(it) } }.awaitAll()
    }
}
