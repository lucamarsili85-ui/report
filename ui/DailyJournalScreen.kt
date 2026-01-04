package com.example.workreport.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.workreport.data.entity.ActivityEntity
import com.example.workreport.data.entity.ClientSectionEntity
import com.example.workreport.data.entity.DailyReportEntity
import com.example.workreport.ui.viewmodel.DailyReportViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Daily Journal screen for progressive work reporting.
 * 
 * This screen implements the daily journal workflow with:
 * - DRAFT vs FINAL state management
 * - Progressive saving of activities
 * - Preview-only mode when finalized
 * - Multiple clients per day support
 * 
 * @param viewModel The DailyReportViewModel for managing data
 * @param onNavigateBack Callback to navigate back to the previous screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyJournalScreen(
    viewModel: DailyReportViewModel,
    onNavigateBack: () -> Unit
) {
    val currentReport by viewModel.currentDailyReport.collectAsState()
    val clientSections by viewModel.currentClientSections.collectAsState()
    val activitiesByClient by viewModel.activitiesByClientSection.collectAsState()
    val isPreviewMode by viewModel.isPreviewMode.collectAsState()
    val totalHours by viewModel.totalHours.collectAsState()
    
    // Load or create today's draft when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadOrCreateTodaysDraft()
    }
    
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (isPreviewMode) "Daily Report (Final)" else "Daily Journal (Draft)")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isPreviewMode) 
                        MaterialTheme.colorScheme.tertiaryContainer
                    else 
                        MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = if (isPreviewMode)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer
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
            // Date and summary
            currentReport?.let { report ->
                PreviewDashboard(
                    date = report.date,
                    totalHours = totalHours,
                    trasferta = report.trasferta,
                    clientCount = clientSections.size,
                    dateFormat = dateFormat,
                    isPreviewMode = isPreviewMode,
                    onTrasfertaToggle = { viewModel.updateTrasferta(it) }
                )
            }
            
            // Add Client Section (only in draft mode)
            if (!isPreviewMode) {
                AddClientSection(viewModel)
            }
            
            // Client sections with activities
            clientSections.forEach { clientSection ->
                ClientSectionCard(
                    clientSection = clientSection,
                    activities = activitiesByClient[clientSection.id] ?: emptyList(),
                    isPreviewMode = isPreviewMode,
                    viewModel = viewModel
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Action button (Finalize or Edit)
            if (isPreviewMode) {
                Button(
                    onClick = { viewModel.reopenDailyReport() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Edit Report")
                }
            } else {
                Button(
                    onClick = { viewModel.finalizeDailyReport() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = clientSections.isNotEmpty()
                ) {
                    Text("Save & Finalize Daily Report")
                }
            }
        }
    }
}

/**
 * Preview dashboard showing summary information.
 */
@Composable
private fun PreviewDashboard(
    date: Long,
    totalHours: Double,
    trasferta: Boolean,
    clientCount: Int,
    dateFormat: SimpleDateFormat,
    isPreviewMode: Boolean,
    onTrasfertaToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = dateFormat.format(Date(date)),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Hours",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = String.format("%.1f h", totalHours),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Clients",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "$clientCount",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Trasferta",
                        style = MaterialTheme.typography.labelMedium
                    )
                    if (isPreviewMode) {
                        Text(
                            text = if (trasferta) "Yes" else "No",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Switch(
                                checked = trasferta,
                                onCheckedChange = onTrasfertaToggle
                            )
                            Text(
                                text = if (trasferta) "Yes" else "No",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section for adding a new client.
 */
@Composable
private fun AddClientSection(viewModel: DailyReportViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var clientName by remember { mutableStateOf("") }
    var jobSite by remember { mutableStateOf("") }
    
    OutlinedButton(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add Client")
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Client") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Client Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = jobSite,
                        onValueChange = { jobSite = it },
                        label = { Text("Job Site Location *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (clientName.trim().isNotEmpty() && jobSite.trim().isNotEmpty()) {
                            viewModel.addClientSection(clientName.trim(), jobSite.trim())
                            clientName = ""
                            jobSite = ""
                            showDialog = false
                        }
                    },
                    enabled = clientName.trim().isNotEmpty() && jobSite.trim().isNotEmpty()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Card displaying a client section with its activities.
 */
@Composable
private fun ClientSectionCard(
    clientSection: ClientSectionEntity,
    activities: List<ActivityEntity>,
    isPreviewMode: Boolean,
    viewModel: DailyReportViewModel
) {
    var expanded by remember { mutableStateOf(true) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editClientName by remember(clientSection.id) { mutableStateOf(clientSection.clientName) }
    var editJobSite by remember(clientSection.id) { mutableStateOf(clientSection.jobSite) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Client header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = clientSection.clientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = clientSection.jobSite,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                
                if (!isPreviewMode) {
                    Row {
                        IconButton(onClick = { 
                            // Reset fields to latest values when reopening dialog
                            editClientName = clientSection.clientName
                            editJobSite = clientSection.jobSite
                            showEditDialog = true 
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit client"
                            )
                        }
                        IconButton(onClick = { viewModel.deleteClientSection(clientSection) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete client",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            if (showEditDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Edit Client") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editClientName,
                                onValueChange = { editClientName = it },
                                label = { Text("Client Name *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = editJobSite,
                                onValueChange = { editJobSite = it },
                                label = { Text("Job Site Location *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (editClientName.trim().isNotEmpty() && editJobSite.trim().isNotEmpty()) {
                                    viewModel.updateClientSection(
                                        clientSection.id,
                                        editClientName.trim(),
                                        editJobSite.trim()
                                    )
                                    showEditDialog = false
                                }
                            },
                            enabled = editClientName.trim().isNotEmpty() && editJobSite.trim().isNotEmpty()
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Activities summary
            val machineActivities = activities.filter { it.activityType == ActivityEntity.TYPE_MACHINE }
            val materialActivities = activities.filter { it.activityType == ActivityEntity.TYPE_MATERIAL }
            val clientHours = machineActivities.sumOf { it.hours ?: 0.0 }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Hours: ${String.format("%.1f", clientHours)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Materials: ${materialActivities.size}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Activities: ${activities.size}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Add activity buttons (only in draft mode)
            if (!isPreviewMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AddMachineActivityButton(
                        clientSectionId = clientSection.id,
                        viewModel = viewModel,
                        modifier = Modifier.weight(1f)
                    )
                    AddMaterialActivityButton(
                        clientSectionId = clientSection.id,
                        viewModel = viewModel,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Activities list (collapsible)
            if (activities.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Hide Activities" else "Show Activities (${activities.size})")
                }
                
                if (expanded) {
                    activities.forEach { activity ->
                        ActivityItem(
                            activity = activity,
                            isPreviewMode = isPreviewMode,
                            onDelete = { viewModel.deleteActivity(activity) },
                            onEditMachine = { machine, hours, description ->
                                viewModel.updateMachineActivity(
                                    activity.id,
                                    clientSection.id,
                                    machine,
                                    hours,
                                    description
                                )
                            },
                            onEditMaterial = { materialName, quantity, unit, notes ->
                                viewModel.updateMaterialActivity(
                                    activity.id,
                                    clientSection.id,
                                    materialName,
                                    quantity,
                                    unit,
                                    notes
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Button to add a machine activity.
 */
@Composable
private fun AddMachineActivityButton(
    clientSectionId: Long,
    viewModel: DailyReportViewModel,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var machine by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    OutlinedButton(
        onClick = { showDialog = true },
        modifier = modifier
    ) {
        Text("+ Machine", style = MaterialTheme.typography.labelSmall)
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Machine Activity") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = machine,
                        onValueChange = { machine = it },
                        label = { Text("Machine Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = hours,
                        onValueChange = { hours = it },
                        label = { Text("Hours *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val hoursValue = hours.toDoubleOrNull()
                        if (machine.trim().isNotEmpty() && hoursValue != null && hoursValue > 0) {
                            viewModel.addMachineActivity(
                                clientSectionId,
                                machine.trim(),
                                hoursValue,
                                description.trim()
                            )
                            machine = ""
                            hours = ""
                            description = ""
                            showDialog = false
                        }
                    },
                    enabled = machine.trim().isNotEmpty() && 
                              hours.toDoubleOrNull()?.let { it > 0 } == true
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Button to add a material activity.
 */
@Composable
private fun AddMaterialActivityButton(
    clientSectionId: Long,
    viewModel: DailyReportViewModel,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var materialName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("m³") }
    var notes by remember { mutableStateOf("") }
    
    OutlinedButton(
        onClick = { showDialog = true },
        modifier = modifier
    ) {
        Text("+ Material", style = MaterialTheme.typography.labelSmall)
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Material Activity") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = materialName,
                        onValueChange = { materialName = it },
                        label = { Text("Material Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    
                    // Unit selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = unit == "m³",
                            onClick = { unit = "m³" },
                            label = { Text("m³") }
                        )
                        FilterChip(
                            selected = unit == "ton",
                            onClick = { unit = "ton" },
                            label = { Text("ton") }
                        )
                    }
                    
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val quantityValue = quantity.toDoubleOrNull()
                        if (materialName.trim().isNotEmpty() && quantityValue != null && quantityValue > 0) {
                            viewModel.addMaterialActivity(
                                clientSectionId,
                                materialName.trim(),
                                quantityValue,
                                unit,
                                notes.trim()
                            )
                            materialName = ""
                            quantity = ""
                            unit = "m³"
                            notes = ""
                            showDialog = false
                        }
                    },
                    enabled = materialName.trim().isNotEmpty() && 
                              quantity.toDoubleOrNull()?.let { it > 0 } == true
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Display a single activity item.
 */
@Composable
private fun ActivityItem(
    activity: ActivityEntity,
    isPreviewMode: Boolean,
    onDelete: () -> Unit,
    onEditMachine: (String, Double, String) -> Unit,
    onEditMaterial: (String, Double, String, String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editMachine by remember(activity.id) { mutableStateOf(activity.machine.orEmpty()) }
    var editHours by remember(activity.id) { mutableStateOf(activity.hours?.toString().orEmpty()) }
    var editDescription by remember(activity.id) { mutableStateOf(activity.description.orEmpty()) }
    var editMaterialName by remember(activity.id) { mutableStateOf(activity.materialName.orEmpty()) }
    var editQuantity by remember(activity.id) { mutableStateOf(activity.quantity?.toString().orEmpty()) }
    var editUnit by remember(activity.id) { mutableStateOf(activity.unit ?: "m³") }
    var editNotes by remember(activity.id) { mutableStateOf(activity.notes.orEmpty()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                if (activity.activityType == ActivityEntity.TYPE_MACHINE) {
                    Text(
                        text = "🔧 ${activity.machine}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${activity.hours} hours",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (!activity.description.isNullOrBlank()) {
                        Text(
                            text = activity.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    Text(
                        text = "📦 ${activity.materialName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${activity.quantity} ${activity.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (!activity.notes.isNullOrBlank()) {
                        Text(
                            text = activity.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            if (!isPreviewMode) {
                Row {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit activity"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete activity",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
    
    if (showEditDialog) {
        if (activity.activityType == ActivityEntity.TYPE_MACHINE) {
            val parsedHours = editHours.toDoubleOrNull()
            val isMachineValid = editMachine.trim().isNotEmpty() &&
                    parsedHours?.let { it > 0 } == true
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Edit Machine Activity") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editMachine,
                            onValueChange = { editMachine = it },
                            label = { Text("Machine Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editHours,
                            onValueChange = { editHours = it },
                            label = { Text("Hours *") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editDescription,
                            onValueChange = { editDescription = it },
                            label = { Text("Description (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val hoursValue = parsedHours
                            if (isMachineValid && hoursValue != null) {
                                onEditMachine(
                                    editMachine.trim(),
                                    hoursValue,
                                    editDescription.trim()
                                )
                                showEditDialog = false
                            }
                        },
                        enabled = isMachineValid
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        } else {
            val parsedQuantity = editQuantity.toDoubleOrNull()
            val isMaterialValid = editMaterialName.trim().isNotEmpty() &&
                    parsedQuantity?.let { it > 0 } == true
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Edit Material Activity") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editMaterialName,
                            onValueChange = { editMaterialName = it },
                            label = { Text("Material Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editQuantity,
                            onValueChange = { editQuantity = it },
                            label = { Text("Quantity *") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = editUnit == "m³",
                                onClick = { editUnit = "m³" },
                                label = { Text("m³") }
                            )
                            FilterChip(
                                selected = editUnit == "ton",
                                onClick = { editUnit = "ton" },
                                label = { Text("ton") }
                            )
                        }
                        
                        OutlinedTextField(
                            value = editNotes,
                            onValueChange = { editNotes = it },
                            label = { Text("Notes (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val quantityValue = parsedQuantity
                            if (isMaterialValid && quantityValue != null) {
                                onEditMaterial(
                                    editMaterialName.trim(),
                                    quantityValue,
                                    editUnit,
                                    editNotes.trim()
                                )
                                showEditDialog = false
                            }
                        },
                        enabled = isMaterialValid
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
