package com.fittracksa.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val calories: Int,
    val timestamp: Instant = Instant.now(),
    val isSynced: Boolean = false
)
