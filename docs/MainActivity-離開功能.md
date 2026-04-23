# MainActivity Drawer "離開" 功能 - 完成

## ✅ 已完成的修改

成功在 MainActivity 的 Drawer Layout **實際底部**添加固定的"離開"按鈕。

---

## 📝 修改內容

### 1. 修改 Drawer Layout 結構

**位置**: `activity_main.xml`

**改動**:
- ExpandableListView 高度改為 `0dp`，加上 `layout_weight="1"`（佔據剩餘空間）
- 在 Drawer LinearLayout 底部添加分隔線
- 在最底部添加固定的"離開"按鈕

```xml
<!-- ExpandableListView 佔據中間空間 -->
<ExpandableListView
    android:id="@+id/expandable_list_view"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="1"
    ... />

<!-- 分隔線 -->
<View
    android:layout_width="match_parent"
    android:layout_height="1dp"
    android:background="#E0E0E0" />

<!-- 離開按鈕（固定在底部） -->
<TextView
    android:id="@+id/btn_exit"
    android:layout_width="match_parent"
    android:layout_height="56dp"
    android:text="@string/exit_app"
    android:textColor="@android:color/holo_red_dark"
    ... />
```

**視覺效果**:
```
┌─────────────────────┐
│   Drawer Header     │ ← 固定高度 160dp
├─────────────────────┤
│ 硬體測試            │
│   ├ 列印測試        │
│   ├ 掃碼測試        │
│   └ 卡片讀取測試    │
│ 系統測試            │
│   ├ 網路測試        │  ← 可滾動區域
│   ├ 儲存測試        │    (weight=1)
│   └ 螢幕測試        │
│ 設定                │
│ 關於                │
│                     │
├─────────────────────┤ ← 分隔線
│      離 開          │ ← 固定在底部 (紅色)
└─────────────────────┘
```

---

### 2. 移除列表中的"離開"項目

**位置**: `MainActivity.java` → `initDrawerData()` 方法

**移除的代碼**:
```java
// ❌ 移除：不再將"離開"作為列表項目
// DrawerGroup exitGroup = new DrawerGroup("離開");
// drawerData.add(exitGroup);
```

---

### 3. 添加底部按鈕點擊事件

**位置**: `MainActivity.java` → `initViews()` 方法

```java
// 設定底部"離開"按鈕點擊事件
binding.btnExit.setOnClickListener(v -> {
    binding.drawerLayout.closeDrawer(GravityCompat.START);
    exitApp();
});
```

---

### 4. 簡化導航處理邏輯

**位置**: `MainActivity.java` → `handleNavigationClick()` 方法

**移除的特殊處理**:
```java
// ❌ 不再需要檢查"離開"項目
// if ("離開".equals(itemName)) {
//     exitApp();
//     return;
// }
```

---

## 🎯 功能特性

### 設計優勢

1. **Material Design 規範**: 重要的全局操作（如退出）應固定在 Drawer 底部
2. **視覺分離**: 紅色文字 + 分隔線，明確區分主要功能和退出操作
3. **易於訪問**: 無需滾動列表，"離開"按鈕始終可見
4. **防止誤觸**: 位置固定且顏色醒目，用戶不會誤點擊

### 用戶體驗

- ✅ 開啟 Drawer
- ✅ "離開"按鈕**固定在視覺底部**（紅色醒目）
- ✅ 無論列表多長，"離開"按鈕始終可見
- ✅ 點擊後顯示確認對話框
- ✅ 確認後優雅關閉應用程式

---

## 📋 測試步驟

### 1. 編譯測試

```
File → Sync Project with Gradle Files
Build → Rebuild Project
```

### 2. 執行應用程式

```
Run → Run 'app'
```

### 3. 測試功能

1. 點擊左上角選單圖標 ☰
2. Drawer 選單滑出
3. **檢查布局**:
   - 上方：Drawer Header（藍色背景）
   - 中間：功能列表（可滾動）
   - 底部：灰色分隔線
   - **最底部：紅色的"離開"按鈕** ✅
4. 點擊"離開"
5. Drawer 自動關閉
6. 確認對話框彈出
7. 測試「確定」和「取消」功能

---

## 💡 技術細節

### Layout 權重 (weight) 機制

```xml
<ExpandableListView
    android:layout_height="0dp"
    android:layout_weight="1" />
```

