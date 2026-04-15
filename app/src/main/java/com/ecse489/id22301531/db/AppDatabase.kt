package com.ecse489.id22301531.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ecse489.id22301531.model.Landmark
import com.ecse489.id22301531.model.Visit

@Database(entities = [Landmark::class, Visit::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun landmarkDao(): LandmarkDao
    abstract fun visitDao(): VisitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "landmark_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}