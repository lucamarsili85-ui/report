package com.example.workreport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * WorkReport entity representing a daily work report.
 * 
 * This entity is used by Room to create the work_reports table in the database.
 * Each instance represents a single day's work report with details about the job site,
 * machine used, hours worked, and any additional notes.
 */
@Entity(tableName = "work_reports")
data class WorkReport(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Date of the work (stored as timestamp in milliseconds)
     */
    val date: Long,
    
    /**
     * Name or location of the job site
     */
    val jobSite: String,
    
    /**
     * Machine or equipment used during the work
     */
    val machine: String,
    
    /**
     * Number of hours worked (can include decimals, e.g., 8.5)
     */
    val hoursWorked: Double,
    
    /**
     * Optional notes or comments about the work
     */
    val notes: String = "",
    
    /**
     * Timestamp when the report was created (milliseconds)
     */
    val createdAt: Long = System.currentTimeMillis(),
    
    /**
     * Timestamp when the report was last updated (milliseconds)
     */
    val updatedAt: Long = System.currentTimeMillis()
)
