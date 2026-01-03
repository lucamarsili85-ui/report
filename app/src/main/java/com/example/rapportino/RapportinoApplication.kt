package com.example.rapportino

import android.app.Application
import com.example.rapportino.data.database.AppDatabase
import com.example.rapportino.data.repository.ReportRepository

class RapportinoApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ReportRepository(database.reportDao()) }
}
