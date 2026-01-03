package com.example.workreport.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workreport.data.entity.WorkReport
import com.example.workreport.data.repository.WorkReportRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for managing work report data.
 * 
 * This ViewModel exposes UI state and handles user actions for work report management.
 * It communicates with the Repository to perform data operations and transforms
 * repository data into UI state.
 */
class WorkReportViewModel(
    private val repository: WorkReportRepository
) : ViewModel() {
    
    /**
     * StateFlow of all work reports for the dashboard.
     * Automatically updates when data changes in the database.
     */
    val allReports: StateFlow<List<WorkReport>> = repository.allReports
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    /**
     * StateFlow for total hours across all reports.
     * Computed from the list of all reports.
     */
    val totalHours: StateFlow<Double> = allReports
        .map { reports -> reports.sumOf { it.hoursWorked } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )
    
    /**
     * Insert a new work report.
     * 
     * @param date Date of work (timestamp)
     * @param jobSite Job site name
     * @param machine Machine used
     * @param hoursWorked Hours worked
     * @param notes Optional notes
     */
    fun insertReport(
        date: Long,
        jobSite: String,
        machine: String,
        hoursWorked: Double,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val report = WorkReport(
                date = date,
                jobSite = jobSite,
                machine = machine,
                hoursWorked = hoursWorked,
                notes = notes
            )
            repository.insertReport(report)
        }
    }
    
    /**
     * Update an existing work report.
     * 
     * @param report The report to update
     */
    fun updateReport(report: WorkReport) {
        viewModelScope.launch {
            repository.updateReport(report)
        }
    }
    
    /**
     * Delete a work report.
     * 
     * @param report The report to delete
     */
    fun deleteReport(report: WorkReport) {
        viewModelScope.launch {
            repository.deleteReport(report)
        }
    }
    
    /**
     * Get reports within a date range.
     * 
     * @param startDate Start date timestamp
     * @param endDate End date timestamp
     * @return Flow of reports within the range
     */
    fun getReportsByDateRange(startDate: Long, endDate: Long): Flow<List<WorkReport>> {
        return repository.getReportsByDateRange(startDate, endDate)
    }
    
    /**
     * Get total hours for a specific date range.
     * 
     * @param startDate Start date timestamp
     * @param endDate End date timestamp
     * @param onResult Callback with the total hours
     */
    fun getTotalHoursForRange(
        startDate: Long,
        endDate: Long,
        onResult: (Double) -> Unit
    ) {
        viewModelScope.launch {
            val total = repository.getTotalHours(startDate, endDate)
            onResult(total)
        }
    }
}
