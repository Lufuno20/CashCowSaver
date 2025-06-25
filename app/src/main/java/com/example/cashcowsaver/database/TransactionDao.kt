package com.example.cashcowsaver.database


import androidx.room.*
import com.example.cashcowsaver.models.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("SELECT * FROM transactions")
    fun getAll(): Flow<List<TransactionEntity>>
}

