package com.fittracksa.app.domain

import com.fittracksa.app.data.local.ActivityDao
import com.fittracksa.app.data.local.ActivityEntity
import com.fittracksa.app.data.local.AchievementDao
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
    private val syncQueue: SyncQueue
) : FitTrackRepository {

    override val activities: Flow<List<ActivityEntity>> = activityDao.observeActivities()
    override val meals: Flow<List<MealEntity>> = mealDao.observeMeals()
    override val achievements: Flow<List<AchievementEntity>> = achievementDao.observeAchievements()

    override suspend fun logActivity(type: String, durationMinutes: Int) {
        syncQueue.enqueueActivity {
            insert(
                ActivityEntity(
                    type = type,
                    durationMinutes = durationMinutes,
                    timestamp = Instant.now(),
                    isSynced = false
                )
            )
        }
    }

    override suspend fun logMeal(description: String, calories: Int) {
        syncQueue.enqueueMeal {
            insert(
                MealEntity(
                    description = description,
                    calories = calories,
                    timestamp = Instant.now(),
                    isSynced = false
                )
            )
        }
    }

    override suspend fun updateMeal(meal: MealEntity) {
        syncQueue.enqueueMeal { update(meal.copy(isSynced = false)) }
    }

    override suspend fun deleteMeal(meal: MealEntity) {
        syncQueue.enqueueMeal { delete(meal) }
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

            if (pendingActivities.isNotEmpty()) {
                activityDao.markSynced(pendingActivities.map(ActivityEntity::id), true)
            }
            if (pendingMeals.isNotEmpty()) {
                mealDao.markSynced(pendingMeals.map(MealEntity::id), true)
            }
        }
    }
}
