package com.example.cashcowsaver.database

import androidx.room.*
import com.example.cashcowsaver.models.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert
    suspend fun insert(goal: GoalEntity)

    @Query("SELECT * FROM goals")
    fun getAllGoals(): Flow<List<GoalEntity>>
}