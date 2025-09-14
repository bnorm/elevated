package dev.bnorm.elevated.log

import kotlin.time.Clock
import platform.posix.fflush
import platform.posix.fprintf
import platform.posix.stdout

actual fun getLogger(name: String): Logger {
    return StdLogger(Clock.System, name)
}

private class StdLogger(
    private val clock: Clock,
    private val name: String,
) : Logger {
    override fun trace(t: Throwable?, msg: () -> String) {
        stdout(t, "TRACE", msg)
    }

    override fun debug(t: Throwable?, msg: () -> String) {
        stdout(t, "DEBUG", msg)
    }

    override fun info(t: Throwable?, msg: () -> String) {
        stdout(t, "INFO", msg)
    }

    override fun warn(t: Throwable?, msg: () -> String) {
        stdout(t, "WARN", msg)
    }

    override fun error(t: Throwable?, msg: () -> String) {
        stdout(t, "ERROR", msg)
    }

    private fun stdout(t: Throwable?, level: String, msg: () -> String) {
        fprintf(stdout, buildString(t, level, msg))
        fflush(stdout)
    }

    private fun buildString(t: Throwable?, level: String, msg: () -> String): String = buildString {
        append(clock.now())
        append(" ")
        append(level)
        append(" ")
        append(name)
        append(" ")
        append(msg())
        if (t != null) {
            appendLine()
            append(t.stackTraceToString())
        }
        appendLine()
    }
}
