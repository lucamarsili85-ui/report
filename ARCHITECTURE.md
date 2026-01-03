# Project Architecture Documentation

## Overview
Rapportino is a work daily report Android application built with modern Android development practices using Kotlin and Jetpack Compose.

## Technology Stack

### Core Technologies
- **Language**: Kotlin 1.8.20
- **Build System**: Gradle 8.2
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

### Libraries and Frameworks

#### UI Layer
- **Jetpack Compose**: Modern declarative UI framework
  - Compose BOM 2023.10.01
  - Material3 for Material Design components
  - Material Icons Extended for icons
  - Navigation Compose for screen navigation

#### Architecture Components
- **ViewModel**: For UI state management and survival of configuration changes
- **LiveData/Flow**: For reactive data streams
- **Navigation Component**: Type-safe navigation between screens

#### Data Layer
- **Room 2.6.0**: SQLite database abstraction
  - Room Runtime
  - Room KTX (Kotlin extensions and coroutines support)
  - Room Compiler (KSP)

#### Dependency Injection
- Manual dependency injection via Application class (ready for Hilt/Dagger migration)

## Application Structure

### Package Organization
```
com.example.rapportino/
├── data/                          # Data layer
│   ├── dao/                       # Data Access Objects
│   │   └── ReportDao.kt          # Database operations for Report entity
│   ├── database/                  # Database configuration
│   │   └── AppDatabase.kt        # Room database instance
│   ├── entity/                    # Data models
│   │   └── Report.kt             # Report entity with Room annotations
│   └── repository/                # Repository pattern
│       └── ReportRepository.kt   # Abstraction over data sources
├── ui/                            # UI layer
│   ├── dashboard/                 # Dashboard feature
│   │   └── DashboardScreen.kt    # Main screen with report list
│   ├── addreport/                 # Add report feature
│   │   └── AddReportScreen.kt    # Form to create new reports
│   ├── navigation/                # Navigation setup
│   │   ├── Screen.kt             # Screen destinations
│   │   └── RapportinoNavGraph.kt # Navigation graph
│   └── theme/                     # App theming
│       ├── Color.kt              # Color palette
│       ├── Type.kt               # Typography
│       └── Theme.kt              # Theme composition
├── viewmodel/                     # ViewModels
│   ├── DashboardViewModel.kt     # Dashboard screen state
│   └── AddReportViewModel.kt     # Add report screen state
├── MainActivity.kt                # Main entry point
└── RapportinoApplication.kt      # Application class
```

## Architecture Pattern: MVVM (Model-View-ViewModel)

### Model Layer
- **Entity**: `Report` - Represents a work report with fields:
  - `id`: Auto-generated primary key
  - `date`: Report date (timestamp)
  - `jobSite`: Work site name (Cantiere)
  - `machine`: Machine name (Macchina)
  - `workedHours`: Number of hours worked
  - `notes`: Optional notes
  - `createdAt`: Record creation timestamp

- **DAO**: `ReportDao` - Defines database operations:
  - `getAllReports()`: Returns Flow of all reports ordered by date
  - `getReportById(id)`: Retrieves specific report
  - `insertReport(report)`: Adds new report
  - `updateReport(report)`: Updates existing report
  - `deleteReport(report)`: Removes report
  - `deleteReportById(id)`: Removes report by ID

- **Repository**: `ReportRepository` - Provides clean API to access data:
  - Abstracts the data source (Room database)
  - Ready for future integration with remote data sources (Firebase)
  - Provides Flow for reactive updates

### ViewModel Layer
- **DashboardViewModel**:
  - Exposes `StateFlow<List<Report>>` for report list
  - Handles delete operations
  - Survives configuration changes
  
- **AddReportViewModel**:
  - Handles report creation logic
  - Validates input data
  - Provides date utility functions

### View Layer (Composables)
- **DashboardScreen**:
  - Displays list of reports in reverse chronological order
  - Floating Action Button to add new reports
  - Swipe-to-delete functionality (via delete icon)
  - Empty state when no reports exist

