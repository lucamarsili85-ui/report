package com.example.workreport.data.dao

import androidx.room.*
import com.example.workreport.data.entity.WorkReport
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for WorkReport entity.
 * 
 * Provides methods to interact with the work_reports table in the database.
 * Uses Flow for reactive data observation and suspend functions for async operations.
 */
@Dao
interface WorkReportDao {
    
    /**
     * Get all work reports ordered by date descending (newest first).
     * Returns a Flow that emits the list whenever the data changes.
     */
    @Query("SELECT * FROM work_reports ORDER BY date DESC")
    fun getAllReports(): Flow<List<WorkReport>>
    
    /**
     * Get all work reports within a date range.
     * 
     * @param startDate Start date timestamp (inclusive)
     * @param endDate End date timestamp (inclusive)
     * @return Flow of reports within the date range
     */
    @Query("SELECT * FROM work_reports WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getReportsByDateRange(startDate: Long, endDate: Long): Flow<List<WorkReport>>
    
    /**
     * Get a single work report by ID.
     * 
     * @param reportId The ID of the report to retrieve
     * @return Flow of the report, or null if not found
     */
    @Query("SELECT * FROM work_reports WHERE id = :reportId")
    fun getReportById(reportId: Long): Flow<WorkReport?>
    
    /**
     * Insert a new work report.
     * 
     * @param report The report to insert
     * @return The row ID of the newly inserted report
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: WorkReport): Long
    
    /**
     * Update an existing work report.
     * 
     * @param report The report to update
     * @return The number of rows updated (should be 1)
     */
    @Update
    suspend fun updateReport(report: WorkReport): Int
    
    /**
     * Delete a work report.
     * 
     * @param report The report to delete
     * @return The number of rows deleted (should be 1)
     */
    @Delete
    suspend fun deleteReport(report: WorkReport): Int
    
    /**
     * Delete all work reports (useful for testing or data reset).
     * 
     * @return The number of rows deleted
     */
    @Query("DELETE FROM work_reports")
    suspend fun deleteAllReports(): Int
    
    /**
     * Get total hours worked within a date range.
     * 
     * @param startDate Start date timestamp (inclusive)
     * @param endDate End date timestamp (inclusive)
     * @return Total hours worked
     */
    @Query("SELECT SUM(hoursWorked) FROM work_reports WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalHours(startDate: Long, endDate: Long): Double?
}
