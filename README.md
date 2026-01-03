# Rapportino - Daily Work Report App

An Android application for managing daily work reports (rapportino di lavoro) built with Kotlin and Jetpack Compose.

## Features

- **Dashboard Screen**: View all your work reports in a clean, organized list
- **Add Report Screen**: Create new reports with the following information:
  - Date (with date picker)
  - Job site (Cantiere)
  - Machine (Macchina)
  - Worked hours (Ore lavorate)
  - Notes (optional)
- **Local Storage**: All data is stored locally using Room database
- **Modern UI**: Clean and simple interface built with Jetpack Compose
- **Navigation**: Smooth navigation between screens

## Architecture

The app follows modern Android development best practices:

```
app/src/main/java/com/example/rapportino/
├── data/
│   ├── dao/           # Data Access Objects
│   ├── database/      # Room database configuration
│   ├── entity/        # Data entities
│   └── repository/    # Repository pattern implementation
├── ui/
│   ├── dashboard/     # Dashboard screen
│   ├── addreport/     # Add report screen
│   ├── navigation/    # Navigation setup
│   └── theme/         # App theme (colors, typography)
├── viewmodel/         # ViewModels for UI state management
├── MainActivity.kt    # Main activity
└── RapportinoApplication.kt  # Application class
```

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Database**: Room (SQLite)
- **Architecture Components**:
  - ViewModel
  - LiveData/Flow
  - Navigation Component
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Building the Project

### Prerequisites

- Android Studio (latest version recommended)
- JDK 17 or higher
- Android SDK 34

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/lucamarsili85-ui/report.git
   cd report
   ```

2. Open the project in Android Studio

3. Sync Gradle files

4. Run the app on an emulator or physical device

Alternatively, build from command line:
```bash
./gradlew assembleDebug
```

## Future Enhancements

The project is structured to easily support:
- Firebase integration for cloud storage and sync
- User authentication
- Report editing functionality
- Export reports to PDF
- Advanced filtering and search
- Statistics and analytics

## License

This project is open source and available under standard licensing terms.
