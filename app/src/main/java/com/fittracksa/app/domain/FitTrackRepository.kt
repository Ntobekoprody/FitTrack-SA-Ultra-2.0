package com.fittracksa.app.domain

import com.fittracksa.app.data.local.ActivityEntity
import com.fittracksa.app.data.local.AchievementEntity
import com.fittracksa.app.data.local.MealEntity
import kotlinx.coroutines.flow.Flow

interface FitTrackRepository {
    val activities: Flow<List<ActivityEntity>>
    val meals: Flow<List<MealEntity>>
    val achievements: Flow<List<AchievementEntity>>

    suspend fun logActivity(type: String, durationMinutes: Int)
    suspend fun logMeal(description: String, calories: Int)
    suspend fun updateMeal(meal: MealEntity)
    suspend fun deleteMeal(meal: MealEntity)
    suspend fun joinChallenge(id: Long)
    suspend fun syncPending()
}
