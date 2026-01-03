package com.workreport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "work_reports")
data class WorkReport(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val jobSite: String,
    val machine: String,
    val workedHours: Float,
    val notes: String
)
