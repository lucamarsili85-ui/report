// Storage keys
const CURRENT_REPORT_KEY = 'currentDailyReport';
const SAVED_REPORTS_KEY = 'savedDailyReports';

// Status constants
const STATUS_DRAFT = 'draft';
const STATUS_FINAL = 'final';

// Application state
let currentReport = null;
let savedReports = [];
let clientColorIndex = 0;
let confirmCallback = null;

// Client colors for visual distinction
const CLIENT_COLORS = ['color-1', 'color-2', 'color-3', 'color-4', 'color-5', 'color-6'];

// Data Models
class DailyReport {
    constructor(date) {
        this.id = Date.now();
        this.date = date; // YYYY-MM-DD
        this.clients = [];
        this.status = STATUS_DRAFT; // 'draft' or 'final'
        this.createdAt = Date.now();
    }
    
    getTotalHours() {
        let total = 0;
        this.clients.forEach(client => {
            client.activities.forEach(activity => {
                if (activity.type === 'machine' && activity.hours) {
                    total += parseFloat(activity.hours) || 0;
                }
            });
        });
        return total;
    }
    
    isDraft() {
        return this.status === STATUS_DRAFT;
    }
    
    isFinalized() {
        return this.status === STATUS_FINAL;
    }
}

class ClientSection {
    constructor(clientName, jobSite) {
        this.id = Date.now() + Math.random();
        this.clientName = clientName;
        this.jobSite = jobSite;
        this.activities = [];
        this.colorClass = CLIENT_COLORS[clientColorIndex % CLIENT_COLORS.length];
        clientColorIndex++;
    }
}

class Activity {
    constructor(type, data) {
        this.id = Date.now() + Math.random();
        this.type = type; // 'machine' or 'material'
        this.createdAt = Date.now();
        Object.assign(this, data);
    }
}

// Initialize the app
document.addEventListener('DOMContentLoaded', () => {
    loadDataFromStorage();
    setupEventListeners();
    showScreen('home-screen');
});

// Load data from localStorage
function loadDataFromStorage() {
    try {
        const currentReportData = localStorage.getItem(CURRENT_REPORT_KEY);
        if (currentReportData) {
            const data = JSON.parse(currentReportData);
            // Reconstruct the DailyReport object with methods
            currentReport = Object.assign(new DailyReport(data.date), data);
        }
        
        const savedReportsData = localStorage.getItem(SAVED_REPORTS_KEY);
        if (savedReportsData) {
            savedReports = JSON.parse(savedReportsData);
        }
    } catch (error) {
        console.error('Error loading data:', error);
    }
}

// Save data to localStorage
function saveCurrentReport() {
    try {
        if (currentReport) {
            localStorage.setItem(CURRENT_REPORT_KEY, JSON.stringify(currentReport));
        }
    } catch (error) {
        console.error('Error saving current report:', error);
    }
}

// Autosave draft - called whenever data changes
function autosaveDraft() {
    if (currentReport && currentReport.isDraft()) {
        saveCurrentReport();
        // TODO: Replace with proper logging mechanism in production
        // console.log('Draft autosaved at', new Date().toLocaleTimeString());
    }
}

function saveFinalizedReports() {
    try {
        localStorage.setItem(SAVED_REPORTS_KEY, JSON.stringify(savedReports));
    } catch (error) {
        console.error('Error saving reports:', error);
    }
}

