# API Level 衝突問題 - AAction & ActionBind

## ⚠️ 問題發現

當您將 minSdk 改為 22 時，會遇到以下類別的 API 版本衝突：

### 受影響的類別（都要求 API 24）

| 類別 | 註解 | 說明 |
|------|------|------|
| `AAction.java` | `@RequiresApi(api = Build.VERSION_CODES.N)` | Action 基礎類別 |
| `ActionBind.java` | `@RequiresApi(api = Build.VERSION_CODES.N)` | Action 綁定類別 |
| `ActionContext.java` | `@RequiresApi(api = Build.VERSION_CODES.N)` | Action 上下文管理 |
| `ActionMonitor.java` | `@RequiresApi(api = Build.VERSION_CODES.N)` | Action 監控 |
| `ActionResultDispatcher.java` | `@RequiresApi(api = Build.VERSION_CODES.N)` | 結果分發器 |
| `BaseTask.java` | `@RequiresApi(api = Build.VERSION_CODES.N)` | Task 基礎類別 |
| `BaseFragment.java` | `@RequiresApi(api = Build.VERSION_CODES.N)` | Fragment 基礎類別 |
| `BaseNoUiFragment.java` | `@RequiresApi(api = Build.VERSION_CODES.N)` | 無 UI Fragment |
| `BaseDialogFragment.java` | `@RequiresApi(api = Build.VERSION_CODES.N)` | Dialog Fragment |
| `BaseActivity.java` | `@RequiresApi(api = Build.VERSION_CODES.N)` | Activity 基礎類別 |

---

## 🔍 根本原因分析

### 為什麼這些類別標註 API 24？

檢查代碼後發現這些類別使用了：

1. **`@FunctionalInterface` 註解**
   ```java
   @FunctionalInterface
   public interface ActionBindEndListener {
       void onEnd(ActionResult result);
   }
   ```

2. **Lambda 表達式**
   ```java
   task.setEndListener(result -> onSubTaskEnd(state, result));
   ```

3. **`ConcurrentHashMap`**
   ```java
   private final Map<String, ActionBind> taskMap = new ConcurrentHashMap<>();
   ```

### 實際上需要 API 24 嗎？

**答案：不需要！** ❌

- ✅ `@FunctionalInterface` 是 **Java 8 語法**，不是 Android API
- ✅ **Lambda 表達式** 是 **Java 8 語法**，透過 D8/R8 desugar 可以在任何 Android 版本使用
- ✅ `ConcurrentHashMap` 從 **API 1** 就支援了

**結論**: 這些 `@RequiresApi(api = Build.VERSION_CODES.N)` 註解是**過度保守**的，實際上只要編譯設定正確，可以在 API 22 上運行。

---

## 🎯 解決方案

### 方案 1: 保持 minSdk 24（推薦 - 最簡單）

**優點**:
- ✅ 不需要修改 Core 模組代碼
- ✅ 不會有編譯警告
- ✅ 完全兼容現有代碼

**缺點**:
- ❌ 無法支援 Android 5.x/6.x 設備（API 22-23）
- ❌ 限制了應用程式的市場覆蓋率

**適用情況**:
- EDC 設備通常運行 Android 7.0+ 
- 不需要支援舊版 Android 設備

### 方案 2: 移除 @RequiresApi 註解（需修改 Core）

**步驟**:
1. 移除所有 Action 相關類別的 `@RequiresApi` 註解
2. 確保 Java 8 desugar 已啟用
3. 測試在 API 22 設備上運行

**修改範圍**:
- `AAction.java`
- `ActionBind.java`
- `ActionContext.java`
- `ActionMonitor.java`
- `ActionResultDispatcher.java`
- `BaseTask.java`
- `BaseFragment.java`
- `BaseNoUiFragment.java`
- `BaseDialogFragment.java`
- `BaseActivity.java`

**優點**:
- ✅ 可以使用 minSdk 22
- ✅ 擴大設備支援範圍

**缺點**:
- ❌ 需要修改 Core 子模組（10+ 個文件）
- ❌ 需要提交到 Core 倉庫
- ❌ 需要團隊其他成員同步更新

### 方案 3: 不使用 AAction/ActionBind（如果可行）

**適用情況**:
- 如果您的 MainActivity **不需要**使用 Action 相關功能
- 只使用基本的 BaseActivity 功能

**檢查方式**:
```java
// 如果您的代碼中沒有使用以下內容，可能不受影響：
// - AAction
// - ActionBind
// - BaseTask
// - ActionContext
```

**BUT**: 您的 MainActivity 已經繼承了 `BaseActivity`，而 `BaseActivity` 本身就有 `@RequiresApi(api = Build.VERSION_CODES.N)`，所以這個方案不可行。

---

