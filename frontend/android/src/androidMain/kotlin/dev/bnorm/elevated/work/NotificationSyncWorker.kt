package dev.bnorm.elevated.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.bnorm.elevated.state.NotificationManager

class NotificationSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationManager: NotificationManager,
) : CoroutineWorker(context, workerParams) {
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
