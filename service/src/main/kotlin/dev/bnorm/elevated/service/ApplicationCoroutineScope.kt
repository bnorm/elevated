package dev.bnorm.elevated.service

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.Closeable

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

    override fun close(): Unit = runBlocking {
        coroutineContext[Job]!!.cancelAndJoin()
    }
}
