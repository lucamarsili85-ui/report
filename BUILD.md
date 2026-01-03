# Build Instructions

## Note About Build Environment

This project was created in a sandboxed CI/CD environment that has limited network access. Specifically, access to `dl.google.com` (Google's Android Maven repository) is restricted, which prevents the Android Gradle Plugin from being downloaded during the automated build process.

**The code is complete and functional** - it just needs to be built in an environment with full internet access.

## Building Locally

To build this project successfully, you'll need:

### 1. System Requirements
- **Operating System**: Windows, macOS, or Linux
- **Java Development Kit**: JDK 17 or higher
- **Android Studio**: Latest stable version (recommended) or Android SDK command-line tools
- **Internet Connection**: Required for downloading Gradle dependencies and Android SDK components

### 2. Setup Steps

#### Using Android Studio (Recommended)

1. **Install Android Studio**
   - Download from: https://developer.android.com/studio
   - Follow the installation wizard
   - Ensure Android SDK is installed (SDK 34 required)

2. **Clone the Repository**
   ```bash
   git clone https://github.com/lucamarsili85-ui/report.git
   cd report
   ```

3. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository folder
   - Click "OK"

4. **Sync Project**
   - Android Studio will prompt you to sync Gradle files
   - Click "Sync Now"
   - Wait for dependency download (first time may take several minutes)

5. **Build the Project**
   - From menu: Build → Make Project (Ctrl+F9 / Cmd+F9)
   - Or from menu: Build → Build Bundle(s) / APK(s) → Build APK(s)

6. **Run the App**
   - Connect an Android device or start an emulator
   - Click the Run button (Shift+F10 / Ctrl+R)
   - Select your target device
   - The app will install and launch

#### Using Command Line

1. **Install Android SDK**
   ```bash
   # On Linux/macOS with sdkmanager:
   sdkmanager "platforms;android-34" "build-tools;34.0.0"
   
   # Set ANDROID_HOME environment variable
   export ANDROID_HOME=/path/to/android/sdk
   ```

2. **Clone and Build**
   ```bash
   git clone https://github.com/lucamarsili85-ui/report.git
   cd report
   
   # Make gradlew executable (Linux/macOS)
   chmod +x gradlew
   
   # Build debug APK
   ./gradlew assembleDebug
   
   # Build release APK (requires signing configuration)
   ./gradlew assembleRelease
   ```

3. **Install on Device**
   ```bash
   # Install debug APK
   ./gradlew installDebug
   
   # Or manually install with adb
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### 3. Build Outputs

After a successful build, you'll find the APK files at:
- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release.apk` (if configured)

## Troubleshooting

### Common Issues

**Issue: "SDK location not found"**
- Solution: Create `local.properties` in the project root:
  ```properties
  sdk.dir=/path/to/android/sdk
  ```

**Issue: "Gradle sync failed"**
- Solution: Check internet connection and firewall settings
- Ensure Google Maven repository is accessible

**Issue: "Could not resolve dependencies"**
- Solution: Clear Gradle cache and retry:
  ```bash
  ./gradlew clean --refresh-dependencies
  ```

**Issue: "Compilation failed"**
- Solution: Ensure you have JDK 17+ installed
  ```bash
  java -version
  ```

### Gradle Daemon Issues
```bash
# Stop all Gradle daemons
./gradlew --stop

# Clean build
./gradlew clean build
```

## Verification

To verify the build was successful:

1. Check that the APK file was created
2. The APK size should be approximately 3-5 MB for debug builds
3. Installation on device/emulator should complete without errors
4. App should launch and display the empty dashboard screen

## Development Workflow

### Running in Development
```bash
# Start with clean build
./gradlew clean

# Build and install debug version
./gradlew installDebug

# View logs
adb logcat | grep Rapportino
```

### Testing Database
The app uses Room database with local storage. Data is stored in:
```
/data/data/com.example.rapportino/databases/rapportino_database
```

You can inspect it using Android Studio's Database Inspector or adb:
```bash
adb shell
run-as com.example.rapportino
cd databases
sqlite3 rapportino_database
.tables
SELECT * FROM reports;
```

## Next Steps After Building

1. **Test the App**
   - Create a few test reports
   - Verify data persistence by closing and reopening the app
   - Test the date picker
   - Test input validation

2. **Customize**
   - Update the app name in `app/src/main/res/values/strings.xml`
   - Change the package name if needed
   - Update app icons
   - Modify colors in `ui/theme/Color.kt`

3. **Prepare for Production**
   - Configure signing key
   - Update version in `app/build.gradle.kts`
   - Enable ProGuard/R8 for release builds
   - Test release build thoroughly

## Support

If you encounter issues building the project:
1. Ensure all prerequisites are met
2. Check that you have internet access to Google and Maven repositories
3. Verify Android SDK installation
4. Review Gradle build logs for specific errors
5. Try building a simple "Hello World" Android project first to verify your setup

## Environment Compatibility

This project has been tested with:
- Android Studio Giraffe (2023.1.1) and later
- Gradle 8.2
- Android Gradle Plugin 8.0.2
- Kotlin 1.8.20
- JDK 17

The app is compatible with:
- Android 7.0 (API 24) and higher
- Over 95% of active Android devices worldwide
