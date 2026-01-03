# Quick Start Guide: Daily Journal

This guide provides a quick overview of how to use the Daily Journal implementation.

## For Developers

### What Was Implemented

The Daily Journal workflow replaces the single-report form with a progressive daily diary that supports:

1. **DRAFT State** - Work in progress, fully editable
2. **FINAL State** - End-of-day locked report  
3. **Progressive Save** - Every action saves immediately
4. **Multiple Clients** - Track work for multiple clients per day
5. **Activities** - Machine usage and materials per client
6. **Preview Dashboard** - Summary view of the day's work

### File Structure

```
app_structure/
  ├── DailyReportEntity.kt          ← Journal container
  ├── ClientSectionEntity.kt        ← Client within journal
  ├── ActivityEntity.kt             ← Machine/Material activities
  ├── DailyReportDao.kt             ← Database queries
  ├── DailyReportRepository.kt      ← Business logic
  ├── DailyReportViewModel.kt       ← UI state management
  └── AppDatabase.kt                ← Database config (v2)

ui/
  ├── DailyJournalScreen.kt         ← Main journal UI
  └── DashboardScreen.kt            ← History view (updated)

domain/
  ├── DailyReport.kt                ← Domain model (conceptual)
  ├── ClientSection.kt              ← Domain model (conceptual)
  └── Activity.kt                   ← Domain model (conceptual)
```

### Key Classes

**DailyReportViewModel**
```kotlin
class DailyReportViewModel(repository: DailyReportRepository) {
    // State
    val currentDailyReport: StateFlow<DailyReportEntity?>
    val currentClientSections: StateFlow<List<ClientSectionEntity>>
    val isPreviewMode: StateFlow<Boolean>
    val totalHours: StateFlow<Double>
    
    // Operations
    fun loadOrCreateTodaysDraft()
    fun addClientSection(clientName: String, jobSite: String)
    fun addMachineActivity(clientSectionId: Long, machine: String, hours: Double)
    fun addMaterialActivity(clientSectionId: Long, name: String, quantity: Double, unit: String)
    fun finalizeDailyReport()
    fun reopenDailyReport()
}
```

**DailyReportRepository**
```kotlin
class DailyReportRepository(dao: DailyReportDao) {
    // Get or create today's draft
    suspend fun getOrCreateTodaysDraft(): Long
    
    // Progressive save operations
    suspend fun addClientSection(reportId: Long, clientName: String, jobSite: String): Long
    suspend fun addMachineActivity(clientSectionId: Long, machine: String, hours: Double): Long
    suspend fun addMaterialActivity(clientSectionId: Long, name: String, quantity: Double, unit: String): Long
    
    // State transitions
    suspend fun finalizeDailyReport(reportId: Long)
    suspend fun reopenAsDraft(reportId: Long)
}
```

### Database Schema

```sql
-- Main journal
CREATE TABLE daily_reports (
    id INTEGER PRIMARY KEY,
    date INTEGER,              -- Timestamp (start of day)
    status TEXT,               -- 'DRAFT' or 'FINAL'
    totalHours REAL,           -- Calculated on finalize
    trasferta INTEGER,         -- Boolean: 0 or 1
    createdAt INTEGER,
    finalizedAt INTEGER
);

-- Client sections
CREATE TABLE client_sections (
    id INTEGER PRIMARY KEY,
    dailyReportId INTEGER,     -- FK to daily_reports
    clientName TEXT,
    jobSite TEXT,
    colorClass TEXT,
    createdAt INTEGER,
    FOREIGN KEY (dailyReportId) REFERENCES daily_reports(id) ON DELETE CASCADE
);

-- Activities (machine and material)
CREATE TABLE activities (
    id INTEGER PRIMARY KEY,
    clientSectionId INTEGER,   -- FK to client_sections
    activityType TEXT,         -- 'MACHINE' or 'MATERIAL'
    -- Machine fields
    machine TEXT,
    hours REAL,
    description TEXT,
    -- Material fields
    materialName TEXT,
    quantity REAL,
    unit TEXT,
    notes TEXT,
    createdAt INTEGER,
    FOREIGN KEY (clientSectionId) REFERENCES client_sections(id) ON DELETE CASCADE
);
```

## For Users

### How to Use the Daily Journal

#### 1. Start Your Day

1. Open the app
2. Tap the **+** (FAB) button on Dashboard
3. The app automatically creates or resumes today's draft

#### 2. Add Clients

1. Tap **"Add Client"** button
2. Enter:
   - Client name (e.g., "ACME Corp")
   - Job site location (e.g., "Via Roma 10, Milano")
3. Tap **"Add"**
4. Client card appears immediately (auto-saved!)

#### 3. Add Activities

For each client, you can add:

**Machine Activities:**
1. Tap **"+ Machine"** on the client card
2. Enter:
   - Machine name (e.g., "Excavator")
   - Hours worked (e.g., "8")
   - Description (optional)
