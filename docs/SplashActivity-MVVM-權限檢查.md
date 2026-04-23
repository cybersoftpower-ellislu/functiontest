# SplashActivity MVVM 架構及權限檢查重構 - 完成報告

## 概述
將 SplashActivity 從簡單的 AppCompatActivity 重構為遵循專案規範的 BaseActivity MVVM 架構，並將應用程式權限檢查從 MainActivity 遷移至 SplashActivity。

## 修改日期
2026-04-23

## 修改內容

### 1. SplashActivity 重構 ✅
**檔案**: `app/src/main/java/com/cyberpower/functiontest/SplashActivity.java`

#### 主要變更：
- ✅ 繼承 `BaseActivity<ActivitySplashBinding, SplashViewModel>` 替代 AppCompatActivity
- ✅ 實現 MVVM 架構所需的方法：
  - `bindViewModelId()` - 返回 BR.viewModel
  - `bindContentView()` - 返回 R.layout.activity_splash
- ✅ 加入完整的權限檢查邏輯
- ✅ 使用 SingleLiveEvent 處理權限檢查完成事件
- ✅ 使用 SingleLiveEvent 處理導航事件
- ✅ 實現最短顯示時間機制（1.5 秒）

#### 權限檢查流程：
```
SplashActivity.onCreate()
    ↓
checkAndRequestPermissions() - 檢查 5 項權限
    ↓
onRequestPermissionsResult() - 接收權限結果
    ↓
viewModel.onPermissionResult() - 通知 ViewModel
    ↓
permissionCheckCompleteEvent - 觸發完成事件
    ↓
navigateToMainWithDelay() - 確保最短顯示時間
    ↓
navigateToMain() - 跳轉到 MainActivity
```

#### 需要的權限清單：
1. `Manifest.permission.CAMERA` - 相機權限
2. `Manifest.permission.READ_EXTERNAL_STORAGE` - 讀取儲存
3. `Manifest.permission.WRITE_EXTERNAL_STORAGE` - 寫入儲存
4. `Manifest.permission.ACCESS_FINE_LOCATION` - 精確位置
5. `Manifest.permission.ACCESS_COARSE_LOCATION` - 粗略位置

### 2. SplashViewModel ✅
**檔案**: `app/src/main/java/com/cyberpower/functiontest/SplashViewModel.java`

已存在，包含：
- ✅ `permissionCheckCompleteEvent` - 權限檢查完成事件
- ✅ `navigateToMainEvent` - 跳轉到主畫面事件
- ✅ `onPermissionResult(boolean)` - 處理權限結果
- ✅ `startNavigation()` - 觸發導航

### 3. MainActivity 清理 ✅
**檔案**: `app/src/main/java/com/cyberpower/functiontest/MainActivity.java`

#### 移除內容：
- ✅ 移除 `REQUEST_CODE_PERMISSIONS` 常數
- ✅ 移除 `REQUIRED_PERMISSIONS` 陣列
- ✅ 移除 `checkAndRequestPermissions()` 方法
- ✅ 移除 `onRequestPermissionsResult()` 方法
- ✅ 移除 `showPermissionDeniedDialog()` 方法
- ✅ 移除 `onCreate()` 中的 `checkAndRequestPermissions()` 調用
- ✅ 移除權限相關的 import 語句
- ✅ 移除權限觀察者 `permissionResultEvent.observe()`

#### 保留內容：
- ✅ 所有 Drawer 導航功能
- ✅ 測試功能按鈕
- ✅ 離開應用程式功能
- ✅ Toast 和導航事件觀察者

#### 新增註釋：
```java
/**
 * MainActivity
 * 主畫面，包含兩層 Drawer 導航
 * 注意：權限檢查已在 SplashActivity 中完成
 */
```

### 4. MainViewModel 清理 ✅
**檔案**: `app/src/main/java/com/cyberpower/functiontest/MainViewModel.java`

#### 移除內容：
- ✅ 移除 `permissionResultEvent` SingleLiveEvent
- ✅ 移除 `onPermissionResult()` 方法

#### 保留內容：
- ✅ `drawerEvent` - Drawer 開關事件
- ✅ `navigationEvent` - 導航事件
- ✅ `toastEvent` - Toast 事件（繼承自 BaseViewModel）
- ✅ `finishEvent` - 結束事件（繼承自 BaseViewModel）
- ✅ 所有測試相關方法

### 5. Layout 檔案 ✅
**檔案**: `app/src/main/res/layout/activity_splash.xml`

已經是 DataBinding 格式，包含：
- ✅ `<layout>` 根標籤
- ✅ `<data>` 區塊定義了 `viewModel` 變數（SplashViewModel）
- ✅ App 名稱、副標題、進度條、版本資訊

### 6. AndroidManifest.xml ✅
**檔案**: `app/src/main/AndroidManifest.xml`

已正確配置：
- ✅ SplashActivity 設為 LAUNCHER（啟動 Activity）
- ✅ MainActivity 設為 exported="false"
- ✅ 所有需要的權限已在 manifest 中聲明

