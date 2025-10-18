package com.fittracksa.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val badgeName: String,
    val description: String,
    val joined: Boolean = false,
    val streakDays: Int = 0
)
