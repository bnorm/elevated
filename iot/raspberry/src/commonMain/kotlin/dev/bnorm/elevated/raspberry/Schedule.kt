package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.log.getLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.until
import kotlin.time.Duration

private val log = getLogger("dev.bnorm.elevated.schedule")

fun CoroutineScope.schedule(
    name: String,
    frequency: Duration,
    immediate: Boolean = false,
    action: suspend () -> Unit,
) {
    suspend fun perform(timestamp: Instant) {
        try {
            log.debug { "Performing scheduled action $name" }
            action()
        } catch (t: Throwable) {
            if (t is CancellationException) throw t
            log.warn(t) { "Unable to perform scheduled action $name at $timestamp" }
        }
    }

    launch(start = if (immediate) CoroutineStart.UNDISPATCHED else CoroutineStart.DEFAULT) {
        val start = Clock.System.now()
        val truncated = (start.toEpochMilliseconds() / frequency.inWholeMilliseconds) * frequency.inWholeMilliseconds
        var next = Instant.fromEpochMilliseconds(truncated)

        if (immediate) {
            perform(start)
        }

        while (isActive) {
            val now = Clock.System.now()
            while (next < now) next += frequency
            log.debug { "Waiting until $next to perform scheduled action $name" }
            delay(now.until(next, DateTimeUnit.MILLISECOND))
            perform(next)
        }
    }
}
