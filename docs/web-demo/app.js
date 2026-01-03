// Storage keys
const DRAFT_DAYS_KEY = 'draftDays';
const SAVED_REPORTS_KEY = 'savedReports';
const ROLE_KEY = 'userRole';

// Application state
let draftDays = []; // Array of DraftDay objects
let savedReports = []; // Array of finalized reports
let currentDraftDay = null; // Currently selected DraftDay
let userRole = null;
let entryCounter = 0;
let clientCounter = 0;
let currentScreen = 'dashboard';

// Data Models
class DraftDay {
    constructor(date, role) {
        this.id = Date.now() + Math.random();
        this.date = date; // Date string YYYY-MM-DD
        this.role = role;
        this.status = 'draft'; // 'draft' or 'closed'
        this.clients = []; // Array of ClientSection objects
        this.createdAt = Date.now();
    }
    
    getTotalHours() {
        let total = 0;
        this.clients.forEach(client => {
            client.entries.forEach(entry => {
                if (entry.type === 'activity' && entry.data.hours) {
                    total += parseFloat(entry.data.hours);
                }
            });
        });
        return total;
    }
}

class ClientSection {
    constructor() {
        this.id = Date.now() + Math.random();
        this.clientName = '';
        this.jobSiteName = '';
        this.jobSiteLocation = '';
        this.entries = []; // Array of Entry objects
    }
}

class Entry {
    constructor(type) {
        this.id = Date.now() + Math.random();
        this.type = type; // 'activity', 'material', 'vehicleMovement'
        this.data = {};
        this.createdAt = Date.now();
    }
}

// Initialize the app
document.addEventListener('DOMContentLoaded', () => {
    loadDataFromStorage();
    loadUserRole();
    
    if (!userRole) {
        showRolePicker();
    } else {
        initializeApp();
    }
});

// Role management
function loadUserRole() {
    try {
        userRole = localStorage.getItem(ROLE_KEY);
    } catch (error) {
        console.error('Error loading user role:', error);
    }
}

function saveUserRole(role) {
    try {
        userRole = role;
        localStorage.setItem(ROLE_KEY, role);
    } catch (error) {
        console.error('Error saving user role:', error);
    }
}

function showRolePicker() {
    const modal = document.getElementById('role-picker-modal');
    modal.classList.remove('hidden');
    
    document.querySelectorAll('.role-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const role = btn.dataset.role;
            saveUserRole(role);
            modal.classList.add('hidden');
            initializeApp();
        });
    });
}

// Initialize app after role is set
function initializeApp() {
    setupEventListeners();
    setDefaultDate();
    loadOrCreateTodayDraft();
    updateDashboard();
    renderArchiveReports();
    switchScreen('home');
}

// LocalStorage operations
function loadDataFromStorage() {
    try {
        const draftData = localStorage.getItem(DRAFT_DAYS_KEY);
        if (draftData) {
            draftDays = JSON.parse(draftData);
        }
        
        const savedData = localStorage.getItem(SAVED_REPORTS_KEY);
        if (savedData) {
            savedReports = JSON.parse(savedData);
        }
    } catch (error) {
        console.error('Error loading data from storage:', error);
        draftDays = [];
        savedReports = [];
    }
}

function saveDraftDaysToStorage() {
    try {
        localStorage.setItem(DRAFT_DAYS_KEY, JSON.stringify(draftDays));
    } catch (error) {
        console.error('Error saving draft days to storage:', error);
        alert('Impossibile salvare i dati. Lo storage potrebbe essere pieno.');
    }
}

function saveSavedReportsToStorage() {
    try {
        localStorage.setItem(SAVED_REPORTS_KEY, JSON.stringify(savedReports));
    } catch (error) {
        console.error('Error saving reports to storage:', error);
        alert('Impossibile salvare i dati. Lo storage potrebbe essere pieno.');
    }
}

// Draft Day Management
function loadOrCreateTodayDraft() {
    const today = new Date().toISOString().split('T')[0];
    let draft = draftDays.find(d => d.date === today && d.status === 'draft');
    
    if (!draft) {
        draft = new DraftDay(today, userRole);
        draftDays.push(draft);
        saveDraftDaysToStorage();
    }
    
    currentDraftDay = draft;
    renderDailyPreview();
}

