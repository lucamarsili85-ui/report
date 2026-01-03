package com.example.workreport.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.workreport.data.dao.WorkReportDao
import com.example.workreport.data.entity.WorkReport

/**
 * Room database for the Work Report application.
 * 
 * This is the main database configuration. It defines the entities and version,
 * and provides access to the DAOs.
 */
@Database(
    entities = [WorkReport::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Provides access to WorkReport DAO
     */
    abstract fun workReportDao(): WorkReportDao
    
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
