package com.example.cashcowsaver.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val categoryName: String,
    val iconResId: Int,
    val colorResId: Int,
    val date: String,
   // val status: String
)