function getDraftDayByDate(date) {
    return draftDays.find(d => d.date === date);
}

function finalizeDraftDay() {
    if (!currentDraftDay) return;
    
    if (currentDraftDay.clients.length === 0) {
        alert('Aggiungi almeno un cliente prima di finalizzare il giorno.');
        return;
    }
    
    if (!confirm('Sei sicuro di voler finalizzare questo giorno? Non potrai più modificarlo.')) {
        return;
    }
    
    currentDraftDay.status = 'closed';
    
    // Create saved report from draft day
    const report = {
        id: currentDraftDay.id,
        date: currentDraftDay.date,
        role: currentDraftDay.role,
        clients: currentDraftDay.clients,
        totalHours: currentDraftDay.getTotalHours(),
        createdAt: currentDraftDay.createdAt,
        finalizedAt: Date.now()
    };
    
    savedReports.push(report);
    saveDraftDaysToStorage();
    saveSavedReportsToStorage();
    
    alert('Giorno finalizzato con successo!');
    
    // Load next day or create new
    loadOrCreateTodayDraft();
    updateDashboard();
    switchScreen('home');
}

// Event listeners setup
function setupEventListeners() {
    // Bottom navigation
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const screen = btn.dataset.screen;
            switchScreen(screen);
        });
    });

    // Home cards navigation
    document.querySelectorAll('.home-card').forEach(card => {
        card.addEventListener('click', () => {
            const navigate = card.dataset.navigate;
            if (navigate) {
                switchScreen(navigate);
            }
        });
    });

    // Settings
    const openSettingsBtn = document.getElementById('open-settings');
    if (openSettingsBtn) openSettingsBtn.addEventListener('click', openSettings);
    
    const closeSettingsBtn = document.getElementById('close-settings');
    if (closeSettingsBtn) closeSettingsBtn.addEventListener('click', closeSettings);
    
    const changeRoleBtn = document.getElementById('change-role-btn');
    if (changeRoleBtn) changeRoleBtn.addEventListener('click', changeRole);
    
    const clearDataBtn = document.getElementById('clear-data-btn');
    if (clearDataBtn) clearDataBtn.addEventListener('click', clearAllData);

    // Client management
    const addClientBtn = document.getElementById('add-client-btn');
    if (addClientBtn) addClientBtn.addEventListener('click', addNewClient);
    
    // Finalize day
    const finalizeDayBtn = document.getElementById('finalize-day-btn');
    if (finalizeDayBtn) finalizeDayBtn.addEventListener('click', finalizeDraftDay);
    
    // Archive filters
    const applyFiltersBtn = document.getElementById('apply-filters-btn');
    if (applyFiltersBtn) applyFiltersBtn.addEventListener('click', applyFilters);
    
    const clearFiltersBtn = document.getElementById('clear-filters-btn');
    if (clearFiltersBtn) clearFiltersBtn.addEventListener('click', clearFilters);
}

// Screen switching
function switchScreen(screenName) {
    currentScreen = screenName;
    
    // Update navigation
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.screen === screenName);
    });

    // Update screens
    document.querySelectorAll('.screen').forEach(screen => {
        screen.classList.remove('active');
    });

    // Map screen names to IDs
    const screenMap = {
        'home': 'home-screen',
        'rapportino': 'rapportino-screen',
        'storico': 'storico-screen'
    };

    const screenId = screenMap[screenName];
    document.getElementById(screenId).classList.add('active');

    // Update header
    const titles = {
        'home': 'Home',
        'rapportino': 'Rapportino Giornaliero',
        'storico': 'Storico'
    };
    document.getElementById('screen-title').textContent = titles[screenName];

    // Update header accent
    const accent = document.getElementById('header-accent');
    accent.className = 'header-accent ' + screenName;

    // Refresh data when needed
    if (screenName === 'home') {
        updateDashboard();
    } else if (screenName === 'rapportino') {
        renderDailyPreview();
    } else if (screenName === 'storico') {
        renderArchiveReports();
    }
}