3. Tap **"Add"**
4. Activity appears immediately (auto-saved!)

**Material Activities:**
1. Tap **"+ Material"** on the client card
2. Enter:
   - Material name (e.g., "Concrete")
   - Quantity (e.g., "25")
   - Unit (select "m³" or "ton")
   - Notes (optional)
3. Tap **"Add"**
4. Activity appears immediately (auto-saved!)

#### 4. Work Progressively

- Add clients and activities as you work throughout the day
- You can close the app anytime
- When you reopen, your work is preserved
- No need to "save" manually - everything auto-saves!

#### 5. Preview Your Work

The **Preview Dashboard** at the top shows:
- Date
- Total hours (calculated from all machine activities)
- Number of clients
- Trasferta status

Each **Client Card** shows:
- Client name and job site
- Hours for this client
- Number of materials
- Number of activities
- List of activities (tap to show/hide)

#### 6. End Your Day

1. When you're done for the day, tap **"Save & Finalize Daily Report"**
2. The report status changes to **FINAL**
3. The UI switches to **Preview Mode**:
   - All "Add" buttons disappear
   - All delete buttons disappear
   - You see an **"Edit Report"** button instead

#### 7. View History

1. Tap the back arrow to return to Dashboard
2. Your finalized report appears in the history
3. Status badge shows "FINAL"
4. Tap any report to view it

#### 8. Edit a Finalized Report (if needed)

1. Open a finalized report from history OR
2. If viewing the current final report, tap **"Edit Report"**
3. Status changes back to **DRAFT**
4. All edit buttons reappear
5. You can add/remove activities
6. When done, finalize again

## Data Safety

### Your Data is Safe

- ✅ **Auto-save**: Every action saves immediately to local database
- ✅ **No data loss**: Even if app crashes, your work is preserved
- ✅ **Offline-first**: Everything works without internet
- ✅ **Cascade delete**: Deleting a client removes its activities
- ✅ **Atomic operations**: Each save is a complete transaction

### What Gets Saved When

| Action | What Gets Saved | When |
|--------|----------------|------|
| Add Client | ClientSectionEntity | Immediately on "Add" |
| Add Machine | ActivityEntity (MACHINE type) | Immediately on "Add" |
| Add Material | ActivityEntity (MATERIAL type) | Immediately on "Add" |
| Delete Activity | Activity removed from DB | Immediately on delete |
| Delete Client | Client + all activities removed | Immediately on delete |
| Finalize | Status → FINAL, total hours calculated | On "Finalize" button |
| Reopen | Status → DRAFT | On "Edit Report" button |

## Workflow Example

### Complete Day Example

**Morning:**
```
8:00 AM - Open app → Today's draft created
8:15 AM - Add Client "ACME Corp" → Auto-saved
8:20 AM - Add Machine "Excavator" 4h → Auto-saved
8:25 AM - Add Material "Gravel" 15 m³ → Auto-saved
9:00 AM - Close app → All data preserved
```

**Afternoon:**
```
1:00 PM - Reopen app → Resume draft
1:05 PM - Add Machine "Compactor" 4h → Auto-saved
1:10 PM - Add Client "Beta Ltd" → Auto-saved
1:15 PM - Add Machine "Loader" 3h → Auto-saved
```

**End of Day:**
```
5:00 PM - Review work in preview dashboard
         Total: 11 hours across 2 clients
5:05 PM - Tap "Finalize" → Report locked
         Status: DRAFT → FINAL
```

**Next Day:**
```
8:00 AM - Open app → New draft created for today
         Yesterday's report in history
```

## Tips & Best Practices

### Do's
✅ Add clients and activities as you work  
✅ Close and reopen app freely - data is safe  
✅ Use descriptive names for machines and materials  
✅ Finalize at end of each day  
✅ Review preview dashboard before finalizing  

### Don'ts
❌ Don't wait until end of day to add everything  
❌ Don't worry about saving - it's automatic  
❌ Don't create multiple drafts for same day (app prevents this)  

## Troubleshooting

### "I don't see my draft when I reopen the app"
- Check the date - the app creates one draft per day
- Make sure you're looking at today's date
- If you finalized yesterday's report, today is a new draft

### "I can't add activities"
- Make sure the report is in DRAFT mode (not FINAL)
- If it's finalized, tap "Edit Report" first

### "Total hours doesn't match my calculation"
- Total hours only counts machine activities
- Material activities don't contribute to hours
- Check all client sections

### "I accidentally finalized too early"
- No problem! Tap "Edit Report"
- Add missing activities
- Finalize again when done

## Next Steps

- See `INTEGRATION_EXAMPLE.md` for how to integrate into an Android project
- See `DAILY_JOURNAL_IMPLEMENTATION.md` for technical details
- See `ARCHITECTURE_DIAGRAM.md` for system architecture

## Support

For technical questions or issues:
1. Check the documentation files
2. Review the code comments
3. Look at the web demo in `/docs/web-demo-v2/` for a working example
