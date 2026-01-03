package com.example.rapportino.data.repository

import com.example.rapportino.data.dao.ReportDao
import com.example.rapportino.data.entity.Report
import kotlinx.coroutines.flow.Flow

class ReportRepository(private val reportDao: ReportDao) {
    val allReports: Flow<List<Report>> = reportDao.getAllReports()

    suspend fun getReportById(id: Long): Report? {
        return reportDao.getReportById(id)
    }

    suspend fun insert(report: Report): Long {
        return reportDao.insertReport(report)
    }

    suspend fun update(report: Report) {
        reportDao.updateReport(report)
    }

    suspend fun delete(report: Report) {
        reportDao.deleteReport(report)
    }

    suspend fun deleteById(id: Long) {
        reportDao.deleteReportById(id)
    }
}
