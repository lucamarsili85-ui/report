package com.example.workreport.domain

/**
 * DailyReport domain model representing a complete daily work report.
 * 
 * This model represents a single day's work across multiple clients.
 * It acts as a container for multiple client sections, each with their own activities.
 * The report can be in draft status (editable) or finalized (locked).
 */
data class DailyReport(
    /**
     * Unique identifier for the daily report
     */
    val id: Long = 0,
    
    /**
     * Date of the work (stored as timestamp in milliseconds)
     */
    val date: Long,
    
    /**
     * List of client sections in this daily report
     */
    val clients: List<ClientSection> = emptyList(),
    
    /**
     * Status of the report: "draft" or "finalized"
     * Draft reports can be edited, finalized reports are locked
     */
    val status: String = "draft",
    
    /**
     * Timestamp when the report was created (milliseconds)
     */
    val createdAt: Long = System.currentTimeMillis(),
    
    /**
     * Timestamp when the report was finalized (milliseconds)
     * Null if the report is still in draft status
     */
    val finalizedAt: Long? = null,
    
    /**
     * Total hours calculated when the report is finalized
     */
    val totalHours: Double = 0.0
) {
    /**
     * Calculate total hours across all machine activities in all clients
     */
    fun calculateTotalHours(): Double {
        return clients.sumOf { client ->
            client.activities
                .filterIsInstance<MachineActivity>()
                .sumOf { it.hours }
        }
    }
    
    /**
     * Check if the report is in draft state
     */
    fun isDraft(): Boolean = status == STATUS_DRAFT
    
    /**
     * Check if the report is finalized
     */
    fun isFinalized(): Boolean = status == STATUS_FINAL
    
    /**
     * Finalize the report, locking it for editing
     */
    fun finalize(): DailyReport {
        return copy(
            status = STATUS_FINAL,
            finalizedAt = System.currentTimeMillis(),
            totalHours = calculateTotalHours()
        )
    }
    
    /**
     * Reopen a finalized report as draft for editing
     */
    fun reopenAsDraft(): DailyReport {
        return copy(
            status = STATUS_DRAFT,
            finalizedAt = null
        )
    }
    
    companion object {
        const val STATUS_DRAFT = "draft"
        const val STATUS_FINAL = "final"
    }
}
