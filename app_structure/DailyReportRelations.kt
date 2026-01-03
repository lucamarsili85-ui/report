package com.example.workreport.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.workreport.data.entity.ActivityEntity
import com.example.workreport.data.entity.ClientSectionEntity
import com.example.workreport.data.entity.DailyReportEntity

/**
 * Data class representing a complete daily report with all its client sections and activities.
 * 
 * This uses Room's @Relation annotation to automatically fetch related data.
 */
data class DailyReportWithClients(
    @Embedded val dailyReport: DailyReportEntity,
    @Relation(
        entity = ClientSectionEntity::class,
        parentColumn = "id",
        entityColumn = "dailyReportId"
    )
    val clients: List<ClientSectionWithActivities>
)

/**
 * Data class representing a client section with all its activities.
 */
data class ClientSectionWithActivities(
    @Embedded val clientSection: ClientSectionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "clientSectionId"
    )
    val activities: List<ActivityEntity>
)
