package com.example.cashcowsaver.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // should be "Income" or "Expense"
    val category: String,
    val description: String,
    val date: String,
    val amount: Double,
    val iconResId: Int
)



