package com.example.cashcowsaver.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cashcowsaver.models.TransactionEntity


@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAll(): List<TransactionEntity>
}