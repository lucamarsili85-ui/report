# Architecture Diagram: Daily Journal

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                          UI Layer                                │
│                     (Jetpack Compose)                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────┐        ┌──────────────────────┐      │
│  │ DailyJournalScreen   │        │   DashboardScreen    │      │
│  │                      │        │                      │      │
│  │ - Preview Dashboard  │        │ - History View       │      │
│  │ - Client Cards       │        │ - Finalized Reports  │      │
│  │ - Activity Forms     │        │ - Status Badges      │      │
│  │ - Add Buttons        │        │                      │      │
│  └──────────┬───────────┘        └──────────┬───────────┘      │
│             │                               │                   │
└─────────────┼───────────────────────────────┼───────────────────┘
              │                               │
              │   ┌───────────────────────────┘
              │   │
              ▼   ▼
┌─────────────────────────────────────────────────────────────────┐
│                       ViewModel Layer                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │           DailyReportViewModel                           │  │
│  │                                                          │  │
│  │  State (StateFlow):                                      │  │
│  │  • currentDailyReport: DailyReportEntity?               │  │
│  │  • currentClientSections: List<ClientSectionEntity>     │  │
│  │  • activitiesByClientSection: Map<Long, List<Activity>> │  │
│  │  • isPreviewMode: Boolean                               │  │
│  │  • totalHours: Double                                   │  │
│  │                                                          │  │
│  │  Operations:                                             │  │
│  │  • loadOrCreateTodaysDraft()                            │  │
│  │  • addClientSection()                                    │  │
│  │  • addMachineActivity()                                  │  │
│  │  • addMaterialActivity()                                 │  │
│  │  • finalizeDailyReport()                                 │  │
│  │  • reopenDailyReport()                                   │  │
│  └──────────────────────┬───────────────────────────────────┘  │
│                         │                                       │
└─────────────────────────┼───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Repository Layer                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │           DailyReportRepository                          │  │
│  │                                                          │  │
│  │  Business Logic:                                         │  │
│  │  • getOrCreateTodaysDraft() → Long                      │  │
│  │  • addClientSection() → Long                            │  │
│  │  • addMachineActivity() → Long                          │  │
│  │  • addMaterialActivity() → Long                         │  │
│  │  • finalizeDailyReport()                                 │  │
│  │  • reopenAsDraft()                                       │  │
│  │  • calculateTotalHours() → Double                       │  │
│  │                                                          │  │
│  │  Date Management:                                        │  │
│  │  • getTodayBounds() → Pair<Long, Long>                  │  │
│  └──────────────────────┬───────────────────────────────────┘  │
│                         │                                       │
└─────────────────────────┼───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Data Layer (Room)                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              DailyReportDao                              │  │
│  │                                                          │  │
│  │  Queries (Flow-based):                                   │  │
│  │  • getTodaysDraftReport()                               │  │
│  │  • getAllDailyReports()                                 │  │
│  │  • getFinalizedReports()                                │  │
│  │  • getClientSectionsForReport()                         │  │
│  │  • getActivitiesForClientSection()                      │  │
│  │                                                          │  │
│  │  Operations (Suspend):                                   │  │
│  │  • insertDailyReport()                                   │  │
│  │  • updateDailyReport()                                   │  │
│  │  • insertClientSection()                                 │  │
│  │  • insertActivity()                                      │  │
│  │  • deleteActivity()                                      │  │
│  │  • calculateTotalHours()                                 │  │
│  └──────────────────────┬───────────────────────────────────┘  │
│                         │                                       │
└─────────────────────────┼───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Database (SQLite)                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │ daily_reports                                             │ │
│  │ ───────────────────────────────────────────────────────── │ │
│  │ id | date | status | totalHours | trasferta | ...        │ │
│  └───────────────────┬───────────────────────────────────────┘ │
│                      │ 1                                        │
│                      │                                          │
│                      │ *                                        │
│  ┌───────────────────▼───────────────────────────────────────┐ │
│  │ client_sections                                           │ │
│  │ ───────────────────────────────────────────────────────── │ │
│  │ id | dailyReportId | clientName | jobSite | ...          │ │
│  └───────────────────┬───────────────────────────────────────┘ │
│                      │ 1                                        │
│                      │                                          │
│                      │ *                                        │
│  ┌───────────────────▼───────────────────────────────────────┐ │
│  │ activities                                                │ │
│  │ ───────────────────────────────────────────────────────── │ │
│  │ id | clientSectionId | activityType | machine | hours    │ │
│  │    | materialName | quantity | unit | notes | ...        │ │
│  └───────────────────────────────────────────────────────────┘ │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow: Progressive Save

