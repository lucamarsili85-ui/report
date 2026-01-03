package com.example.workreport.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * ActivityEntity representing an activity (machine or material) within a client section.
 * 
 * This entity is used by Room to create the activities table in the database.
 * Each activity belongs to a client section and can represent either machine usage
 * or material usage, distinguished by the activityType field.
 */
@Entity(
    tableName = "activities",
    foreignKeys = [
        ForeignKey(
            entity = ClientSectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientSectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clientSectionId")]
)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Foreign key to the parent client section
     */
    val clientSectionId: Long,
    
    /**
     * Type of activity: "MACHINE" or "MATERIAL"
     */
    val activityType: String,
    
    // Machine activity fields
    /**
     * Name of the machine or equipment used (for MACHINE type)
     */
    val machine: String? = null,
    
    /**
     * Number of hours worked (for MACHINE type)
     */
    val hours: Double? = null,
    
    /**
     * Optional description of the activity (for MACHINE type)
     */
    val description: String? = null,
    
    // Material activity fields
    /**
     * Name or description of the material (for MATERIAL type)
     */
    val materialName: String? = null,
    
    /**
     * Quantity of the material used (for MATERIAL type)
     */
    val quantity: Double? = null,
    
    /**
     * Unit of measurement: "m³" or "ton" (for MATERIAL type)
     */
    val unit: String? = null,
    
    /**
     * Optional notes about the material (for MATERIAL type)
     */
    val notes: String? = null,
    
    /**
     * Timestamp when the activity was created (milliseconds)
     */
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val TYPE_MACHINE = "MACHINE"
        const val TYPE_MATERIAL = "MATERIAL"
    }
}
