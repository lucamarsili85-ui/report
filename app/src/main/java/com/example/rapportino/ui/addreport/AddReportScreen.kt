package com.example.rapportino.ui.addreport

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.rapportino.viewmodel.AddReportViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReportScreen(
    viewModel: AddReportViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedDate by remember { mutableLongStateOf(viewModel.getTodayStartOfDay()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var jobSite by remember { mutableStateOf("") }
    var machine by remember { mutableStateOf("") }
    var workedHours by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var jobSiteError by remember { mutableStateOf(false) }
    var machineError by remember { mutableStateOf(false) }
    var workedHoursError by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuovo Rapportino") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Field
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "Seleziona data",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Data: ${dateFormat.format(Date(selectedDate))}")
            }

            // Job Site Field
            OutlinedTextField(
                value = jobSite,
                onValueChange = {
                    jobSite = it
                    jobSiteError = false
                },
                label = { Text("Cantiere") },
                modifier = Modifier.fillMaxWidth(),
                isError = jobSiteError,
                supportingText = if (jobSiteError) {
                    { Text("Campo obbligatorio") }
                } else null,
                singleLine = true
            )

            // Machine Field
            OutlinedTextField(
                value = machine,
                onValueChange = {
                    machine = it
                    machineError = false
                },
                label = { Text("Macchina") },
                modifier = Modifier.fillMaxWidth(),
                isError = machineError,
                supportingText = if (machineError) {
                    { Text("Campo obbligatorio") }
                } else null,
                singleLine = true
            )

            // Worked Hours Field
            OutlinedTextField(
                value = workedHours,
                onValueChange = {
                    workedHours = it
                    workedHoursError = false
                },
                label = { Text("Ore lavorate") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = workedHoursError,
                supportingText = if (workedHoursError) {
                    { Text("Inserire un valore numerico valido") }
                } else null,
                singleLine = true
            )

            // Notes Field
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Note") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    jobSiteError = jobSite.isBlank()
                    machineError = machine.isBlank()
                    
                    val hours = workedHours.toDoubleOrNull()
                    workedHoursError = hours == null || hours <= 0

                    if (!jobSiteError && !machineError && !workedHoursError) {
                        viewModel.saveReport(
                            date = selectedDate,
                            jobSite = jobSite,
                            machine = machine,
                            workedHours = hours!!,
                            notes = notes,
                            onSuccess = onNavigateBack
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Salva Rapportino")
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = date
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
