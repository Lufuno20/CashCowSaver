package com.example.cashcowsaver.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val description: String,
    val amount: Double,
    val date: String,
    val iconResId: Int,
    val type: String // "Income" or "Expense"
)


