package dev.bnorm.elevated.service

import org.slf4j.Logger

fun Logger.debug(throwable: Throwable? = null, message: () -> String) {
    if (isDebugEnabled) debug(message(), throwable)
}
