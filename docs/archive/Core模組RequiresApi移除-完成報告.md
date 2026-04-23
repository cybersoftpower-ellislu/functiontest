# Core 模組 @RequiresApi 移除 - 完成報告

## ✅ 修改完成

已成功移除 Core 模組中所有 `@RequiresApi(api = Build.VERSION_CODES.N)` 註解，使專案可以在 Android 5.x/6.x (API 22-23) 設備上運行。

---

## 📋 修改的文件清單

### Action 相關類別（5 個）

| 檔案 | 位置 | 狀態 |
|------|------|------|
| 1. `AAction.java` | `core/src/main/java/com/cyberpower/edc/core/action/` | ✅ 已修改 |
| 2. `ActionBind.java` | `core/src/main/java/com/cyberpower/edc/core/action/` | ✅ 已修改 |
| 3. `ActionContext.java` | `core/src/main/java/com/cyberpower/edc/core/action/` | ✅ 已修改 |
| 4. `ActionMonitor.java` | `core/src/main/java/com/cyberpower/edc/core/action/` | ✅ 已修改 |
| 5. `ActionResultDispatcher.java` | `core/src/main/java/com/cyberpower/edc/core/action/` | ✅ 已修改 |

### Base 類別（5 個）

| 檔案 | 位置 | 狀態 |
|------|------|------|
| 6. `BaseTask.java` | `core/src/main/java/com/cyberpower/edc/core/base/` | ✅ 已修改 |
| 7. `BaseFragment.java` | `core/src/main/java/com/cyberpower/edc/core/base/` | ✅ 已修改 |
| 8. `BaseNoUiFragment.java` | `core/src/main/java/com/cyberpower/edc/core/base/` | ✅ 已修改 |
| 9. `BaseDialogFragment.java` | `core/src/main/java/com/cyberpower/edc/core/base/` | ✅ 已修改 |
| 10. `BaseFragmentWithTick.java` | `core/src/main/java/com/cyberpower/edc/core/base/` | ✅ 已修改 |

**總計**: 10 個文件已成功修改 ✅

---

## 🔧 修改內容

### 移除的內容

每個文件都移除了以下內容：

```java
// 移除的 import
import android.os.Build;
import androidx.annotation.RequiresApi;

// 移除的註解
@RequiresApi(api = Build.VERSION_CODES.N)
```

### 保留的功能

✅ **所有功能都保留**：
- Lambda 表達式
- `@FunctionalInterface` 註解
- `ConcurrentHashMap`
- 所有業務邏輯

這些 Java 8 語法特性透過 Gradle desugar 可以在 API 22+ 運行。

---

## 🚀 下一步操作（重要！）

### ⚠️ 這些修改是在 Core 子模組中

Core 是一個獨立的 Git 倉庫（submodule），需要分別提交：

### 步驟 1: 在 Core 倉庫提交修改

```bash
# 進入 core 目錄
cd core

# 檢查修改
git status

# 查看具體變更
git diff

# 添加所有修改的文件
git add .

# 提交（使用有意義的訊息）
git commit -m "移除 @RequiresApi(API 24) 註解以支援 Android 5.x/6.x 設備

- 移除 AAction、ActionBind、ActionContext 等 10 個類別的 @RequiresApi 註解
- Java 8 語法（Lambda、@FunctionalInterface）透過 desugar 支援 API 22+
- 使專案可在 minSdk 22 環境下編譯和運行"

# 推送到 Core 遠端倉庫
git push origin main
```

### 步驟 2: 在主專案更新 Submodule 指標

```bash
# 回到主專案根目錄
cd ..

# 檢查 core submodule 的變更
git status
# 應該會看到：modified: core (new commits)

# 添加 submodule 指標更新
git add core

# 提交主專案
git commit -m "更新 core submodule：支援 minSdk 22"

# 推送到主專案遠端倉庫
git push origin main
```

### 步驟 3: 在 Android Studio 編譯測試

```
1. File → Sync Project with Gradle Files
2. Build → Clean Project
3. Build → Rebuild Project
4. Run → Run 'app'
```

---

## ⚠️ 重要提醒

### 雙重提交要求

由於 Core 是 Git submodule，需要：

1. ✅ **在 Core 倉庫提交**：提交實際的代碼修改
2. ✅ **在主專案提交**：更新 submodule 指標

**兩者都必須完成**，其他團隊成員才能獲得完整的更新！

### 團隊成員同步方式

其他開發者需要執行：

```bash
# 拉取主專案
git pull

# 更新 submodule
git submodule update --init --recursive

# 在 Android Studio 執行
# File → Sync Project with Gradle Files
```

