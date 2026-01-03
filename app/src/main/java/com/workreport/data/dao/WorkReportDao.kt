package com.workreport.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.workreport.data.entity.WorkReport
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkReportDao {
    @Insert
    suspend fun insert(workReport: WorkReport)

    @Query("SELECT * FROM work_reports ORDER BY date DESC")
    fun getAllReports(): Flow<List<WorkReport>>

    @Query("SELECT * FROM work_reports WHERE id = :id")
    suspend fun getReportById(id: Long): WorkReport?
}
