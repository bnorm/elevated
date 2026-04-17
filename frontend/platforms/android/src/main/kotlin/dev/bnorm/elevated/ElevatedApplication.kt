package dev.bnorm.elevated

import android.app.Application
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.bnorm.elevated.di.ElevatedAppGraph
import dev.bnorm.elevated.state.SharedPreferenceTokenStore
import dev.bnorm.elevated.work.NotificationSyncWorker
import dev.bnorm.elevated.work.UserSessionRefreshWorker
import dev.bnorm.elevated.work.constraints
import dev.zacsweers.metro.createGraphFactory
import java.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ElevatedApplication : Application() {
    lateinit var graph: ElevatedAppGraph

    override fun onCreate() {
        graph = createGraphFactory<ElevatedAppGraph.Factory>().create(
            tokenStore = SharedPreferenceTokenStore(getSharedPreferences("KEYS", MODE_PRIVATE)),
            viewModelCoroutineScope = CoroutineScope(SupervisorJob() + AndroidUiDispatcher.Main),
        )

        super.onCreate()

        val workManagerConfiguration = Configuration.Builder()
            .setWorkerFactory(graph.workerFactory)
            .build()
        WorkManager.initialize(this, workManagerConfiguration)

        val workManager = WorkManager.getInstance(this)

        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = "NotificationSync",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
            request = PeriodicWorkRequestBuilder<NotificationSyncWorker>(
                repeatInterval = Duration.ofMinutes(15),
                flexTimeInterval = Duration.ofMinutes(5),
            ).apply {
                constraints {
                    setRequiredNetworkType(NetworkType.CONNECTED)
                }
            }.build(),
        )

        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = "UserSessionRefresh",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
            request = PeriodicWorkRequestBuilder<UserSessionRefreshWorker>(
                repeatInterval = Duration.ofHours(1),
                flexTimeInterval = Duration.ofHours(1),
            ).apply {
                constraints {
                    setRequiredNetworkType(NetworkType.CONNECTED)
                }
            }.build(),
        )
    }
}
