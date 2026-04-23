# SplashActivity 啟動畫面 - 完整說明

## 🎯 功能說明

**Splash Screen（啟動畫面）** 或稱為 **SplashActivity（閃屏頁面）** 是 Android 應用程式中常見的設計模式。

### 解決的問題

您遇到的「啟動到顯示 MainActivity 之間畫面空白」是 Android 應用冷啟動（Cold Start）時的正常現象：

1. **冷啟動過程**:
   ```
   點擊應用圖標
       ↓
   系統創建進程
       ↓
   Application.onCreate() - 初始化
       ↓
   MainActivity.onCreate()
       ↓
   畫面顯示
   ```

2. **空白畫面出現原因**:
   - Application 初始化（Core.init()）需要時間
   - MainActivity 佈局渲染需要時間
   - 資源加載需要時間
   - 這段時間系統會顯示默認的空白啟動窗口

3. **解決方案 - SplashActivity**:
   - 創建一個輕量級的啟動 Activity
   - 設置自定義主題和背景
   - 顯示品牌 Logo 和載入提示
   - 在背景完成初始化後跳轉到 MainActivity

---

## ✅ 已完成的實作

### 1. 創建 SplashActivity.java

**位置**: `app/src/main/java/com/cyberpower/functiontest/SplashActivity.java`

**功能**:
- 顯示啟動畫面 2 秒
- 自動跳轉到 MainActivity
- 禁用返回鍵（避免用戶退出）
- 添加淡入淡出轉場動畫

```java
public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DISPLAY_TIME = 2000; // 2秒
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // 延遲後跳轉到主畫面
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, SPLASH_DISPLAY_TIME);
    }
}
```

---

### 2. 創建 activity_splash.xml

**位置**: `app/src/main/res/layout/activity_splash.xml`

**內容**:
- 藍色背景（使用 colorPrimary）
- 應用程式名稱（大字體、白色、粗體）
- 副標題（硬體功能測試系統）
- 載入進度條
- 版本資訊

**視覺效果**:
```
┌──────────────────────────┐
│                          │
│                          │
│    Function Test         │ ← 應用名稱（大）
│  硬體功能測試系統         │ ← 副標題
│                          │
│        ⊙                 │ ← 載入圈
│                          │
│                          │
│                          │
│      Version 1.0         │ ← 版本（底部）
└──────────────────────────┘
```

---

### 3. 創建 Theme.Splash 主題

**位置**: `app/src/main/res/values/themes.xml`

**特性**:
- 全螢幕顯示（無標題欄、無狀態欄）
- 背景色設為 colorPrimary（藍色）
- 快速顯示（避免透明度或動畫延遲）

```xml
<style name="Theme.Splash" parent="Theme.AppCompat.Light.NoActionBar">
    <item name="android:windowBackground">@color/colorPrimary</item>
    <item name="android:windowFullscreen">true</item>
    <item name="android:windowNoTitle">true</item>
</style>
```

---

### 4. 更新 AndroidManifest.xml

**改動**:
- ✅ SplashActivity 設為 LAUNCHER（應用入口）
- ✅ MainActivity 改為 exported="false"（內部 Activity）
- ✅ SplashActivity 使用 Theme.Splash 主題

```xml
<!-- Splash Screen - 啟動畫面 -->
<activity
    android:name=".SplashActivity"
    android:exported="true"
    android:theme="@style/Theme.Splash">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<!-- Main Activity -->
<activity
    android:name=".MainActivity"
    android:exported="false" />
```

---

### 5. 添加字串資源

**位置**: `app/src/main/res/values/strings.xml`

```xml
<string name="splash_subtitle">硬體功能測試系統</string>
<string name="splash_version">Version 1.0</string>
```

---

## 📱 用戶體驗流程

### 啟動流程（有 SplashActivity）

```
點擊應用圖標
    ↓
立即顯示 Splash Screen
（藍色背景 + Logo）
    ↓
Application 初始化
Core.init() 執行
（用戶看到的是 Splash，不是空白）
    ↓
等待 2 秒
    ↓
淡入淡出動畫
    ↓
顯示 MainActivity
（Drawer + 功能列表）
```

### 對比：無 SplashActivity

```
點擊應用圖標
    ↓
❌ 空白畫面 1-3 秒
（用戶不知道發生什麼）
    ↓
突然顯示 MainActivity
```

---

## 🎨 自訂選項

### 1. 調整顯示時間

修改 `SplashActivity.java`:
```java
private static final long SPLASH_DISPLAY_TIME = 1500; // 改為 1.5 秒
```

### 2. 添加 Logo 圖片

修改 `activity_splash.xml`:
```xml
<ImageView
    android:id="@+id/iv_logo"
    android:layout_width="120dp"
    android:layout_height="120dp"
    android:src="@drawable/app_logo"
    android:contentDescription="@string/app_name"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />
```

### 3. 改變背景顏色

修改 `themes.xml`:
```xml
<item name="android:windowBackground">@android:color/white</item>
```

或創建漸層背景：
```xml
<!-- res/drawable/splash_background.xml -->
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <gradient
        android:startColor="#2196F3"
        android:endColor="#1976D2"
        android:angle="90" />
</shape>
```

### 4. 添加動畫效果

修改 `activity_splash.xml`，為 TextView 添加動畫：
```java
// 在 SplashActivity.onCreate() 中
TextView tvAppName = findViewById(R.id.tv_app_name);
Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
tvAppName.startAnimation(fadeIn);
```

### 5. 執行實際初始化工作

如果需要在 Splash 期間執行初始化：

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    
    // 在背景線程執行初始化
    new Thread(() -> {
        // 執行耗時操作
        performInitialization();
        
        // 回到主線程跳轉
        runOnUiThread(() -> {
            startMainActivity();
        });
    }).start();
}

