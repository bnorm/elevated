package dev.bnorm.elevated.service

import dev.bnorm.elevated.service.auth.currentJwtToken
import dev.bnorm.elevated.service.auth.email
import dev.bnorm.elevated.service.auth.role
import kotlinx.coroutines.CancellationException
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
            jwt.role?.let { context["jwt.role"] = it.name }
            jwt.email?.let { context["jwt.email"] = it.value }
        }

        withContext(MDCContext(context)) {
            log.debug("marker=Http.Request.Start method={} path={}",
                exchange.request.methodValue,
                exchange.request.path)
            val start = Instant.now()
            try {
                proceed(exchange)
                log.debug("marker=Http.Request.Complete method={} path={} duration={} status={}",
                    exchange.request.methodValue, exchange.request.path, Duration.between(start, Instant.now()),
                    exchange.response.rawStatusCode)
            } catch (t: CancellationException) {
                log.debug("marker=Http.Request.Cancelled method={} path={} duration={}",
                    exchange.request.methodValue, exchange.request.path, Duration.between(start, Instant.now()))
                throw t
            } catch (t: Throwable) {
                log.debug("marker=Http.Request.Error method={} path={} duration={} status={}",
                    exchange.request.methodValue, exchange.request.path, Duration.between(start, Instant.now()),
                    exchange.response.rawStatusCode, t)
                throw t
            }
        }
    }

    private fun Random.randomRequestId(): String =
        nextUBytes(16).joinToString("") { it.toString(radix = 16).padStart(2, padChar = '0') }
}