## 架構優勢

### MVVM 架構遵循：
1. **View (Activity)** - 只負責 UI 顯示和用戶交互
2. **ViewModel** - 處理業務邏輯和資料
3. **DataBinding** - 自動綁定 View 和 ViewModel
4. **SingleLiveEvent** - 處理一次性事件（Toast、導航等）

### 權限檢查優化：
1. **集中管理** - 所有權限在 SplashActivity 統一檢查
2. **用戶體驗** - 在啟動畫面時完成權限請求，不干擾主畫面
3. **容錯處理** - 即使權限未完全授予，仍允許進入應用（部分功能可能受限）
4. **版本兼容** - 自動處理 Android 6.0+ 的動態權限

### 生命週期管理：
1. **Activity 堆疊** - 使用 `AppManager.getAppManager().addActivity(this)`
2. **RxJava 清理** - BaseViewModel 自動清理 CompositeDisposable
3. **記憶體洩漏防護** - LiveData 自動管理生命週期

## 測試指南

### 手動測試步驟：
1. **首次安裝測試**：
   - 安裝 APK
   - 啟動應用
   - 應看到 SplashActivity
   - 應自動彈出權限請求對話框
   - 授予權限後應自動跳轉到 MainActivity

2. **權限拒絕測試**：
   - 在權限請求時選擇"拒絕"
   - 應顯示說明對話框
   - 點擊"確定"後仍可進入 MainActivity
   - Toast 應顯示"部分權限未授予"

3. **再次啟動測試**：
   - 關閉應用
   - 再次啟動
   - 因權限已授予，應快速通過 SplashActivity
   - 延遲 1.5 秒後跳轉到 MainActivity

4. **返回鍵測試**：
   - 在 SplashActivity 按返回鍵
   - 應無反應（已禁用）
   - 在 MainActivity 按返回鍵
   - Drawer 若開啟應先關閉，否則顯示確認對話框

### 日誌檢查：
使用 XLog 或 Logcat 觀察以下日誌：
```
SplashActivity onCreate - 啟動畫面開始
SplashViewModel 初始化
請求權限: 5 項 (或) 所有權限已授予
權限結果: 全部授予 (或) 部分拒絕
延遲 xxx ms 後跳轉
準備跳轉到 MainActivity
跳轉到 MainActivity
MainActivity onCreate
```

## 相容性

- ✅ Android 6.0 (API 23) 以下：自動跳過動態權限檢查
- ✅ Android 6.0+ (API 23+)：執行動態權限請求
- ✅ 支援傳統中文 UI 和日誌
- ✅ 遵循專案既有的 MVVM 模式

## 後續建議

### 可選改進：
1. **權限教育畫面**：在首次請求權限前顯示說明頁面
2. **設定頁面引導**：權限被拒絕時提供"前往設定"按鈕
3. **權限狀態持久化**：使用 SharedPreferences 記錄權限狀態
4. **漸進式權限**：根據功能使用時才請求對應權限（而非一次全部請求）

### 潛在問題：
1. 如果用戶永久拒絕權限（Don't ask again），應提供引導到系統設定的功能
2. Android 10+ 的儲存權限變更（Scoped Storage）可能需要調整
3. Android 13+ 的通知權限需要額外處理（如需要）

## 檔案清單

### 修改的檔案：
1. `app/src/main/java/com/cyberpower/functiontest/SplashActivity.java` ✅
2. `app/src/main/java/com/cyberpower/functiontest/MainActivity.java` ✅
3. `app/src/main/java/com/cyberpower/functiontest/MainViewModel.java` ✅

### 已存在的檔案（未修改）：
1. `app/src/main/java/com/cyberpower/functiontest/SplashViewModel.java` ✅
2. `app/src/main/res/layout/activity_splash.xml` ✅
3. `app/src/main/AndroidManifest.xml` ✅

### 文檔：
1. `docs/SplashActivity-MVVM-權限檢查.md` ✅ (本檔案)

## 建置指南

### 使用 Android Studio：
1. 開啟專案
2. File → Sync Project with Gradle Files
3. Build → Rebuild Project
4. 確認沒有編譯錯誤
5. 運行到設備或模擬器

### 檢查點：
- [ ] 專案同步成功
- [ ] 無編譯錯誤
- [ ] DataBinding 正確生成（ActivitySplashBinding）
- [ ] ViewModel 正確注入
- [ ] 權限流程正常運作

## 結論

✅ **重構完成**

SplashActivity 已成功重構為 MVVM 架構，並整合了完整的權限檢查流程。所有修改遵循專案既有的架構模式，程式碼風格與其他 Activity/ViewModel 保持一致。

MainActivity 已清理所有權限相關程式碼，責任單一化，只負責主畫面的導航和功能測試。

整體架構更加清晰，維護性提升，符合 SOLID 原則中的單一職責原則（SRP）。

