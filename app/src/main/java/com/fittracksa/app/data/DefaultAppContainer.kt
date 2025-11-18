package com.fittracksa.app.data

import android.content.Context
import androidx.room.Room
import com.fittracksa.app.data.cloud.FirebaseSyncDataSource
import com.fittracksa.app.data.local.AchievementEntity
import com.fittracksa.app.data.local.FitTrackDatabase
import com.fittracksa.app.data.preferences.SettingsRepository
import com.fittracksa.app.data.preferences.SettingsRepositoryImpl
import com.fittracksa.app.data.sync.SyncQueue
import com.fittracksa.app.domain.FitTrackRepository
import com.fittracksa.app.domain.FitTrackRepositoryImpl
import com.fittracksa.app.notifications.FitTrackNotifier
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DefaultAppContainer(context: Context) : AppContainer {
    private val database: FitTrackDatabase = Room.databaseBuilder(
        context,
        FitTrackDatabase::class.java,
        "fittrack.db"
    ).fallbackToDestructiveMigration().build()

    private val syncQueue = SyncQueue(database)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            syncQueue.seedAchievements(
                listOf(
                    AchievementEntity(badgeName = "Consistency Champ", description = "Complete 5 workouts", streakDays = 5),
                    AchievementEntity(badgeName = "Nutrition Ninja", description = "Log meals 7 days", streakDays = 7),
                    AchievementEntity(badgeName = "Streak Star", description = "Hit 14-day streak", streakDays = 14)
                )
            )
        }
    }

    private val settingsRepo: SettingsRepository by lazy {
        SettingsRepositoryImpl(context)
    }

    private val cloudSync by lazy {
        FirebaseSyncDataSource(
            firestore = Firebase.firestore,
            settingsRepository = settingsRepo
        )
    }

    override val repository: FitTrackRepository by lazy {
        FitTrackRepositoryImpl(
            activityDao = database.activityDao(),
            mealDao = database.mealDao(),
            achievementDao = database.achievementDao(),
            syncQueue = syncQueue,
            cloudSync = cloudSync
        )
    }

    override val settingsRepository: SettingsRepository
        get() = settingsRepo

    override val notifier: FitTrackNotifier by lazy {
        FitTrackNotifier(context, settingsRepo)
    }
}
