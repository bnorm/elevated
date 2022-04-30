package dev.bnorm.elevated.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

abstract class CoroutineWebFilter : WebFilter {
    final override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return mono(Dispatchers.Unconfined) {
            filter(exchange) {
                chain.filter(it).awaitSingleOrNull()
            }
        }.then()
    }

    abstract suspend fun filter(exchange: ServerWebExchange, proceed: suspend (exchange: ServerWebExchange) -> Unit)
}
