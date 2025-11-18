package com.fittracksa.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meals ORDER BY timestamp DESC")
    fun observeMeals(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE isSynced = 0")
    suspend fun getPendingMeals(): List<MealEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: MealEntity): Long

    @Update
    suspend fun update(meal: MealEntity)

    @Delete
    suspend fun delete(meal: MealEntity)

    @Query("UPDATE meals SET isSynced = :synced WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<Long>, synced: Boolean)
}
