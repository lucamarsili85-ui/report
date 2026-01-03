// Storage key for LocalStorage
const STORAGE_KEY = 'dailyWorkReports';

// Application state
let reports = [];
let materialCounter = 0;

// Initialize the app
document.addEventListener('DOMContentLoaded', () => {
    loadReportsFromStorage();
    setupEventListeners();
    updateDashboard();
    renderRecentReports();
    renderArchiveReports();
    setDefaultDate();
});

// LocalStorage operations
function loadReportsFromStorage() {
    try {
        const data = localStorage.getItem(STORAGE_KEY);
        if (data) {
            const parsed = JSON.parse(data);
            // Validate data structure
            if (Array.isArray(parsed)) {
                reports = parsed.map(report => ({
                    id: report.id || Date.now(),
                    date: report.date || Date.now(),
                    jobSite: report.jobSite || '',
                    machine: report.machine || '',
                    hoursWorked: parseFloat(report.hoursWorked) || 0,
                    notes: report.notes || '',
                    materials: Array.isArray(report.materials) ? report.materials : [],
                    createdAt: report.createdAt || Date.now()
                }));
            }
        }
    } catch (error) {
        console.error('Error loading reports from storage:', error);
        reports = [];
    }
}

function saveReportsToStorage() {
    try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(reports));
    } catch (error) {
        console.error('Error saving reports to storage:', error);
        alert('Failed to save data. Storage might be full.');
    }
}

// Event listeners setup
function setupEventListeners() {
    // Tab navigation
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', () => switchTab(btn.dataset.tab));
    });

    // Report form submission
    document.getElementById('report-form').addEventListener('submit', handleReportSubmit);
    document.getElementById('cancel-btn').addEventListener('click', resetReportForm);

    // Materials management
    document.getElementById('add-material-btn').addEventListener('click', addMaterialRow);

    // Job site autocomplete
    const jobSiteInput = document.getElementById('job-site');
    jobSiteInput.addEventListener('input', handleJobSiteInput);
    jobSiteInput.addEventListener('blur', () => {
        setTimeout(() => hideSuggestions(), 200);
    });

    // Archive filters
    document.getElementById('apply-filters-btn').addEventListener('click', applyFilters);
    document.getElementById('clear-filters-btn').addEventListener('click', clearFilters);
}

// Tab switching
function switchTab(tabName) {
    // Update tab buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.tab === tabName);
    });

    // Update tab content
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.toggle('active', content.id === tabName);
    });

    // Refresh data when switching to dashboard or archive
    if (tabName === 'dashboard') {
        updateDashboard();
        renderRecentReports();
    } else if (tabName === 'archive') {
        renderArchiveReports();
    }
}

// Set default date to today
function setDefaultDate() {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('report-date').value = today;
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
    weekStart.setDate(now.getDate() - now.getDay()); // Start of week (Sunday)
    weekStart.setHours(0, 0, 0, 0);

    return reports
        .filter(report => new Date(report.date) >= weekStart)
        .reduce((sum, report) => sum + report.hoursWorked, 0);
}

function calculateMonthlyHours() {
    const now = new Date();
    const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);

    return reports
        .filter(report => new Date(report.date) >= monthStart)
        .reduce((sum, report) => sum + report.hoursWorked, 0);
}

// Recent reports rendering
function renderRecentReports() {
    const container = document.getElementById('recent-reports');
    const recentReports = [...reports]
        .sort((a, b) => b.createdAt - a.createdAt)
        .slice(0, 5);

    if (recentReports.length === 0) {
        container.innerHTML = '<p class="empty-state">No reports yet. Create your first report!</p>';
        return;
    }

    container.innerHTML = recentReports.map(report => createReportCard(report)).join('');
    
    // Add delete event listeners
    container.querySelectorAll('.btn-delete').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const id = parseInt(e.target.dataset.id);
            deleteReport(id);
        });
    });
}

// Archive reports rendering
function renderArchiveReports(filteredReports = null) {
    const container = document.getElementById('archive-reports');
    const reportsToShow = filteredReports || [...reports].sort((a, b) => b.date - a.date);

    if (reportsToShow.length === 0) {
        container.innerHTML = '<p class="empty-state">No reports found.</p>';
        return;
    }

    container.innerHTML = reportsToShow.map(report => createReportCard(report)).join('');
    
    // Add delete event listeners
    container.querySelectorAll('.btn-delete').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const id = parseInt(e.target.dataset.id);
            deleteReport(id);
        });
    });
}

// Create report card HTML
function createReportCard(report) {
    const date = new Date(report.date);
    const formattedDate = date.toLocaleDateString('en-US', { 
        weekday: 'short', 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });

    const materialsCount = report.materials && report.materials.length > 0
        ? `<span class="materials-count">${report.materials.length} material${report.materials.length > 1 ? 's' : ''}</span>`
        : '';

    const notesHtml = report.notes
        ? `<div class="report-notes">${escapeHtml(report.notes)}</div>`
        : '';

    return `
        <div class="report-card">
            <div class="report-header">
                <div class="report-date">${formattedDate}</div>
                <div class="report-hours">${report.hoursWorked} hrs</div>
            </div>
            <div class="report-details">
                <div class="report-detail">
                    <strong>Job Site:</strong> ${escapeHtml(report.jobSite)}
                </div>
                <div class="report-detail">
                    <strong>Machine:</strong> ${escapeHtml(report.machine)} ${materialsCount}
                </div>
            </div>
            ${notesHtml}
            <button class="btn-delete" data-id="${report.id}">Delete</button>
        </div>
    `;
}

