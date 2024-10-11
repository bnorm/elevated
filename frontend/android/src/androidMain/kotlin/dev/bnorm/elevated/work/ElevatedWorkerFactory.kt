package dev.bnorm.elevated.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ElevatedWorkerFactory @Inject constructor(
    private val workerFactories: Map<Class<out ListenableWorker>, @JvmSuppressWildcards AbstractWorkerFactory<*>>
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val forName = Class.forName(workerClassName)
        val foundEntry = workerFactories.entries.find { forName.isAssignableFrom(it.key) }
        val factoryProvider = foundEntry?.value ?: return null
        return factoryProvider.create(appContext, workerParameters)
    }
}
