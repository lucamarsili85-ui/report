package com.example.workreport.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.workreport.domain.Material
import com.example.workreport.ui.viewmodel.WorkReportViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen for creating or editing a work report.
 * 
 * Provides a form with fields for date, job site, machine, hours worked, notes, and materials.
 * Validates input and saves the report to the database.
 * 
 * NOTE: This sample demonstrates materials tracking UI. The materials data is collected
 * and managed in the UI state but not yet persisted. To complete the implementation,
 * update the ViewModel, Repository, and Room entity to handle materials persistence.
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
    
    // Materials state
    var materials by remember { mutableStateOf(listOf<Material>()) }
    var materialName by remember { mutableStateOf("") }
    var materialQuantity by remember { mutableStateOf("") }
    var materialUnit by remember { mutableStateOf("") }
    var materialNote by remember { mutableStateOf("") }
    
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
            
            // Materials Section
            Text(
                text = "Materials",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            // Add Material Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Add Material",
                        style = MaterialTheme.typography.labelLarge
                    )
                    
                    OutlinedTextField(
                        value = materialName,
                        onValueChange = { materialName = it },
                        label = { Text("Material Name") },
                        placeholder = { Text("e.g., Concrete, Steel, Paint") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = materialQuantity,
                            onValueChange = { materialQuantity = it },
                            label = { Text("Quantity") },
                            placeholder = { Text("e.g., 10.5") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        
                        OutlinedTextField(
                            value = materialUnit,
                            onValueChange = { materialUnit = it },
                            label = { Text("Unit") },
                            placeholder = { Text("e.g., kg, m³") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    
                    OutlinedTextField(
                        value = materialNote,
                        onValueChange = { materialNote = it },
                        label = { Text("Note (Optional)") },
                        placeholder = { Text("Additional information") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Button(
                        onClick = {
                            val trimmedName = materialName.trim()
                            val trimmedQuantity = materialQuantity.trim()
                            val trimmedUnit = materialUnit.trim()
                            
                            if (trimmedName.isNotEmpty() && trimmedQuantity.isNotEmpty() && trimmedUnit.isNotEmpty()) {
                                val quantity = trimmedQuantity.toDoubleOrNull()
                                if (quantity != null && quantity > 0) {
                                    materials = materials + Material(
                                        name = trimmedName,
                                        quantity = quantity,
                                        unit = trimmedUnit,
                                        note = materialNote.trim()
                                    )
                                    // Clear form
                                    materialName = ""
                                    materialQuantity = ""
                                    materialUnit = ""
                                    materialNote = ""
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        enabled = materialName.trim().isNotEmpty() && 
                                  materialQuantity.trim().isNotEmpty() && 
                                  materialUnit.trim().isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Material")
                    }
                }
            }
            
            // Materials List
            if (materials.isNotEmpty()) {
                Text(
                    text = "Materials List (${materials.size})",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                materials.forEachIndexed { index, material ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = material.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${material.quantity} ${material.unit}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                )
                                if (material.note.isNotBlank()) {
                                    Text(
                                        text = material.note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    materials = materials.filterIndexed { i, _ -> i != index }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete material",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
            
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
                        // Trim values before validation
                        val trimmedJobSite = jobSite.trim()
                        val trimmedMachine = machine.trim()
                        val trimmedHours = hoursWorked.trim()
                        
                        // Validate input
                        val validation = validateInput(trimmedJobSite, trimmedMachine, trimmedHours)
                        if (validation.isValid) {
                            // Safe to use toDouble() here as validation confirmed it's valid
                            
                            // TODO: This is a UI sample demonstrating materials management.
                            // In a full implementation:
                            // 1. Update WorkReportViewModel.insertReport signature to accept materials parameter
                            // 2. Update WorkReportRepository to handle materials persistence
                            // 3. Update Room entity to store materials (convert List<Material> to JSON or separate table)
                            // Example call: viewModel.insertReport(date, jobSite, machine, hoursWorked, notes, materials)
                            
                            viewModel.insertReport(
                                date = date,
                                jobSite = trimmedJobSite,
                                machine = trimmedMachine,
                                hoursWorked = trimmedHours.toDouble(),
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
