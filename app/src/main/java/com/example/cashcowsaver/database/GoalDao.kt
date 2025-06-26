package com.example.cashcowsaver.database

import androidx.room.*
import com.example.cashcowsaver.models.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: GoalEntity)

    @Query("SELECT * FROM goals ORDER BY id DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT SUM(savedAmount) FROM goals")
    fun getTotalSaved(): Flow<Double>

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Query("DELETE FROM goals")
    suspend fun deleteAllGoals()

}

