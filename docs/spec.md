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
- Quick search for job sites

### 2. Create/Edit Report
- Add new daily work report
- Edit existing reports
- Delete reports
- Validate required fields
- Track materials used with quantities and units
- Add, edit, and remove materials from reports

### 3. Job Site Quick Search
- Filter job sites by text search
- Real-time filtering as user types
- Quick selection from filtered results
- Display match count

### 4. Materials Tracking
- Add multiple materials per work report
- Record material name, quantity, unit, and optional notes
- View all materials in a list
- Remove materials from the list
- Included in work report data model

### 5. Data Persistence
- Local storage using Room database
- Offline-first architecture
- Data survives app restarts

### 6. PDF Export (Specification Available)
- Export work reports to PDF format
- Professional layout with company branding
- Materials table included in export
- Signature blocks and metadata
- See `/docs/pdf_export.md` for complete specification

## Screens

### Dashboard Screen
**Purpose**: Main screen showing all work reports

**Components**:
- Top app bar with title and "Add Report" button
- Search bar for quick job site filtering
- List of work reports (RecyclerView/LazyColumn)
- Each report card shows:
  - Date
  - Job site name
  - Machine used
  - Hours worked
  - Materials count (if any)
  - Tap to edit

### New/Edit Report Screen
**Purpose**: Form to create or edit a work report

**Components**:
- Date picker field (required)
- Job site text field (required) with quick search option
- Machine text field (required)
- Hours worked number field (required, decimal)
- Notes multi-line text field (optional)
- Materials section:
  - Add material form (name, quantity, unit, note)
  - Materials list with delete option
  - Material count display
- Save button
- Cancel button

### Job Site Quick Search Screen
**Purpose**: Filter and select job sites quickly

**Components**:
- Search text field with clear button
- Real-time filtered results list
- Result count display
- Clickable job site cards
- Empty state message when no results

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
| materials | List<Material> | No | List of materials used (default: empty list) |

### Material Model
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | String | Yes | Name or description of the material |
| quantity | Double | Yes | Quantity of material used |
| unit | String | Yes | Unit of measurement (kg, m³, units, liters, etc.) |
| note | String | No | Optional notes about the material |

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
- Material Name: 1-100 characters (when adding materials)
- Material Quantity: Must be a positive number > 0
- Material Unit: 1-20 characters (when adding materials)
- Material Note: 0-200 characters (optional)

## Future Enhancements
- Export reports to PDF/CSV (see `/docs/pdf_export.md` for PDF specification)
- Photo attachments
- GPS location tracking
- Offline sync with cloud
- Report templates
- Statistics and analytics
- Multi-user support
- Weather integration
- Time tracking with clock in/out
- Crew member management
- Equipment maintenance tracking
- Cost tracking per material
