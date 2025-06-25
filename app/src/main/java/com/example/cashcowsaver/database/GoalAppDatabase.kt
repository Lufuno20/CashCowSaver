package com.example.cashcowsaver.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cashcowsaver.models.GoalEntity

@Database(entities = [GoalEntity::class], version = 1, exportSchema = false)
abstract class GoalAppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile private var INSTANCE: GoalAppDatabase? = null

        fun getDatabase(context: Context): GoalAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GoalAppDatabase::class.java,
                    "goal_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
