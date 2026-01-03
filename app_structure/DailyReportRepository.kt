package com.example.workreport.data.repository

import com.example.workreport.data.dao.DailyReportDao
import com.example.workreport.data.entity.ActivityEntity
import com.example.workreport.data.entity.ClientSectionEntity
import com.example.workreport.data.entity.DailyReportEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*

/**
 * Repository class for DailyReport.
 * 
 * This class abstracts access to the data layer and provides a clean API
 * for the ViewModel. It handles draft management, progressive saving,
 * and state transitions.
 */
class DailyReportRepository(private val dao: DailyReportDao) {
    
    /**
     * Get all daily reports as a Flow.
     */
    val allDailyReports: Flow<List<DailyReportEntity>> = dao.getAllDailyReports()
    
    /**
     * Get all finalized reports.
     */
    val finalizedReports: Flow<List<DailyReportEntity>> = dao.getFinalizedReports()
    
    /**
     * Get or create today's draft report.
     * 
     * If a draft exists for today, return it.
     * Otherwise, create a new draft report for today.
     * 
     * @return The ID of today's draft report
     */
    suspend fun getOrCreateTodaysDraft(): Long {
        val (todayStart, todayEnd) = getTodayBounds()
        val existingDraft = dao.getTodaysDraftReport(todayStart, todayEnd).first()
        
        return if (existingDraft != null) {
            existingDraft.id
        } else {
            // Create new draft for today
            val newDraft = DailyReportEntity(
                date = todayStart,
                status = DailyReportEntity.STATUS_DRAFT
            )
            dao.insertDailyReport(newDraft)
        }
    }
    
    /**
     * Get today's draft report as a Flow.
     */
    fun getTodaysDraftReport(): Flow<DailyReportEntity?> {
        val (todayStart, todayEnd) = getTodayBounds()
        return dao.getTodaysDraftReport(todayStart, todayEnd)
    }
    
    /**
     * Get a daily report by ID.
     */
    fun getDailyReportById(reportId: Long): Flow<DailyReportEntity?> {
        return dao.getDailyReportById(reportId)
    }
    
    /**
     * Get client sections for a daily report.
     */
    fun getClientSectionsForReport(reportId: Long): Flow<List<ClientSectionEntity>> {
        return dao.getClientSectionsForReport(reportId)
    }
    
    /**
     * Get activities for a client section.
     */
    fun getActivitiesForClientSection(clientSectionId: Long): Flow<List<ActivityEntity>> {
        return dao.getActivitiesForClientSection(clientSectionId)
    }
    
    /**
     * Add a new client section to a daily report.
     * This is a progressive save operation.
     */
    suspend fun addClientSection(
        dailyReportId: Long,
        clientName: String,
        jobSite: String,
        colorClass: String = "color-1"
    ): Long {
        val clientSection = ClientSectionEntity(
            dailyReportId = dailyReportId,
            clientName = clientName,
            jobSite = jobSite,
            colorClass = colorClass
        )
        return dao.insertClientSection(clientSection)
    }
    
    /**
     * Delete a client section.
     */
    suspend fun deleteClientSection(clientSection: ClientSectionEntity) {
        dao.deleteClientSection(clientSection)
    }
    
    /**
     * Add a machine activity to a client section.
     * This is a progressive save operation.
     */
    suspend fun addMachineActivity(
        clientSectionId: Long,
        machine: String,
        hours: Double,
        description: String = ""
    ): Long {
        val activity = ActivityEntity(
            clientSectionId = clientSectionId,
            activityType = ActivityEntity.TYPE_MACHINE,
            machine = machine,
            hours = hours,
            description = description
        )
        return dao.insertActivity(activity)
    }
    
    /**
     * Add a material activity to a client section.
     * This is a progressive save operation.
     */
    suspend fun addMaterialActivity(
        clientSectionId: Long,
        materialName: String,
        quantity: Double,
        unit: String,
        notes: String = ""
    ): Long {
        val activity = ActivityEntity(
            clientSectionId = clientSectionId,
            activityType = ActivityEntity.TYPE_MATERIAL,
            materialName = materialName,
            quantity = quantity,
            unit = unit,
            notes = notes
        )
        return dao.insertActivity(activity)
    }
    
    /**
     * Delete an activity.
     */
    suspend fun deleteActivity(activity: ActivityEntity) {
        dao.deleteActivity(activity)
    }
    
    /**
     * Finalize a daily report.
     * 
     * Transitions the report from DRAFT to FINAL state.
     * Calculates total hours and sets finalized timestamp.
     */
    suspend fun finalizeDailyReport(reportId: Long) {
        val report = dao.getDailyReportById(reportId).first() ?: return
        val totalHours = dao.calculateTotalHours(reportId)
        
        val finalizedReport = report.copy(
            status = DailyReportEntity.STATUS_FINAL,
            totalHours = totalHours,
            finalizedAt = System.currentTimeMillis()
        )
        dao.updateDailyReport(finalizedReport)
    }
    
    /**
     * Reopen a finalized report as draft.
     * 
     * Transitions the report from FINAL back to DRAFT state.
     */
    suspend fun reopenAsDraft(reportId: Long) {
        val report = dao.getDailyReportById(reportId).first() ?: return
        
        val draftReport = report.copy(
            status = DailyReportEntity.STATUS_DRAFT,
            finalizedAt = null
        )
        dao.updateDailyReport(draftReport)
    }
    
    /**
     * Update trasferta status.
     */
    suspend fun updateTrasferta(reportId: Long, trasferta: Boolean) {
        val report = dao.getDailyReportById(reportId).first() ?: return
        dao.updateDailyReport(report.copy(trasferta = trasferta))
    }
    
    /**
     * Calculate total hours for a daily report.
     */
    suspend fun calculateTotalHours(reportId: Long): Double {
        return dao.calculateTotalHours(reportId)
    }
    
    /**
     * Helper function to get today's date bounds (start and end of day).
     */
    private fun getTodayBounds(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayStart = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val todayEnd = calendar.timeInMillis
        
        return Pair(todayStart, todayEnd)
    }
}
