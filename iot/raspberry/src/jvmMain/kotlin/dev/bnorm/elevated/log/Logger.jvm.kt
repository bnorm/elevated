package dev.bnorm.elevated.log

import org.slf4j.LoggerFactory

actual fun getLogger(name: String): Logger {
    return Slf4jLogger(LoggerFactory.getLogger(name))
}

private class Slf4jLogger(
    private val logger: org.slf4j.Logger,
) : Logger {
    override fun trace(t: Throwable?, msg: () -> String) {
        if (logger.isTraceEnabled) {
            if (t != null) logger.trace(msg(), t) else logger.trace(msg())
        }
    }

    override fun debug(t: Throwable?, msg: () -> String) {
        if (logger.isDebugEnabled) {
            if (t != null) logger.debug(msg(), t) else logger.debug(msg())
        }
    }

    override fun info(t: Throwable?, msg: () -> String) {
        if (logger.isInfoEnabled) {
            if (t != null) logger.info(msg(), t) else logger.info(msg())
        }
    }

    override fun warn(t: Throwable?, msg: () -> String) {
        if (logger.isWarnEnabled) {
            if (t != null) logger.warn(msg(), t) else logger.warn(msg())
        }
    }

    override fun error(t: Throwable?, msg: () -> String) {
        if (logger.isErrorEnabled) {
            if (t != null) logger.error(msg(), t) else logger.error(msg())
        }
    }
}
