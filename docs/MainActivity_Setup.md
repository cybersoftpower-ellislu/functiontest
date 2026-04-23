# MainActivity 設置完成指南

## 已完成的工作

### 1. 文件結構
已創建以下文件：
- `FunctionTestApplication.java` - Application 類別，初始化 Core 和服務
- `MainActivity.java` - 主 Activity，繼承 BaseActivity
- `MainViewModel.java` - ViewModel，處理業務邏輯
- `DrawerAdapter.java` - 兩層 Drawer 導航適配器
- `activity_main.xml` - 主畫面 layout (DataBinding + DrawerLayout)
- `drawer_group_item.xml` - Drawer 群組項目 layout
- `drawer_child_item.xml` - Drawer 子項目 layout
- `ic_menu.xml` - 選單圖標

### 2. 配置更新
- ✅ 啟用 DataBinding (`app/build.gradle`)
- ✅ **更新 minSdk 為 24** (`app/build.gradle`) - 因為 Core 的 BaseActivity 要求 API 24
- ✅ 添加權限聲明 (`AndroidManifest.xml`)
- ✅ 註冊 Application 類別 (`AndroidManifest.xml`)
- ✅ 更新主題為 NoActionBar (`themes.xml`)
- ✅ 添加中文字串資源 (`strings.xml`)

**重要**: minSdk 從 22 更新為 24 是必要的，因為 Core 模組的 `BaseActivity` 使用 `@RequiresApi(api = Build.VERSION_CODES.N)`（API 24）。這意味著應用程式需要 Android 7.0 或更高版本。

### 3. Application 初始化
`FunctionTestApplication` 在應用啟動時自動初始化：
- **BaseApplication** - Stetho debug 工具（用於網路監控）
- **Core** - XLog 日誌系統、HardwareManager、PAX libraries
- **AppManager** - Activity 堆疊管理

日誌文件位置：`/sdcard/xlog/com.cyberpower.functiontest/`

### 4. 功能特性

#### MVVM 架構
- MainActivity 繼承 `BaseActivity<ActivityMainBinding, MainViewModel>`
- 使用 DataBinding 進行畫面綁定
- ViewModel 處理所有業務邏輯

#### 兩層 Drawer 導航
```
硬體測試
├── 列印測試
├── 掃碼測試
└── 卡片讀取測試
系統測試
├── 網路測試
├── 儲存測試
└── 螢幕測試
設定
關於
```

#### 權限處理
自動請求以下權限：
- 相機權限 (CAMERA)
- 儲存權限 (READ/WRITE_EXTERNAL_STORAGE)
- 位置權限 (ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
- 網路權限 (INTERNET, ACCESS_NETWORK_STATE)

## 下一步操作

### 在 Android Studio 中完成設置

1. **開啟專案**
   ```
   File → Open → 選擇 C:\DevAndroid\functiontest
   ```

2. **執行 Gradle Sync**
   ```
   File → Sync Project with Gradle Files
   ```
   
   這會：
   - 生成 DataBinding 類別 (ActivityMainBinding)
   - 生成 BR 類別 (用於 bindViewModelId)
   - 生成 R 類別資源 ID

3. **確認 Core 模組**
   - 確保 core submodule 已正確載入
   - 如果 core 資料夾是空的，執行：
     ```bash
     git submodule update --init
     ```

4. **建置專案**
   ```
   Build → Make Project (Ctrl+F9)
   ```

5. **執行應用程式**
   ```
   Run → Run 'app' (Shift+F10)
   ```

### 預期行為

啟動後，應用程式會：
1. 自動檢查並請求必要權限
2. 顯示主畫面，包含：
   - Toolbar (左上角選單圖標)
   - 歡迎訊息
   - 測試按鈕
3. 點擊選單圖標或從左邊滑動可開啟 Drawer
4. Drawer 顯示兩層導航選單
5. 點擊任何項目會觸發對應的測試功能

## 架構說明

### 生命週期管理
- `BaseActivity` 自動管理 ViewModel 生命週期
- `ProgressObserver` 綁定至 Activity 生命週期
- RxJava disposables 自動清理

### 事件處理
使用 `SingleLiveEvent` 避免重複觸發：
- `toastEvent` - 顯示 Toast 訊息
- `drawerEvent` - 控制 Drawer 開關
- `navigationEvent` - 處理導航項目點擊
- `permissionResultEvent` - 權限請求結果
- `finishEvent` - 結束 Activity

### 快速點擊保護
- 使用 `QuickClickProtection` 單例模式
- 所有點擊事件透過 `onClickProtected()` 處理

## 疑難排解

### DataBinding 編譯錯誤
如果看到 "Cannot resolve symbol 'databinding'" 錯誤：
1. Clean Project: `Build → Clean Project`
2. Rebuild Project: `Build → Rebuild Project`
3. Invalidate Caches: `File → Invalidate Caches / Restart`

### Core 模組錯誤
如果 BaseActivity 或其他 core 類別找不到：
1. 確認 `core/` 子模組已初始化
2. 在 `settings.gradle` 中確認包含 `:core`
3. Gradle Sync

### 權限問題
- Android 6.0+ 需要動態請求權限
- 確保 AndroidManifest.xml 包含所有權限聲明
- 測試時可能需要手動在設定中授予權限

## 擴展功能

### 添加新的測試項目
在 `MainActivity.initDrawerData()` 中添加：
```java
DrawerGroup newGroup = new DrawerGroup("新測試類別");
newGroup.addChild("測試項目1");
newGroup.addChild("測試項目2");
drawerData.add(newGroup);
```

### 實作測試邏輯
在 `MainViewModel.performTest()` 中實作：
```java
public void performTest(String testName) {
    switch (testName) {
        case "列印測試":
            // 呼叫 HardwareManager 進行列印測試
            break;
        case "掃碼測試":
            // 啟動掃碼功能
            break;
        // ... 其他測試
    }
}
```

### 導航到其他 Activity
透過 ViewModel 觸發導航：
```java
// 在 MainViewModel
public SingleLiveEvent<Class<?>> navigateEvent = new SingleLiveEvent<>();

public void navigateToTest(String testName) {
    navigateEvent.setValue(TestActivity.class);
}

// 在 MainActivity
viewModel.navigateEvent.observe(this, activityClass -> {
    Intent intent = new Intent(this, activityClass);
    startActivity(intent);
});
```

## 參考資料
- AGENTS.md - 專案架構說明
- core/base/BaseActivity.java - 基礎 Activity 實作
- core/base/BaseViewModel.java - 基礎 ViewModel 實作




