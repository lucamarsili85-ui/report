# Work Report - Android App

Daily work report application built with Kotlin and Jetpack Compose.

## Features

- Dashboard to view all work reports
- Create new work reports with the following fields:
  - Date
  - Job Site
  - Machine
  - Worked Hours
  - Notes
- Data persistence using Room database
- MVVM architecture
- Material Design 3 UI

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room
- **Navigation**: Jetpack Navigation Compose
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Building the Project

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 8 or later
- Android SDK 34

### Build Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Wait for Gradle sync to complete
4. Build the project: `Build > Make Project`
5. Run on an emulator or physical device: `Run > Run 'app'`

### Command Line Build

```bash
./gradlew build
```

## Project Structure

```
app/
├── src/main/java/com/workreport/
│   ├── MainActivity.kt
│   ├── data/
│   │   ├── entity/WorkReport.kt
│   │   ├── dao/WorkReportDao.kt
│   │   └── database/WorkReportDatabase.kt
│   ├── viewmodel/
│   │   └── WorkReportViewModel.kt
│   └── ui/
│       ├── screens/
│       │   ├── DashboardScreen.kt
│       │   └── NewReportScreen.kt
│       ├── navigation/
│       │   ├── Screen.kt
│       │   └── NavHost.kt
│       └── theme/
│           ├── Color.kt
│           ├── Theme.kt
│           └── Type.kt
```

## Future Enhancements

The app is prepared for future Firebase integration for:
- Cloud data synchronization
- User authentication
- Real-time updates

## License

This project is for personal/internal use.
