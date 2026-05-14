# AGENTS.md

## Project Overview
Android hardware function test application for EDC (Electronic Data Capture) devices. Multi-module Gradle project with `app` (test harness) and `core` (shared library module via Git submodule).

**Language:** Traditional Chinese (繁體中文) - Comments, logs, and documentation are primarily in Chinese.

## Architecture

### Module Structure
- **app/** - Hardware function test application (`com.cyberpower.functiontest`)
  - SplashActivity (entry point with permission handling)
  - MainActivity (Drawer navigation with device-based dynamic menu)
  - ApiTestFragment (8 hardware test scenarios)
  - Package: `com.cyberpower.functiontest`
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

### App Module Features
**SplashActivity** - Application entry point:
- Runtime permission requests (camera, storage, location)
- Minimum display time (1.5s) enforced via Handler
- Transition animation to MainActivity
- MVVM with SplashViewModel

**MainActivity** - Device-based drawer navigation:
- Detects device type via `HardwareManager.getHelper()` (PaxHelper/CastlesHelper)
- Enables/disables menu groups based on device manufacturer
- Dynamic ActionBar title via `toolbarTitle` TextView (centered)
- Exit confirmation dialog using `AppManager.AppExit()`

**ApiTestFragment** - Hardware testing interface:
- Spinner-based test selection (8 scenarios: system info, hardware manager, print, scan, card reader, network, crypto, stress)
- Scrollable log output via `SingleLiveEvent<String>` in ViewModel
- Auto-scroll to bottom on new messages
- Fragment transaction via `getSupportFragmentManager().beginTransaction().replace().addToBackStack()`

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
- **compileSdk:** 34, **minSdk:** 22 (both modules), **targetSdk:** 34
- **JDK:** 8 (Java 1.8 source/target compatibility)
- **NDK:** armeabi ABI only (32-bit ARM for POS terminals)
- **Gradle:** 8.1.4 (Android Gradle Plugin)

## Project-Specific Conventions

### Application Initialization
Entry point is `FunctionTestApplication.onCreate()`:
```java
BaseApplication.setApplication(this);  // Stetho debug tools
Core.init(this);                       // XLog, HardwareManager, PAX libs
AppManager.getAppManager();            // Activity stack manager
```
**Critical**: LogUtils/XLog unavailable until after `Core.init()` completes.

### Runtime Permissions
All runtime permissions requested in `SplashActivity.checkAndRequestPermissions()`:
- Camera, External Storage (Read/Write), Fine/Coarse Location
- Graceful degradation if denied (shows dialog, continues to MainActivity)
- Uses `ActivityCompat.requestPermissions()` with callback to `SplashViewModel.onPermissionResult()`

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

### Fragment Navigation
Fragments replace content via MainActivity's `fragment_container`:
```java
getSupportFragmentManager()
    .beginTransaction()
    .replace(R.id.fragment_container, new ApiTestFragment())
    .addToBackStack("ApiTest")
    .commit();
```
Fragments extend `BaseFragment<ViewDataBinding, ViewModel>` with same DataBinding pattern as Activities.

### DataBinding
All Activities/Fragments use DataBinding:
- Generated binding classes: `Activity{Name}Binding`, `Fragment{Name}Binding`
- ViewModel bound via `BR.viewModel` (auto-generated by DataBinding)
- Layout files must use `<layout>` root tag with `<data><variable name="viewModel" type="..." /></data>`
- Access views via `binding.viewId` instead of `findViewById()`

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
- `docs/BUILD_GRADLE_ISSUES.md` - Compilation and Gradle troubleshooting
- `docs/DEVELOPMENT_GUIDE.md` - Development standards and feature implementation guide
- `docs/TESTING_ISSUES.md` - Runtime issue solutions

### App Module Files
- `app/src/main/java/com/cyberpower/functiontest/`:
  - `FunctionTestApplication.java` - Application entry, calls `Core.init()` and `BaseApplication.setApplication()`
  - `SplashActivity.java` / `SplashViewModel.java` - Entry point with permissions
  - `MainActivity.java` / `MainViewModel.java` - Main screen with Drawer
  - `ApiTestFragment.java` / `ApiTestViewModel.java` - Hardware test interface
  - `DrawerAdapter.java` - ExpandableListView adapter for two-level navigation
- `app/src/main/res/layout/`:
  - `activity_splash.xml` - Splash screen layout
  - `activity_main.xml` - DrawerLayout + Toolbar + Fragment container
  - `fragment_api_test.xml` - Test selection Spinner + ScrollView log output
  - `drawer_group_item.xml` / `drawer_child_item.xml` - Drawer layouts

## Common Pitfalls
- **Don't forget dual-commit** when modifying core/
- **Native libs are armeabi only** - No x86/arm64-v8a support
- **Device-specific code** must go through HardwareManager abstraction
- **Chinese comments/strings** are intentional, not localization bugs
- **buildConfig must be enabled** in core module (required by legacy code)
- **LogUtils unavailable before Core.init()** - Don't log in Application.onCreate() before Core initialization
- **Fragment container required** - MainActivity needs `FrameLayout` with `id="@+id/fragment_container"` for Fragment transactions
- **DataBinding layout errors** - If binding class not generated, check layout has `<layout>` root and `<variable>` declaration
- **SingleLiveEvent fires once** - Don't observe the same event in multiple places; value consumed after first observer