// Setup event listeners
function setupEventListeners() {
    // Home screen
    document.getElementById('continue-draft-btn').addEventListener('click', continueDraft);
    document.getElementById('new-report-btn').addEventListener('click', startNewReport);
    document.getElementById('view-all-history-btn').addEventListener('click', showHistory);
    
    // Daily report screen
    document.getElementById('back-to-home').addEventListener('click', () => showScreen('home-screen'));
    document.getElementById('create-client-btn').addEventListener('click', createClientSection);
    document.getElementById('save-report-btn').addEventListener('click', handleSaveButtonClick);
    
    // History screen
    document.getElementById('back-from-history').addEventListener('click', () => showScreen('home-screen'));
    
    // Modal close
    document.getElementById('close-detail').addEventListener('click', () => hideModal('report-detail-modal'));
    
    // Confirm dialog
    document.getElementById('confirm-cancel').addEventListener('click', () => {
        hideModal('confirm-dialog');
        confirmCallback = null;
    });
    document.getElementById('confirm-ok').addEventListener('click', () => {
        if (confirmCallback) {
            confirmCallback();
            confirmCallback = null;
        }
        hideModal('confirm-dialog');
    });
}

// Handle save button click - either finalize or reopen for editing
function handleSaveButtonClick() {
    if (currentReport.isFinalized()) {
        reopenReportForEditing();
    } else {
        finalizeDailyReport();
    }
}

// Screen management
function showScreen(screenId) {
    document.querySelectorAll('.screen').forEach(screen => {
        screen.classList.add('hidden');
    });
    document.getElementById(screenId).classList.remove('hidden');
    
    // Update content when showing screens
    if (screenId === 'home-screen') {
        updateDashboard();
    } else if (screenId === 'daily-report-screen') {
        updateDailyReportScreen();
    } else if (screenId === 'history-screen') {
        updateHistoryScreen();
    }
}

function showModal(modalId) {
    document.getElementById(modalId).classList.remove('hidden');
}

function hideModal(modalId) {
    document.getElementById(modalId).classList.add('hidden');
}

// Start daily report
function startDailyReport() {
    const today = new Date().toISOString().split('T')[0];
    
    // Check if there's already a report for today
    if (!currentReport || currentReport.date !== today) {
        currentReport = new DailyReport(today);
        clientColorIndex = 0;
        autosaveDraft();
    }
    
    showScreen('daily-report-screen');
}

// Continue editing existing draft
function continueDraft() {
    if (currentReport) {
        showScreen('daily-report-screen');
    }
}

// Start a completely new report
function startNewReport() {
    const today = new Date().toISOString().split('T')[0];
    
    // If there's a current draft for today, just continue it
    if (currentReport && currentReport.date === today && currentReport.isDraft()) {
        showScreen('daily-report-screen');
        return;
    }
    
    // If there's a finalized report for today or a draft from another day, ask what to do
    if (currentReport && currentReport.isFinalized() && currentReport.date === today) {
        showConfirmDialog(
            'Rapporto già finalizzato',
            'Esiste già un rapporto finalizzato per oggi. Vuoi modificarlo?',
            () => {
                reopenReportForEditing();
            }
        );
        return;
    }
    
    // Otherwise create a new report
    currentReport = new DailyReport(today);
    clientColorIndex = 0;
    autosaveDraft();
    showScreen('daily-report-screen');
}

// Update dashboard with summary information
function updateDashboard() {
    const today = new Date().toISOString().split('T')[0];
    
    // Update today's status
    const todayStatusEl = document.getElementById('today-status');
    const continueDraftBtn = document.getElementById('continue-draft-btn');
    
    if (currentReport && currentReport.date === today && currentReport.isDraft()) {
        todayStatusEl.textContent = 'In corso';
        continueDraftBtn.classList.remove('hidden');
    } else if (currentReport && currentReport.date === today && currentReport.isFinalized()) {
        todayStatusEl.textContent = 'Completato';
        continueDraftBtn.classList.add('hidden');
    } else {
        todayStatusEl.textContent = 'Nessuna bozza';
        continueDraftBtn.classList.add('hidden');
    }
    
    // Update total reports count
    document.getElementById('total-reports').textContent = savedReports.length;
    
    // Calculate week hours
    const weekHours = calculateWeekHours();
    document.getElementById('week-hours').textContent = weekHours.toFixed(1);
    
    // Update latest reports preview (show last 3)
    updateLatestReportsPreview();
}

