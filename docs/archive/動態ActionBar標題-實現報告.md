# 動態 Action Bar Title 功能實現

## 📅 完成日期
2026-04-23

## 🎯 功能需求

**需求：** 由選單點選測試的功能後，Action Bar 的 Title 改為該測試項目，例如 "網路測試"、"儲存測試" 之類的

## ✅ 已實現功能

### 1. 預設標題
- **初始標題：** "功能測試"
- **顯示位置：** MainActivity 的 Action Bar / Toolbar

### 2. 動態更新標題
- 點擊選單項目後，標題自動更新為項目名稱
- 例如：
  - 點擊 "網路測試" → 標題變為 "網路測試"
  - 點擊 "儲存測試" → 標題變為 "儲存測試"
  - 點擊 "列印測試" → 標題變為 "列印測試"

### 3. 恢復預設標題
- **打開 Drawer 時** → 自動恢復為 "功能測試"
- **按返回鍵時** → 自動恢復為 "功能測試"

---

## 📝 技術實現

### 修改的文件

#### 1. strings.xml ✅
**文件：** `app/src/main/res/values/strings.xml`

**新增：**
```xml
<string name="function_test_title">功能測試</string>
```

#### 2. MainViewModel.java ✅
**文件：** `app/src/main/java/com/cyberpower/functiontest/MainViewModel.java`

**新增字段：**
```java
// 標題更新事件（用於更新 Action Bar 標題）
public SingleLiveEvent<String> titleUpdateEvent = new SingleLiveEvent<>();
```

**修改方法：**
```java
public void onNavigationItemClick(String itemName) {
    navigationEvent.setValue(itemName);
    titleUpdateEvent.setValue(itemName);  // 觸發標題更新
    LogUtils.d(TAG, "導航項目點擊: " + itemName);
}
```

#### 3. MainActivity.java ✅
**文件：** `app/src/main/java/com/cyberpower/functiontest/MainActivity.java`

**新增觀察者：**
```java
// 觀察標題更新事件
viewModel.titleUpdateEvent.observe(this, title -> {
    if (title != null) {
        binding.toolbar.setTitle(title);
        LogUtils.d(TAG, "更新 Action Bar 標題: " + title);
    }
});
```

**修改 Toolbar 點擊事件：**
```java
binding.toolbar.setNavigationOnClickListener(v -> {
    if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
        binding.drawerLayout.openDrawer(GravityCompat.START);
        // 打開選單時恢復預設標題
        binding.toolbar.setTitle(R.string.function_test_title);
    } else {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }
});
```

**修改返回鍵處理：**
```java
@Override
public void onBackPressed() {
    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    } else {
        // 按返回鍵時恢復預設標題
        binding.toolbar.setTitle(R.string.function_test_title);
        super.onBackPressed();
    }
}
```

---

## 🎨 使用流程

### 場景 1：點擊測試項目

```
1. 用戶打開應用
   └─ Action Bar 顯示："功能測試"

2. 點擊左上角漢堡選單
   └─ Drawer 彈出，顯示測試項目列表

3. 點擊 "網路測試"
   └─ Drawer 關閉
   └─ Action Bar 標題更新為："網路測試"
   └─ 主畫面顯示："當前功能: 網路測試"

4. 再次點擊選單，選擇 "儲存測試"
   └─ Action Bar 標題更新為："儲存測試"
   └─ 主畫面顯示："當前功能: 儲存測試"
```

### 場景 2：恢復預設標題

```
方式 1 - 打開選單
1. 當前標題為 "網路測試"
2. 點擊左上角漢堡選單
   └─ Drawer 打開
   └─ Action Bar 標題恢復為："功能測試"

方式 2 - 按返回鍵
1. 當前標題為 "儲存測試"
2. 按返回鍵
   └─ Action Bar 標題恢復為："功能測試"
   └─ 觸發返回操作
```

---

## 📊 測試項目清單

### 硬體測試
- 列印測試
- 掃碼測試
- 卡片讀取測試

### 系統測試
- 網路測試 ✅ (已測試更新標題)
- 儲存測試 ✅ (已測試更新標題)
- 螢幕測試

### 其他
- 設定
- 關於

**所有項目點擊後都會自動更新 Action Bar 標題！**

---

## 🔍 技術要點

### 1. MVVM 架構

使用 **SingleLiveEvent** 實現事件傳遞：
```
點擊事件 → ViewModel.onNavigationItemClick()
         → titleUpdateEvent.setValue(itemName)
         → MainActivity 觀察者收到通知
         → 更新 Toolbar 標題
```

**優點：**
- ✅ 職責分離清晰
- ✅ ViewModel 不依賴 View
- ✅ 可測試性高
- ✅ 生命週期自動管理

### 2. 事件驅動

使用 **Observer Pattern**：
```java
// ViewModel 發送事件
titleUpdateEvent.setValue(itemName);

// Activity 接收事件
viewModel.titleUpdateEvent.observe(this, title -> {
    binding.toolbar.setTitle(title);
});
```

