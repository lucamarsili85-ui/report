package com.example.workreport.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.workreport.data.entity.WorkReport
import com.example.workreport.ui.viewmodel.WorkReportViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dashboard screen displaying all work reports.
 * 
 * Shows a list of work reports with summary information and provides
 * quick access to create new reports.
 * 
 * @param viewModel The WorkReportViewModel for managing data
 * @param onNavigateToNewReport Callback to navigate to the new report screen
 * @param onReportClick Callback when a report is clicked for editing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: WorkReportViewModel,
    onNavigateToNewReport: () -> Unit,
    onReportClick: (WorkReport) -> Unit
) {
    val reports by viewModel.allReports.collectAsState()
    val totalHours by viewModel.totalHours.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Work Reports") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewReport,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Report"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Total hours summary card
            if (reports.isNotEmpty()) {
                SummaryCard(totalHours = totalHours)
            }
            
            // Reports list
            if (reports.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(reports) { report ->
                        ReportCard(
                            report = report,
                            onClick = { onReportClick(report) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Summary card showing total hours worked.
 */
@Composable
private fun SummaryCard(totalHours: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Hours Logged",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format("%.1f", totalHours),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

/**
 * Empty state shown when there are no reports.
 */
@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No work reports yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap + to add your first report",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Individual report card item.
 */
@Composable
private fun ReportCard(
    report: WorkReport,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Date
            Text(
                text = dateFormat.format(Date(report.date)),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Job Site
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Job Site",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = report.jobSite,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Hours
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Hours",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = String.format("%.1f", report.hoursWorked),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Machine
            Text(
                text = "Machine: ${report.machine}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Notes preview (if available)
            if (report.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = report.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}
