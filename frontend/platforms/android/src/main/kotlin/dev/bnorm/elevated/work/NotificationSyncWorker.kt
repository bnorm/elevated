package dev.bnorm.elevated.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.bnorm.elevated.state.NotificationManager
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class NotificationSyncWorker(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val notificationManager: NotificationManager,
) : CoroutineWorker(appContext, params) {
    @AssistedFactory
    interface Factory : AbstractWorkerFactory<NotificationSyncWorker>

    override suspend fun doWork(): Result {
        return try {
            val newNotifications = notificationManager.updateNotifications()
            Log.i("WM-NotificationSync", "Loaded ${newNotifications.size} new notifications")

//            val notificationManager = NotificationManagerCompat.from(applicationContext)
//            for (notification in newNotifications) {
////            val snoozeIntent = Intent(this, MyBroadcastReceiver::class.java).apply {
////                action = ACTION_SNOOZE
////                putExtra(EXTRA_NOTIFICATION_ID, 0)
////            }
////            val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, snoozeIntent, 0)
//                val builder = NotificationCompat.Builder(applicationContext, "ELEVATED_NOTIFICATIONS")
////                    .setSmallIcon(R.drawable.notification_icon)
//                    .setContentTitle("Elevated Notification")
//                    .setContentText(notification.message)
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
////                    .setContentIntent(pendingIntent)
////                    .addAction(
////                        R.drawable.ic_snooze, getString(R.string.snooze),
////                        snoozePendingIntent
////                    )
//                    .build()
//                notificationManager.notify(notification.id.hashCode(), builder)
//            }

            Result.success()
        } catch (t: Throwable) {
            Log.e("WM-NotificationSync", "Failed to sync notifications", t)
            Result.failure()
        }
    }
}