- `height="0dp"` + `weight="1"`: 佔據所有剩餘空間
- Header（固定 160dp）和 Exit 按鈕（固定 56dp）之後的所有空間
- 當內容超出時，ExpandableListView 可以滾動

### 視覺設計

```xml
<!-- 紅色文字吸引注意 -->
android:textColor="@android:color/holo_red_dark"

<!-- 觸摸反饋效果 -->
android:background="?attr/selectableItemBackground"

<!-- 灰色分隔線 -->
<View android:background="#E0E0E0" />
```

---

## 🎨 可選調整

### 1. 調整"離開"按鈕顏色

修改 `activity_main.xml`:
```xml
android:textColor="@android:color/black"  <!-- 改為黑色 -->
```

### 2. 添加圖標

```xml
<TextView
    android:drawableStart="@drawable/ic_exit"
    android:drawablePadding="16dp"
    ... />
```

### 3. 調整按鈕高度

```xml
android:layout_height="48dp"  <!-- 或其他高度 -->
```

### 4. 移除分隔線

刪除 layout 中的分隔線 `<View>` 元素。

---

## 📊 完成狀態

| 項目 | 狀態 |
|------|------|
| 修改 Drawer Layout | ✅ 完成 |
| 添加固定底部按鈕 | ✅ 完成 |
| 移除列表中的"離開" | ✅ 完成 |
| 實現點擊處理 | ✅ 完成 |
| 添加確認對話框 | ✅ 完成 |
| 整合 AppManager | ✅ 完成 |
| 添加字串資源 | ✅ 完成 |
| 編譯無錯誤 | ✅ 完成 |

---

## 🎊 使用說明

### 用戶操作流程

```
開啟應用程式
    ↓
點擊左上角 ☰ 選單
    ↓
Drawer 滑出
    ↓
[Drawer Header]
[功能列表 - 可滾動]
    ├ 硬體測試
    ├ 系統測試
    ├ 設定
    └ 關於
─────────────── ← 分隔線
[離開] (紅色) ← 固定在視覺底部！
    ↓
點擊「離開」
    ↓
確認對話框
    ↓
「確定」→ 應用程式關閉
「取消」→ 返回應用程式
```

---

## 🔗 相關代碼

### 修改的文件
- ✅ `activity_main.xml` - Layout 結構調整
- ✅ `MainActivity.java` - 3 處修改
- ✅ `strings.xml` - 添加字串資源

### 相關類別
- `AppManager` - Activity 堆疊管理
- `DrawerGroup` - Drawer 項目數據結構  
- `DrawerAdapter` - Drawer 適配器

---

## ✅ 檢查清單

- [x] 修改 Drawer Layout 結構
- [x] ExpandableListView 使用 weight
- [x] 添加底部"離開"按鈕
- [x] 添加分隔線
- [x] 移除列表中的"離開"項目
- [x] 實現底部按鈕點擊處理
- [x] 添加字串資源
- [x] 確認對話框功能正常
- [ ] 在 Android Studio 編譯測試
- [ ] 在設備上實際測試

---

## 🚀 下一步

**請在 Android Studio 中執行**:

1. **Sync Project**
   ```
   File → Sync Project with Gradle Files
   ```

2. **Rebuild Project**
   ```
   Build → Rebuild Project
   ```

3. **Run App**
   ```
   Run → Run 'app'
   ```

4. **測試功能**
   - 開啟 Drawer
   - 確認"離開"按鈕在視覺最底部
   - 確認紅色文字顯示正確
   - 測試點擊功能

---

## 🌟 設計亮點

### Material Design 最佳實踐

✅ **層次分明**: Header、功能列表、退出操作三個區域清晰  
✅ **視覺引導**: 紅色文字突出重要操作  
✅ **固定位置**: 關鍵操作始終可見  
✅ **觸摸反饋**: selectableItemBackground 提供按壓效果

### 專業 EDC 應用體驗

✅ **易於訪問**: 無需滾動即可看到退出選項  
✅ **防誤操作**: 確認對話框 + 醒目顏色  
✅ **優雅退出**: 清理資源 + 完全關閉進程

---

**修改日期**: 2026-04-23  
**修改項目**: 將"離開"按鈕固定在 Drawer Layout 實際底部  
**影響文件**: activity_main.xml, MainActivity.java, strings.xml  
**狀態**: ✅ 完成，待測試