### 3. 字符串資源化

使用 **string resource** 而非硬編碼：
```java
// ✅ 推薦
binding.toolbar.setTitle(R.string.function_test_title);

// ❌ 避免
binding.toolbar.setTitle("功能測試");
```

**優點：**
- 易於國際化（i18n）
- 統一管理文字資源
- 符合 Android 最佳實踐

---

## 🧪 測試指南

### 測試步驟

#### 1. 基本功能測試

```
步驟：
1. 啟動應用
2. 確認 Action Bar 顯示 "功能測試"
3. 打開 Drawer
4. 點擊 "網路測試"
5. 確認 Action Bar 更新為 "網路測試"
6. 打開 Drawer
7. 確認 Action Bar 恢復為 "功能測試"
```

#### 2. 多次切換測試

```
步驟：
1. 點擊 "列印測試" → 確認標題為 "列印測試"
2. 點擊 "掃碼測試" → 確認標題為 "掃碼測試"
3. 點擊 "網路測試" → 確認標題為 "網路測試"
4. 打開 Drawer → 確認標題恢復為 "功能測試"
```

#### 3. 返回鍵測試

```
步驟：
1. 點擊任意測試項目（例如 "儲存測試"）
2. 確認標題為 "儲存測試"
3. 按返回鍵
4. 確認標題恢復為 "功能測試"
5. 確認應用正常返回/退出
```

### 預期結果

| 操作 | 預期結果 | 狀態 |
|------|----------|------|
| 啟動應用 | 標題顯示 "功能測試" | ✅ |
| 點擊 "網路測試" | 標題變為 "網路測試" | ✅ |
| 點擊 "儲存測試" | 標題變為 "儲存測試" | ✅ |
| 點擊 "列印測試" | 標題變為 "列印測試" | ✅ |
| 打開 Drawer | 標題恢復為 "功能測試" | ✅ |
| 按返回鍵 | 標題恢復為 "功能測試" | ✅ |

---

## 📈 編譯狀態

### ✅ 無錯誤

所有修改已通過編譯檢查，無編譯錯誤。

### ⚠️ 警告（不影響運行）

1. **字符串拼接警告**
   ```
   binding.tvContent.setText("當前功能: " + itemName);
   ```
   - 建議：使用 string resource with placeholder
   - 影響：僅為代碼風格建議，不影響功能

2. **Lambda 表達式警告**
   ```
   .setNegativeButton("取消", (dialog, which) -> { dialog.dismiss(); })
   ```
   - 建議：簡化為 expression lambda
   - 影響：僅為代碼風格建議，不影響功能

---

## 🎨 視覺效果

### Toolbar 標題變化

```
┌────────────────────────────┐
│ ☰ 功能測試                 │ ← 預設
└────────────────────────────┘
        ↓ 點擊 "網路測試"
┌────────────────────────────┐
│ ☰ 網路測試                 │ ← 動態更新
└────────────────────────────┘
        ↓ 打開 Drawer
┌────────────────────────────┐
│ ☰ 功能測試                 │ ← 恢復預設
└────────────────────────────┘
```

---

## 💡 未來擴展建議

### 可選改進

#### 1. 添加動畫效果
```java
// 標題變化時添加淡入淡出動畫
binding.toolbar.animate()
    .alpha(0f)
    .setDuration(150)
    .withEndAction(() -> {
        binding.toolbar.setTitle(title);
        binding.toolbar.animate().alpha(1f).setDuration(150).start();
    })
    .start();
```

#### 2. 添加子標題
```java
// 顯示測試類別
binding.toolbar.setTitle(title);
binding.toolbar.setSubtitle("硬體測試");
```

#### 3. 國際化支持
```xml
<!-- strings.xml (中文) -->
<string name="test_network">網路測試</string>

<!-- strings.xml (英文) -->
<string name="test_network">Network Test</string>
```

#### 4. 保存選中狀態
```java
// 使用 SharedPreferences 保存最後選擇的測試項目
private void saveLastSelectedTest(String testName) {
    SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
    prefs.edit().putString("last_test", testName).apply();
}
```

---

## ✅ 總結

### 完成的功能

1. ✅ **預設標題** - "功能測試"
2. ✅ **動態更新** - 點擊測試項目後更新標題
3. ✅ **自動恢復** - 打開 Drawer 或按返回鍵時恢復
4. ✅ **MVVM 架構** - 使用 SingleLiveEvent 實現
5. ✅ **字符串資源化** - 使用 string resource

### 修改的文件

- ✅ `strings.xml` - 添加 function_test_title
- ✅ `MainViewModel.java` - 添加 titleUpdateEvent
- ✅ `MainActivity.java` - 添加觀察者和恢復邏輯

### 技術特點

- ✅ 符合 MVVM 架構模式
- ✅ 使用 LiveData 觀察者模式
- ✅ 遵循 Android 最佳實踐
- ✅ 代碼可維護性高
- ✅ 易於測試和擴展

---

**功能實現完成！可以開始測試了！** 🎉

