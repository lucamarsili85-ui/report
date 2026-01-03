package com.workreport.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.workreport.R
import com.workreport.data.entity.WorkReport
import com.workreport.viewmodel.WorkReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToNewReport: () -> Unit,
    viewModel: WorkReportViewModel
) {
    val reports by viewModel.allReports.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dashboard)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNewReport) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_report))
            }
        }
    ) { paddingValues ->
        if (reports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No reports yet. Tap + to create one.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reports) { report ->
                    ReportCard(report = report)
                }
            }
        }
    }
}

@Composable
fun ReportCard(report: WorkReport) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = report.date,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Job Site: ${report.jobSite}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Machine: ${report.machine}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Hours: ${report.workedHours}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (report.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes: ${report.notes}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
