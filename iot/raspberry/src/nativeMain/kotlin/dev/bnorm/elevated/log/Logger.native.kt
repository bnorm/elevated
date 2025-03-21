package dev.bnorm.elevated.log

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.*
import kotlinx.datetime.Clock

actual fun getLogger(name: String): Logger {
    return StdLogger(Clock.System, name)
}

@OptIn(ExperimentalForeignApi::class)
private class StdLogger(
    private val clock: Clock,
    private val name: String,
) : Logger {
    override fun trace(t: Throwable?, msg: () -> String) {
        stdout(t, msg)
    }

    override fun debug(t: Throwable?, msg: () -> String) {
        stdout(t, msg)
    }

    override fun info(t: Throwable?, msg: () -> String) {
        stdout(t, msg)
    }

    override fun warn(t: Throwable?, msg: () -> String) {
        stderr(t, msg)
    }

    override fun error(t: Throwable?, msg: () -> String) {
        stderr(t, msg)
    }

    private fun stdout(t: Throwable?, msg: () -> String) {
        fprintf(stdout, buildString(t, msg))
    }

    private fun stderr(t: Throwable?, msg: () -> String) {
        fprintf(stderr, buildString(t, msg))
    }

    private fun buildString(t: Throwable?, msg: () -> String): String = buildString {
        append(clock.now())
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