// Calculate total hours for current week (Monday to Sunday)
function calculateWeekHours() {
    const now = new Date();
    const startOfWeek = getStartOfWeek(now);
    
    let totalHours = 0;
    const countedIds = new Set();
    
    // Count finalized reports from this week
    savedReports.forEach(report => {
        const reportDate = new Date(report.date + 'T00:00:00');
        if (reportDate >= startOfWeek && reportDate <= now) {
            totalHours += report.totalHours || 0;
            countedIds.add(report.id);
        }
    });
    
    // Add current draft if it's from this week and not already counted
    if (currentReport && !countedIds.has(currentReport.id)) {
        const reportDate = new Date(currentReport.date + 'T00:00:00');
        if (reportDate >= startOfWeek && reportDate <= now) {
            totalHours += currentReport.getTotalHours();
        }
    }
    
    return totalHours;
}

// Get Monday of current week
function getStartOfWeek(date) {
    const startOfWeek = new Date(date);
    const day = date.getDay();
    // If Sunday (0), go back 6 days to Monday; otherwise calculate difference from Monday
    const diff = day === 0 ? -6 : 1 - day;
    startOfWeek.setDate(date.getDate() + diff);
    startOfWeek.setHours(0, 0, 0, 0);
    return startOfWeek;
}

// Update latest reports preview on dashboard
function updateLatestReportsPreview() {
    const container = document.getElementById('latest-reports-list');
    const emptyState = document.getElementById('empty-latest');
    
    if (savedReports.length === 0) {
        container.innerHTML = '';
        emptyState.classList.remove('hidden');
        return;
    }
    
    emptyState.classList.add('hidden');
    container.innerHTML = '';
    
    // Show last 3 reports
    const reportsToShow = savedReports.slice(0, 3);
    
    reportsToShow.forEach((report, index) => {
        const previewCard = createReportPreviewCard(report, index);
        container.appendChild(previewCard);
    });
}

// Create report preview card for dashboard
function createReportPreviewCard(report, index) {
    const div = document.createElement('div');
    div.className = 'report-preview-card';
    
    // Get first job site and main machines
    const firstClient = report.clients[0];
    const jobSite = firstClient ? firstClient.jobSite : 'N/A';
    
    // Get main machines (first 2) - stop early for performance
    const machines = [];
    outerLoop:
    for (const client of report.clients) {
        for (const activity of client.activities) {
            if (activity.type === 'machine') {
                machines.push(activity.machine);
                if (machines.length >= 2) break outerLoop;
            }
        }
    }
    
    const machinesText = machines.length > 0 ? machines.join(', ') : 'Nessuna macchina';
    
    div.innerHTML = `
        <div class="report-preview-header">
            <div class="report-preview-date">${formatDateShort(report.date)}</div>
            <div class="report-preview-hours">${report.totalHours.toFixed(1)}h</div>
        </div>
        <div class="report-preview-info">📍 ${escapeHtml(jobSite)}</div>
        <div class="report-preview-machines">🔧 ${escapeHtml(machinesText)}</div>
    `;
    
    div.addEventListener('click', () => {
        showReportDetail(index);
    });
    
    return div;
}

// Format date in short format for preview
function formatDateShort(dateStr) {
    const date = new Date(dateStr + 'T00:00:00');
    const options = { day: 'numeric', month: 'short' };
    return date.toLocaleDateString('it-IT', options);
}

