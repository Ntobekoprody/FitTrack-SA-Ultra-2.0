package com.fittracksa.app.domain

import com.fittracksa.app.data.cloud.FirebaseSyncDataSource
import com.fittracksa.app.data.local.ActivityDao
import com.fittracksa.app.data.local.ActivityEntity
import com.fittracksa.app.data.local.AchievementDao
import com.fittracksa.app.data.local.AchievementEntity
import com.fittracksa.app.data.local.MealDao
import com.fittracksa.app.data.local.MealEntity
import com.fittracksa.app.data.sync.SyncQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Instant

class FitTrackRepositoryImpl(
    private val activityDao: ActivityDao,
    private val mealDao: MealDao,
    private val achievementDao: AchievementDao,
    private val syncQueue: SyncQueue,
    private val cloudSync: FirebaseSyncDataSource
) : FitTrackRepository {

    override val activities: Flow<List<ActivityEntity>> = activityDao.observeActivities()
    override val meals: Flow<List<MealEntity>> = mealDao.observeMeals()
    override val achievements: Flow<List<AchievementEntity>> = achievementDao.observeAchievements()

    override suspend fun logActivity(type: String, durationMinutes: Int) {
        val entity = ActivityEntity(
            type = type,
            durationMinutes = durationMinutes,
            timestamp = Instant.now(),
            isSynced = false
        )
        syncQueue.enqueueActivity { insert(entity) }
        cloudSync.upsertActivity(entity)
    }

    override suspend fun logMeal(description: String, calories: Int) {
        val entity = MealEntity(
            description = description,
            calories = calories,
            timestamp = Instant.now(),
            isSynced = false
        )
        syncQueue.enqueueMeal { insert(entity) }
        cloudSync.upsertMeal(entity)
    }

    override suspend fun updateMeal(meal: MealEntity) {
        val updated = meal.copy(isSynced = false)
        syncQueue.enqueueMeal { update(updated) }
        cloudSync.upsertMeal(updated)
    }

    override suspend fun deleteMeal(meal: MealEntity) {
        syncQueue.enqueueMeal { delete(meal) }
        cloudSync.deleteMeal(meal)
    }

    override suspend fun joinChallenge(id: Long) {
        withContext(Dispatchers.IO) {
            val achievement = achievementDao.findById(id) ?: return@withContext
            achievementDao.update(achievement.copy(joined = true))
        }
    }

    override suspend fun syncPending() {
        withContext(Dispatchers.IO) {
            val pendingActivities = activityDao.getPendingActivities()
            val pendingMeals = mealDao.getPendingMeals()
            if (pendingActivities.isNotEmpty() || pendingMeals.isNotEmpty()) {
                cloudSync.syncPending(pendingActivities, pendingMeals)
            }
            if (pendingActivities.isNotEmpty()) {
                activityDao.markSynced(pendingActivities.map(ActivityEntity::id), true)
            }
            if (pendingMeals.isNotEmpty()) {
                mealDao.markSynced(pendingMeals.map(MealEntity::id), true)
            }
        }
    }
}
