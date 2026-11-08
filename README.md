<div align="center">

<img src="./assets/banner.svg" alt="HeMan Vibes Banner" width="100%" />

# 🎵 HeMan Vibes

**Next-Gen Cross-Platform Music Player powered by Compose Multiplatform & YouTube Music**

*Crafted by [Heman Subedi](https://github.com/hemansubedi10)*

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.7.1-4285F4.svg?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Platform](https://img.shields.io/badge/Platforms-Android%20%7C%20Windows%20%7C%20macOS%20%7C%20iOS%20%7C%20Linux-00F5FF.svg)](#-platform-support)
[![License: MIT](https://img.shields.io/badge/License-MIT-FF007F.svg)](./LICENSE)
[![GitHub](https://img.shields.io/badge/Author-@hemansubedi10-181717.svg?logo=github&logoColor=white)](https://github.com/hemansubedi10)

<p align="center">
  <img src="./assets/icon.jpg" alt="HeMan Vibes Icon" width="120" style="border-radius: 28px;" />
</p>

</div>

---

## 🌟 Overview

**HeMan Vibes** is an ultra-fast, modern, open-source music streaming application designed to run seamlessly across all your devices — **Android, Windows, macOS, iOS, and Linux**.

Built using **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP)** with a single shared UI and business logic, HeMan Vibes extracts audio directly from **YouTube Music's InnerTube API** and pairs it with real-time synchronized lyrics via **LRCLIB** — offering an ad-free, high-fidelity listening experience.

---

## ✨ Key Features

- 🎧 **Unlimited Music Catalog:** Direct access to YouTube Music's global catalog with zero ads.
- ⚡ **Real-Time Synced Lyrics:** Dynamic karaoke-style timestamped lyrics auto-scrolling with millisecond precision powered by [LRCLIB](https://lrclib.net/).
- 📱 **True Multiplatform UI:** Responsive Material 3 design that switches dynamically between a Mobile layout (mini player, bottom bar) and a Desktop layout (navigation rail, persistent playback bar).
- 🔊 **High-Fidelity Audio:** Streams Opus (@ 160 kbps) and AAC (@ 128 kbps) direct from Google CDN.
- 🎨 **Sleek Cyber-Dark Theme:** Matte obsidian background with vibrant electric cyan (`#00F5FF`) and magenta-pink (`#FF007F`) neon accents.
- 💾 **Local Playlists & Library:** Manage favorites, history, and custom queues on-device.

---

## 📱 Platform Support & Installation

<details open>
<summary><b>🤖 Android</b></summary>

### Features
* Native ExoPlayer integration via **AndroidX Media3**.
* Background playback service and lock-screen notification media controls.
* Android Auto & Bluetooth audio metadata streaming.

### Build & Run
```bash
# Debug APK
./gradlew :androidApp:assembleDebug

# Install directly to connected device / emulator
./gradlew :androidApp:installDebug
```
The output APK will be located at `androidApp/build/outputs/apk/debug/androidApp-debug.apk`.
</details>

<details open>
<summary><b>🪟 Windows (PC)</b></summary>

### Features
* Desktop native window with fluid resizing, minimize-to-tray, and system media keys.
* Full keyboard shortcuts (`Space` for play/pause, `Left`/`Right` to seek).

### Build & Run
```bash
# Run desktop app directly in development
./gradlew :desktopApp:run

# Package Windows MSI & EXE installer
./gradlew :desktopApp:packageMsi
./gradlew :desktopApp:packageExe
```
The installer will be generated in `desktopApp/build/compose/binaries/main/msi/`.
</details>

<details open>
<summary><b>🍎 macOS</b></summary>

### Features
* Native Apple Silicon (M1/M2/M3/M4) & Intel DMG package.
* macOS Now Playing Control Center integration.

### Build & Run
```bash
# Run on macOS
./gradlew :desktopApp:run

# Generate DMG installer
./gradlew :desktopApp:packageDmg
```
</details>

<details open>
<summary><b>📱 iOS (iPhone & iPad)</b></summary>

### Features
* Compose Multiplatform UI rendered natively using Skiko.
* AVQueuePlayer background audio with `MPRemoteCommandCenter` and Dynamic Island support.

### Build & Run
Open the `iosApp` folder in Xcode on macOS:
```bash
open iosApp/iosApp.xcodeproj
```
Select your iOS Simulator or connected iPhone and click **Run**.
</details>

<details>
<summary><b>🐧 Linux</b></summary>

### Build & Run
```bash
./gradlew :desktopApp:run
./gradlew :desktopApp:packageDeb
```
</details>

---

## 🏗️ Architecture

HeMan Vibes follows **Clean Architecture** principles across all targets:

```
┌────────────────────────────────────────────────────────────────────────┐
│                   Shared Compose Multiplatform UI                      │
│   (HomeScreen, SearchScreen, LibraryScreen, NowPlayingScreen)          │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ StateFlow / Events
┌───────────────────────────────────▼────────────────────────────────────┐
│                    Shared Domain & ViewModels                          │
│               (AudioPlayer, SearchEngine, LyricsEngine)                │
└───────────────────┬─────────────────────────────────┬──────────────────┘
                    │                                 │
┌───────────────────▼─────────────┐     ┌─────────────▼──────────────────┐
│      Scraper & API Layer        │     │      Audio Player Engine       │
│  • YouTube InnerTube API        │     │     (expect/actual interface)  │
│  • LRCLIB Synced Lyrics Client  │     └─────────────┬──────────────────┘
└─────────────────────────────────┘                   │
                                        ┌─────────────┴─────────────┐
                                        ▼                           ▼
                                Android Media3              iOS AVPlayer
                                (ExoPlayer)                 Desktop JavaFX / VLC
```

### Module Structure

```
HeManVibes/
├── assets/                  # Brand logos, icons, banners
├── gradle/                  # Version catalogs & wrapper
├── composeApp/              # Shared 100% Kotlin Multiplatform UI & logic
│   ├── src/commonMain/      # Models, InnerTube client, LRCLIB client, UI screens
│   ├── src/androidMain/     # Android-specific audio player (Media3)
│   ├── src/desktopMain/     # Desktop audio player & JVM bindings
│   └── src/iosMain/         # iOS-specific player & ViewController
├── androidApp/              # Android app launcher & Manifest
├── desktopApp/              # Windows / macOS / Linux launcher & packaging
└── iosApp/                  # Xcode iOS project
```

---

## 🚀 Tech Stack

| Technology | Purpose |
| :--- | :--- |
| **Kotlin 2.1.0** | Core programming language |
| **Compose Multiplatform 1.7.1** | Declarative cross-platform UI |
| **Ktor 3.0.1** | Multiplatform asynchronous HTTP engine |
| **Kotlinx Serialization** | High-performance JSON parsing |
| **Coil 3** | Asynchronous image loading & disk caching |
| **AndroidX Media3** | Android audio playback engine & ExoPlayer |
| **LRCLIB API** | Free, open-source synced LRC lyrics provider |

---

## 🛠️ Prerequisites

To build and run HeMan Vibes locally:

1. **Java JDK 21+** (Microsoft OpenJDK 21 or Eclipse Temurin)
2. **Android Studio** (for Android development)
3. **Xcode 15+** (for macOS & iOS targets, requires macOS)
4. **Git**

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](./LICENSE) file for details.

```
Copyright (c) 2026 Heman Subedi. All rights reserved.
```