// Update daily report screen
function updateDailyReportScreen() {
    if (!currentReport) return;
    
    // Update date display
    const dateStr = formatDate(currentReport.date);
    document.getElementById('current-date').textContent = dateStr;
    
    const isInPreviewMode = currentReport.isFinalized();
    
    // Show/hide new client dashboard based on status
    const newClientDashboard = document.getElementById('new-client-dashboard');
    if (isInPreviewMode) {
        newClientDashboard.classList.add('hidden');
    } else {
        newClientDashboard.classList.remove('hidden');
    }
    
    // Update clients container
    const container = document.getElementById('clients-container');
    container.innerHTML = '';
    
    currentReport.clients.forEach((client, index) => {
        const clientElement = createClientElement(client, index, isInPreviewMode);
        container.appendChild(clientElement);
    });
    
    // Update total hours
    const totalHours = currentReport.getTotalHours();
    document.getElementById('total-hours').textContent = totalHours.toFixed(1);
    
    // Show/hide total hours and save button
    const totalHoursPreview = document.getElementById('total-hours-preview');
    const saveButton = document.getElementById('save-report-btn');
    
    if (currentReport.clients.length > 0) {
        totalHoursPreview.classList.remove('hidden');
        
        // Update save button based on status
        if (isInPreviewMode) {
            saveButton.textContent = 'Modifica Rapporto';
            saveButton.classList.remove('btn-success');
            saveButton.classList.add('btn-primary');
            saveButton.classList.remove('hidden');
        } else {
            saveButton.textContent = 'Salva Rapportino Giornaliero';
            saveButton.classList.add('btn-success');
            saveButton.classList.remove('btn-primary');
            saveButton.classList.remove('hidden');
        }
    } else {
        totalHoursPreview.classList.add('hidden');
        saveButton.classList.add('hidden');
    }
}

// Create client section element
function createClientElement(client, clientIndex, isPreviewMode = false) {
    const div = document.createElement('div');
    div.className = `client-section ${client.colorClass}`;
    
    // In preview mode, don't show delete button or add activity buttons
    const deleteButtonHtml = !isPreviewMode 
        ? `<button class="icon-btn delete-client-btn" data-index="${clientIndex}" title="Elimina cliente">🗑️</button>` 
        : '';
    
    const activityButtonsHtml = !isPreviewMode 
        ? `<div class="activity-buttons">
            <button class="btn btn-secondary btn-small add-machine-btn" data-index="${clientIndex}">+ Aggiungi Macchina</button>
            <button class="btn btn-secondary btn-small add-material-btn" data-index="${clientIndex}">+ Aggiungi Materiale</button>
        </div>` 
        : '';
    
    div.innerHTML = `
        <div class="client-header">
            <div class="client-info">
                <h3>${escapeHtml(client.clientName)}</h3>
                <p>${escapeHtml(client.jobSite)}</p>
            </div>
            <div class="client-actions">
                ${deleteButtonHtml}
            </div>
        </div>
        
        ${activityButtonsHtml}
        
        <div class="activities-list" id="activities-${clientIndex}"></div>
    `;
    
    // Add activities
    const activitiesList = div.querySelector(`#activities-${clientIndex}`);
    client.activities.forEach((activity, actIndex) => {
        const activityElement = createActivityElement(activity, clientIndex, actIndex, isPreviewMode);
        activitiesList.appendChild(activityElement);
    });
    
    // Event listeners (only if not in preview mode)
    if (!isPreviewMode) {
        const deleteBtn = div.querySelector('.delete-client-btn');
        if (deleteBtn) {
            deleteBtn.addEventListener('click', (e) => {
                const index = parseInt(e.currentTarget.dataset.index);
                deleteClient(index);
            });
        }
        
        const addMachineBtn = div.querySelector('.add-machine-btn');
        if (addMachineBtn) {
            addMachineBtn.addEventListener('click', (e) => {
                const index = parseInt(e.currentTarget.dataset.index);
                addMachineActivity(index);
            });
        }
        
        const addMaterialBtn = div.querySelector('.add-material-btn');
        if (addMaterialBtn) {
            addMaterialBtn.addEventListener('click', (e) => {
                const index = parseInt(e.currentTarget.dataset.index);
                addMaterialActivity(index);
            });
        }
    }
    
    return div;
}

