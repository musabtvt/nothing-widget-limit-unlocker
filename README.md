# 🚀 Nothing OS & CMF Phone Widget Limit Unlocker

<div align="center">

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Android 14 / 15 / 16](https://img.shields.io/badge/Android-14%20%7C%2015%20%7C%2016-green.svg?logo=android)](#)
[![No Root Required](https://img.shields.io/badge/Access-No%20Root%20Required-brightgreen)](#)
[![Release](https://img.shields.io/badge/Release-v1.0.0-orange.svg)](https://github.com/musabtvt/nothing-widget-limit-unlocker/releases/tag/v1.0.0)

**Expand the hardcoded 2-widget AI & Card limit to 7 (or any number) on Nothing OS & CMF Phone without root or breaking OTA updates.**

[Why We Built This](#-why-we-built-this-a-note-from-the-author) • [Quick Start](#-quick-start-in-1-minute) • [Download Releases](#-downloads) • [How It Works](#-reverse-engineering-deep-dive) • [Manual Steps](#-manual-execution-steps) • [Reset to Factory](#-how-to-reset-to-default)

</div>

---

## 💡 Why We Built This (A Note from the Author)

On Nothing OS (including CMF Phone 1 and CMF Phone 2 Pro), attempting to add more than two AI / Card widgets to your home screen displays a frustrating prompt:

> *"Widget limit reached. Please remove one to continue."*

We wanted to add multiple essential smart widgets—including health monitors, AI voice assistants, news feeds, and smart cards—without being arbitrarily throttled to just 2 cards.

After disassembling the launcher and system framework:
1. We located the constraint inside Nothing Launcher's bytecode where the fallback count is strictly pinned to `2` on non-flagship devices.
2. Because Nothing Launcher is an updated platform-signed system app, modifying the APK directly triggers `INSTALL_FAILED_UPDATE_INCOMPATIBLE` on non-rooted Android 16.
3. Instead of fighting signature enforcement or requiring root, we traced Nothing OS's internal `OnlineConfig` synchronization pipeline and developed a clean, native payload injection that unlocks the limit in real-time.

We are releasing the complete standalone tool, source code, and patched APK 100% free and open-source.

— **Mus'ab ([@musabtvt](https://github.com/musabtvt))**

---

## 📦 Downloads

| Package | Description | Direct Download |
|---|---|---|
| **1-Click ADB Unlock Pack** | Recommended. Precompiled DEX + 1-click scripts (`.sh` & `.bat`). Zero root, zero system modifications. | [⬇️ nothing-widget-unlock-pack.zip](https://github.com/musabtvt/nothing-widget-limit-unlocker/releases/download/v1.0.0/nothing-widget-unlock-pack.zip) |
| **Patched Nothing Launcher APK** | Pre-patched `v3.4.1` APK with smali limits hardcoded to 7 (for Magisk CorePatch / Rooted environments). | [⬇️ NothingLauncher_v3.4.1_unlocked_limit7.apk](https://github.com/musabtvt/nothing-widget-limit-unlocker/releases/download/v1.0.0/NothingLauncher_v3.4.1_unlocked_limit7.apk) |

---

## ⚡ Quick Start (In 1 Minute)

### Prerequisites
- Enable **Developer Options** on your phone (Settings > About Phone > Tap *Build Number* 7 times).
- Turn on **USB Debugging** (or **Wireless Debugging**).
- Connect your phone to your PC via USB or Wireless ADB.

### Option A: Linux, macOS & Termux
```bash
git clone https://github.com/musabtvt/nothing-widget-limit-unlocker.git
cd nothing-widget-limit-unlocker
./unlock.sh 7
```

### Option B: Windows
Double-click `unlock.bat` or run:
```cmd
unlock.bat 7
```

*(You can pass any number as an argument, e.g. `./unlock.sh 10` or `./unlock.sh 20`)*

---

## 🔬 Reverse Engineering Deep Dive

### 1. The Trigger
Inside `NTLauncher`, widget placement checks the current card count via `com.nothing.launcher.card.ai.c`:
- If `currentCardCount >= b8.c.c()`, it halts addition and triggers `showCardLimitedSnackBar`.
- `b8.c.c()` queries `SingleResultBean.getAiWidgetConfig().getMaxAiWidgetNumber()`.
- On devices without an active cloud config override, `b8.c` falls back to static field `f = 2`.

### 2. The Native OnlineConfig Pipeline
Nothing OS framework contains a background provider authority:
`content://com.nothing.proxy.onlineconfig.provider/launcher_single_config`

When this provider receives an updated `project_id`, Nothing Launcher's `ConfigObserver` catches the database event, decrypts the payload via `com.nothing.utils.Utils.decrypt()`, verifies `newVersion > currentVersion`, and updates the runtime cap dynamically.

### 3. Payload Injection
Our DEX utility (`EncryptAndInsert`):
1. Builds the exact `SingleResultBean` configuration:
   ```json
   [{"version": 2000000, "aiWidgetConfig": {"maxAiWidgetNumber": 7}}]
   ```
2. Packages the payload with Nothing's two-stage Base64 and framework encryption keys.
3. Injects the signed row into the provider.
4. The launcher accepts the new version `2000000` and instantly expands the AI widget limit without a reboot.

---

## 🔄 Persistence Across Reboots

- The configuration row is stored in Nothing Proxy's persistent SQLite database (`config_database`).
- The launcher caches the parsed bean in its device-protected `ConfigLoader.prefs`.
- It remains active across phone restarts and screen locks.

---

## 🛠 How to Reset to Default

If you ever wish to restore the factory default limit of 2:
```bash
adb shell "content delete --uri content://com.nothing.proxy.onlineconfig.provider/launcher_single_config"
adb shell am force-stop com.nothing.launcher
```

---

## 📄 License

Licensed under the [Apache 2.0 License](LICENSE).
