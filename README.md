# Work Report - Android App

Daily work report application built with Kotlin and Jetpack Compose.

## ⚠️ Build Environment Note

**This project is fully structured and ready to compile in Android Studio.** However, it cannot be built in this sandboxed environment due to network restrictions preventing access to Google's Maven repository. See [BUILD_LIMITATIONS.md](BUILD_LIMITATIONS.md) for details.

## Features

- ✅ Dashboard to view all work reports
- ✅ Create new work reports with the following fields:
  - Date (with date picker)
  - Job Site
  - Machine
  - Worked Hours
  - Notes
- ✅ Data persistence using Room database
- ✅ MVVM architecture with ViewModel
- ✅ Material Design 3 UI with Jetpack Compose
- ✅ Navigation between screens

## Technology Stack

- **Language**: Kotlin 1.8.20
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room 2.6.1
- **Navigation**: Jetpack Navigation Compose 2.7.5
- **Build Tool**: Gradle 8.0 with Kotlin DSL
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Project Structure

```
app/
├── src/main/
│   ├── java/com/workreport/
│   │   ├── MainActivity.kt                    # Main activity with Compose setup
│   │   ├── data/
│   │   │   ├── entity/
│   │   │   │   └── WorkReport.kt             # Room entity for work reports
│   │   │   ├── dao/
│   │   │   │   └── WorkReportDao.kt          # Room DAO for database operations
│   │   │   └── database/
│   │   │       └── WorkReportDatabase.kt      # Room database singleton
│   │   ├── viewmodel/
│   │   │   └── WorkReportViewModel.kt         # ViewModel for managing reports
│   │   └── ui/
│   │       ├── screens/
│   │       │   ├── DashboardScreen.kt         # Dashboard with reports list
│   │       │   └── NewReportScreen.kt         # Form for new reports
│   │       ├── navigation/
│   │       │   ├── Screen.kt                  # Screen routes
│   │       │   └── NavHost.kt                 # Navigation setup
│   │       └── theme/
│   │           ├── Color.kt                   # Color definitions
│   │           ├── Theme.kt                   # Material theme setup
│   │           └── Type.kt                    # Typography definitions
│   ├── res/
│   │   ├── values/
│   │   │   ├── strings.xml                    # String resources
│   │   │   ├── colors.xml                     # Color resources
│   │   │   └── themes.xml                     # Theme resources
│   │   └── drawable/                          # Icons and drawables
│   └── AndroidManifest.xml                     # App manifest
├── build.gradle.kts                            # Module build configuration
└── proguard-rules.pro                          # ProGuard rules

build.gradle.kts                                # Project build configuration
settings.gradle.kts                             # Gradle settings
```

## Building the Project

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 8 or higher
- Android SDK 34
- Internet connection (for downloading dependencies from Google's Maven repository)

### Build Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/lucamarsili85-ui/report.git
   cd report
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open" and navigate to the project directory
   - Wait for Gradle sync to complete (first sync downloads dependencies)

3. **Build the project**
   - Via IDE: `Build > Make Project` (Ctrl+F9 / Cmd+F9)
   - Via command line: `./gradlew assembleDebug`

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press Shift+F10 / Ctrl+R
   - Or via command line: `./gradlew installDebug`

## Architecture Overview

### MVVM Pattern

- **Model**: Room database entities and DAOs (`WorkReport`, `WorkReportDao`)
- **View**: Composable screens (`DashboardScreen`, `NewReportScreen`)
- **ViewModel**: `WorkReportViewModel` manages UI state and database operations

### Data Flow

1. User interacts with UI (Composable screens)
2. ViewModel receives actions and updates database via Room
3. Room provides Flow of data that ViewModel exposes to UI
4. UI automatically recomposes when data changes

### Navigation

Uses Jetpack Navigation Compose with two routes:
- `/dashboard` - Main screen showing all reports
- `/new_report` - Form for creating new reports

## Key Features Implementation

### Dashboard Screen
- Displays list of work reports using `LazyColumn`
- Each report shown in a `Card` with date, job site, machine, hours, and notes
- FloatingActionButton to navigate to new report screen
- Empty state message when no reports exist

### New Report Screen
- Form fields for all required data
- Material 3 `DatePicker` for date selection
- Input validation (job site, machine, and hours are required)
- Data saved to Room database via ViewModel
- Automatic navigation back to dashboard after save

### Room Database
- Entity: `WorkReport` with auto-generated ID
- DAO: Provides insert and query operations
- Database: Singleton pattern ensures single instance
- Flow-based queries for reactive UI updates

## Future Enhancements

The app structure is prepared for future Firebase integration:
- User authentication (Firebase Auth)
- Cloud data synchronization (Firestore)
- Real-time updates across devices
- Cloud backup and restore

## Dependencies

All dependencies are declared in `app/build.gradle.kts`:
- AndroidX Core & Lifecycle
- Jetpack Compose (UI, Material 3, Navigation)
- Room Database & KSP processor
- ViewModel & LiveData

## License

This project is for personal/internal use.

## Development Status

✅ Project structure complete  
✅ All source files implemented  
✅ MVVM architecture in place  
✅ Room database configured  
✅ Navigation setup complete  
✅ UI screens implemented  
✅ Ready for compilation in Android Studio
