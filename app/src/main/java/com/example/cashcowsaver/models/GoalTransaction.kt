package com.example.cashcowsaver.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal_transactions")
data class GoalTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val goalId: Int,
    val amount: Double,
    val date: String,
    val isAdd: Boolean // true = add, false = cash out
)
