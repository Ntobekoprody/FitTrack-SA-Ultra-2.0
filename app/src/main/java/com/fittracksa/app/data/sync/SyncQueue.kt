package com.fittracksa.app.data.sync

import com.fittracksa.app.data.local.ActivityDao
import com.fittracksa.app.data.local.FitTrackDatabase
import com.fittracksa.app.data.local.MealDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncQueue(private val database: FitTrackDatabase) {
    suspend fun <T> enqueueActivity(operation: suspend ActivityDao.() -> T): T =
        withContext(Dispatchers.IO) { database.activityDao().operation() }

    suspend fun <T> enqueueMeal(operation: suspend MealDao.() -> T): T =
        withContext(Dispatchers.IO) { database.mealDao().operation() }

    suspend fun seedAchievements(achievements: List<com.fittracksa.app.data.local.AchievementEntity>) {
        withContext(Dispatchers.IO) {
            database.achievementDao().insertAll(achievements)
        }
    }
}
