package com.example.workreport.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workreport.data.entity.ActivityEntity
import com.example.workreport.data.entity.ClientSectionEntity
import com.example.workreport.data.entity.DailyReportEntity
import com.example.workreport.data.repository.DailyReportRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for managing daily report data.
 * 
 * This ViewModel handles the daily journal workflow with progressive saving,
 * draft management, and preview mode for finalized reports.
 */
class DailyReportViewModel(
    private val repository: DailyReportRepository
) : ViewModel() {
    
    // ==================== State Management ====================
    
    /**
     * The current daily report being worked on (draft or finalized).
     */
    private val _currentReportId = MutableStateFlow<Long?>(null)
    
    /**
     * Current daily report as StateFlow.
     */
    val currentDailyReport: StateFlow<DailyReportEntity?> = _currentReportId
        .flatMapLatest { reportId ->
            if (reportId != null) {
                repository.getDailyReportById(reportId)
            } else {
                flowOf(null)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    /**
     * Client sections for the current daily report.
     */
    val currentClientSections: StateFlow<List<ClientSectionEntity>> = _currentReportId
        .flatMapLatest { reportId ->
            if (reportId != null) {
                repository.getClientSectionsForReport(reportId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    /**
     * Map of client section ID to its activities.
     * This is updated when client sections change.
     */
    private val _activitiesByClientSection = MutableStateFlow<Map<Long, List<ActivityEntity>>>(emptyMap())
    val activitiesByClientSection: StateFlow<Map<Long, List<ActivityEntity>>> = _activitiesByClientSection
    
    /**
     * All finalized reports for history view.
     */
    val finalizedReports: StateFlow<List<DailyReportEntity>> = repository.finalizedReports
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    /**
     * Whether the current report is in preview mode (finalized).
     */
    val isPreviewMode: StateFlow<Boolean> = currentDailyReport
        .map { it?.status == DailyReportEntity.STATUS_FINAL }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    /**
     * Total hours for the current report.
     */
    val totalHours: StateFlow<Double> = _currentReportId
        .flatMapLatest { reportId ->
            flow {
                if (reportId != null) {
                    emit(repository.calculateTotalHours(reportId))
                } else {
                    emit(0.0)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )
    
    init {
        // Load activities when client sections change
        viewModelScope.launch {
            currentClientSections.collect { sections ->
                val activitiesMap = mutableMapOf<Long, List<ActivityEntity>>()
                sections.forEach { section ->
                    repository.getActivitiesForClientSection(section.id).first().let { activities ->
                        activitiesMap[section.id] = activities
                    }
                }
                _activitiesByClientSection.value = activitiesMap
            }
        }
    }
    
    // ==================== Daily Report Operations ====================
    
    /**
     * Load or create today's draft report.
     * This should be called when the user opens the Daily Journal screen.
     */
    fun loadOrCreateTodaysDraft() {
        viewModelScope.launch {
            val reportId = repository.getOrCreateTodaysDraft()
            _currentReportId.value = reportId
        }
    }
    
    /**
     * Load a specific daily report by ID.
     * Used when editing a finalized report from history.
     */
    fun loadDailyReport(reportId: Long) {
        _currentReportId.value = reportId
    }
    
    /**
     * Finalize the current daily report.
     * Transitions from DRAFT to FINAL state.
     */
    fun finalizeDailyReport() {
        viewModelScope.launch {
            _currentReportId.value?.let { reportId ->
                repository.finalizeDailyReport(reportId)
            }
        }
    }
    
    /**
     * Reopen a finalized report for editing.
     * Transitions from FINAL back to DRAFT state.
     */
    fun reopenDailyReport() {
        viewModelScope.launch {
            _currentReportId.value?.let { reportId ->
                repository.reopenAsDraft(reportId)
            }
        }
    }
    
    /**
     * Update trasferta status.
     */
    fun updateTrasferta(trasferta: Boolean) {
        viewModelScope.launch {
            _currentReportId.value?.let { reportId ->
                repository.updateTrasferta(reportId, trasferta)
            }
        }
    }
    
    // ==================== Client Section Operations ====================
    
    /**
     * Add a new client section (progressive save).
     */
    fun addClientSection(
        clientName: String,
        jobSite: String,
        colorClass: String = "color-1"
    ) {
        viewModelScope.launch {
            _currentReportId.value?.let { reportId ->
                repository.addClientSection(reportId, clientName, jobSite, colorClass)
            }
        }
    }
    
    /**
     * Delete a client section.
     */
    fun deleteClientSection(clientSection: ClientSectionEntity) {
        viewModelScope.launch {
            repository.deleteClientSection(clientSection)
        }
    }
    
    // ==================== Activity Operations ====================
    
    /**
     * Add a machine activity (progressive save).
     */
    fun addMachineActivity(
        clientSectionId: Long,
        machine: String,
        hours: Double,
        description: String = ""
    ) {
        viewModelScope.launch {
            repository.addMachineActivity(clientSectionId, machine, hours, description)
            // Refresh activities for this client section
            refreshActivitiesForClientSection(clientSectionId)
        }
    }
    
    /**
     * Add a material activity (progressive save).
     */
    fun addMaterialActivity(
        clientSectionId: Long,
        materialName: String,
        quantity: Double,
        unit: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.addMaterialActivity(clientSectionId, materialName, quantity, unit, notes)
            // Refresh activities for this client section
            refreshActivitiesForClientSection(clientSectionId)
        }
    }
    
    /**
     * Delete an activity.
     */
    fun deleteActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
            // Refresh activities for the client section
            refreshActivitiesForClientSection(activity.clientSectionId)
        }
    }
    
    /**
     * Refresh activities for a specific client section.
     */
    private suspend fun refreshActivitiesForClientSection(clientSectionId: Long) {
        val activities = repository.getActivitiesForClientSection(clientSectionId).first()
        val currentMap = _activitiesByClientSection.value.toMutableMap()
        currentMap[clientSectionId] = activities
        _activitiesByClientSection.value = currentMap
    }
}