// Job site autocomplete
function handleJobSiteInput(e) {
    const input = e.target.value.trim().toLowerCase();
    
    if (input.length < 1) {
        hideSuggestions();
        return;
    }

    // Get unique job sites from existing reports
    const jobSites = [...new Set(reports.map(r => r.jobSite))];
    const matches = jobSites.filter(site => 
        site.toLowerCase().includes(input)
    );

    if (matches.length > 0) {
        showSuggestions(matches);
    } else {
        hideSuggestions();
    }
}

function showSuggestions(suggestions) {
    const container = document.getElementById('job-site-suggestions');
    container.innerHTML = suggestions
        .map(site => `<div class="suggestion-item" data-value="${escapeHtml(site)}">${escapeHtml(site)}</div>`)
        .join('');
    
    container.classList.add('show');

    // Add click handlers
    container.querySelectorAll('.suggestion-item').forEach(item => {
        item.addEventListener('click', () => {
            document.getElementById('job-site').value = item.dataset.value;
            hideSuggestions();
        });
    });
}

function hideSuggestions() {
    document.getElementById('job-site-suggestions').classList.remove('show');
}

// Materials management
function addMaterialRow() {
    materialCounter++;
    const container = document.getElementById('materials-list');
    const row = document.createElement('div');
    row.className = 'material-row';
    row.dataset.id = materialCounter;
    row.innerHTML = `
        <input type="text" placeholder="Material name" class="material-name">
        <input type="number" placeholder="Qty" class="material-quantity" step="0.1" min="0.1">
        <input type="text" placeholder="Unit" class="material-unit">
        <input type="text" placeholder="Note (optional)" class="material-note">
        <button type="button" class="btn-remove" onclick="removeMaterialRow(${materialCounter})">Remove</button>
    `;
    container.appendChild(row);
}

function removeMaterialRow(id) {
    const row = document.querySelector(`.material-row[data-id="${id}"]`);
    if (row) {
        row.remove();
    }
}

// Form handling
function handleReportSubmit(e) {
    e.preventDefault();

    // Get form values
    const dateValue = document.getElementById('report-date').value;
    const jobSite = document.getElementById('job-site').value.trim();
    const machine = document.getElementById('machine').value.trim();
    const hours = parseFloat(document.getElementById('hours').value);
    const notes = document.getElementById('notes').value.trim();

    // Validation
    if (!dateValue || !jobSite || !machine || !hours) {
        alert('Please fill in all required fields.');
        return;
    }

    if (hours <= 0 || hours > 24) {
        alert('Hours must be between 0.1 and 24.');
        return;
    }

    if (jobSite.length > 100) {
        alert('Job site name must be 100 characters or less.');
        return;
    }

    if (machine.length > 50) {
        alert('Machine name must be 50 characters or less.');
        return;
    }

    if (notes.length > 500) {
        alert('Notes must be 500 characters or less.');
        return;
    }

    // Get materials
    const materials = [];
    document.querySelectorAll('.material-row').forEach(row => {
        const name = row.querySelector('.material-name').value.trim();
        const quantity = parseFloat(row.querySelector('.material-quantity').value);
        const unit = row.querySelector('.material-unit').value.trim();
        const note = row.querySelector('.material-note').value.trim();

        if (name && quantity && unit) {
            if (quantity <= 0) {
                alert('Material quantity must be greater than 0.');
                return;
            }
            materials.push({ name, quantity, unit, note });
        }
    });

    // Create report object
    const report = {
        id: Date.now(),
        date: new Date(dateValue).getTime(),
        jobSite,
        machine,
        hoursWorked: hours,
        notes,
        materials,
        createdAt: Date.now()
    };

    // Add to reports array
    reports.push(report);
    saveReportsToStorage();

    // Reset form and switch to dashboard
    resetReportForm();
    switchTab('dashboard');
    
    alert('Report saved successfully!');
}

function resetReportForm() {
    document.getElementById('report-form').reset();
    document.getElementById('materials-list').innerHTML = '';
    setDefaultDate();
    hideSuggestions();
}

// Archive filtering
function applyFilters() {
    const dateFrom = document.getElementById('filter-date-from').value;
    const dateTo = document.getElementById('filter-date-to').value;
    const jobSite = document.getElementById('filter-job-site').value.trim().toLowerCase();
    const machine = document.getElementById('filter-machine').value.trim().toLowerCase();

    let filtered = [...reports];

    // Date range filter
    if (dateFrom) {
        const fromTime = new Date(dateFrom).getTime();
        filtered = filtered.filter(report => report.date >= fromTime);
    }

    if (dateTo) {
        const toTime = new Date(dateTo).getTime();
        toTime.setHours(23, 59, 59, 999); // End of day
        filtered = filtered.filter(report => report.date <= toTime);
    }

    // Job site filter
    if (jobSite) {
        filtered = filtered.filter(report => 
            report.jobSite.toLowerCase().includes(jobSite)
        );
    }

    // Machine filter
    if (machine) {
        filtered = filtered.filter(report => 
            report.machine.toLowerCase().includes(machine)
        );
    }

    // Sort by date descending
    filtered.sort((a, b) => b.date - a.date);

    renderArchiveReports(filtered);
}

function clearFilters() {
    document.getElementById('filter-date-from').value = '';
    document.getElementById('filter-date-to').value = '';
    document.getElementById('filter-job-site').value = '';
    document.getElementById('filter-machine').value = '';
    renderArchiveReports();
}

// Delete report
function deleteReport(id) {
    if (!confirm('Are you sure you want to delete this report?')) {
        return;
    }

    reports = reports.filter(report => report.id !== id);
    saveReportsToStorage();

    // Refresh current view
    const activeTab = document.querySelector('.tab-content.active').id;
    if (activeTab === 'dashboard') {
        updateDashboard();
        renderRecentReports();
    } else if (activeTab === 'archive') {
        renderArchiveReports();
    }

    alert('Report deleted successfully.');
}

// Utility function to escape HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