// Settings
function openSettings() {
    document.getElementById('current-role-display').textContent = 
        userRole === 'autista' ? 'Autista 🚛' : 'Operatore 👷';
    document.getElementById('settings-modal').classList.remove('hidden');
}

function closeSettings() {
    document.getElementById('settings-modal').classList.add('hidden');
}

function changeRole() {
    if (confirm('Vuoi cambiare il tuo ruolo?')) {
        localStorage.removeItem(ROLE_KEY);
        userRole = null;
        closeSettings();
        showRolePicker();
    }
}

function clearAllData() {
    if (confirm('Sei sicuro di voler eliminare tutti i dati? Questa operazione non può essere annullata.')) {
        draftDays = [];
        savedReports = [];
        currentDraftDay = null;
        saveDraftDaysToStorage();
        saveSavedReportsToStorage();
        updateDashboard();
        renderRecentReports();
        renderArchiveReports();
        closeSettings();
        alert('Tutti i dati sono stati eliminati.');
        loadOrCreateTodayDraft();
    }
}

// Set default date
function setDefaultDate() {
    const today = new Date().toISOString().split('T')[0];
    const dateInput = document.getElementById('draft-date');
    if (dateInput) {
        dateInput.value = today;
    }
}

// Client Management
function addNewClient() {
    if (!currentDraftDay) {
        loadOrCreateTodayDraft();
    }
    
    const client = new ClientSection();
    currentDraftDay.clients.push(client);
    saveDraftDaysToStorage();
    renderDailyPreview();
}

function removeClient(clientId) {
    if (!confirm('Sei sicuro di voler rimuovere questo cliente?')) {
        return;
    }
    
    currentDraftDay.clients = currentDraftDay.clients.filter(c => c.id !== clientId);
    saveDraftDaysToStorage();
    renderDailyPreview();
}

function updateClientInfo(clientId, field, value) {
    const client = currentDraftDay.clients.find(c => c.id === clientId);
    if (client) {
        client[field] = value;
        saveDraftDaysToStorage();
    }
}

// Entry Management
function showEntryTypeModal() {
    const modal = document.getElementById('entry-type-modal');
    if (modal) {
        modal.classList.remove('hidden');
    }
}

