// Storage keys
const CURRENT_REPORT_KEY = 'currentDailyReport';
const SAVED_REPORTS_KEY = 'savedDailyReports';

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
        this.status = 'draft'; // 'draft' or 'finalized'
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
            currentReport = JSON.parse(currentReportData);
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
    document.getElementById('start-report-btn').addEventListener('click', startDailyReport);
    document.getElementById('history-btn').addEventListener('click', showHistory);
    
    // Daily report screen
    document.getElementById('back-to-home').addEventListener('click', () => showScreen('home-screen'));
    document.getElementById('create-client-btn').addEventListener('click', createClientSection);
    document.getElementById('save-report-btn').addEventListener('click', finalizeDailyReport);
    
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

// Screen management
function showScreen(screenId) {
    document.querySelectorAll('.screen').forEach(screen => {
        screen.classList.add('hidden');
    });
    document.getElementById(screenId).classList.remove('hidden');
    
    // Update content when showing screens
    if (screenId === 'daily-report-screen') {
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
        saveCurrentReport();
    }
    
    showScreen('daily-report-screen');
}

// Update daily report screen
function updateDailyReportScreen() {
    if (!currentReport) return;
    
    // Update date display
    const dateStr = formatDate(currentReport.date);
    document.getElementById('current-date').textContent = dateStr;
    
    // Update clients container
    const container = document.getElementById('clients-container');
    container.innerHTML = '';
    
    currentReport.clients.forEach((client, index) => {
        const clientElement = createClientElement(client, index);
        container.appendChild(clientElement);
    });
    
    // Update total hours
    const totalHours = currentReport.getTotalHours();
    document.getElementById('total-hours').textContent = totalHours.toFixed(1);
    
    // Show/hide total hours and save button
    if (currentReport.clients.length > 0) {
        document.getElementById('total-hours-preview').classList.remove('hidden');
        document.getElementById('save-report-btn').classList.remove('hidden');
    } else {
        document.getElementById('total-hours-preview').classList.add('hidden');
        document.getElementById('save-report-btn').classList.add('hidden');
    }
}

// Create client section element
function createClientElement(client, clientIndex) {
    const div = document.createElement('div');
    div.className = `client-section ${client.colorClass}`;
    
    div.innerHTML = `
        <div class="client-header">
            <div class="client-info">
                <h3>${escapeHtml(client.clientName)}</h3>
                <p>${escapeHtml(client.jobSite)}</p>
            </div>
            <div class="client-actions">
                <button class="icon-btn delete-client-btn" data-index="${clientIndex}" title="Elimina cliente">🗑️</button>
            </div>
        </div>
        
        <div class="activity-buttons">
            <button class="btn btn-secondary btn-small add-machine-btn" data-index="${clientIndex}">+ Aggiungi Macchina</button>
            <button class="btn btn-secondary btn-small add-material-btn" data-index="${clientIndex}">+ Aggiungi Materiale</button>
        </div>
        
        <div class="activities-list" id="activities-${clientIndex}"></div>
    `;
    
    // Add activities
    const activitiesList = div.querySelector(`#activities-${clientIndex}`);
    client.activities.forEach((activity, actIndex) => {
        const activityElement = createActivityElement(activity, clientIndex, actIndex);
        activitiesList.appendChild(activityElement);
    });
    
    // Event listeners
    div.querySelector('.delete-client-btn').addEventListener('click', (e) => {
        const index = parseInt(e.currentTarget.dataset.index);
        deleteClient(index);
    });
    
    div.querySelector('.add-machine-btn').addEventListener('click', (e) => {
        const index = parseInt(e.currentTarget.dataset.index);
        addMachineActivity(index);
    });
    
    div.querySelector('.add-material-btn').addEventListener('click', (e) => {
        const index = parseInt(e.currentTarget.dataset.index);
        addMaterialActivity(index);
    });
    
    return div;
}

// Create activity element
function createActivityElement(activity, clientIndex, actIndex) {
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
    
    div.innerHTML = `
        <div class="activity-info">
            ${detailsHtml}
        </div>
        <button class="delete-activity-btn" data-client="${clientIndex}" data-activity="${actIndex}">×</button>
    `;
    
    div.querySelector('.delete-activity-btn').addEventListener('click', (e) => {
        const cIndex = parseInt(e.currentTarget.dataset.client);
        const aIndex = parseInt(e.currentTarget.dataset.activity);
        deleteActivity(cIndex, aIndex);
    });
    
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
    
    saveCurrentReport();
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
    saveCurrentReport();
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
    saveCurrentReport();
    updateDailyReportScreen();
}

// Delete client
function deleteClient(index) {
    showConfirmDialog(
        'Elimina Cliente',
        'Sei sicuro di voler eliminare questo cliente e tutte le sue attività?',
        () => {
            currentReport.clients.splice(index, 1);
            saveCurrentReport();
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
            saveCurrentReport();
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
        'Confermi di voler salvare il rapportino giornaliero? Non potrai più modificarlo.',
        () => {
            currentReport.status = 'finalized';
            currentReport.finalizedAt = Date.now();
            currentReport.totalHours = currentReport.getTotalHours();
            
            savedReports.unshift(currentReport);
            saveFinalizedReports();
            
            // Clear current report
            localStorage.removeItem(CURRENT_REPORT_KEY);
            currentReport = null;
            
            alert('Rapportino salvato con successo!');
            showScreen('home-screen');
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
        </div>
    `;
    
    div.querySelector('.view-report-btn').addEventListener('click', (e) => {
        const idx = parseInt(e.currentTarget.dataset.index);
        showReportDetail(idx);
    });
    
    return div;
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
