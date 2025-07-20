package dev.bnorm.elevated.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlin.reflect.KClass

@SingleIn(AppScope::class)
@Inject
class ElevatedWorkerFactory(
    private val workerFactories: Map<KClass<out ListenableWorker>, AbstractWorkerFactory<*>>
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val forName = Class.forName(workerClassName)
        val foundEntry = workerFactories.entries.find { forName.isAssignableFrom(it.key.java) }
        val factoryProvider = foundEntry?.value ?: return null
        return factoryProvider.create(appContext, workerParameters)
    }
}
