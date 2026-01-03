package com.example.rapportino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rapportino.data.entity.Report
import com.example.rapportino.data.repository.ReportRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class AddReportViewModel(private val repository: ReportRepository) : ViewModel() {
    
    fun saveReport(
        date: Long,
        jobSite: String,
        machine: String,
        workedHours: Double,
        notes: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val report = Report(
                date = date,
                jobSite = jobSite,
                machine = machine,
                workedHours = workedHours,
                notes = notes
            )
            repository.insert(report)
            onSuccess()
        }
    }

    fun getTodayStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

class AddReportViewModelFactory(private val repository: ReportRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddReportViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
