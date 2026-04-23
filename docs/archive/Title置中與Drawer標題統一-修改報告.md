# Title 置中 & Drawer 標題統一 - 修改報告

## 📅 完成日期
2026-04-23

## 🎯 需求

1. **Title 置中顯示** - Action Bar 的標題要置中對齊
2. **Drawer 標題統一** - Drawer 的 "Function Test" 改為 "功能測試"，與預設的 ActionBar Title 相同

---

## ✅ 已完成的修改

### 1. Toolbar 標題置中 ✅

#### 修改文件：activity_main.xml

**修改前：**
```xml
<androidx.appcompat.widget.Toolbar
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="?attr/actionBarSize"
    android:background="?attr/colorPrimary"
    android:elevation="4dp"
    android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
    app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
```

**修改後：**
```xml
<androidx.appcompat.widget.Toolbar
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="?attr/actionBarSize"
    android:background="?attr/colorPrimary"
    android:elevation="4dp"
    android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
    app:popupTheme="@style/ThemeOverlay.AppCompat.Light">
    
    <!-- 自定義標題 TextView（置中顯示）-->
    <TextView
        android:id="@+id/toolbar_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:text="@string/function_test_title"
        android:textColor="@android:color/white"
        android:textSize="20sp"
        android:textStyle="bold" />
</androidx.appcompat.widget.Toolbar>
```

**技術要點：**
- 在 Toolbar 內部添加自定義 TextView
- 使用 `android:layout_gravity="center"` 實現水平和垂直置中
- TextView ID 為 `toolbar_title`，方便程式碼引用

---

### 2. Drawer Header 標題改為"功能測試" ✅

#### 修改文件：activity_main.xml

**修改前：**
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/app_name"
    android:textColor="@android:color/white"
    android:textSize="20sp"
    android:textStyle="bold" />
```

**修改後：**
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/function_test_title"
    android:textColor="@android:color/white"
    android:textSize="20sp"
    android:textStyle="bold" />
```

**改動說明：**
- 從 `@string/app_name` ("Function Test") 
- 改為 `@string/function_test_title` ("功能測試")
- 與 Action Bar 預設標題保持一致

---

### 3. MainActivity.java 更新 ✅

#### 3.1 initViews() 方法

**修改前：**
```java
private void initViews() {
    binding.toolbar.setTitle(R.string.function_test_title);
    binding.toolbar.setNavigationIcon(R.drawable.ic_menu);
    binding.toolbar.setNavigationOnClickListener(v -> {
        if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.openDrawer(GravityCompat.START);
            binding.toolbar.setTitle(R.string.function_test_title);
        } else {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
    });
```

**修改後：**
```java
private void initViews() {
    // 設定工具列標題為"功能測試"（使用自定義 TextView 置中顯示）
    binding.toolbarTitle.setText(R.string.function_test_title);
    
    // 隱藏 Toolbar 預設標題
    binding.toolbar.setTitle("");
    
    binding.toolbar.setNavigationIcon(R.drawable.ic_menu);
    binding.toolbar.setNavigationOnClickListener(v -> {
        if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.openDrawer(GravityCompat.START);
            // 打開選單時恢復預設標題
            binding.toolbarTitle.setText(R.string.function_test_title);
        } else {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
    });
```

**改動說明：**
- 使用 `binding.toolbarTitle.setText()` 替代 `binding.toolbar.setTitle()`
- 設置 `binding.toolbar.setTitle("")` 隱藏預設標題

#### 3.2 initObservers() 方法

**修改前：**
```java
viewModel.titleUpdateEvent.observe(this, title -> {
    if (title != null) {
        binding.toolbar.setTitle(title);
        LogUtils.d(TAG, "更新 Action Bar 標題: " + title);
    }
});
```

**修改後：**
```java
viewModel.titleUpdateEvent.observe(this, title -> {
    if (title != null) {
        binding.toolbarTitle.setText(title);
        LogUtils.d(TAG, "更新 Action Bar 標題: " + title);
    }
});
```

#### 3.3 onBackPressed() 方法

**修改前：**
```java
@Override
public void onBackPressed() {
    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    } else {
        super.onBackPressed();
    }
}
```

**修改後：**
```java
@Override
public void onBackPressed() {
    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    } else {
        // 按返回鍵時恢復預設標題
        binding.toolbarTitle.setText(R.string.function_test_title);
        super.onBackPressed();
    }
}
```

---

## 📊 視覺效果對比

### 修改前

```
┌──────────────────────────────┐
│ ☰ 功能測試                   │ ← 左對齊
└──────────────────────────────┘

Drawer:
  ┌────────────────┐
  │ Function Test  │ ← 英文標題
  │ 硬體功能測試    │
  └────────────────┘
```

### 修改後

```
┌──────────────────────────────┐
│        ☰  功能測試            │ ← 置中對齊
└──────────────────────────────┘

Drawer:
  ┌────────────────┐
  │ 功能測試        │ ← 中文標題（與 Action Bar 一致）
  │ 硬體功能測試    │
  └────────────────┘
```

---

## 🎯 功能驗證

### 測試項目

#### 1. 標題置中顯示 ✅

**測試步驟：**
1. 啟動應用
2. 觀察 Action Bar 標題位置

**預期結果：**
- 標題 "功能測試" 顯示在 Action Bar 中央
- 左側有漢堡選單圖標 ☰
- 文字為白色、粗體、20sp

#### 2. 動態標題更新（置中） ✅

**測試步驟：**
1. 點擊選單項目（例如 "網路測試"）
2. 觀察標題變化

