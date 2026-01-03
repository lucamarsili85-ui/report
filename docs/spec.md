# Daily Work Report App - Specification

## Overview
Android application for logging daily work reports with job site details, machine usage, work hours, and notes.

## Features

### 1. Dashboard
- View all work reports in a list
- Display summary information (date, job site, total hours)
- Filter reports by date range
- Quick access to create new report
- Calculate total hours for selected period

### 2. Create/Edit Report
- Add new daily work report
- Edit existing reports
- Delete reports
- Validate required fields

### 3. Data Persistence
- Local storage using Room database
- Offline-first architecture
- Data survives app restarts

## Screens

### Dashboard Screen
**Purpose**: Main screen showing all work reports

**Components**:
- Top app bar with title and "Add Report" button
- List of work reports (RecyclerView/LazyColumn)
- Each report card shows:
  - Date
  - Job site name
  - Machine used
  - Hours worked
  - Tap to edit

### New/Edit Report Screen
**Purpose**: Form to create or edit a work report

**Components**:
- Date picker field (required)
- Job site text field (required)
- Machine text field (required)
- Hours worked number field (required, decimal)
- Notes multi-line text field (optional)
- Save button
- Cancel button

## Data Fields

### WorkReport Entity
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | Long | Auto | Primary key, auto-generated |
| date | Long | Yes | Date of work (timestamp) |
| jobSite | String | Yes | Name/location of job site |
| machine | String | Yes | Machine or equipment used |
| hoursWorked | Double | Yes | Number of hours worked (e.g., 8.5) |
| notes | String | No | Additional notes or comments |
| createdAt | Long | Auto | Timestamp when record was created |
| updatedAt | Long | Auto | Timestamp when record was last updated |

## Technical Architecture

### Layers
1. **UI Layer** (Jetpack Compose)
   - Screens and composables
   - UI state management
   - Navigation

2. **ViewModel Layer**
   - State holders
   - Business logic
   - UI state transformation

3. **Repository Layer**
   - Data access abstraction
   - Single source of truth

4. **Data Layer** (Room)
   - Database definition
   - DAOs (Data Access Objects)
   - Entities

### Technology Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Database**: Room
- **Architecture**: MVVM (Model-View-ViewModel)
- **Async**: Kotlin Coroutines & Flow
- **DI**: Hilt (when integrated)

## Validation Rules
- Date: Cannot be in the future
- Job Site: 1-100 characters
- Machine: 1-50 characters
- Hours Worked: 0.1 - 24.0 hours
- Notes: 0-500 characters

## Future Enhancements
- Export reports to PDF/CSV
- Photo attachments
- GPS location tracking
- Offline sync with cloud
- Report templates
- Statistics and analytics
- Multi-user support
