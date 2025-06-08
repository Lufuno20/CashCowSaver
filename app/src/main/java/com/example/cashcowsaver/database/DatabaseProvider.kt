package com.example.cashcowsaver.database

import android.content.Context
import androidx.room.Room
import com.example.cashcowsaver.database.AppDatabase

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "transaction_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
