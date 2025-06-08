package com.example.cashcowsaver.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cashcowsaver.models.TransactionEntity

@Database(entities = [TransactionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
