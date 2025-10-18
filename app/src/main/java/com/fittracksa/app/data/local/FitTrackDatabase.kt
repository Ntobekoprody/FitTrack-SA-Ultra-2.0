package com.fittracksa.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ActivityEntity::class, MealEntity::class, AchievementEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FitTrackDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun mealDao(): MealDao
    abstract fun achievementDao(): AchievementDao
}