// Create activity element
function createActivityElement(activity, clientIndex, actIndex, isPreviewMode = false) {
    const div = document.createElement('div');
    div.className = 'activity-item';
    
    let detailsHtml = '';
    
    if (activity.type === 'machine') {
        detailsHtml = `
            <div class="activity-type">Macchina</div>
            <div class="activity-details">
                <strong>${escapeHtml(activity.machine)}</strong> - ${activity.hours} ore
            </div>
            ${activity.description ? `<div class="activity-notes">${escapeHtml(activity.description)}</div>` : ''}
        `;
    } else if (activity.type === 'material') {
        detailsHtml = `
            <div class="activity-type">Materiale</div>
            <div class="activity-details">
                <strong>${escapeHtml(activity.name)}</strong> - ${activity.quantity} ${activity.unit}
            </div>
            ${activity.notes ? `<div class="activity-notes">${escapeHtml(activity.notes)}</div>` : ''}
        `;
    }
    
    const deleteButtonHtml = !isPreviewMode 
        ? `<button class="delete-activity-btn" data-client="${clientIndex}" data-activity="${actIndex}">×</button>` 
        : '';
    
    div.innerHTML = `
        <div class="activity-info">
            ${detailsHtml}
        </div>
        ${deleteButtonHtml}
    `;
    
    if (!isPreviewMode) {
        const deleteBtn = div.querySelector('.delete-activity-btn');
        if (deleteBtn) {
            deleteBtn.addEventListener('click', (e) => {
                const cIndex = parseInt(e.currentTarget.dataset.client);
                const aIndex = parseInt(e.currentTarget.dataset.activity);
                deleteActivity(cIndex, aIndex);
            });
        }
    }
    
    return div;
}

// Create client section
function createClientSection() {
    const clientName = document.getElementById('client-name').value.trim();
    const jobSite = document.getElementById('job-site').value.trim();
    
    if (!clientName || !jobSite) {
        alert('Inserisci nome cliente e località cantiere');
        return;
    }
    
    const client = new ClientSection(clientName, jobSite);
    currentReport.clients.push(client);
    
    // Clear form
    document.getElementById('client-name').value = '';
    document.getElementById('job-site').value = '';
    
    autosaveDraft();
    updateDailyReportScreen();
}

// Add machine activity
function addMachineActivity(clientIndex) {
    const client = currentReport.clients[clientIndex];
    
    const machine = prompt('Nome macchina:', '');
    if (!machine) return;
    
    const hours = prompt('Ore lavorate (es. 8.5):', '');
    if (!hours) return;
    
    const hoursNum = parseFloat(hours);
    if (isNaN(hoursNum) || hoursNum <= 0) {
        alert('Inserisci un numero valido per le ore');
        return;
    }
    
    const description = prompt('Descrizione attività (opzionale):', '');
    
    const activity = new Activity('machine', {
        machine: machine.trim(),
        hours: hoursNum,
        description: description ? description.trim() : ''
    });
    
    client.activities.push(activity);
    autosaveDraft();
    updateDailyReportScreen();
}

// Add material activity
function addMaterialActivity(clientIndex) {
    const client = currentReport.clients[clientIndex];
    
    const name = prompt('Nome materiale:', '');
    if (!name) return;
    
    const quantity = prompt('Quantità:', '');
    if (!quantity) return;
    
    const quantityNum = parseFloat(quantity);
    if (isNaN(quantityNum) || quantityNum <= 0) {
        alert('Inserisci un numero valido per la quantità');
        return;
    }
    
    // Unit selection
    const unitChoice = confirm('Premi OK per m³, Annulla per tonnellate');
    const unit = unitChoice ? 'm³' : 'ton';
    
    const notes = prompt('Note (opzionale):', '');
    
    const activity = new Activity('material', {
        name: name.trim(),
        quantity: quantityNum,
        unit: unit,
        notes: notes ? notes.trim() : ''
    });
    
    client.activities.push(activity);
    autosaveDraft();
    updateDailyReportScreen();
}

