package com.fittracksa.app.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fittracksa.app.data.DefaultAppContainer

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    private val repository = DefaultAppContainer(context).repository

    override suspend fun doWork(): Result {
        return try {
            repository.syncPending()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
