package dev.bnorm.elevated.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface AbstractWorkerFactory<W : ListenableWorker> {
    fun create(appContext: Context, params: WorkerParameters): W
}
