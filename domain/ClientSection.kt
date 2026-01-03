package com.example.workreport.domain

/**
 * ClientSection domain model representing a client within a daily report.
 * 
 * Each client section contains information about the client and job site,
 * along with all activities (machines and materials) performed for that client.
 * Each section has a distinct color for visual identification.
 */
data class ClientSection(
    /**
     * Unique identifier for the client section
     */
    val id: Long = 0,
    
    /**
     * Name of the client or company
     */
    val clientName: String,
    
    /**
     * Location of the job site (e.g., "Via Roma 10, Milano")
     */
    val jobSite: String,
    
    /**
     * List of activities performed for this client
     * Can contain both MachineActivity and MaterialActivity instances
     */
    val activities: List<Activity> = emptyList(),
    
    /**
     * Color identifier for visual distinction (e.g., "color-1", "color-2")
     * Used to apply different colored borders to client sections in the UI
     */
    val colorClass: String = "color-1",
    
    /**
     * Timestamp when the client section was created (milliseconds)
     */
    val createdAt: Long = System.currentTimeMillis()
)
