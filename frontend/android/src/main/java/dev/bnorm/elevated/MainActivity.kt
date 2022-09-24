package dev.bnorm.elevated

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.android.AndroidInjection
import dev.bnorm.elevated.ui.MainComponent
import dev.bnorm.elevated.work.NotificationSyncWorker
import dev.bnorm.elevated.work.UserSessionRefreshWorker
import dev.bnorm.elevated.work.constraints
import java.time.Duration
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var component: MainComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContent { component.render() }

        val workManager = WorkManager.getInstance(this)

        workManager.enqueueUniquePeriodicWork(
            "NotificationSync",
            ExistingPeriodicWorkPolicy.REPLACE,
            PeriodicWorkRequestBuilder<NotificationSyncWorker>(
                repeatInterval = Duration.ofMinutes(15),
                flexTimeInterval = Duration.ofMinutes(5),
            ).apply {
                constraints {
                    setRequiredNetworkType(NetworkType.CONNECTED)
                }
            }.build(),
        )

        workManager.enqueueUniquePeriodicWork(
            "UserSessionRefresh",
            ExistingPeriodicWorkPolicy.REPLACE,
            PeriodicWorkRequestBuilder<UserSessionRefreshWorker>(
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