---

## 📊 驗證清單

請完成以下驗證步驟：

- [ ] Core 倉庫已提交並推送
- [ ] 主專案 submodule 指標已更新並推送
- [ ] Gradle Sync 成功
- [ ] Rebuild Project 成功（無 API Level 錯誤）
- [ ] 應用程式可以正常執行
- [ ] 在 Android 5.x/6.x 設備上測試（如有實體機）
- [ ] 在 API 24+ 設備上測試（確保向上兼容）
- [ ] 團隊成員已通知更新

---

## 🧪 測試建議

### 在不同 API Level 測試

1. **API 22 (Android 5.1)** - 最低支援版本
   ```bash
   # 創建模擬器
   avdmanager create avd -n api22 -k "system-images;android-22;default;armeabi-v7a"
   ```

2. **API 24 (Android 7.0)** - 原本的 minSdk
3. **API 34 (Android 14)** - 最新版本

### 重點測試項目

- ✅ AAction 和 ActionBind 的創建和執行
- ✅ Lambda 表達式正常運作
- ✅ BaseTask、BaseFragment 等基礎類別功能
- ✅ ActionContext 的任務註冊和管理
- ✅ 沒有 ClassNotFoundException 或 NoSuchMethodError

---

## 📝 技術說明

### 為什麼可以移除 @RequiresApi？

1. **Lambda 表達式和 @FunctionalInterface**
   - 這是 **Java 8 語法**特性，不是 Android API
   - Android Gradle Plugin 自動啟用 **D8/R8 desugaring**
   - 將 Java 8 語法轉換為 API 14+ 兼容的字節碼

2. **ConcurrentHashMap**
   - 從 Android API 1 就支援
   - 不需要 API 24

3. **其他 Java 集合類別**
   - HashMap, Map, List 等都是標準 Java API
   - 所有 Android 版本都支援

### Desugaring 配置（已自動啟用）

您的 build.gradle 已經包含：
```groovy
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```

這會自動啟用 desugaring，支援：
- Lambda 表達式 → API 14+
- Method references → API 14+
- @FunctionalInterface → API 14+
- Try-with-resources → API 14+

---

## 🔗 相關文檔

- 📖 [minSdk-22-vs-24-決策指南.md](./minSdk-22-vs-24-決策指南.md)
- 📖 [API-Level衝突-AAction問題.md](./API-Level衝突-AAction問題.md)
- 📖 [README.md](../README.md) - Git Submodule 工作流程

---

## 💡 後續維護建議

### 1. 避免使用真正需要 API 24+ 的特性

以下特性確實需要 API 24+（應避免使用）：
- `java.util.stream.*` - Stream API
- `java.time.*` - 新的日期時間 API
- `java.util.Optional` - Optional 類別

如需使用，請考慮：
- Stream API → 使用 RxJava 或傳統循環
- java.time → 使用 ThreeTenABP 或傳統 Date/Calendar
- Optional → 使用 @Nullable/@NonNull 註解

### 2. 代碼審查

未來添加新的 Action 或 Base 類別時，請確保：
- ✅ 不添加 `@RequiresApi` 註解（除非真的需要）
- ✅ 避免使用 API 24+ 特定的 Android API
- ✅ Java 8 語法可以放心使用（會自動 desugar）

### 3. CI/CD 測試

建議在 CI/CD 流程中加入多版本測試：
- API 22 測試（最低版本）
- API 24 測試（先前的基準）
- API 34 測試（最新版本）

---

## ✅ 完成狀態

| 項目 | 狀態 |
|------|------|
| 代碼修改 | ✅ 完成（10 個文件） |
| Core 倉庫提交 | ⏳ 待執行 |
| 主專案 submodule 更新 | ⏳ 待執行 |
| Gradle Sync | ⏳ 待執行 |
| 編譯測試 | ⏳ 待執行 |
| 設備測試 | ⏳ 待執行 |
| 團隊通知 | ⏳ 待執行 |

---

## 🎉 恭喜！

代碼修改部分已完成！

**下一步**: 請執行上述的 Git 提交步驟，然後在 Android Studio 中測試編譯。

如果遇到任何問題，請參考相關文檔或檢查錯誤訊息。

---

**修改日期**: 2026-04-23  
**修改人**: AI Assistant  
**修改原因**: 支援 Android 5.x/6.x (API 22-23) 設備  
**影響範圍**: Core 模組 10 個文件  
**風險評估**: 低（Java 8 語法會自動 desugar）

