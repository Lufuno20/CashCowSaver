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

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = 'Income' ORDER BY date DESC")
    fun getIncomeTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = 'Expense' ORDER BY date DESC")
    fun getExpenseTransactions(): Flow<List<TransactionEntity>>

}

