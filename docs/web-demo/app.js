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
            client.activities.forEach(activity => {
                if (activity.type === 'mezzo' && activity.hours) {
                    total += parseFloat(activity.hours);
                }
            });
        });
        return total;
    }
}

class ClientSection {
    constructor(clientName, jobSite) {
        this.id = Date.now() + Math.random();
        this.clientName = clientName || '';
        this.jobSite = jobSite || '';
        this.activities = []; // Array of Activity objects (mezzi and materiali)
    }
}

class Activity {
    constructor(type) {
        this.id = Date.now() + Math.random();
        this.type = type; // 'mezzo' or 'materiale'
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
    // Home cards navigation
    document.querySelectorAll('.home-card').forEach(card => {
        card.addEventListener('click', () => {
            const navigate = card.dataset.navigate;
            if (navigate) {
                switchScreen(navigate);
            }
        });
    });

    // Back arrow navigation
    const backArrow = document.getElementById('back-arrow');
    if (backArrow) backArrow.addEventListener('click', goBack);

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
    if (addClientBtn) addClientBtn.addEventListener('click', showAddClientModal);
    
    const closeAddClientBtn = document.getElementById('close-add-client');
    if (closeAddClientBtn) closeAddClientBtn.addEventListener('click', hideAddClientModal);
    
    const cancelAddClientBtn = document.getElementById('cancel-add-client');
    if (cancelAddClientBtn) cancelAddClientBtn.addEventListener('click', hideAddClientModal);
    
    const confirmAddClientBtn = document.getElementById('confirm-add-client');
    if (confirmAddClientBtn) confirmAddClientBtn.addEventListener('click', confirmAddClient);
    
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

    // Show/hide back arrow
    const backArrow = document.getElementById('back-arrow');
    if (screenName === 'home') {
        backArrow.classList.add('hidden');
    } else {
        backArrow.classList.remove('hidden');
    }

    // Refresh data when needed
    if (screenName === 'home') {
        updateDashboard();
    } else if (screenName === 'rapportino') {
        renderClientSlides();
    } else if (screenName === 'storico') {
        renderArchiveReports();
    }
}

function goBack() {
    switchScreen('home');
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
function showAddClientModal() {
    const modal = document.getElementById('add-client-modal');
    if (modal) {
        // Clear previous inputs
        document.getElementById('new-client-name').value = '';
        document.getElementById('new-job-site').value = '';
        modal.classList.remove('hidden');
    }
}

function hideAddClientModal() {
    const modal = document.getElementById('add-client-modal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

function confirmAddClient() {
    const clientName = document.getElementById('new-client-name').value.trim();
    const jobSite = document.getElementById('new-job-site').value.trim();
    
    if (!clientName || !jobSite) {
        alert('Inserisci sia il nome del cliente che la località/cantiere.');
        return;
    }
    
    if (!currentDraftDay) {
        loadOrCreateTodayDraft();
    }
    
    const client = new ClientSection(clientName, jobSite);
    currentDraftDay.clients.push(client);
    saveDraftDaysToStorage();
    renderClientSlides();
    hideAddClientModal();
}

function removeClient(clientId) {
    if (!confirm('Sei sicuro di voler rimuovere questo cliente e tutte le sue attività?')) {
        return;
    }
    
    currentDraftDay.clients = currentDraftDay.clients.filter(c => c.id !== clientId);
    saveDraftDaysToStorage();
    renderClientSlides();
}

function updateClientInfo(clientId, field, value) {
    const client = currentDraftDay.clients.find(c => c.id === clientId);
    if (client) {
        client[field] = value;
        saveDraftDaysToStorage();
    }
}

// Activity Management (Mezzi and Materiali)
function addActivity(clientId, type) {
    const client = currentDraftDay.clients.find(c => c.id === clientId);
    if (!client) return;
    
    const activity = new Activity(type);
    
    // Initialize activity data based on type
    if (type === 'mezzo') {
        activity.data = { name: '', hours: 0, notes: '' };
    } else if (type === 'materiale') {
        activity.data = { name: '', quantity: 0, unit: 'mc', notes: '' };
    }
    
    client.activities.push(activity);
    saveDraftDaysToStorage();
    renderClientSlides();
}

function removeActivity(clientId, activityId) {
    if (!confirm('Sei sicuro di voler rimuovere questa attività?')) {
        return;
    }
    
    const client = currentDraftDay.clients.find(c => c.id === clientId);
    if (client) {
        client.activities = client.activities.filter(a => a.id !== activityId);
        saveDraftDaysToStorage();
        renderClientSlides();
    }
}

function updateActivityData(clientId, activityId, field, value) {
    const client = currentDraftDay.clients.find(c => c.id === clientId);
    if (client) {
        const activity = client.activities.find(a => a.id === activityId);
        if (activity) {
            activity.data[field] = value;
            saveDraftDaysToStorage();
            // Update display without full re-render for smoother UX
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
                <p><strong>Località/Cantiere:</strong> ${escapeHtml(client.jobSite || 'N/A')}</p>
                
                <div class="entries-list">
                    ${renderActivitiesView(client.activities || client.entries || [])}
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

function renderActivitiesView(activities) {
    if (!activities || activities.length === 0) {
        return '<p class="empty-state-small">Nessuna attività</p>';
    }
    
    return activities.map(activity => {
        // Handle both new (mezzo/materiale) and old (activity/material/vehicleMovement) formats
        if (activity.type === 'mezzo' || activity.type === 'activity') {
            return `
                <div class="entry-view mezzo">
                    <strong>🚜 Mezzo</strong>
                    <p>Nome: ${escapeHtml(activity.data.name || activity.data.machine || 'N/A')}</p>
                    <p>Ore: ${activity.data.hours || 0}</p>
                    ${activity.data.notes ? `<p>Note: ${escapeHtml(activity.data.notes)}</p>` : ''}
                </div>
            `;
        } else if (activity.type === 'materiale' || activity.type === 'material') {
            const unit = getDisplayUnit(activity.data.unit);
            return `
                <div class="entry-view materiale">
                    <strong>📦 Materiale</strong>
                    <p>Nome: ${escapeHtml(activity.data.name || 'N/A')}</p>
                    <p>Quantità: ${activity.data.quantity || 0} ${unit}</p>
                    ${activity.data.notes ? `<p>Note: ${escapeHtml(activity.data.notes)}</p>` : ''}
                </div>
            `;
        } else if (activity.type === 'vehicleMovement') {
            return `
                <div class="entry-view vehicle">
                    <strong>Movimento veicolo</strong>
                    <p>Veicolo: ${escapeHtml(activity.data.vehicle || 'N/A')}</p>
                    <p>Metodo: ${escapeHtml(activity.data.transportMethod || 'N/A')}</p>
                    <p>Da: ${escapeHtml(activity.data.fromLocation || 'N/A')}</p>
                    <p>A: ${escapeHtml(activity.data.toLocation || 'N/A')}</p>
                    ${activity.data.notes ? `<p>Note: ${escapeHtml(activity.data.notes)}</p>` : ''}
                </div>
            `;
        }
        return '';
    }).join('');
}

// Client Slides Rendering
function renderClientSlides() {
    if (!currentDraftDay) return;
    
    const container = document.getElementById('client-slides');
    const dateDisplay = document.getElementById('current-date-display');
    
    if (dateDisplay) {
        const date = new Date(currentDraftDay.date + 'T00:00:00');
        dateDisplay.textContent = date.toLocaleDateString('it-IT', { 
            weekday: 'long', 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
        });
    }
    
    if (!container) return;
    
    if (currentDraftDay.clients.length === 0) {
        container.innerHTML = '<div class="empty-state-card"><p>Nessun cliente aggiunto.</p><p class="help-text">Clicca "Aggiungi cliente alla giornata" per iniziare.</p></div>';
        return;
    }
    
    container.innerHTML = currentDraftDay.clients.map((client, idx) => renderClientSlide(client, idx)).join('');
    
    // Attach event listeners for client slides
    attachClientSlideListeners();
}

function renderClientSlide(client, index) {
    // Generate a color for the client card based on index
    const colors = [
        '#4CAF50', // Green
        '#2196F3', // Blue
        '#FF9800', // Orange
        '#9C27B0', // Purple
        '#00BCD4', // Cyan
        '#E91E63', // Pink
        '#8BC34A', // Light Green
        '#FFC107', // Amber
    ];
    const borderColor = colors[index % colors.length];
    
    return `
        <div class="client-slide" data-client-id="${client.id}" style="border-left: 6px solid ${borderColor};">
            <div class="client-slide-header">
                <div class="client-info-compact">
                    <h3>${escapeHtml(client.clientName)}</h3>
                    <p class="job-site">${escapeHtml(client.jobSite)}</p>
                </div>
                <button class="btn-icon-remove" onclick="removeClient(${client.id})" aria-label="Rimuovi cliente">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="18" y1="6" x2="6" y2="18"></line>
                        <line x1="6" y1="6" x2="18" y2="18"></line>
                    </svg>
                </button>
            </div>
            
            <div class="activities-section">
                <div class="activities-list">
                    ${renderActivities(client)}
                </div>
                
                <div class="activity-actions">
                    <button class="btn btn-outline btn-add-activity" data-client-id="${client.id}" data-type="mezzo">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="1" y="3" width="15" height="13"></rect>
                            <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
                            <circle cx="5.5" cy="18.5" r="2.5"></circle>
                            <circle cx="18.5" cy="18.5" r="2.5"></circle>
                        </svg>
                        Aggiungi Mezzo
                    </button>
                    <button class="btn btn-outline btn-add-activity" data-client-id="${client.id}" data-type="materiale">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                        </svg>
                        Aggiungi Materiale
                    </button>
                </div>
            </div>
        </div>
    `;
}

function renderActivities(client) {
    if (client.activities.length === 0) {
        return '<p class="empty-state-small">Nessuna attività. Aggiungi mezzi o materiali.</p>';
    }
    
    return client.activities.map(activity => renderActivityCard(client.id, activity)).join('');
}

function renderActivityCard(clientId, activity) {
    if (activity.type === 'mezzo') {
        return `
            <div class="activity-card mezzo" data-activity-id="${activity.id}">
                <div class="activity-header">
                    <div class="activity-type-badge mezzo">🚜 Mezzo</div>
                    <button class="btn-icon-remove-small" onclick="removeActivity(${clientId}, ${activity.id})" aria-label="Rimuovi">×</button>
                </div>
                <div class="activity-fields">
                    <input type="text" class="input-activity" data-field="name" 
                           value="${escapeHtml(activity.data.name || '')}" 
                           placeholder="Nome mezzo (es. Escavatore)" 
                           onchange="updateActivityData(${clientId}, ${activity.id}, 'name', this.value)">
                    <div class="input-group">
                        <input type="number" inputmode="decimal" class="input-activity" data-field="hours" 
                               value="${activity.data.hours || 0}" step="0.5" min="0"
                               placeholder="Ore" 
                               onchange="updateActivityData(${clientId}, ${activity.id}, 'hours', parseFloat(this.value))">
                        <span class="input-suffix">ore</span>
                    </div>
                    <input type="text" class="input-activity" data-field="notes" 
                           value="${escapeHtml(activity.data.notes || '')}" 
                           placeholder="Note (opzionale)" 
                           onchange="updateActivityData(${clientId}, ${activity.id}, 'notes', this.value)">
                </div>
            </div>
        `;
    } else if (activity.type === 'materiale') {
        const unit = activity.data.unit || 'mc';
        return `
            <div class="activity-card materiale" data-activity-id="${activity.id}">
                <div class="activity-header">
                    <div class="activity-type-badge materiale">📦 Materiale</div>
                    <button class="btn-icon-remove-small" onclick="removeActivity(${clientId}, ${activity.id})" aria-label="Rimuovi">×</button>
                </div>
                <div class="activity-fields">
                    <input type="text" class="input-activity" data-field="name" 
                           value="${escapeHtml(activity.data.name || '')}" 
                           placeholder="Nome materiale (es. Cemento)" 
                           onchange="updateActivityData(${clientId}, ${activity.id}, 'name', this.value)">
                    <div class="input-group">
                        <input type="number" inputmode="decimal" class="input-activity" data-field="quantity" 
                               value="${activity.data.quantity || 0}" step="0.1" min="0"
                               placeholder="Quantità" 
                               onchange="updateActivityData(${clientId}, ${activity.id}, 'quantity', parseFloat(this.value))">
                        <select class="input-activity input-unit" data-field="unit" 
                                onchange="updateActivityData(${clientId}, ${activity.id}, 'unit', this.value)">
                            <option value="mc" ${unit === 'mc' ? 'selected' : ''}>m³</option>
                            <option value="ton" ${unit === 'ton' ? 'selected' : ''}>ton</option>
                        </select>
                    </div>
                    <input type="text" class="input-activity" data-field="notes" 
                           value="${escapeHtml(activity.data.notes || '')}" 
                           placeholder="Note (opzionale)" 
                           onchange="updateActivityData(${clientId}, ${activity.id}, 'notes', this.value)">
                </div>
            </div>
        `;
    }
    return '';
}

function attachClientSlideListeners() {
    // Add activity buttons
    document.querySelectorAll('.btn-add-activity').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const clientId = parseFloat(e.currentTarget.dataset.clientId);
            const type = e.currentTarget.dataset.type;
            addActivity(clientId, type);
        });
    });
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
                (client.clientName && client.clientName.toLowerCase().includes(clientName)) ||
                (client.jobSite && client.jobSite.toLowerCase().includes(clientName)) ||
                (client.jobSiteName && client.jobSiteName.toLowerCase().includes(clientName))
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

// Utility function to get display unit for material
function getDisplayUnit(unit) {
    return (unit === 'mc' || unit === 'metric') ? 'm³' : 'ton';
}

// Utility function to normalize material unit (backward compatibility)
function normalizeUnit(unit) {
    return (unit === 'metric') ? 'mc' : unit;
}

// Utility function to escape HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text || '';
    return div.innerHTML;
}