// Delete client
function deleteClient(index) {
    showConfirmDialog(
        'Elimina Cliente',
        'Sei sicuro di voler eliminare questo cliente e tutte le sue attività?',
        () => {
            currentReport.clients.splice(index, 1);
            autosaveDraft();
            updateDailyReportScreen();
        }
    );
}

// Delete activity
function deleteActivity(clientIndex, activityIndex) {
    showConfirmDialog(
        'Elimina Attività',
        'Sei sicuro di voler eliminare questa attività?',
        () => {
            currentReport.clients[clientIndex].activities.splice(activityIndex, 1);
            autosaveDraft();
            updateDailyReportScreen();
        }
    );
}

// Finalize daily report
function finalizeDailyReport() {
    if (!currentReport || currentReport.clients.length === 0) {
        alert('Aggiungi almeno un cliente prima di salvare il rapporto');
        return;
    }
    
    // Check if there are any activities
    let hasActivities = false;
    currentReport.clients.forEach(client => {
        if (client.activities.length > 0) {
            hasActivities = true;
        }
    });
    
    if (!hasActivities) {
        alert('Aggiungi almeno un\'attività prima di salvare il rapporto');
        return;
    }
    
    showConfirmDialog(
        'Finalizza Rapportino',
        'Confermi di voler salvare il rapportino giornaliero? Passerà in modalità anteprima.',
        () => {
            currentReport.status = STATUS_FINAL;
            currentReport.finalizedAt = Date.now();
            currentReport.totalHours = currentReport.getTotalHours();
            
            savedReports.unshift(currentReport);
            saveFinalizedReports();
            
            // Keep current report but in finalized state
            saveCurrentReport();
            
            alert('Rapportino finalizzato! Ora è in modalità anteprima.');
            updateDailyReportScreen();
        }
    );
}

// Reopen a finalized report for editing
function reopenReportForEditing() {
    showConfirmDialog(
        'Modifica Rapportino',
        'Vuoi modificare questo rapporto? Tornerà in modalità bozza.',
        () => {
            // Change status back to draft
            currentReport.status = STATUS_DRAFT;
            currentReport.finalizedAt = null;
            
            // Remove from saved reports
            savedReports = savedReports.filter(r => r.id !== currentReport.id);
            saveFinalizedReports();
            
            // Save as current draft
            saveCurrentReport();
            
            alert('Rapporto riaperto per modifica');
            updateDailyReportScreen();
        }
    );
}

// Show history
function showHistory() {
    showScreen('history-screen');
}

// Update history screen
function updateHistoryScreen() {
    const reportsList = document.getElementById('reports-list');
    const emptyState = document.getElementById('empty-history');
    
    if (savedReports.length === 0) {
        reportsList.innerHTML = '';
        emptyState.classList.remove('hidden');
        return;
    }
    
    emptyState.classList.add('hidden');
    reportsList.innerHTML = '';
    
    savedReports.forEach((report, index) => {
        const card = createReportCard(report, index);
        reportsList.appendChild(card);
    });
}

// Create report card
function createReportCard(report, index) {
    const div = document.createElement('div');
    div.className = 'report-card';
    
    const clientNames = report.clients.map(c => c.clientName).join(', ');
    
    div.innerHTML = `
        <div class="report-header">
            <div class="report-date">${formatDate(report.date)}</div>
            <div class="report-hours">${report.totalHours.toFixed(1)} ore</div>
        </div>
        <div class="report-clients">
            Clienti: ${escapeHtml(clientNames)}
        </div>
        <div class="report-actions">
            <button class="btn btn-primary btn-small view-report-btn" data-index="${index}">Visualizza</button>
            <button class="btn btn-secondary btn-small edit-report-btn" data-index="${index}">Modifica</button>
        </div>
    `;
    
    div.querySelector('.view-report-btn').addEventListener('click', (e) => {
        const idx = parseInt(e.currentTarget.dataset.index);
        showReportDetail(idx);
    });
    
    div.querySelector('.edit-report-btn').addEventListener('click', (e) => {
        const idx = parseInt(e.currentTarget.dataset.index);
        editReportFromHistory(idx);
    });
    
    return div;
}

