package com.example.workreport.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.workreport.data.dao.DailyReportDao
import com.example.workreport.data.dao.WorkReportDao
import com.example.workreport.data.entity.ActivityEntity
import com.example.workreport.data.entity.ClientSectionEntity
import com.example.workreport.data.entity.DailyReportEntity
import com.example.workreport.data.entity.WorkReport

/**
 * Room database for the Work Report application.
 * 
 * This is the main database configuration. It defines the entities and version,
 * and provides access to the DAOs.
 * 
 * Version 2 adds support for DailyReport with multiple clients and activities.
 */
@Database(
    entities = [
        WorkReport::class,
        DailyReportEntity::class,
        ClientSectionEntity::class,
        ActivityEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Provides access to WorkReport DAO (legacy)
     */
    abstract fun workReportDao(): WorkReportDao
    
    /**
     * Provides access to DailyReport DAO
     */
    abstract fun dailyReportDao(): DailyReportDao
    
    companion object {
        // Singleton prevents multiple instances of database opening at the same time
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Gets the singleton instance of AppDatabase.
         * 
         * If the instance doesn't exist, it creates a new one using Room.databaseBuilder.
         * The database file is named "work_report_database".
         * 
         * @param context Application context
         * @return Singleton instance of AppDatabase
         */
        fun getDatabase(context: Context): AppDatabase {
            // If INSTANCE is not null, return it; otherwise create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "work_report_database"
                )
                    // Migration strategy: destructive migration for development
                    // In production, you should provide proper migration paths
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
