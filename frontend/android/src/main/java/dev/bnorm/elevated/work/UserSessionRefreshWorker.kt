package dev.bnorm.elevated.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.bnorm.elevated.state.auth.UserSession

class UserSessionRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userSession: UserSession,
) : CoroutineWorker(context, workerParams) {
    @AssistedFactory
    interface Factory : AbstractWorkerFactory<UserSessionRefreshWorker>

    override suspend fun doWork(): Result {
        return try {
            userSession.refresh()
            Log.i("WM-UserSession", "Refreshed user session")
            Result.success()
        } catch (t: Throwable) {
            Log.e("WM-UserSession", "Failed to refresh user session", t)
            Result.failure()
        }
    }
}
