package com.example.workreport.domain

/**
 * Base interface for all activity types within a client section.
 * 
 * Activities represent work performed or materials used for a specific client.
 * There are two main types: MachineActivity and MaterialActivity.
 */
sealed interface Activity {
    val id: Long
    val createdAt: Long
}

/**
 * MachineActivity represents machine/equipment usage.
 * 
 * Tracks the machine used, hours worked, and an optional description
 * of the work performed.
 */
data class MachineActivity(
    override val id: Long = 0,
    
    /**
     * Name of the machine or equipment used
     */
    val machine: String,
    
    /**
     * Number of hours worked (can include decimals, e.g., 8.5)
     */
    val hours: Double,
    
    /**
     * Optional description of the activity performed
     */
    val description: String = "",
    
    override val createdAt: Long = System.currentTimeMillis()
) : Activity

/**
 * MaterialActivity represents materials used or transported.
 * 
 * Tracks the material name, quantity, unit of measurement, and optional notes.
 * Can also represent equipment transport (e.g., roller moved from A to B by truck).
 */
data class MaterialActivity(
    override val id: Long = 0,
    
    /**
     * Name or description of the material
     */
    val name: String,
    
    /**
     * Quantity of the material used
     */
    val quantity: Double,
    
    /**
     * Unit of measurement: "m³" (cubic meters) or "ton" (tonnes)
     */
    val unit: String,
    
    /**
     * Optional notes about this material or transport details
     */
    val notes: String = "",
    
    override val createdAt: Long = System.currentTimeMillis()
) : Activity