function hideEntryTypeModal() {
    const modal = document.getElementById('entry-type-modal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

function addEntry(clientId, type) {
    const client = currentDraftDay.clients.find(c => c.id === clientId);
    if (!client) return;
    
    const entry = new Entry(type);
    
    // Initialize entry data based on type
    if (type === 'activity') {
        entry.data = { machine: '', hours: 8.0, notes: '' };
    } else if (type === 'material') {
        entry.data = { name: '', quantity: 0, unit: 'mc', fromLocation: '', toLocation: '' };
    } else if (type === 'vehicleMovement') {
        entry.data = { vehicle: '', transportMethod: '', fromLocation: '', toLocation: '', notes: '' };
    }
    
    client.entries.push(entry);
    saveDraftDaysToStorage();
    renderDailyPreview();
    hideEntryTypeModal();
}

function removeEntry(clientId, entryId) {
    if (!confirm('Sei sicuro di voler rimuovere questa voce?')) {
        return;
    }
    
    const client = currentDraftDay.clients.find(c => c.id === clientId);
    if (client) {
        client.entries = client.entries.filter(e => e.id !== entryId);
        saveDraftDaysToStorage();
        renderDailyPreview();
    }
}

function updateEntryData(clientId, entryId, field, value) {
    const client = currentDraftDay.clients.find(c => c.id === clientId);
    if (client) {
        const entry = client.entries.find(e => e.id === entryId);
        if (entry) {
            entry.data[field] = value;
            saveDraftDaysToStorage();
            renderDailyPreview();
        }
    }
}

// Dashboard calculations
function updateDashboard() {
    const weeklyHours = calculateWeeklyHours();
    const monthlyHours = calculateMonthlyHours();

    document.getElementById('weekly-hours').textContent = weeklyHours.toFixed(1);
    document.getElementById('monthly-hours').textContent = monthlyHours.toFixed(1);
}

function calculateWeeklyHours() {
    const now = new Date();
    const weekStart = new Date(now);
    weekStart.setDate(now.getDate() - now.getDay());
    weekStart.setHours(0, 0, 0, 0);
    const weekStartStr = weekStart.toISOString().split('T')[0];

    let total = 0;
    
    // From saved reports
    savedReports.forEach(report => {
        if (report.date >= weekStartStr) {
            total += report.totalHours || 0;
        }
    });
    
    // From draft days
    draftDays.forEach(draft => {
        if (draft.date >= weekStartStr && draft.status === 'draft') {
            total += draft.getTotalHours();
        }
    });

    return total;
}

function calculateMonthlyHours() {
    const now = new Date();
    const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);
    const monthStartStr = monthStart.toISOString().split('T')[0];

    let total = 0;
    
    // From saved reports
    savedReports.forEach(report => {
        if (report.date >= monthStartStr) {
            total += report.totalHours || 0;
        }
    });
    
    // From draft days
    draftDays.forEach(draft => {
        if (draft.date >= monthStartStr && draft.status === 'draft') {
            total += draft.getTotalHours();
        }
    });

    return total;
}

// Archive reports rendering
function renderArchiveReports(filteredReports = null) {
    const container = document.getElementById('archive-reports');
    const reportsToShow = filteredReports || [...savedReports].sort((a, b) => new Date(b.date) - new Date(a.date));

    if (reportsToShow.length === 0) {
        container.innerHTML = '<p class="empty-state">Nessun rapporto trovato.</p>';
        return;
    }

    container.innerHTML = reportsToShow.map(report => createReportCard(report)).join('');
    
    attachReportViewListeners(container);
}

// Create report card HTML
function createReportCard(report) {
    const date = new Date(report.date + 'T00:00:00');
    const formattedDate = date.toLocaleDateString('it-IT', { 
        weekday: 'short', 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });

    const clientsCount = report.clients ? report.clients.length : 0;
    const totalHours = report.totalHours || 0;

    return `
        <div class="report-card">
            <div class="report-header">
                <div class="report-date">${formattedDate}</div>
                <div class="report-hours">${totalHours.toFixed(1)} ore</div>
            </div>
            <div class="report-details">
                <div class="report-detail">
                    <strong>Clienti:</strong>
                    ${clientsCount} cliente${clientsCount !== 1 ? 'i' : ''}
                </div>
                <div class="report-detail">
                    <strong>Ruolo:</strong>
                    ${escapeHtml(report.role || 'N/A')}
                </div>
            </div>
            <button class="btn-view" data-id="${report.id}">Visualizza</button>
        </div>
    `;
}

function attachReportViewListeners(container) {
    container.querySelectorAll('.btn-view').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const id = parseFloat(e.target.dataset.id);
            viewReport(id);
        });
    });
}

function viewReport(reportId) {
    const report = savedReports.find(r => r.id === reportId);
    if (!report) return;
    
    // Show modal with report details
    showReportModal(report);
}

function showReportModal(report) {
    const modal = document.getElementById('report-view-modal');
    if (!modal) return;
    
    const content = document.getElementById('report-modal-content');
    const date = new Date(report.date + 'T00:00:00');
    const formattedDate = date.toLocaleDateString('it-IT', { 
        weekday: 'long', 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
    });
    
    let html = `
        <div class="report-view-header">
            <h2>${formattedDate}</h2>
            <p><strong>Ruolo:</strong> ${escapeHtml(report.role)}</p>
            <p><strong>Ore totali:</strong> ${report.totalHours.toFixed(1)}</p>
        </div>
    `;
    
    report.clients.forEach((client, idx) => {
        html += `
            <div class="client-section-view">
                <h3>Cliente ${idx + 1}</h3>
                <p><strong>Nome:</strong> ${escapeHtml(client.clientName || 'N/A')}</p>
                <p><strong>Cantiere:</strong> ${escapeHtml(client.jobSiteName || 'N/A')}</p>
                <p><strong>Località:</strong> ${escapeHtml(client.jobSiteLocation || 'N/A')}</p>
                
                <div class="entries-list">
                    ${renderEntriesView(client.entries)}
                </div>
            </div>
        `;
    });
    
    content.innerHTML = html;
    modal.classList.remove('hidden');
}

