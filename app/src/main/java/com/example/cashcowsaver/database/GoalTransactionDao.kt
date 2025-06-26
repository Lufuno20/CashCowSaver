package com.example.cashcowsaver.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cashcowsaver.models.GoalTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalTransactionDao {

    @Insert
    suspend fun insert(transaction: GoalTransaction)

    @Query("SELECT * FROM goal_transactions WHERE goalId = :goalId ORDER BY id DESC")
    fun getTransactionsForGoal(goalId: Int): Flow<List<GoalTransaction>>
}

