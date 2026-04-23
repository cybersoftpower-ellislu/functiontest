# AGENTS.md

## Project Overview
Android hardware function test application for EDC (Electronic Data Capture) devices. Multi-module Gradle project with `app` (test harness) and `core` (shared library module via Git submodule).

**Language:** Traditional Chinese (繁體中文) - Comments, logs, and documentation are primarily in Chinese.

## Architecture

### Module Structure
- **app/** - Minimal test application (`com.cyberpower.functiontest`)
- **core/** - Shared library (`com.cyberpower.edc.core`) linked as Git submodule
  - Houses all base classes, utilities, and hardware abstractions
  - Independent repository requiring separate commits (see README workflow)

### Hardware Abstraction Layer
Multi-vendor device support via strategy pattern in `core/src/main/java/com/cyberpower/edc/core/device/hardware/`:

```java
HardwareManager.getInstance() // Singleton initialized in Core.init()
  -> IHelper implementations:
     - PaxHelper (PAX, lephone manufacturers)
     - CastlesHelper (Castles manufacturer)  
     - DummyHelper (fallback for unknown devices)
```

Device detection via `Build.MANUFACTURER` in `HardwareManager.InitDevice()`. Hardware interfaces (cSys, cPrinter, cReader, cIcc, etc.) abstract vendor-specific SDKs.

### MVVM Pattern
All Activities/Fragments extend typed base classes with DataBinding + ViewModel:

```java
BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel>
BaseFragment<V extends ViewDataBinding, VM extends BaseViewModel>
```

ViewModels use `SingleLiveEvent` for one-time events (toasts, navigation). RxJava disposables auto-managed via `CompositeDisposable` in `BaseViewModel.onCleared()`.

## Critical Dependencies

### Native Libraries in `core/libs/`
- **CTOS.\*.jar** - Castles Terminal OS SDKs (crypto, KMS, loader, print, reader, system)
- **GL\*.jar** - PAX Global libraries (GLComm, GLPacker, GLImgProcessing, GLPage)
- **NeptuneLiteApi** / **CyberSoftApi** - Vendor-specific payment APIs
- Loaded via `flatDir { dirs "${rootDir}/core/libs" }` in settings.gradle

### Key External Dependencies (core/build.gradle)
- RxJava 3.x with RxBinding/RxLifecycle for async operations
- Retrofit + OkHttp for networking
- XLog (elvishew) for file-based logging to `/sdcard/xlog/{package}/`
- ZXing for QR code scanning
- Guava, Gson, BouncyCastle for utilities

## Build & Development

### Initial Setup
```bash
# Clone with submodule
git clone --recurse-submodules https://github.com/cybersoftpower-ellislu/functiontest.git

# If core/ is empty
git submodule update --init
```

### Updating Submodule
```bash
# Update core to latest commit
git submodule update --remote

# Then sync Gradle in Android Studio
File → Sync Project with Gradle Files
```

### Dual-Commit Workflow for core/ Changes
1. Commit & push changes in `core` repository
2. Commit & push submodule pointer update in `functiontest` repository

(GitHub Desktop users: switch repositories in top-left dropdown)

### Build Configuration
**Note**: App module requires minSdk 24 due to BaseActivity's `@RequiresApi` annotation.

- **compileSdk:** 34, **minSdk:** 24 (app) / 22 (core), **targetSdk:** 34
- **JDK:** 8 (Java 1.8 source/target compatibility)
- **NDK:** armeabi ABI only (32-bit ARM for POS terminals)
- **Gradle:** 8.1.4 (Android Gradle Plugin)

## Project-Specific Conventions

### Logging
Use XLog instead of Android Log:
```java
XLog.d("message");  // Auto-configured in Core.init()
LogUtils.e(TAG, "error");  // Wrapper in core/basis/LogUtils
```
Logs auto-rotate at 10MB, cleaned when folder exceeds 500MB.

### Quick Click Protection
Singleton pattern prevents double-taps:
```java
quickClickProtection.isDoubleClick() // In BaseActivity
```

### Lifecycle Management
- `AppManager` tracks activity stack
- `ProgressObserver` auto-binds to Activity lifecycle for progress dialogs
- All Fragments use `RxFragment` for lifecycle-aware Rx subscriptions

### UI Conventions
- System UI hidden by default (`hideSystemUI()` in BaseActivity.onResume())
- Power key disabled during operations (`enablePowerKey(false)`)
- Chinese resource strings expected in `res/values/strings.xml`

## Key Files
- `core/Core.java` - Singleton initialization (XLog, HardwareManager, PAX libraries)
- `core/base/BaseActivity.java` - Activity template with DataBinding + ViewModel wiring
- `core/device/hardware/HardwareManager.java` - Device detection & HAL initialization
- `settings.gradle` - Submodule inclusion and flatDir library configuration
- `README.md` - Detailed Git workflow for submodule management

## Common Pitfalls
- **Don't forget dual-commit** when modifying core/
- **Native libs are armeabi only** - No x86/arm64-v8a support
- **Device-specific code** must go through HardwareManager abstraction
- **Chinese comments/strings** are intentional, not localization bugs
- **buildConfig must be enabled** in core module (required by legacy code)
