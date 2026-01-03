package com.example.workreport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DailyReportEntity representing a daily work journal.
 * 
 * This entity is used by Room to create the daily_reports table in the database.
 * Each instance represents a single day's work journal that can contain multiple clients.
 * The journal can be in DRAFT state (editable) or FINAL state (locked).
 */
@Entity(tableName = "daily_reports")
data class DailyReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Date of the work (stored as timestamp in milliseconds, normalized to start of day)
     */
    val date: Long,
    
    /**
     * Status of the report: "DRAFT" or "FINAL"
     * DRAFT reports can be edited, FINAL reports are locked
     */
    val status: String = STATUS_DRAFT,
    
    /**
     * Total hours calculated when the report is finalized
     */
    val totalHours: Double = 0.0,
    
    /**
     * Whether the worker was on trasferta (away from home base)
     */
    val trasferta: Boolean = false,
    
    /**
     * Timestamp when the report was created (milliseconds)
     */
    val createdAt: Long = System.currentTimeMillis(),
    
    /**
     * Timestamp when the report was finalized (milliseconds)
     * Null if the report is still in draft status
     */
    val finalizedAt: Long? = null
) {
    companion object {
        const val STATUS_DRAFT = "DRAFT"
        const val STATUS_FINAL = "FINAL"
    }
}
