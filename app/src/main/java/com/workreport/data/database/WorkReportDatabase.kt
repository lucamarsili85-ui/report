package com.workreport.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.workreport.data.dao.WorkReportDao
import com.workreport.data.entity.WorkReport

@Database(entities = [WorkReport::class], version = 1, exportSchema = false)
abstract class WorkReportDatabase : RoomDatabase() {
    abstract fun workReportDao(): WorkReportDao

    companion object {
        @Volatile
        private var INSTANCE: WorkReportDatabase? = null

        fun getDatabase(context: Context): WorkReportDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkReportDatabase::class.java,
                    "work_report_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
