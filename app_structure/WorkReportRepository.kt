package com.example.workreport.data.repository

import com.example.workreport.data.dao.WorkReportDao
import com.example.workreport.data.entity.WorkReport
import kotlinx.coroutines.flow.Flow

/**
 * Repository class for WorkReport.
 * 
 * This class abstracts access to the data layer and provides a clean API
 * for the ViewModel. It acts as a single source of truth for work report data.
 * 
 * In a more complex app, this repository might combine data from multiple sources
 * (e.g., local database + remote API), but for this app it only uses Room.
 */
class WorkReportRepository(private val workReportDao: WorkReportDao) {
    
    /**
     * Get all work reports as a Flow.
     * The Flow will emit new values whenever the data in the database changes.
     */
    val allReports: Flow<List<WorkReport>> = workReportDao.getAllReports()
    
    /**
     * Get reports within a specific date range.
     * 
     * @param startDate Start date timestamp (inclusive)
     * @param endDate End date timestamp (inclusive)
     * @return Flow of reports within the date range
     */
    fun getReportsByDateRange(startDate: Long, endDate: Long): Flow<List<WorkReport>> {
        return workReportDao.getReportsByDateRange(startDate, endDate)
    }
    
    /**
     * Get a single report by ID.
     * 
     * @param reportId The ID of the report
     * @return Flow of the report (null if not found)
     */
    fun getReportById(reportId: Long): Flow<WorkReport?> {
        return workReportDao.getReportById(reportId)
    }
    
    /**
     * Insert a new work report.
     * This is a suspend function and must be called from a coroutine.
     * 
     * @param report The report to insert
     * @return The row ID of the newly inserted report
     */
    suspend fun insertReport(report: WorkReport): Long {
        return workReportDao.insertReport(report)
    }
    
    /**
     * Update an existing work report.
     * 
     * @param report The report to update (with updated fields and timestamp)
     * @return The number of rows updated
     */
    suspend fun updateReport(report: WorkReport): Int {
        val updatedReport = report.copy(updatedAt = System.currentTimeMillis())
        return workReportDao.updateReport(updatedReport)
    }
    
    /**
     * Delete a work report.
     * 
     * @param report The report to delete
     * @return The number of rows deleted
     */
    suspend fun deleteReport(report: WorkReport): Int {
        return workReportDao.deleteReport(report)
    }
    
    /**
     * Delete all work reports.
     * 
     * @return The number of rows deleted
     */
    suspend fun deleteAllReports(): Int {
        return workReportDao.deleteAllReports()
    }
    
    /**
     * Calculate total hours worked within a date range.
     * 
     * @param startDate Start date timestamp (inclusive)
     * @param endDate End date timestamp (inclusive)
     * @return Total hours worked (or 0.0 if no reports found)
     */
    suspend fun getTotalHours(startDate: Long, endDate: Long): Double {
        return workReportDao.getTotalHours(startDate, endDate) ?: 0.0
    }
}
