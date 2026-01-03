# Quick Start Guide

## What is Rapportino?

Rapportino is a mobile app for managing daily work reports. It helps workers and supervisors track:
- **When**: Which date the work was performed
- **Where**: Which job site (cantiere)
- **What**: Which machine was used
- **How long**: How many hours were worked
- **Notes**: Any additional observations

## Screenshots (Expected UI)

### Dashboard Screen
```
┌─────────────────────────────────────┐
│ ← Rapportino - Dashboard            │
├─────────────────────────────────────┤
│                                     │
│  ┌───────────────────────────┐     │
│  │ 📅 03/01/2026             │ 🗑️ │
│  │ Cantiere: Site A          │     │
│  │ Macchina: Excavator 1     │     │
│  │ Ore lavorate: 8.0h        │     │
│  │ Note: Completed foundation│     │
│  └───────────────────────────┘     │
│                                     │
│  ┌───────────────────────────┐     │
│  │ 📅 02/01/2026             │ 🗑️ │
│  │ Cantiere: Site B          │     │
│  │ Macchina: Crane 2         │     │
│  │ Ore lavorate: 7.5h        │     │
│  └───────────────────────────┘     │
│                                     │
└─────────────────────────────────────┘
                                    [+]
```

### Add Report Screen
```
┌─────────────────────────────────────┐
│ ← Nuovo Rapportino                  │
├─────────────────────────────────────┤
│                                     │
│  ┌───────────────────────────┐     │
│  │ 📅 Data: 03/01/2026      │     │
│  └───────────────────────────┘     │
│                                     │
│  ┌───────────────────────────┐     │
│  │ Cantiere                  │     │
│  │ [____________Site A______]│     │
│  └───────────────────────────┘     │
│                                     │
│  ┌───────────────────────────┐     │
│  │ Macchina                  │     │
│  │ [__________Excavator 1___]│     │
│  └───────────────────────────┘     │
│                                     │
│  ┌───────────────────────────┐     │
│  │ Ore lavorate              │     │
│  │ [_____________8.0________]│     │
│  └───────────────────────────┘     │
│                                     │
│  ┌───────────────────────────┐     │
│  │ Note                      │     │
│  │ [_______________________]│     │
│  │ [_______________________]│     │
│  │ [_______________________]│     │
│  └───────────────────────────┘     │
│                                     │
│  ┌───────────────────────────┐     │
│  │   Salva Rapportino        │     │
│  └───────────────────────────┘     │
│                                     │
└─────────────────────────────────────┘
```

## How to Use

### First Time Setup
1. **Install the app** on your Android device (Android 7.0 or higher)
2. **Launch** the app - you'll see an empty dashboard
3. **Tap the + button** to create your first report

### Creating a Daily Report
1. **Select the date** by tapping the date button
2. **Enter the job site** name (e.g., "Construction Site A")
3. **Enter the machine** used (e.g., "Excavator CAT 320")
4. **Enter hours worked** (minimum 0.1 hours)
5. **Add notes** (optional) - any important observations
6. **Tap "Salva Rapportino"** to save

### Viewing Reports
- All reports appear on the dashboard
- Newest reports appear at the top
- Tap the delete (🗑️) icon to remove a report

### Data Storage
- All data is stored **locally on your device**
- Data persists even if you close the app
- No internet connection required
- Your data stays private on your device

## User Flow

```
App Launch
    ↓
Dashboard (Empty State)
    ↓
Tap [+] Button
    ↓
Add Report Screen
    ↓
Fill in Details
    ↓
Tap "Salva Rapportino"
    ↓
Back to Dashboard (Shows New Report)
```

## Input Validation

The app ensures data quality:
- ✅ **Job site** is required (cannot be empty)
- ✅ **Machine** is required (cannot be empty)
- ✅ **Hours** must be a number ≥ 0.1
- ✅ **Date** must be selected (defaults to today)
- ℹ️ **Notes** are optional

## Tips & Tricks

### Efficient Data Entry
- The date defaults to today
- Use consistent naming for job sites and machines
- Add detailed notes for unusual situations
- Review your reports regularly

### Best Practices
- Create reports at the end of each work day
- Use clear, descriptive names for machines
- Include location details in job site name
- Note any issues or delays in the notes field

### Data Management
- Review old reports periodically
- Delete test or duplicate entries
- Keep consistent formatting for easier tracking

## Common Questions

**Q: Can I edit a report after creating it?**
A: Not in the current version, but this feature is planned for future updates.

**Q: Is my data backed up?**
A: Currently, data is stored only on your device. Cloud backup with Firebase is planned.

**Q: Can multiple people use the same app?**
A: Yes, but each person's data is separate on their device. Team features are planned.

**Q: How much storage does the app use?**
A: Very little - each report is only a few bytes. You can store thousands of reports.

**Q: Can I export my reports?**
A: Not yet, but PDF/CSV export is planned for future versions.

## Future Features

Coming soon:
- 📝 Edit existing reports
- 📊 Statistics and summaries
- 📄 Export to PDF
- ☁️ Cloud backup
- 👥 Team collaboration
- 📸 Photo attachments
- 🔔 Daily reminders
- 🔍 Search and filter

## Support

For issues or questions:
1. Check the BUILD.md file for build issues
2. Check ARCHITECTURE.md for technical details
3. Review this guide for usage questions

## Technical Requirements

- **Device**: Android smartphone or tablet
- **OS Version**: Android 7.0 (Nougat) or higher
- **Storage**: ~10 MB for app
- **Permissions**: None required (no internet, camera, etc.)

## Privacy & Security

- ✅ All data stored locally
- ✅ No internet connection required
- ✅ No data collection
- ✅ No ads or tracking
- ✅ Open source code
- ✅ Your data stays on your device

## Getting Started

Ready to start? Just:
1. Open the app
2. Tap the + button
3. Fill in your first report
4. Tap save

That's it! You're now tracking your daily work reports.
