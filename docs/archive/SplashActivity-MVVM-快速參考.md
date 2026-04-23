# SplashActivity MVVM 重構 - 快速參考

## 修改摘要

### ✅ 已完成的工作

1. **SplashActivity** - 重構為 MVVM 架構
   - 繼承 `BaseActivity<ActivitySplashBinding, SplashViewModel>`
   - 實現 `bindViewModelId()` 和 `bindContentView()` 方法
   - 加入完整的權限檢查流程（5 項權限）
   - 使用 SingleLiveEvent 處理事件
   - 實現最短顯示時間機制（1.5 秒）

2. **MainActivity** - 移除權限檢查
   - 移除所有權限相關的常數、方法、imports
   - 移除 `checkAndRequestPermissions()` 調用
   - 清理 `initObservers()` 中的權限觀察者
   - 保留所有 Drawer 導航和測試功能

3. **MainViewModel** - 簡化職責
   - 移除 `permissionResultEvent` 和 `onPermissionResult()` 方法
   - 保留 drawer 和導航相關功能

4. **SplashViewModel** - 無需修改
   - 已經有 `permissionCheckCompleteEvent` 和 `navigateToMainEvent`
   - 已經有 `onPermissionResult()` 和 `startNavigation()` 方法

## 權限檢查流程

```
應用啟動
    ↓
SplashActivity (啟動畫面)
    ↓
檢查權限並請求
    ↓
[權限授予]
    ↓
延迟 1.5 秒（確保顯示時間）
    ↓
跳轉到 MainActivity
```

## 需要檢查的權限

1. `CAMERA` - 相機
2. `READ_EXTERNAL_STORAGE` - 讀取儲存
3. `WRITE_EXTERNAL_STORAGE` - 寫入儲存
4. `ACCESS_FINE_LOCATION` - 精確位置
5. `ACCESS_COARSE_LOCATION` - 粗略位置

## 建置步驟

1. 在 Android Studio 中開啟專案
2. **File → Sync Project with Gradle Files**
3. **Build → Rebuild Project**
4. 檢查是否有錯誤
5. 運行到設備或模擬器

## 測試檢查清單

- [ ] SplashActivity 正常顯示
- [ ] 首次安裝時自動請求權限
- [ ] 權限授予後跳轉到 MainActivity
- [ ] 權限拒絕後仍可進入（顯示提示）
- [ ] 再次啟動時快速通過 Splash（1.5 秒）
- [ ] MainActivity 功能正常（Drawer、測試按鈕）
- [ ] 無編譯錯誤
- [ ] DataBinding 正確生成

## 相關檔案

### 修改的檔案：
- `app/src/main/java/com/cyberpower/functiontest/SplashActivity.java` ✅
- `app/src/main/java/com/cyberpower/functiontest/MainActivity.java` ✅
- `app/src/main/java/com/cyberpower/functiontest/MainViewModel.java` ✅

### 未修改的檔案：
- `app/src/main/java/com/cyberpower/functiontest/SplashViewModel.java` ✅
- `app/src/main/res/layout/activity_splash.xml` ✅
- `app/src/main/AndroidManifest.xml` ✅

### 文檔：
- `docs/SplashActivity-MVVM-權限檢查.md` - 完整文檔 ✅
- `docs/SplashActivity-MVVM-快速參考.md` - 本檔案 ✅

## 潛在問題排查

### 問題：DataBinding 錯誤
**解決方案：** 
```
File → Invalidate Caches / Restart
Build → Clean Project
Build → Rebuild Project
```

### 問題：找不到 ActivitySplashBinding
**解決方案：** 確保 `activity_splash.xml` 使用 `<layout>` 標籤包裹

### 問題：ViewModel 注入失敗
**解決方案：** 檢查 SplashActivity 是否正確實現 `bindViewModelId()` 方法

### 問題：權限請求沒有彈出
**解決方案：** 
1. 檢查 AndroidManifest.xml 是否聲明權限
2. 檢查設備 Android 版本（6.0+）
3. 查看 Logcat 日誌

## 架構優勢

### 單一職責原則
- SplashActivity：啟動畫面 + 權限檢查
- MainActivity：主畫面 + 功能導航
- 職責清晰，易於維護

### MVVM 模式
- View：專注 UI 顯示
- ViewModel：處理業務邏輯
- DataBinding：自動綁定
- LiveData：生命週期感知

### 用戶體驗
- 無冷啟動白屏
- 權限在啟動時統一檢查
- 最短顯示時間確保品牌展示
- 平滑的轉場動畫

## 後續改進建議

1. **權限教育頁面**：首次請求前顯示說明
2. **設定引導**：永久拒絕時提供"前往設定"按鈕
3. **漸進式權限**：按需請求（而非一次全部）
4. **權限狀態追蹤**：使用 SharedPreferences 記錄
5. **Android 13+ 通知權限**：如需要推播通知

## 日誌範例

### 正常流程：
```
SplashActivity onCreate - 啟動畫面開始
SplashViewModel 初始化
請求權限: 5 項
權限結果: 全部授予
延遲 500 ms 後跳轉
準備跳轉到 MainActivity
跳轉到 MainActivity
MainActivity onCreate
```

### 權限拒絕：
```
SplashActivity onCreate - 啟動畫面開始
SplashViewModel 初始化
請求權限: 5 項
被拒絕的權限: [android.permission.CAMERA]
權限結果: 部分拒絕
延遲 800 ms 後跳轉
準備跳轉到 MainActivity
跳轉到 MainActivity
MainActivity onCreate
```

---

重構完成日期：2026-04-23  
架構模式：BaseActivity + ViewModel + DataBinding + SingleLiveEvent  
支援版本：Android 6.0+ (API 23+)

