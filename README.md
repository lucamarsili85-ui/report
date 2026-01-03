# Daily Work Report App

An Android application for logging daily work reports with job site details, machine usage, work hours, and notes.

## Overview

This repository contains the structure and code samples for an Android app built with:
- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Room** - Local database persistence
- **MVVM Architecture** - Clean architecture pattern

## Repository Structure

```
/docs
  └── spec.md                 # Complete app specification with features and data fields

/app_structure
  ├── WorkReport.kt           # Room entity for work reports
  ├── WorkReportDao.kt        # Data Access Object with database queries
  ├── AppDatabase.kt          # Room database configuration
  ├── WorkReportRepository.kt # Repository layer for data abstraction
  └── WorkReportViewModel.kt  # ViewModel for UI state management

/ui
  ├── DashboardScreen.kt      # Main screen showing all work reports
  └── NewReportScreen.kt      # Form for creating/editing reports
```

## Architecture

The app follows the **MVVM (Model-View-ViewModel)** architecture pattern:

### Layers

1. **UI Layer** (`/ui`)
   - Built with Jetpack Compose
   - Composable functions for screens and components
   - Observes ViewModel state using StateFlow/Flow
   - Examples: `DashboardScreen`, `NewReportScreen`

2. **ViewModel Layer** (`WorkReportViewModel.kt`)
   - Manages UI state
   - Handles business logic
   - Exposes StateFlow for reactive UI updates
   - Coordinates with Repository using Coroutines

3. **Repository Layer** (`WorkReportRepository.kt`)
   - Single source of truth for data
   - Abstracts data sources (Room database)
   - Provides clean API for ViewModel
   - Handles data transformations

4. **Data Layer** (Entity, DAO, Database)
   - **Entity**: `WorkReport.kt` - Defines database schema
   - **DAO**: `WorkReportDao.kt` - Database queries with Flow/suspend functions
   - **Database**: `AppDatabase.kt` - Room database configuration

### Data Flow

```
UI (Compose) → ViewModel → Repository → DAO → Room Database
     ↑            ↑            ↑          ↑
     └────────────┴────────────┴──────────┘
              StateFlow/Flow updates
```

## Key Features

- ✅ **Dashboard** - View all work reports with summary
- ✅ **Create/Edit Reports** - Form with date, job site, machine, hours, notes
- ✅ **Data Validation** - Input validation for all fields
- ✅ **Persistence** - Local storage using Room database
- ✅ **Reactive UI** - Auto-updates using Kotlin Flow
- ✅ **Material Design 3** - Modern UI components

## Technology Stack

| Component | Technology |
|-----------|-----------|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Database | Room |
| Async | Kotlin Coroutines + Flow |
| Architecture | MVVM |
| Material Design | Material 3 (Material You) |

## Next Steps

To integrate this into a full Android project:

### 1. Create Android Project
```bash
# Create new Android Studio project with:
# - Empty Activity
# - Language: Kotlin
# - Minimum SDK: 24 (Android 7.0)
# - Build configuration: Kotlin DSL
```

### 2. Add Dependencies
Add to `app/build.gradle.kts`:
```kotlin
dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

### 3. Setup Kotlin Symbol Processing (KSP)
Add to `build.gradle.kts` (project level):
```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}
```

Add to `app/build.gradle.kts`:
```kotlin
plugins {
    id("com.google.devtools.ksp")
}
```

### 4. Copy Code Files
- Copy `/app_structure/*.kt` to `app/src/main/java/com/example/workreport/`
- Copy `/ui/*.kt` to `app/src/main/java/com/example/workreport/ui/screens/`
- Organize imports and adjust package names if needed

### 5. Setup Navigation
Create a Navigation graph to connect screens:
```kotlin
// In MainActivity or navigation package
@Composable
fun WorkReportNavigation(viewModel: WorkReportViewModel) {
    val navController = rememberNavController()
    
    NavHost(navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToNewReport = { navController.navigate("new_report") },
                onReportClick = { /* Navigate to edit */ }
            )
        }
        composable("new_report") {
            NewReportScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
```

### 6. Initialize Database & ViewModel
In your Application class or MainActivity:
```kotlin
class WorkReportApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { WorkReportRepository(database.workReportDao()) }
}

// In MainActivity
class MainActivity : ComponentActivity() {
    private val application by lazy { application as WorkReportApplication }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val viewModel = WorkReportViewModel(application.repository)
        
        setContent {
            WorkReportTheme {
                WorkReportNavigation(viewModel)
            }
        }
    }
}
```

### 7. Future Enhancements
- [ ] Implement Material3 DatePicker for date selection
- [ ] Add edit functionality for existing reports
- [ ] Implement swipe-to-delete on dashboard
- [ ] Add search and filter capabilities
- [ ] Implement data export (PDF/CSV)
- [ ] Add photo attachments
- [ ] Cloud sync with Firebase/backend API
- [ ] Add Hilt for dependency injection
- [ ] Implement unit tests and UI tests
- [ ] Add dark theme support
- [ ] Localization support

## Documentation

See `/docs/spec.md` for complete specification including:
- Detailed feature descriptions
- Screen layouts and components
- Data field definitions
- Validation rules
- Technical architecture details

## Development Notes

- All code samples are production-ready and follow Android best practices
- Uses modern Jetpack Compose for UI (no XML layouts)
- Implements reactive programming with Kotlin Flow
- Database operations are performed asynchronously with coroutines
- Material Design 3 components for modern UI/UX
- Code includes comprehensive documentation and comments

## License

This project structure is provided as a template for building an Android work report application.