function hideReportModal() {
    const modal = document.getElementById('report-view-modal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

function renderEntriesView(entries) {
    if (entries.length === 0) {
        return '<p class="empty-state-small">Nessuna voce</p>';
    }
    
    return entries.map(entry => {
        if (entry.type === 'activity') {
            return `
                <div class="entry-view activity">
                    <strong>Attività</strong>
                    <p>Macchina: ${escapeHtml(entry.data.machine || 'N/A')}</p>
                    <p>Ore: ${entry.data.hours || 0}</p>
                    ${entry.data.notes ? `<p>Note: ${escapeHtml(entry.data.notes)}</p>` : ''}
                </div>
            `;
        } else if (entry.type === 'material') {
            const unit = entry.data.unit === 'mc' || entry.data.unit === 'metric' ? 'm³' : 'ton';
            return `
                <div class="entry-view material">
                    <strong>Materiale</strong>
                    <p>Nome: ${escapeHtml(entry.data.name || 'N/A')}</p>
                    <p>Quantità: ${entry.data.quantity || 0} ${unit}</p>
                    <p>Da: ${escapeHtml(entry.data.fromLocation || 'N/A')}</p>
                    <p>A: ${escapeHtml(entry.data.toLocation || 'N/A')}</p>
                </div>
            `;
        } else if (entry.type === 'vehicleMovement') {
            return `
                <div class="entry-view vehicle">
                    <strong>Movimento veicolo</strong>
                    <p>Veicolo: ${escapeHtml(entry.data.vehicle || 'N/A')}</p>
                    <p>Metodo: ${escapeHtml(entry.data.transportMethod || 'N/A')}</p>
                    <p>Da: ${escapeHtml(entry.data.fromLocation || 'N/A')}</p>
                    <p>A: ${escapeHtml(entry.data.toLocation || 'N/A')}</p>
                    ${entry.data.notes ? `<p>Note: ${escapeHtml(entry.data.notes)}</p>` : ''}
                </div>
            `;
        }
        return '';
    }).join('');
}

// Daily Preview Rendering
function renderDailyPreview() {
    if (!currentDraftDay) return;
    
    const container = document.getElementById('daily-preview');
    const dateDisplay = document.getElementById('current-date-display');
    const totalHoursDisplay = document.getElementById('total-hours-display');
    
    if (dateDisplay) {
        const date = new Date(currentDraftDay.date + 'T00:00:00');
        dateDisplay.textContent = date.toLocaleDateString('it-IT', { 
            weekday: 'long', 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
        });
    }
    
    if (totalHoursDisplay) {
        totalHoursDisplay.textContent = currentDraftDay.getTotalHours().toFixed(1);
    }
    
    if (!container) return;
    
    if (currentDraftDay.clients.length === 0) {
        container.innerHTML = '<p class="empty-state">Nessun cliente aggiunto. Clicca "Aggiungi Cliente" per iniziare.</p>';
        return;
    }
    
    container.innerHTML = currentDraftDay.clients.map((client, idx) => renderClientSection(client, idx)).join('');
    
    // Attach event listeners for client sections
    attachClientSectionListeners();
}

function renderClientSection(client, index) {
    // Generate a color for the client card based on index
    const colors = [
        '#81C784', // Green
        '#64B5F6', // Blue
        '#FFB74D', // Orange
        '#9575CD', // Purple
        '#4DD0E1', // Cyan
        '#F06292', // Pink
        '#AED581', // Light Green
        '#FFD54F', // Yellow
    ];
    const cardColor = colors[index % colors.length];
    
    return `
        <div class="client-section" data-client-id="${client.id}" style="border-left: 6px solid ${cardColor};">
            <div class="client-header" style="border-bottom-color: ${cardColor};">
                <div class="client-title">
                    <h3>Cliente ${index + 1}</h3>
                    <div class="client-location-badge">
                        ${escapeHtml(client.jobSiteLocation || 'Località non specificata')}
                    </div>
                </div>
                <button class="btn-remove-client" onclick="removeClient(${client.id})">Rimuovi</button>
            </div>
            
            <div class="client-info">
                <div class="form-field">
                    <label>Nome Cliente</label>
                    <input type="text" class="input-client" data-field="clientName" 
                           value="${escapeHtml(client.clientName)}" 
                           placeholder="Nome del cliente">
                </div>
                <div class="form-field">
                    <label>Nome Cantiere</label>
                    <input type="text" class="input-client" data-field="jobSiteName" 
                           value="${escapeHtml(client.jobSiteName)}" 
                           placeholder="Nome del cantiere">
                </div>
                <div class="form-field">
                    <label>Località Cantiere</label>
                    <input type="text" class="input-client" data-field="jobSiteLocation" 
                           value="${escapeHtml(client.jobSiteLocation)}" 
                           placeholder="Località del cantiere">
                </div>
            </div>
            
            <div class="entries-section">
                <h4>Voci</h4>
                <div class="entries-list">
                    ${renderClientEntries(client)}
                </div>
                <button class="btn btn-secondary btn-add-entry" data-client-id="${client.id}">
                    + Aggiungi Voce
                </button>
            </div>
        </div>
    `;
}

function renderClientEntries(client) {
    if (client.entries.length === 0) {
        return '<p class="empty-state-small">Nessuna voce. Aggiungi attività, materiali o movimenti.</p>';
    }
    
    return client.entries.map(entry => renderEntryForm(client.id, entry)).join('');
}

function renderEntryForm(clientId, entry) {
    if (entry.type === 'activity') {
        return `
            <div class="entry-form activity" data-entry-id="${entry.id}">
                <div class="entry-header">
                    <strong>Attività</strong>
                    <button class="btn-remove-entry" onclick="removeEntry(${clientId}, ${entry.id})">×</button>
                </div>
                <div class="entry-fields">
                    <input type="text" class="input-entry" data-field="machine" 
                           value="${escapeHtml(entry.data.machine || '')}" 
                           placeholder="Macchina" onchange="updateEntryData(${clientId}, ${entry.id}, 'machine', this.value)">
                    <input type="number" inputmode="decimal" class="input-entry" data-field="hours" 
                           value="${entry.data.hours || 8.0}" step="0.5" min="0.5" max="24"
                           placeholder="Ore" onchange="updateEntryData(${clientId}, ${entry.id}, 'hours', parseFloat(this.value))">
                    <textarea class="input-entry" data-field="notes" 
                              placeholder="Note (opzionale)" 
                              onchange="updateEntryData(${clientId}, ${entry.id}, 'notes', this.value)">${escapeHtml(entry.data.notes || '')}</textarea>
                </div>
            </div>
        `;
    } else if (entry.type === 'material') {
        return `
            <div class="entry-form material" data-entry-id="${entry.id}">
                <div class="entry-header">
                    <strong>Materiale</strong>
                    <button class="btn-remove-entry" onclick="removeEntry(${clientId}, ${entry.id})">×</button>
                </div>
                <div class="entry-fields">
                    <input type="text" class="input-entry" data-field="name" 
                           value="${escapeHtml(entry.data.name || '')}" 
                           placeholder="Nome materiale" onchange="updateEntryData(${clientId}, ${entry.id}, 'name', this.value)">
                    <input type="number" inputmode="decimal" class="input-entry" data-field="quantity" 
                           value="${entry.data.quantity || 0}" step="0.1" min="0"
                           placeholder="Quantità" onchange="updateEntryData(${clientId}, ${entry.id}, 'quantity', parseFloat(this.value))">
                    <div class="unit-toggle">
                        <label><input type="radio" name="unit-${entry.id}" value="mc" 
                                ${entry.data.unit === 'mc' || entry.data.unit === 'metric' ? 'checked' : ''} 
                                onchange="updateEntryData(${clientId}, ${entry.id}, 'unit', this.value)"> m³</label>
                        <label><input type="radio" name="unit-${entry.id}" value="ton" 
                                ${entry.data.unit === 'ton' ? 'checked' : ''} 
                                onchange="updateEntryData(${clientId}, ${entry.id}, 'unit', this.value)"> ton</label>
                    </div>
                    <input type="text" class="input-entry" data-field="fromLocation" 
                           value="${escapeHtml(entry.data.fromLocation || '')}" 
                           placeholder="Da località" onchange="updateEntryData(${clientId}, ${entry.id}, 'fromLocation', this.value)">
                    <input type="text" class="input-entry" data-field="toLocation" 
                           value="${escapeHtml(entry.data.toLocation || '')}" 
                           placeholder="A località" onchange="updateEntryData(${clientId}, ${entry.id}, 'toLocation', this.value)">
                </div>
            </div>
        `;
    } else if (entry.type === 'vehicleMovement') {
        return `
            <div class="entry-form vehicle" data-entry-id="${entry.id}">
                <div class="entry-header">
                    <strong>Movimento Veicolo</strong>
                    <button class="btn-remove-entry" onclick="removeEntry(${clientId}, ${entry.id})">×</button>
                </div>
                <div class="entry-fields">
                    <input type="text" class="input-entry" data-field="vehicle" 
                           value="${escapeHtml(entry.data.vehicle || '')}" 
                           placeholder="Veicolo" onchange="updateEntryData(${clientId}, ${entry.id}, 'vehicle', this.value)">
                    <input type="text" class="input-entry" data-field="transportMethod" 
                           value="${escapeHtml(entry.data.transportMethod || '')}" 
                           placeholder="Metodo di trasporto" onchange="updateEntryData(${clientId}, ${entry.id}, 'transportMethod', this.value)">
                    <input type="text" class="input-entry" data-field="fromLocation" 
                           value="${escapeHtml(entry.data.fromLocation || '')}" 
                           placeholder="Da località" onchange="updateEntryData(${clientId}, ${entry.id}, 'fromLocation', this.value)">
                    <input type="text" class="input-entry" data-field="toLocation" 
                           value="${escapeHtml(entry.data.toLocation || '')}" 
                           placeholder="A località" onchange="updateEntryData(${clientId}, ${entry.id}, 'toLocation', this.value)">
                    <textarea class="input-entry" data-field="notes" 
                              placeholder="Note (opzionale)" 
                              onchange="updateEntryData(${clientId}, ${entry.id}, 'notes', this.value)">${escapeHtml(entry.data.notes || '')}</textarea>
                </div>
            </div>
        `;
    }
    return '';
}

function attachClientSectionListeners() {
    // Client info inputs
    document.querySelectorAll('.input-client').forEach(input => {
        input.addEventListener('change', (e) => {
            const clientSection = e.target.closest('.client-section');
            const clientId = parseFloat(clientSection.dataset.clientId);
            const field = e.target.dataset.field;
            updateClientInfo(clientId, field, e.target.value);
        });
    });
    
    // Add entry buttons
    document.querySelectorAll('.btn-add-entry').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const clientId = parseFloat(e.target.dataset.clientId);
            showEntryTypeModalForClient(clientId);
        });
    });
}

