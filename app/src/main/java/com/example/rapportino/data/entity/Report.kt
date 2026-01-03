package com.example.rapportino.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val jobSite: String,
    val machine: String,
    val workedHours: Double,
    val notes: String,
    val createdAt: Long = System.currentTimeMillis()
)
