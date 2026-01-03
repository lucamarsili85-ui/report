package com.example.workreport.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * ClientSectionEntity representing a client within a daily report.
 * 
 * This entity is used by Room to create the client_sections table in the database.
 * Each client section belongs to a daily report and contains information about
 * the client and job site.
 */
@Entity(
    tableName = "client_sections",
    foreignKeys = [
        ForeignKey(
            entity = DailyReportEntity::class,
            parentColumns = ["id"],
            childColumns = ["dailyReportId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dailyReportId")]
)
data class ClientSectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Foreign key to the parent daily report
     */
    val dailyReportId: Long,
    
    /**
     * Name of the client or company
     */
    val clientName: String,
    
    /**
     * Location of the job site (e.g., "Via Roma 10, Milano")
     */
    val jobSite: String,
    
    /**
     * Color identifier for visual distinction (e.g., "color-1", "color-2")
     */
    val colorClass: String = "color-1",
    
    /**
     * Timestamp when the client section was created (milliseconds)
     */
    val createdAt: Long = System.currentTimeMillis()
)