function showEntryTypeModalForClient(clientId) {
    const modal = document.getElementById('entry-type-modal');
    if (!modal) return;
    
    modal.dataset.clientId = clientId;
    modal.classList.remove('hidden');
}

function selectEntryType(type) {
    const modal = document.getElementById('entry-type-modal');
    const clientId = parseFloat(modal.dataset.clientId);
    
    addEntry(clientId, type);
    modal.classList.add('hidden');
}

// Archive filtering
function applyFilters() {
    const dateFrom = document.getElementById('filter-date-from').value;
    const dateTo = document.getElementById('filter-date-to').value;
    const clientName = document.getElementById('filter-client').value.trim().toLowerCase();

    let filtered = [...savedReports];

    if (dateFrom) {
        filtered = filtered.filter(report => report.date >= dateFrom);
    }

    if (dateTo) {
        filtered = filtered.filter(report => report.date <= dateTo);
    }

    if (clientName) {
        filtered = filtered.filter(report => {
            return report.clients.some(client => 
                client.clientName.toLowerCase().includes(clientName) ||
                client.jobSiteName.toLowerCase().includes(clientName)
            );
        });
    }

    filtered.sort((a, b) => new Date(b.date) - new Date(a.date));

    renderArchiveReports(filtered);
}

function clearFilters() {
    document.getElementById('filter-date-from').value = '';
    document.getElementById('filter-date-to').value = '';
    document.getElementById('filter-client').value = '';
    renderArchiveReports();
}

// Utility function to escape HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text || '';
    return div.innerHTML;
}