- **AddReportScreen**:
  - Form with input fields:
    - Date picker
    - Text inputs for job site, machine
    - Numeric input for hours
    - Multi-line text for notes
  - Input validation
  - Save and cancel operations

## Navigation Flow
```
┌─────────────────┐
│  MainActivity   │
└────────┬────────┘
         │
         ├── RapportinoTheme
         │
         ├── NavController
         │
         └── RapportinoNavGraph
              │
              ├──[/dashboard]──────> DashboardScreen
              │                      ├── Display reports list
              │                      ├── FAB: navigate to add
              │                      └── Delete button per report
              │
              └──[/add_report]─────> AddReportScreen
                                     ├── Date picker
                                     ├── Input fields
                                     ├── Validation
                                     └── Save: navigate back
```

## Data Flow

### Adding a Report
```
User Input (AddReportScreen)
    ↓
AddReportViewModel.saveReport()
    ↓
ReportRepository.insert()
    ↓
ReportDao.insertReport()
    ↓
Room Database
    ↓
Flow emission
    ↓
DashboardViewModel.reports
    ↓
DashboardScreen updates
```

### Displaying Reports
```
App Launch
    ↓
DashboardViewModel initialization
    ↓
ReportRepository.allReports (Flow)
    ↓
ReportDao.getAllReports()
    ↓
Room Database query
    ↓
StateFlow collection in DashboardScreen
    ↓
LazyColumn renders report cards
```

## Database Schema

### Table: reports
| Column | Type | Constraints |
|--------|------|-------------|
| id | INTEGER | PRIMARY KEY, AUTOINCREMENT |
| date | INTEGER | NOT NULL (timestamp) |
| jobSite | TEXT | NOT NULL |
| machine | TEXT | NOT NULL |
| workedHours | REAL | NOT NULL |
| notes | TEXT | NOT NULL |
| createdAt | INTEGER | NOT NULL (timestamp) |

## Future Enhancements

### Firebase Integration
The architecture is designed to easily support Firebase:
1. **Repository Pattern**: Abstract data source switching
2. **Flow-based APIs**: Compatible with Firestore real-time updates
3. **Modular structure**: Easy to add remote data source

### Planned Features
- User authentication (Firebase Auth)
- Cloud sync (Firestore)
- Report editing
- Report filtering and search
- Export to PDF
- Offline-first with sync
- Multi-user support
- Report templates
- Statistics and charts

### Technical Improvements
- Dependency injection with Hilt
- Unit and UI testing
- CI/CD pipeline
- ProGuard rules optimization
- App signing configuration
- Multi-module architecture

## Building the Project

### Prerequisites
1. Android Studio (latest stable version)
2. JDK 17 or higher
3. Android SDK 34
4. Internet connection for dependency download

### Build Instructions
```bash
# Clone repository
git clone https://github.com/lucamarsili85-ui/report.git
cd report

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing configuration)
./gradlew assembleRelease

# Run on connected device/emulator
./gradlew installDebug
```

### Running the App
1. Open project in Android Studio
2. Sync Gradle files
3. Select target device (emulator or physical)
4. Click Run (Shift+F10)

## UI Screenshots (Expected)

### Dashboard Screen
- Top app bar with title "Rapportino - Dashboard"
- List of report cards showing:
  - Date
  - Job site
  - Machine
  - Hours worked
  - Notes (if present)
  - Delete button
- Floating action button (+) to add new report
- Empty state with instruction text

### Add Report Screen  
- Top app bar with back button and title "Nuovo Rapportino"
- Date selection button with calendar icon
- Text field for "Cantiere" (job site)
- Text field for "Macchina" (machine)
- Numeric field for "Ore lavorate" (worked hours)
- Multi-line text field for "Note"
- Save button at bottom

## License
This project is open source and available for modification and distribution.
