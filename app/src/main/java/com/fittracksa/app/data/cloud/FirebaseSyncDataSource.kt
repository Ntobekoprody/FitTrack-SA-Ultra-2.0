package com.fittracksa.app.data.cloud

import com.fittracksa.app.data.local.ActivityEntity
import com.fittracksa.app.data.local.MealEntity
import com.fittracksa.app.data.preferences.SettingsRepository
import com.fittracksa.app.data.preferences.UserSettings
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class FirebaseSyncDataSource(
    private val firestore: FirebaseFirestore,
    private val settingsRepository: SettingsRepository
) {
    suspend fun upsertActivity(activity: ActivityEntity) {
        runCatching {
            val doc = userDocument().collection(ACTIVITIES_COLLECTION).document(activity.remoteId())
            doc.set(activity.toMap(), SetOptions.merge()).await()
        }
    }

    suspend fun upsertMeal(meal: MealEntity) {
        runCatching {
            val doc = userDocument().collection(MEALS_COLLECTION).document(meal.remoteId())
            doc.set(meal.toMap(), SetOptions.merge()).await()
        }
    }

    suspend fun deleteMeal(meal: MealEntity) {
        runCatching {
            userDocument().collection(MEALS_COLLECTION).document(meal.remoteId()).delete().await()
        }
    }

    suspend fun syncPending(
        activities: List<ActivityEntity>,
        meals: List<MealEntity>
    ) {
        if (activities.isEmpty() && meals.isEmpty()) return
        runCatching {
            val userDoc = userDocument()
            firestore.runBatch { batch ->
                activities.forEach { entity ->
                    val doc = userDoc.collection(ACTIVITIES_COLLECTION).document(entity.remoteId())
                    batch.set(doc, entity.toMap(), SetOptions.merge())
                }
                meals.forEach { meal ->
                    val doc = userDoc.collection(MEALS_COLLECTION).document(meal.remoteId())
                    batch.set(doc, meal.toMap(), SetOptions.merge())
                }
            }.await()
        }
    }

    private suspend fun userDocument(): DocumentReference =
        firestore.collection("users").document(resolveUserId())

    private suspend fun resolveUserId(): String {
        val email = settingsRepository.settings.first().email.ifBlank { UserSettings.DEFAULT_EMAIL }
        return email.lowercase()
    }

    private fun ActivityEntity.toMap(): Map<String, Any> = mapOf(
        "type" to type,
        "durationMinutes" to durationMinutes,
        "timestamp" to timestamp.toString()
    )

    private fun MealEntity.toMap(): Map<String, Any> = mapOf(
        "description" to description,
        "calories" to calories,
        "timestamp" to timestamp.toString()
    )

    private fun ActivityEntity.remoteId(): String = timestamp.toEpochMilli().toString()

    private fun MealEntity.remoteId(): String = timestamp.toEpochMilli().toString()

    companion object {
        private const val ACTIVITIES_COLLECTION = "activities"
        private const val MEALS_COLLECTION = "meals"
    }
}
