package com.example.workreport.data.dao

import androidx.room.*
import com.example.workreport.data.entity.ActivityEntity
import com.example.workreport.data.entity.ClientSectionEntity
import com.example.workreport.data.entity.DailyReportEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for DailyReport entity.
 * 
 * Provides methods to interact with the daily_reports, client_sections, and activities tables.
 * Uses Flow for reactive data observation and suspend functions for async operations.
 */
@Dao
interface DailyReportDao {
    
    // ==================== DailyReport Queries ====================
    
    /**
     * Get all daily reports ordered by date descending (newest first).
     */
    @Query("SELECT * FROM daily_reports ORDER BY date DESC")
    fun getAllDailyReports(): Flow<List<DailyReportEntity>>
    
    /**
     * Get today's DRAFT report if it exists.
     * 
     * @param todayStart Start of today (timestamp in milliseconds)
     * @param todayEnd End of today (timestamp in milliseconds)
     * @return Flow of the draft report, or null if none exists
     */
    @Query("""
        SELECT * FROM daily_reports 
        WHERE date >= :todayStart AND date < :todayEnd AND status = 'DRAFT'
        LIMIT 1
    """)
    fun getTodaysDraftReport(todayStart: Long, todayEnd: Long): Flow<DailyReportEntity?>
    
    /**
     * Get a daily report by ID.
     */
    @Query("SELECT * FROM daily_reports WHERE id = :reportId")
    fun getDailyReportById(reportId: Long): Flow<DailyReportEntity?>
    
    /**
     * Get all finalized reports.
     */
    @Query("SELECT * FROM daily_reports WHERE status = 'FINAL' ORDER BY date DESC")
    fun getFinalizedReports(): Flow<List<DailyReportEntity>>
    
    /**
     * Insert a new daily report.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyReport(report: DailyReportEntity): Long
    
    /**
     * Update an existing daily report.
     */
    @Update
    suspend fun updateDailyReport(report: DailyReportEntity)
    
    /**
     * Delete a daily report.
     */
    @Delete
    suspend fun deleteDailyReport(report: DailyReportEntity)
    
    // ==================== ClientSection Queries ====================
    
    /**
     * Get all client sections for a daily report.
     */
    @Query("SELECT * FROM client_sections WHERE dailyReportId = :dailyReportId ORDER BY createdAt ASC")
    fun getClientSectionsForReport(dailyReportId: Long): Flow<List<ClientSectionEntity>>
    
    /**
     * Get a client section by ID.
     */
    @Query("SELECT * FROM client_sections WHERE id = :clientSectionId")
    suspend fun getClientSectionById(clientSectionId: Long): ClientSectionEntity?
    
    /**
     * Insert a new client section.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClientSection(clientSection: ClientSectionEntity): Long
    
    /**
     * Update a client section.
     */
    @Update
    suspend fun updateClientSection(clientSection: ClientSectionEntity)
    
    /**
     * Delete a client section.
     */
    @Delete
    suspend fun deleteClientSection(clientSection: ClientSectionEntity)
    
    // ==================== Activity Queries ====================
    
    /**
     * Get all activities for a client section.
     */
    @Query("SELECT * FROM activities WHERE clientSectionId = :clientSectionId ORDER BY createdAt ASC")
    fun getActivitiesForClientSection(clientSectionId: Long): Flow<List<ActivityEntity>>
    
    /**
     * Get an activity by ID.
     */
    @Query("SELECT * FROM activities WHERE id = :activityId")
    suspend fun getActivityById(activityId: Long): ActivityEntity?
    
    /**
     * Insert a new activity.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity): Long
    
    /**
     * Update an activity.
     */
    @Update
    suspend fun updateActivity(activity: ActivityEntity)
    
    /**
     * Delete an activity.
     */
    @Delete
    suspend fun deleteActivity(activity: ActivityEntity)
    
    /**
     * Calculate total hours for a daily report by summing all machine activities.
     */
    @Query("""
        SELECT COALESCE(SUM(a.hours), 0)
        FROM activities a
        INNER JOIN client_sections cs ON a.clientSectionId = cs.id
        WHERE cs.dailyReportId = :dailyReportId AND a.activityType = 'MACHINE'
    """)
    suspend fun calculateTotalHours(dailyReportId: Long): Double
}
