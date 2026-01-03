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
import com.example.workreport.data.entity.DailyReportEntity
import com.example.workreport.data.entity.WorkReport
import com.example.workreport.ui.viewmodel.DailyReportViewModel
import com.example.workreport.ui.viewmodel.WorkReportViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dashboard screen displaying all work reports.
 * 
 * Shows a list of work reports with summary information and provides
 * quick access to create new reports.
 * 
 * NOTE: This screen now supports both legacy WorkReport and new DailyReport models.
 * 
 * @param workReportViewModel The WorkReportViewModel for managing legacy data (optional)
 * @param dailyReportViewModel The DailyReportViewModel for managing daily journal data (optional)
 * @param onNavigateToNewReport Callback to navigate to the new report screen (legacy)
 * @param onNavigateToDailyJournal Callback to navigate to the daily journal screen
 * @param onReportClick Callback when a legacy report is clicked for editing
 * @param onDailyReportClick Callback when a daily report is clicked for viewing/editing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    workReportViewModel: WorkReportViewModel? = null,
    dailyReportViewModel: DailyReportViewModel? = null,
    onNavigateToNewReport: (() -> Unit)? = null,
    onNavigateToDailyJournal: (() -> Unit)? = null,
    onReportClick: ((WorkReport) -> Unit)? = null,
    onDailyReportClick: ((DailyReportEntity) -> Unit)? = null
) {
    // Legacy reports
    val legacyReports by (workReportViewModel?.allReports?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    val legacyTotalHours by (workReportViewModel?.totalHours?.collectAsState() ?: remember { mutableStateOf(0.0) })
    
    // Daily reports
    val dailyReports by (dailyReportViewModel?.finalizedReports?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    
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
            // Show Daily Journal FAB if dailyReportViewModel is provided
            if (dailyReportViewModel != null && onNavigateToDailyJournal != null) {
                FloatingActionButton(
                    onClick = onNavigateToDailyJournal,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Daily Journal"
                    )
                }
            } else if (workReportViewModel != null && onNavigateToNewReport != null) {
                // Fallback to legacy FAB
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Summary card (show daily reports total if available, otherwise legacy)
            val showSummary = dailyReports.isNotEmpty() || legacyReports.isNotEmpty()
            if (showSummary) {
                val displayTotalHours = if (dailyReports.isNotEmpty()) {
                    dailyReports.sumOf { it.totalHours }
                } else {
                    legacyTotalHours
                }
                SummaryCard(totalHours = displayTotalHours)
            }
            
            // Show appropriate list based on what's available
            when {
                dailyReports.isNotEmpty() -> {
                    // Show daily reports
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(dailyReports) { report ->
                            DailyReportCard(
                                report = report,
                                onClick = { onDailyReportClick?.invoke(report) }
                            )
                        }
                    }
                }
                legacyReports.isNotEmpty() -> {
                    // Show legacy reports
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(legacyReports) { report ->
                            ReportCard(
                                report = report,
                                onClick = { onReportClick?.invoke(report) }
                            )
                        }
                    }
                }
                else -> {
                    // Empty state
                    EmptyState()
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
 * Individual report card item (legacy).
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

/**
 * Daily report card item (new model).
 */
@Composable
private fun DailyReportCard(
    report: DailyReportEntity,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (report.status == DailyReportEntity.STATUS_FINAL)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Date and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormat.format(Date(report.date)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Status badge
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (report.status == DailyReportEntity.STATUS_FINAL)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = report.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (report.status == DailyReportEntity.STATUS_FINAL)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Summary info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Hours",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = String.format("%.1f h", report.totalHours),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Trasferta",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (report.trasferta) "Yes" else "No",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

