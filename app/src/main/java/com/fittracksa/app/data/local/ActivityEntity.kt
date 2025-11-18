package com.fittracksa.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val durationMinutes: Int,
    val timestamp: Instant = Instant.now(),
    val isSynced: Boolean = false
)
