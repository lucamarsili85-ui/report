package com.workreport.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.workreport.data.database.WorkReportDatabase
import com.workreport.data.entity.WorkReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WorkReportViewModel(application: Application) : AndroidViewModel(application) {
    private val workReportDao = WorkReportDatabase.getDatabase(application).workReportDao()
    
    val allReports: Flow<List<WorkReport>> = workReportDao.getAllReports()

    fun insertReport(workReport: WorkReport) {
        viewModelScope.launch {
            workReportDao.insert(workReport)
        }
    }

    suspend fun getReportById(id: Long): WorkReport? {
        return workReportDao.getReportById(id)
    }
}
