package com.example.workreport.domain

/**
 * Material represents an item used or consumed during work.
 * 
 * This is a simple domain model for tracking materials in a work report.
 * Each material has a name, quantity, unit of measurement, and optional notes.
 */
data class Material(
    /**
     * Name or description of the material
     */
    val name: String,
    
    /**
     * Quantity of the material used
     */
    val quantity: Double,
    
    /**
     * Unit of measurement (e.g., "kg", "m", "units", "liters")
     */
    val unit: String,
    
    /**
     * Optional notes about this material
     */
    val note: String = ""
)
