package dev.bnorm.elevated.service

import java.io.Closeable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ApplicationCoroutineScope : CoroutineScope by createAppScope(), Closeable {
    companion object {
        private val log = LoggerFactory.getLogger(ApplicationCoroutineScope::class.java)

        private fun createAppScope() = CoroutineScope(
            SupervisorJob() +
                Dispatchers.Default +
                CoroutineExceptionHandler { _, t ->
                    log.warn("marker=AppScope.Error message=\"Uncaught exception\"", t)
                }
        )
    }

    override fun close() {
        val job = this.coroutineContext[Job]!!
        runBlocking { job.cancelAndJoin() }
    }
}
