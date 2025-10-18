package com.fittracksa.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY badgeName ASC")
    fun observeAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AchievementEntity>)

    @Query("SELECT * FROM achievements WHERE id = :id")
    suspend fun findById(id: Long): AchievementEntity?

    @Update
    suspend fun update(achievement: AchievementEntity)
}