```
User Action: "Add Machine Activity"
         │
         ▼
┌────────────────────────┐
│ DailyJournalScreen     │
│ (UI Composable)        │
│                        │
│ AddMachineActivityButton
│   onClick { ... }      │
└────────┬───────────────┘
         │
         │ viewModel.addMachineActivity(
         │   clientSectionId,
         │   machine, hours, description
         │ )
         ▼
┌────────────────────────┐
│ DailyReportViewModel   │
│                        │
│ fun addMachineActivity()
│   ├─> repository.addMachineActivity()
│   └─> refreshActivities()
└────────┬───────────────┘
         │
         │ suspend fun addMachineActivity()
         ▼
┌────────────────────────┐
│ DailyReportRepository  │
│                        │
│ ├─> Create ActivityEntity
│ └─> dao.insertActivity()
└────────┬───────────────┘
         │
         │ suspend fun insertActivity()
         ▼
┌────────────────────────┐
│ DailyReportDao         │
│                        │
│ @Insert                │
│ Room auto-generates SQL
└────────┬───────────────┘
         │
         │ INSERT INTO activities
         ▼
┌────────────────────────┐
│ SQLite Database        │
│                        │
│ [Activity Saved]       │
└────────┬───────────────┘
         │
         │ Flow<List<ActivityEntity>>
         │ emits new list
         ▼
┌────────────────────────┐
│ UI Auto-Updates        │
│                        │
│ • Activity appears     │
│ • Total hours updates  │
│ • Preview refreshes    │
└────────────────────────┘
```

## State Transitions

```
                    Initial State
                         │
                         ▼
              ┌──────────────────────┐
              │  No Draft Exists     │
              └──────────┬───────────┘
                         │
                         │ loadOrCreateTodaysDraft()
                         ▼
              ┌──────────────────────┐
              │  Create New DRAFT    │
              │  status = "DRAFT"    │
              └──────────┬───────────┘
                         │
                         ▼
         ┌───────────────────────────────┐
         │                               │
         │   DRAFT State (Editable)      │
         │                               │
         │   • Add Client buttons        │
         │   • + Machine buttons         │
         │   • + Material buttons        │
         │   • Delete buttons            │
         │   • Progressive auto-save     │
         │                               │
         └───────┬───────────────────────┘
                 │
                 │ finalizeDailyReport()
                 │ • Calculate total hours
                 │ • Set finalizedAt timestamp
                 │ • status = "FINAL"
                 ▼
         ┌───────────────────────────────┐
         │                               │
         │   FINAL State (Preview)       │
         │                               │
         │   • No add buttons            │
         │   • No delete buttons         │
         │   • "Edit Report" button      │
         │   • Read-only view            │
         │   • Appears in history        │
         │                               │
         └───────┬───────────────────────┘
                 │
                 │ reopenDailyReport()
                 │ • status = "DRAFT"
                 │ • finalizedAt = null
                 ▼
         ┌───────────────────────────────┐
         │                               │
         │   Back to DRAFT (Editable)    │
         │                               │
         │   • Edit controls restored    │
         │   • Removed from history      │
         │   • Can add/remove activities │
         │                               │
         └───────────────────────────────┘
```

## Key Design Decisions

### 1. Single Table for Activities
- Combined machine and material activities into one `activities` table
- Uses `activityType` field to distinguish between types
- Nullable fields for type-specific data
- **Rationale**: Simplifies queries, reduces JOINs, easier to extend

### 2. Progressive Save
- Every add operation directly inserts into database
- No temporary in-memory state
- **Rationale**: Data never lost, even on app crash

### 3. Flow-based Reactivity
- All data access returns `Flow<T>`
- UI automatically updates when database changes
- **Rationale**: Clean reactive architecture, less boilerplate

### 4. Draft Management
- Only one DRAFT per day allowed
- Query uses date range to find today's draft
- **Rationale**: Prevents duplicate draft confusion

### 5. Status Field
- String field: "DRAFT" or "FINAL"
- Constants defined in entity
- **Rationale**: Simple, extensible, human-readable in database

## Integration Points

```
┌──────────────────────┐
│  MainActivity        │
│                      │
│  • Initialize DB     │
│  • Create ViewModels │
│  • Setup Navigation  │
└──────────┬───────────┘
           │
           ├─────────────────────┬────────────────────┐
           │                     │                    │
           ▼                     ▼                    ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│  Dashboard       │  │  Daily Journal   │  │  (Future)        │
│  Screen          │  │  Screen          │  │  • PDF Export    │
│                  │  │                  │  │  • Photos        │
│  • History       │  │  • Draft/Final   │  │  • Cloud Sync    │
│  • Finalized     │  │  • Progressive   │  │                  │
│    Reports       │  │    Save          │  │                  │
└──────────────────┘  └──────────────────┘  └──────────────────┘
```
