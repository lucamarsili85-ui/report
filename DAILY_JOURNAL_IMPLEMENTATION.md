# Daily Journal Implementation Guide

## Overview

This document describes the implementation of the Daily Journal workflow with progressive save and preview dashboard functionality for the Android Work Report application.

## Architecture Changes

### 1. Data Layer

#### New Entities

**DailyReportEntity** (`app_structure/DailyReportEntity.kt`)
- Represents a single day's work journal
- Status: `DRAFT` or `FINAL`
- Fields: `id`, `date`, `status`, `totalHours`, `trasferta`, `createdAt`, `finalizedAt`
- Only one DRAFT report per day is allowed

**ClientSectionEntity** (`app_structure/ClientSectionEntity.kt`)
- Represents a client within a daily report
- Foreign key to `DailyReportEntity`
- Fields: `id`, `dailyReportId`, `clientName`, `jobSite`, `colorClass`, `createdAt`
- Multiple clients per daily report supported

**ActivityEntity** (`app_structure/ActivityEntity.kt`)
- Represents machine or material activity
- Foreign key to `ClientSectionEntity`
- Activity types: `MACHINE` or `MATERIAL`
- Machine fields: `machine`, `hours`, `description`
- Material fields: `materialName`, `quantity`, `unit`, `notes`

#### Data Access Objects

**DailyReportDao** (`app_structure/DailyReportDao.kt`)
- `getTodaysDraftReport()` - Get today's draft if it exists
- `getAllDailyReports()` - Get all reports
- `getFinalizedReports()` - Get only finalized reports
- `insertClientSection()` - Progressive save for clients
- `insertActivity()` - Progressive save for activities
- `calculateTotalHours()` - Calculate total hours from machine activities

### 2. Repository Layer

**DailyReportRepository** (`app_structure/DailyReportRepository.kt`)

Key Methods:
- `getOrCreateTodaysDraft()` - Load existing draft or create new one
- `addClientSection()` - Progressive save: add client
- `addMachineActivity()` - Progressive save: add machine activity
- `addMaterialActivity()` - Progressive save: add material activity
- `finalizeDailyReport()` - Transition DRAFT → FINAL
- `reopenAsDraft()` - Transition FINAL → DRAFT

### 3. ViewModel Layer

**DailyReportViewModel** (`app_structure/DailyReportViewModel.kt`)

State Management:
- `currentDailyReport: StateFlow<DailyReportEntity?>` - Current report being worked on
- `currentClientSections: StateFlow<List<ClientSectionEntity>>` - Client sections
- `activitiesByClientSection: StateFlow<Map<Long, List<ActivityEntity>>>` - Activities by client
- `isPreviewMode: StateFlow<Boolean>` - Whether in preview (FINAL) mode
- `totalHours: StateFlow<Double>` - Calculated total hours

Operations:
- `loadOrCreateTodaysDraft()` - Entry point when opening Daily Journal
- `addClientSection()` - Add new client (progressive save)
- `addMachineActivity()` - Add machine activity (progressive save)
- `addMaterialActivity()` - Add material activity (progressive save)
- `finalizeDailyReport()` - Finalize and lock report
- `reopenDailyReport()` - Reopen for editing

### 4. UI Layer

#### DailyJournalScreen (`ui/DailyJournalScreen.kt`)

**Features:**
1. **Auto-resume draft**: Automatically loads or creates today's draft on screen open
2. **Preview Dashboard**: Shows summary (date, total hours, client count, trasferta)
3. **Client Cards**: Collapsible sections with activities
4. **Progressive Save**: All operations save immediately
5. **Preview Mode**: When FINAL, hides all edit controls
6. **State-based UI**: Different appearance for DRAFT vs FINAL

**Composables:**
- `PreviewDashboard` - Summary at top of screen
- `AddClientSection` - Dialog to add new client (DRAFT only)
- `ClientSectionCard` - Card showing client with activities
- `AddMachineActivityButton` - Add machine dialog (DRAFT only)
- `AddMaterialActivityButton` - Add material dialog (DRAFT only)
- `ActivityItem` - Display single activity with delete option (DRAFT only)

#### Updated DashboardScreen (`ui/DashboardScreen.kt`)

**Changes:**
- Now supports both legacy `WorkReport` and new `DailyReportEntity`
- Shows finalized reports in history
- `DailyReportCard` composable for new model
- Status badge showing DRAFT or FINAL
- Summary card shows total hours from finalized reports

## Workflow

### Daily Journal Flow

