package com.example.workreport.domain

/**
 * WorkReport domain model representing a daily work report.
 * 
 * This model represents a single day's work report with details about the job site,
 * machine used, hours worked, materials used, and any additional notes.
 */
data class WorkReport(
    /**
     * Unique identifier for the work report
     */
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
    val updatedAt: Long = System.currentTimeMillis(),
    
    /**
     * List of materials used during the work
     * 
     * This is an immutable list. In practice, materials are managed through UI state
     * and a new WorkReport instance is created when materials are modified.
     * Default: empty list
     */
    val materials: List<Material> = emptyList()
)