## ⚙️ Java 8 Desugar 確認

### 當前配置檢查

您的 build.gradle 已經設定：
```groovy
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```

✅ 這個配置會自動啟用 **D8/R8 desugaring**，將 Java 8 語法轉換為 API 19+ 兼容的字節碼。

### Desugar 支援的特性

| 特性 | 需要 API | Desugar 後 |
|------|---------|-----------|
| Lambda 表達式 | Java 8 | API 19+ ✅ |
| @FunctionalInterface | Java 8 | API 19+ ✅ |
| Stream API | API 24 | API 21+ ✅ (需額外設定) |
| java.time | API 26 | API 21+ ✅ (需額外設定) |

**結論**: Lambda 和 @FunctionalInterface 在 API 22 上**完全沒問題**！

---

## 📋 我的建議

### 推薦方案：保持 minSdk 24

**理由**:

1. **EDC 設備現狀**
   - 大部分 EDC 設備運行 Android 7.0+ (API 24)
   - PAX、Castles 等廠商的新機器都是 Android 7.0+
   - API 22-23 (Android 5.x/6.x) 設備已經很少見

2. **維護成本低**
   - 不需要修改 Core 模組
   - 不需要團隊協調
   - 沒有額外的測試負擔

3. **未來兼容性**
   - Google 逐步提升 minSdk 要求
   - 新的 Android 特性需要更高 API
   - API 24 是一個合理的基準線

### 如果必須使用 minSdk 22

如果您的硬體設備確實運行 Android 5.x/6.x，那麼需要：

1. **修改 Core 模組**（需要在 Core 倉庫提 PR）
   - 移除所有 `@RequiresApi(api = Build.VERSION_CODES.N)` 註解
   - 替換為 `@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)` (API 22)
   - 或完全移除註解

2. **測試驗證**
   - 在 API 22 設備或模擬器上測試
   - 確認所有 Action 功能正常
   - 檢查是否有運行時錯誤

3. **團隊同步**
   - Core 子模組需要單獨提交
   - 主專案需要更新 submodule 指標
   - 其他開發者需要同步更新

---

## ⚠️ 編譯時會發生什麼？

### 如果保持 minSdk 22 且使用 @RequiresApi(24) 的類別

```
錯誤: Extending BaseActivity requires API level 24 (current min is 22)
```

**IDE 行為**:
- ❌ Android Studio 會顯示編譯錯誤
- ❌ 無法建置 APK
- ❌ Lint 檢查會失敗

### 如果移除 @RequiresApi 註解

```
✅ 編譯成功
✅ 可以建置 APK
✅ 在 API 22+ 設備上正常運行
```

---

## 🔧 快速測試方案

### 臨時移除 @RequiresApi (僅測試用)

如果您想快速測試，可以：

1. **在 core/build.gradle 添加 Lint 配置**
   ```groovy
   android {
       lintOptions {
           disable 'NewApi'
           abortOnError false
       }
   }
   ```

2. **構建時忽略 API 檢查**
   ```bash
   ./gradlew assembleDebug -x lintVitalRelease
   ```

⚠️ **警告**: 這只是暫時繞過檢查，不是正式解決方案！

---

## 📊 決策矩陣

| 情況 | 建議方案 | 工作量 | 風險 |
|------|---------|--------|------|
| 設備都是 Android 7.0+ | **保持 minSdk 24** | 無 | 無 |
| 有 Android 5.x/6.x 設備 | 移除 @RequiresApi 註解 | 高 | 中 |
| 不確定設備版本 | 諮詢硬體團隊後決定 | - | - |
| 不使用 AAction 功能 | 不適用（BaseActivity 已有註解） | - | - |

---

## 🎯 總結

1. **問題根源**: Core 模組過度保守地使用 `@RequiresApi(api = 24)`
2. **實際需求**: Java 8 語法不需要 Android API 24
3. **當前狀態**: minSdk 22 + @RequiresApi(24) = 編譯錯誤 ❌
4. **推薦方案**: 保持 minSdk 24（符合 EDC 設備現狀）
5. **替代方案**: 修改 Core 移除 @RequiresApi（工作量大）

---

## 📞 下一步行動

### 選項 A: 保持 minSdk 24（推薦）

**立即執行**:
```groovy
// app/build.gradle 和 core/build.gradle
defaultConfig {
    minSdk 24  // 改回 24
    ...
}
```

### 選項 B: 改為 minSdk 22（需要工作）

**需要執行**:
1. 確認硬體設備 Android 版本
2. 在 Core 倉庫移除 @RequiresApi 註解
3. 提交 PR 並測試
4. 更新主專案 submodule

---

**建議**: 除非有明確的 Android 5.x/6.x 設備支援需求，否則保持 `minSdk 24` 是最佳選擇！