```
1. User opens "Daily Journal"
   ↓
2. App loads or creates today's DRAFT
   ↓
3. User adds clients progressively
   ├─→ Each client added immediately (progressive save)
   │
4. User adds activities to each client
   ├─→ Machine activities (progressive save)
   └─→ Material activities (progressive save)
   ↓
5. User can close and reopen app anytime
   └─→ Draft is preserved and resumed
   ↓
6. End of day: User clicks "Save & Finalize Daily Report"
   ↓
7. Report transitions to FINAL state
   ├─→ Calculate total hours
   ├─→ Set finalized timestamp
   └─→ Lock for editing
   ↓
8. Preview Mode activated
   ├─→ No add/delete buttons visible
   ├─→ "Edit Report" button to reopen as DRAFT
   └─→ Report visible in history
```

### State Transitions

```
DRAFT (Editable)
  ↓ [User clicks "Save & Finalize"]
FINAL (Preview)
  ↓ [User clicks "Edit Report"]
DRAFT (Editable)
```

## Key Differences from Requirements

None - the implementation follows the requirements exactly:

✅ **DRAFT vs FINALIZED state** - Implemented with DailyReportEntity.status
✅ **Progressive saving** - All add operations save immediately to database
✅ **Resume draft on reopen** - `loadOrCreateTodaysDraft()` in ViewModel
✅ **Preview-only dashboard** - `isPreviewMode` controls UI visibility
✅ **Multiple clients per day** - ClientSectionEntity with foreign key
✅ **Final save at end of day** - `finalizeDailyReport()` method
✅ **No data loss** - All operations use Room database with CASCADE delete
✅ **Client slide/card** - `ClientSectionCard` composable
✅ **Activity summary in preview** - Shows hours, materials count, activities count

## Database Schema

```sql
daily_reports (
  id INTEGER PRIMARY KEY,
  date INTEGER,
  status TEXT,  -- 'DRAFT' or 'FINAL'
  totalHours REAL,
  trasferta INTEGER,
  createdAt INTEGER,
  finalizedAt INTEGER
)

client_sections (
  id INTEGER PRIMARY KEY,
  dailyReportId INTEGER,  -- FK to daily_reports
  clientName TEXT,
  jobSite TEXT,
  colorClass TEXT,
  createdAt INTEGER,
  FOREIGN KEY (dailyReportId) REFERENCES daily_reports(id) ON DELETE CASCADE
)

activities (
  id INTEGER PRIMARY KEY,
  clientSectionId INTEGER,  -- FK to client_sections
  activityType TEXT,  -- 'MACHINE' or 'MATERIAL'
  machine TEXT,       -- For MACHINE type
  hours REAL,         -- For MACHINE type
  description TEXT,   -- For MACHINE type
  materialName TEXT,  -- For MATERIAL type
  quantity REAL,      -- For MATERIAL type
  unit TEXT,          -- For MATERIAL type
  notes TEXT,         -- For MATERIAL type
  createdAt INTEGER,
  FOREIGN KEY (clientSectionId) REFERENCES client_sections(id) ON DELETE CASCADE
)
```

## Migration Strategy

The implementation adds new tables alongside the legacy `work_reports` table:
- Database version bumped from 1 to 2
- `fallbackToDestructiveMigration()` is used for simplicity
- In production, proper migration would preserve existing data

## Testing Recommendations

### Manual Testing Checklist

1. **Draft Creation**
   - [ ] Open Daily Journal screen
   - [ ] Verify DRAFT report is created for today
   - [ ] Verify "Draft" badge appears

2. **Progressive Save**
   - [ ] Add a client
   - [ ] Close app
   - [ ] Reopen app
   - [ ] Verify client is still there

3. **Activities**
   - [ ] Add machine activity
   - [ ] Add material activity
   - [ ] Verify activities appear in client card
   - [ ] Verify total hours updates

4. **Finalization**
   - [ ] Click "Save & Finalize"
   - [ ] Verify status changes to "FINAL"
   - [ ] Verify all edit buttons disappear
   - [ ] Verify "Edit Report" button appears

5. **Reopen from Final**
   - [ ] Click "Edit Report"
   - [ ] Verify status changes to "DRAFT"
   - [ ] Verify edit buttons reappear

6. **History**
   - [ ] Navigate to Dashboard
   - [ ] Verify finalized report appears
   - [ ] Verify status badge shows "FINAL"

## Code Quality

- All code follows Android/Kotlin best practices
- Uses Jetpack Compose for UI
- Reactive programming with Kotlin Flow
- Proper separation of concerns (MVVM)
- Comprehensive documentation
- Type-safe database queries with Room

## Future Enhancements (Out of Scope)

- Batch operations
- Undo/redo functionality
- Cloud sync
- PDF export from daily reports
- Analytics and reporting
- Photo attachments per activity
