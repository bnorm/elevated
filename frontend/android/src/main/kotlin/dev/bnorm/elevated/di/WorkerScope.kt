package dev.bnorm.elevated.di

import androidx.work.ListenableWorker
import dev.bnorm.elevated.work.AbstractWorkerFactory
import dev.bnorm.elevated.work.NotificationSyncWorker
import dev.bnorm.elevated.work.UserSessionRefreshWorker
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@ContributesTo(AppScope::class)
interface WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(NotificationSyncWorker::class)
    fun bindNotificationSyncWorker(factory: NotificationSyncWorker.Factory): AbstractWorkerFactory<*>

    @Binds
    @IntoMap
    @WorkerKey(UserSessionRefreshWorker::class)
    fun bindUserSessionRefreshWorker(factory: UserSessionRefreshWorker.Factory): AbstractWorkerFactory<*>
}
