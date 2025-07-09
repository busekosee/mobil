# Wi-Fi Provisioning Android App

This is a minimal Android app (Kotlin, Jetpack Compose) for provisioning Wi-Fi credentials to an IoT device over BLE.

## Features
- Scans and lists nearby Wi-Fi networks (SSIDs)
- Lets user select SSID and enter password
- Sends credentials to IoT device via BLE
- Waits for confirmation (success/fail)
- Minimal, user-friendly UI

## Prerequisites
- Android Studio (latest recommended)
- Android device or emulator with BLE support
- Android SDK 33+

## How to Build
1. Clone or download this repository.
2. Open the project in Android Studio.
3. Let Gradle sync and download dependencies.
4. Connect your Android device or start an emulator.
5. Click **Run** (▶️) to build and install the app.

## Permissions
The app requests the following permissions at runtime:
- Location (required for Wi-Fi scan)
- Bluetooth (for BLE communication)

## How to Test
1. Launch the app on your device.
2. Grant all requested permissions.
3. Select a Wi-Fi network from the list.
4. Enter the password and tap **Connect**.
5. The app will send credentials to the IoT device over BLE and show the result.

## Notes
- No backend server is used.
- The IoT device must be in BLE provisioning mode and advertising.
- BLE service/characteristic UUIDs are placeholders; update as needed for your device.

---
MIT License 