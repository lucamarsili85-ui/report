package com.example.workreport.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.workreport.ui.viewmodel.WorkReportViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen for creating or editing a work report.
 * 
 * Provides a form with fields for date, job site, machine, hours worked, and notes.
 * Validates input and saves the report to the database.
 * 
 * @param viewModel The WorkReportViewModel for managing data
 * @param onNavigateBack Callback to navigate back to the previous screen
 * @param reportId Optional report ID for editing (null for new report)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(
    viewModel: WorkReportViewModel,
    onNavigateBack: () -> Unit,
    reportId: Long? = null
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var jobSite by remember { mutableStateOf("") }
    var machine by remember { mutableStateOf("") }
    var hoursWorked by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (reportId == null) "New Report" else "Edit Report") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date field
            OutlinedTextField(
                value = dateFormat.format(Date(date)),
                onValueChange = { },
                label = { Text("Date *") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Job Site field
            OutlinedTextField(
                value = jobSite,
                onValueChange = { 
                    if (it.length <= 100) jobSite = it 
                },
                label = { Text("Job Site *") },
                placeholder = { Text("Enter job site name or location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = { Text("${jobSite.length}/100") }
            )
            
            // Machine field
            OutlinedTextField(
                value = machine,
                onValueChange = { 
                    if (it.length <= 50) machine = it 
                },
                label = { Text("Machine *") },
                placeholder = { Text("Enter machine or equipment used") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = { Text("${machine.length}/50") }
            )
            
            // Hours Worked field
            OutlinedTextField(
                value = hoursWorked,
                onValueChange = { hoursWorked = it },
                label = { Text("Hours Worked *") },
                placeholder = { Text("e.g., 8.5") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                supportingText = { Text("Enter hours (0.1 - 24.0)") }
            )
            
            // Notes field
            OutlinedTextField(
                value = notes,
                onValueChange = { 
                    if (it.length <= 500) notes = it 
                },
                label = { Text("Notes") },
                placeholder = { Text("Additional notes or comments (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6,
                supportingText = { Text("${notes.length}/500") }
            )
            
            // Error message
            if (showError) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cancel button
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                
                // Save button
                Button(
                    onClick = {
                        // Validate input
                        val validation = validateInput(jobSite, machine, hoursWorked)
                        if (validation.isValid) {
                            viewModel.insertReport(
                                date = date,
                                jobSite = jobSite.trim(),
                                machine = machine.trim(),
                                hoursWorked = hoursWorked.toDouble(),
                                notes = notes.trim()
                            )
                            onNavigateBack()
                        } else {
                            showError = true
                            errorMessage = validation.errorMessage
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }
        }
    }
    
    // Date picker dialog
    if (showDatePicker) {
        // Note: In actual implementation, use DatePickerDialog or Material3 DatePicker
        // This is a placeholder showing the concept
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Select Date") },
            text = {
                Text("In actual implementation, integrate Material3 DatePicker or DatePickerDialog here")
            },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            }
        )
    }
}

/**
 * Data class for validation results.
 */
private data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String = ""
)

/**
 * Validates form input.
 * 
 * @param jobSite Job site name
 * @param machine Machine name
 * @param hoursWorked Hours worked as string
 * @return ValidationResult with validation status and error message
 */
private fun validateInput(
    jobSite: String,
    machine: String,
    hoursWorked: String
): ValidationResult {
    // Validate job site
    if (jobSite.isBlank()) {
        return ValidationResult(false, "Job site is required")
    }
    if (jobSite.length > 100) {
        return ValidationResult(false, "Job site must be 100 characters or less")
    }
    
    // Validate machine
    if (machine.isBlank()) {
        return ValidationResult(false, "Machine is required")
    }
    if (machine.length > 50) {
        return ValidationResult(false, "Machine must be 50 characters or less")
    }
    
    // Validate hours worked
    if (hoursWorked.isBlank()) {
        return ValidationResult(false, "Hours worked is required")
    }
    
    val hours = hoursWorked.toDoubleOrNull()
    if (hours == null) {
        return ValidationResult(false, "Please enter a valid number for hours")
    }
    if (hours < 0.1 || hours > 24.0) {
        return ValidationResult(false, "Hours must be between 0.1 and 24.0")
    }
    
    return ValidationResult(true)
}
