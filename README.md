# FitTrack-SA-Ultra-2.0

FitTrack SA is a minimalist Android app written in Kotlin/Jetpack Compose that keeps activity and nutrition logs with an offline-first Room database and a dark-mode friendly black/lime palette.

### Real-time notifications
- The app now provisions a `FitTrack Live Alerts` notification channel and honors the in-app notification toggle.
- Successful logins, offline activity or meal saves, and sign-outs immediately trigger notifications so users receive live feedback even when outside the app.

## Prerequisites
- Android Studio Jellyfish (2023.3.1) or newer with Android Gradle Plugin 8.4+ support
- JetBrains Runtime 21.0.6 (or any JDK 21) configured as the Gradle JDK; Gradle can auto-provision if unavailable
- Android SDK Platform 34 and Google Maven repository enabled

## Building the app

```bash
./gradlew assembleDebug
```

> **Note:** The repository ships a lightweight wrapper script. If the embedded Gradle wrapper binaries are not present on your system, install Gradle locally or regenerate the wrapper via `gradle wrapper` before running the command.

Open the project in Android Studio to install the `app` module on a device or emulator.
