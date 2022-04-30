package dev.bnorm.elevated.service

import dev.bnorm.elevated.service.auth.currentJwtToken
import dev.bnorm.elevated.service.auth.email
import dev.bnorm.elevated.service.auth.role
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import java.time.Duration
import java.time.Instant
import kotlin.random.Random
import kotlin.random.nextUBytes

@Component
class CorrelationFilter : CoroutineWebFilter() {
    private val log = LoggerFactory.getLogger(CorrelationFilter::class.java)

    override suspend fun filter(exchange: ServerWebExchange, proceed: suspend (exchange: ServerWebExchange) -> Unit) {
        val context = MDC.getCopyOfContextMap() ?: mutableMapOf()

        val xRequestId = exchange.request.headers.getFirst("X-Request-ID") ?: Random.randomRequestId()
        context["request.id"] = xRequestId

        val jwt = currentJwtToken()
        if (jwt != null) {
            context["jwt.sub"] = jwt.subject
            jwt.role?.let { context["jwt.role"] = it }
            jwt.email?.let { context["jwt.email"] = it }
        }

        withContext(MDCContext(context)) {
            log.debug("marker=Http.Request.Start uri={}", exchange.request.path)
            val start = Instant.now()
            try {
                proceed(exchange)
                log.debug("marker=Http.Request.Complete uri={} status={} duration={}",
                    exchange.request.path, exchange.response.rawStatusCode, Duration.between(start, Instant.now()))
            } catch (t: Throwable) {
                log.debug("marker=Http.Request.Error uri={} status={} duration={}",
                    exchange.request.path, exchange.response.rawStatusCode, Duration.between(start, Instant.now()), t)
                throw t
            }
        }
    }

    private fun Random.randomRequestId(): String =
        nextUBytes(16).joinToString("") { it.toString(radix = 16).padStart(2, padChar = '0') }
}
