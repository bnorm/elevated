package dev.bnorm.elevated.di

import androidx.work.ListenableWorker
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import dev.bnorm.elevated.work.AbstractWorkerFactory
import dev.bnorm.elevated.work.NotificationSyncWorker
import dev.bnorm.elevated.work.UserSessionRefreshWorker
import kotlin.reflect.KClass

abstract class WorkerScope private constructor()

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@Module
@ContributesTo(WorkerScope::class)
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
