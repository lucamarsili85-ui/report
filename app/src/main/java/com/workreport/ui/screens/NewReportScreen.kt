package com.workreport.ui.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.workreport.R
import com.workreport.data.entity.WorkReport
import com.workreport.viewmodel.WorkReportViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(
    onNavigateBack: () -> Unit,
    viewModel: WorkReportViewModel
) {
    var date by remember { mutableStateOf(getCurrentDate()) }
    var jobSite by remember { mutableStateOf("") }
    var machine by remember { mutableStateOf("") }
    var workedHours by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.new_report)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
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
                value = date,
                onValueChange = { },
                label = { Text(stringResource(R.string.date)) },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.select_date))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Job Site field
            OutlinedTextField(
                value = jobSite,
                onValueChange = { jobSite = it },
                label = { Text(stringResource(R.string.job_site)) },
                modifier = Modifier.fillMaxWidth()
            )

            // Machine field
            OutlinedTextField(
                value = machine,
                onValueChange = { machine = it },
                label = { Text(stringResource(R.string.machine)) },
                modifier = Modifier.fillMaxWidth()
            )

            // Worked Hours field
            OutlinedTextField(
                value = workedHours,
                onValueChange = { workedHours = it },
                label = { Text(stringResource(R.string.worked_hours)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            // Notes field
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.notes)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            // Save Button
            Button(
                onClick = {
                    val hours = workedHours.toFloatOrNull() ?: 0f
                    val report = WorkReport(
                        date = date,
                        jobSite = jobSite,
                        machine = machine,
                        workedHours = hours,
                        notes = notes
                    )
                    viewModel.insertReport(report)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = jobSite.isNotEmpty() && machine.isNotEmpty() && workedHours.isNotEmpty()
            ) {
                Text(stringResource(R.string.save_report))
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { selectedDate ->
                date = selectedDate
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    onDateSelected(dateFormat.format(Date(millis)))
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(Date())
}
