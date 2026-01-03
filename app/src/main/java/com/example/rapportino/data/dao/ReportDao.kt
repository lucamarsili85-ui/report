package com.example.rapportino.data.dao

import androidx.room.*
import com.example.rapportino.data.entity.Report
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY date DESC, createdAt DESC")
    fun getAllReports(): Flow<List<Report>>

    @Query("SELECT * FROM reports WHERE id = :id")
    suspend fun getReportById(id: Long): Report?

    @Insert
    suspend fun insertReport(report: Report): Long

    @Update
    suspend fun updateReport(report: Report)

    @Delete
    suspend fun deleteReport(report: Report)

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteReportById(id: Long)
}
