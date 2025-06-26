package com.example.cashcowsaver.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cashcowsaver.models.GoalEntity
import com.example.cashcowsaver.database.GoalDao
import com.example.cashcowsaver.models.GoalTransaction


@Database(
    entities = [GoalEntity::class, GoalTransaction::class], // ✅ include both
    version = 3,
    exportSchema = false
)
abstract class GoalAppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun goalTransactionDao(): GoalTransactionDao

    companion object {
        @Volatile
        private var INSTANCE: GoalAppDatabase? = null

        fun getDatabase(context: Context): GoalAppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    GoalAppDatabase::class.java,
                    "goal_db"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