// Edit a report from history
function editReportFromHistory(index) {
    const report = savedReports[index];
    
    showConfirmDialog(
        'Modifica Rapporto',
        `Vuoi modificare il rapporto del ${formatDate(report.date)}? Diventerà la bozza corrente.`,
        () => {
            // Deep clone the report to avoid reference issues
            currentReport = JSON.parse(JSON.stringify(report));
            currentReport.status = STATUS_DRAFT;
            currentReport.finalizedAt = null;
            
            // Remove from saved reports
            savedReports.splice(index, 1);
            saveFinalizedReports();
            
            // Save as current draft
            saveCurrentReport();
            
            // Navigate to daily report screen
            showScreen('daily-report-screen');
        }
    );
}

// Show report detail
function showReportDetail(index) {
    const report = savedReports[index];
    const content = document.getElementById('report-detail-content');
    
    let html = `
        <h3>Rapporto del ${formatDate(report.date)}</h3>
        <p><strong>Totale ore:</strong> ${report.totalHours.toFixed(1)}</p>
        <hr style="margin: 20px 0; border: none; border-top: 1px solid #ddd;">
    `;
    
    report.clients.forEach((client, cIndex) => {
        html += `
            <div style="margin-bottom: 30px; padding: 15px; background: #f5f5f5; border-radius: 8px; border-left: 4px solid ${getColorValue(client.colorClass)};">
                <h4 style="color: #4CAF50; margin-bottom: 10px;">${escapeHtml(client.clientName)}</h4>
                <p style="color: #666; margin-bottom: 15px;">${escapeHtml(client.jobSite)}</p>
        `;
        
        if (client.activities.length > 0) {
            html += '<div style="margin-top: 10px;">';
            client.activities.forEach(activity => {
                if (activity.type === 'machine') {
                    html += `
                        <div style="background: white; padding: 10px; margin-bottom: 8px; border-radius: 4px;">
                            <div style="font-size: 12px; color: #666; text-transform: uppercase; font-weight: 600;">MACCHINA</div>
                            <div><strong>${escapeHtml(activity.machine)}</strong> - ${activity.hours} ore</div>
                            ${activity.description ? `<div style="font-size: 14px; color: #666; font-style: italic; margin-top: 4px;">${escapeHtml(activity.description)}</div>` : ''}
                        </div>
                    `;
                } else if (activity.type === 'material') {
                    html += `
                        <div style="background: white; padding: 10px; margin-bottom: 8px; border-radius: 4px;">
                            <div style="font-size: 12px; color: #666; text-transform: uppercase; font-weight: 600;">MATERIALE</div>
                            <div><strong>${escapeHtml(activity.name)}</strong> - ${activity.quantity} ${activity.unit}</div>
                            ${activity.notes ? `<div style="font-size: 14px; color: #666; font-style: italic; margin-top: 4px;">${escapeHtml(activity.notes)}</div>` : ''}
                        </div>
                    `;
                }
            });
            html += '</div>';
        }
        
        html += '</div>';
    });
    
    content.innerHTML = html;
    showModal('report-detail-modal');
}

// Confirm dialog helper
function showConfirmDialog(title, message, callback) {
    document.getElementById('confirm-title').textContent = title;
    document.getElementById('confirm-message').textContent = message;
    confirmCallback = callback;
    showModal('confirm-dialog');
}

// Utility functions
function formatDate(dateStr) {
    const date = new Date(dateStr + 'T00:00:00');
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    return date.toLocaleDateString('it-IT', options);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function getColorValue(colorClass) {
    const colors = {
        'color-1': '#4CAF50',
        'color-2': '#2196F3',
        'color-3': '#FF9800',
        'color-4': '#9C27B0',
        'color-5': '#F44336',
        'color-6': '#00BCD4'
    };
    return colors[colorClass] || '#4CAF50';
}