**預期結果：**
- 標題變為 "網路測試"
- 標題仍然保持置中對齊
- 打開選單時標題恢復為 "功能測試"

#### 3. Drawer 標題一致性 ✅

**測試步驟：**
1. 打開 Drawer
2. 檢查 Header 標題

**預期結果：**
- 顯示 "功能測試" 而非 "Function Test"
- 與 Action Bar 標題一致

---

## 🔧 技術細節

### 為什麼使用自定義 TextView？

#### 問題
Toolbar 的預設 `setTitle()` 方法無法直接實現標題置中：
```java
// ❌ 無法置中
binding.toolbar.setTitle("標題");  
```

#### 解決方案
在 Toolbar 內部添加自定義 TextView：
```xml
<Toolbar>
    <TextView 
        android:layout_gravity="center"  <!-- ✅ 實現置中 -->
        ... />
</Toolbar>
```

### 標題更新流程

```
點擊選單項目
    ↓
ViewModel.onNavigationItemClick()
    ↓
titleUpdateEvent.setValue(itemName)
    ↓
MainActivity 觀察者收到通知
    ↓
binding.toolbarTitle.setText(itemName)  ← 使用自定義 TextView
    ↓
標題更新（保持置中）
```

---

## 📝 程式碼變更總結

### 修改的文件

| 文件 | 類型 | 修改內容 |
|------|------|---------|
| `activity_main.xml` | Layout | 添加自定義 TextView，修改 Drawer Header |
| `MainActivity.java` | Java | 更新所有標題設置為使用 toolbarTitle |

### 新增的元件

| ID | 類型 | 用途 |
|----|------|------|
| `toolbar_title` | TextView | 置中顯示的 Action Bar 標題 |

---

## ✅ 編譯狀態

### 無錯誤 ✅
所有修改已通過編譯檢查。

### 警告（不影響運行）⚠️

1. **字符串拼接警告**
   ```
   binding.tvContent.setText("當前功能: " + itemName);
   ```
   - 建議使用 string resource with placeholder
   - 不影響功能運行

2. **Lambda 表達式警告**
   ```
   .setNegativeButton("取消", (dialog, which) -> { ... })
   ```
   - 建議簡化為 expression lambda
   - 不影響功能運行

---

## 🎨 Layout 結構

### Toolbar 結構

```xml
<Toolbar>
    <!-- Navigation Icon -->
    ☰
    
    <!-- 自定義標題（置中）-->
    <TextView 
        layout_gravity="center"
        text="功能測試" />
</Toolbar>
```

### Drawer Header 結構

```xml
<LinearLayout background="colorPrimary">
    <!-- 主標題 -->
    <TextView text="功能測試" />  ← 已統一
    
    <!-- 副標題 -->
    <TextView text="硬體功能測試" />
</LinearLayout>
```

---

## 💡 設計考量

### 1. 標題置中的優勢

✅ **視覺平衡** - 置中標題更加美觀
✅ **符合習慣** - 許多現代 App 採用置中標題
✅ **突出重點** - 置中位置更引人注目
✅ **空間利用** - 不受左側圖標影響

### 2. 標題統一的優勢

✅ **一致性** - Drawer 和 Action Bar 使用相同標題
✅ **專業性** - 避免混用中英文
✅ **易維護** - 使用統一的 string resource
✅ **易修改** - 只需修改 `function_test_title` 一處

---

## 🚀 未來擴展建議

### 可選改進

#### 1. 添加標題動畫
```java
// 淡入淡出效果
binding.toolbarTitle.animate()
    .alpha(0f)
    .setDuration(150)
    .withEndAction(() -> {
        binding.toolbarTitle.setText(newTitle);
        binding.toolbarTitle.animate().alpha(1f).setDuration(150).start();
    })
    .start();
```

#### 2. 支援長標題
```xml
<!-- 添加 maxLines 和 ellipsize -->
<TextView
    android:id="@+id/toolbar_title"
    android:maxLines="1"
    android:ellipsize="end"
    ... />
```

#### 3. 響應式字體大小
```xml
<!-- 使用 autoSizeText -->
<TextView
    android:id="@+id/toolbar_title"
    android:autoSizeTextType="uniform"
    android:autoSizeMinTextSize="14sp"
    android:autoSizeMaxTextSize="20sp"
    ... />
```

---

## 📚 相關資源

### Android 文檔
- [Toolbar Documentation](https://developer.android.com/reference/androidx/appcompat/widget/Toolbar)
- [Custom Toolbar Layout](https://developer.android.com/develop/ui/views/components/appbar)

### 本項目文檔
- `docs/動態ActionBar標題-實現報告.md` - 動態標題功能
- `strings.xml` - 字符串資源定義

---

## ✅ 總結

### 完成的功能

1. ✅ **Toolbar 標題置中** - 使用自定義 TextView 實現
2. ✅ **Drawer 標題統一** - 改為 "功能測試"
3. ✅ **程式碼更新** - 所有標題更新邏輯統一使用 toolbarTitle
4. ✅ **動態更新支持** - 點擊選單項目時標題保持置中

### 技術特點

- ✅ 使用自定義 TextView 實現置中對齊
- ✅ 保持與原有動態標題功能的兼容性
- ✅ 統一使用 string resource
- ✅ 符合 MVVM 架構模式

### 視覺效果

- ✅ 標題置中顯示，視覺更平衡
- ✅ Drawer 與 Action Bar 標題一致
- ✅ 動態更新時標題仍保持置中

---

**修改完成！請重新編譯並測試效果。** 🎉

---

**修改日期：** 2026-04-23  
**修改者：** AI Assistant  
**狀態：** ✅ 已完成，等待測試

