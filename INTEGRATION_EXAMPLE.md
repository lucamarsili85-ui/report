# Integration Example

This file shows how to integrate the Daily Journal implementation into a complete Android application.

## Step 1: Update Dependencies

Add to `app/build.gradle.kts`:

```kotlin
dependencies {
    // Existing dependencies...
    
    // Room with version 2.6.1 or higher
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
}
```

## Step 2: Application Class

Create or update your Application class:

```kotlin
package com.example.workreport

import android.app.Application
import com.example.workreport.data.database.AppDatabase
import com.example.workreport.data.repository.DailyReportRepository
import com.example.workreport.data.repository.WorkReportRepository

class WorkReportApplication : Application() {
    // Database
    val database by lazy { AppDatabase.getDatabase(this) }
    
    // Repositories
    val workReportRepository by lazy { 
        WorkReportRepository(database.workReportDao()) 
    }
    val dailyReportRepository by lazy { 
        DailyReportRepository(database.dailyReportDao()) 
    }
}
```

Update `AndroidManifest.xml`:

```xml
<application
    android:name=".WorkReportApplication"
    ...>
```

## Step 3: MainActivity with Navigation

```kotlin
package com.example.workreport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.workreport.ui.screens.DailyJournalScreen
import com.example.workreport.ui.screens.DashboardScreen
import com.example.workreport.ui.viewmodel.DailyReportViewModel
import com.example.workreport.ui.viewmodel.WorkReportViewModel
import com.example.workreport.ui.theme.WorkReportTheme

class MainActivity : ComponentActivity() {
    private val application by lazy { application as WorkReportApplication }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            WorkReportTheme {
                WorkReportNavigation()
            }
        }
    }
    
    @Composable
    fun WorkReportNavigation() {
        val navController = rememberNavController()
        
        // Create ViewModels
        val dailyReportViewModel: DailyReportViewModel = viewModel(
            factory = DailyReportViewModelFactory(application.dailyReportRepository)
        )
        
        NavHost(navController, startDestination = "dashboard") {
            composable("dashboard") {
                DashboardScreen(
                    dailyReportViewModel = dailyReportViewModel,
                    onNavigateToDailyJournal = { 
                        navController.navigate("daily_journal") 
                    },
                    onDailyReportClick = { report ->
                        // Load the specific report and navigate to journal
                        dailyReportViewModel.loadDailyReport(report.id)
                        navController.navigate("daily_journal")
                    }
                )
            }
            
            composable("daily_journal") {
                DailyJournalScreen(
                    viewModel = dailyReportViewModel,
                    onNavigateBack = { 
                        navController.popBackStack() 
                    }
                )
            }
        }
    }
}
```

## Step 4: ViewModel Factory

Create a factory to provide the repository to the ViewModel:

```kotlin
package com.example.workreport.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.workreport.data.repository.DailyReportRepository

class DailyReportViewModelFactory(
    private val repository: DailyReportRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DailyReportViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

## Step 5: Theme (Optional)

Create a basic Material 3 theme:

```kotlin
package com.example.workreport.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF006A4E),
    secondary = androidx.compose.ui.graphics.Color(0xFF4CAF50),
    tertiary = androidx.compose.ui.graphics.Color(0xFF607D8B)
)

@Composable
fun WorkReportTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
```

## Usage Flow

### User Journey

1. **App Launch**
   - User sees Dashboard with history of finalized reports
   - FAB button visible to create new daily journal

2. **Create Daily Journal**
   - User taps FAB
   - App navigates to DailyJournalScreen
   - If draft exists for today → resume it
   - If no draft exists → create new one

3. **Add Work Progressively**
   - User taps "Add Client"
   - Enters client name and job site
   - Client card appears immediately (saved to database)
   
   - User taps "+ Machine" on client card
   - Enters machine details
   - Activity appears immediately (saved to database)
   
   - User can close app and reopen
   - All data is preserved

4. **End of Day**
   - User taps "Save & Finalize Daily Report"
   - Report status changes to FINAL
   - UI switches to preview mode
   - Edit controls disappear
   - "Edit Report" button appears

5. **View History**
   - User navigates back to Dashboard
   - Finalized report appears in history
   - Can tap to view or edit

## Testing

Run the app:

```bash
./gradlew installDebug
```

Or use Android Studio's Run button.

### Quick Test Scenario

1. Launch app
2. Tap FAB (+) on Dashboard
3. Tap "Add Client"
   - Client: "ACME Corp"
   - Site: "Via Roma 10, Milano"
4. Tap "+ Machine"
   - Machine: "Excavator"
   - Hours: 8
5. Tap "+ Material"
   - Material: "Concrete"
   - Quantity: 25
   - Unit: m³
6. Verify total hours shows "8.0 h"
7. Tap "Save & Finalize Daily Report"
8. Verify UI changes to preview mode
9. Tap back arrow
10. Verify report appears in Dashboard history

## Troubleshooting

### Database not created
- Make sure `fallbackToDestructiveMigration()` is set
- Check application class is registered in manifest

### ViewModels not persisting
- Use `viewModel()` from `androidx.lifecycle.viewmodel.compose`
- Pass ViewModels from parent composable

### Navigation issues
- Ensure NavHost startDestination matches a composable route
- Use `navController.popBackStack()` to go back

### Compilation errors
- Verify all imports are correct
- Check Room and Compose versions match
- Sync Gradle and rebuild

## Next Steps

After basic integration:

1. Add proper Material 3 DatePicker for date selection
2. Implement swipe-to-delete on history items
3. Add search/filter to Dashboard
4. Implement PDF export for finalized reports
5. Add photo attachments to activities
6. Implement cloud sync (Firebase/backend)