private void performInitialization() {
    // 預加載資料
    // 檢查更新
    // 其他初始化工作
    try {
        Thread.sleep(2000); // 模擬耗時操作
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}

private void startMainActivity() {
    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
}
```

---

## 🔧 進階：Android 12+ Splash Screen API

Android 12（API 31）引入了新的 Splash Screen API。如果需要支援，可以：

### 1. 添加依賴

```gradle
dependencies {
    implementation 'androidx.core:core-splashscreen:1.0.1'
}
```

### 2. 修改 themes.xml

```xml
<style name="Theme.Splash" parent="Theme.SplashScreen">
    <item name="windowSplashScreenBackground">@color/colorPrimary</item>
    <item name="windowSplashScreenAnimatedIcon">@drawable/app_logo</item>
    <item name="postSplashScreenTheme">@style/Theme.Functiontest</item>
</style>
```

### 3. 在 SplashActivity 中安裝

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    // 必須在 super.onCreate() 之前
    SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
    
    super.onCreate(savedInstanceState);
    // ...
}
```

**但是**，對於 EDC 設備（Android 7.0+），目前的簡單方案已經足夠！

---

## 📊 完成狀態

| 項目 | 狀態 |
|------|------|
| 創建 SplashActivity.java | ✅ 完成 |
| 創建 activity_splash.xml | ✅ 完成 |
| 添加 Theme.Splash | ✅ 完成 |
| 更新 AndroidManifest.xml | ✅ 完成 |
| 添加字串資源 | ✅ 完成 |
| 設定 LAUNCHER | ✅ 完成 |

---

## 🚀 測試步驟

### 1. Gradle Sync

```
File → Sync Project with Gradle Files
```

### 2. Rebuild Project

```
Build → Rebuild Project
```

### 3. 執行測試

```
Run → Run 'app'
```

### 4. 預期行為

1. 點擊應用圖標
2. **立即顯示藍色 Splash 畫面**（不再空白）
3. 顯示 "Function Test" + 載入圈
4. 等待 2 秒
5. 淡入淡出動畫
6. 顯示 MainActivity（Drawer 選單）

---

## 💡 技術細節

### 為什麼需要 SplashActivity？

1. **用戶體驗**:
   - 避免啟動時的空白畫面
   - 提供視覺反饋（應用正在啟動）
   - 符合品牌設計規範

2. **技術原因**:
   - Application.onCreate() 需要時間（Core.init()）
   - MainActivity 佈局複雜（Drawer + 權限）
   - 首次啟動時初始化更耗時

3. **最佳實踐**:
   - Splash 畫面應快速顯示（< 100ms）
   - 顯示時間不宜過長（1-3 秒）
   - 避免在 Splash 中執行過多邏輯

### Handler vs Thread

**當前使用 Handler（推薦）**:
```java
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    // 跳轉邏輯
}, 2000);
```

**優點**:
- 簡單明瞭
- 在主線程執行
- 適合純延遲場景

**Thread + 初始化（可選）**:
```java
new Thread(() -> {
    // 背景初始化
    runOnUiThread(() -> {
        // 跳轉
    });
}).start();
```

**適用場景**:
- 需要執行耗時操作
- 預加載資料
- 檢查更新

---

## ⚠️ 注意事項

### 1. 不要過度使用

Splash 畫面應該：
- ✅ 快速顯示品牌
- ✅ 避免空白畫面
- ❌ 不是強制觀看廣告
- ❌ 不是炫耀動畫特效

### 2. 時間控制

建議顯示時間：
- **理想**: 1-2 秒
- **可接受**: 2-3 秒
- **過長**: > 3 秒（用戶會不耐煩）

### 3. 禁用返回鍵

```java
@Override
public void onBackPressed() {
    // 空實作 - 在 Splash 期間不允許返回
}
```

這避免用戶在啟動過程中按返回鍵退出應用。

### 4. Activity 生命週期

SplashActivity 會在跳轉後立即 finish()，不會保留在任務堆疊中。

---

## 🔗 相關文件

### 新增文件
- ✅ `SplashActivity.java` - 啟動 Activity
- ✅ `activity_splash.xml` - Splash 佈局
- ✅ `themes.xml` - 添加 Splash 主題

### 修改文件
- ✅ `AndroidManifest.xml` - 設定 LAUNCHER
- ✅ `strings.xml` - 添加字串資源

---

## 📖 參考資料

### Google 官方指南
- [Launch screens](https://developer.android.com/guide/topics/ui/splash-screen)
- [Splash Screen API](https://developer.android.com/develop/ui/views/launch/splash-screen)
- [App startup time](https://developer.android.com/topic/performance/vitals/launch-time)

### Material Design
- [Launch Screen Patterns](https://material.io/design/communication/launch-screen.html)

---

## ✅ 檢查清單

- [x] 創建 SplashActivity.java
- [x] 創建 activity_splash.xml
- [x] 添加 Theme.Splash
- [x] 更新 AndroidManifest.xml
- [x] 設定 LAUNCHER intent-filter
- [x] 添加字串資源
- [x] 禁用返回鍵
- [x] 添加轉場動畫
- [ ] 在 Android Studio Sync & Build
- [ ] 在設備上測試
- [ ] 確認無空白畫面

---

**修改日期**: 2026-04-23  
**功能**: 添加 SplashActivity 啟動畫面  
**影響文件**: 5 個（新增 2 個，修改 3 個）  
**狀態**: ✅ 完成，待測試

---

**下一步**: 請在 Android Studio 執行 Sync → Rebuild → Run，確認啟動時不再出現空白畫面！

