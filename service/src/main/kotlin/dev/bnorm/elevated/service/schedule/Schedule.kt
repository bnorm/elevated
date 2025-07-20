package dev.bnorm.elevated.service.schedule

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.until
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("dev.bnorm.elevated.schedule")

// TODO put in common module
fun CoroutineScope.schedule(
    name: String,
    frequency: Duration,
    immediate: Boolean = false,
    action: suspend () -> Unit,
) {
    suspend fun perform(timestamp: Instant) {
        try {
            log.debug("Performing scheduled action {}", name)
            action()
        } catch (t: Throwable) {
            if (t is CancellationException) throw t
            log.warn("Unable to perform scheduled action {} at {}", name, timestamp, t)
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
            log.debug("Waiting until {} to perform scheduled action {}", next, name)
            delay(now.until(next, DateTimeUnit.MILLISECOND))
            perform(next)
        }
    }
}
