# minSdk 22 vs 24 - 快速決策指南

## ❓ 您的問題

> 我把 core, functiontest 的 minSdk 改為 22，那在使用 AAction, ActionBind 上會有問題嗎？

## ⚠️ 簡答：會有問題！

如果您將 minSdk 改為 22，會遇到**編譯錯誤**：

```
❌ Extending BaseActivity requires API level 24 (current min is 22)
❌ 無法建置 APK
```

---

## 🔍 問題原因

Core 模組中以下類別都標註了 `@RequiresApi(api = Build.VERSION_CODES.N)` (API 24):

### 受影響的核心類別
- ❌ `BaseActivity` - 您的 MainActivity 繼承的基礎類別
- ❌ `AAction` - Action 基礎類別
- ❌ `ActionBind` - Action 綁定類別  
- ❌ `BaseFragment`, `BaseTask` 等 10+ 個類別

### 為什麼標註 API 24？

這些類別使用了：
- `@FunctionalInterface` 註解
- Lambda 表達式 (如 `() -> {...}`)

**但是**: 這些都是 Java 8 語法，透過 Gradle desugar 可以在 API 22 運行！
所以 `@RequiresApi(24)` 註解是**過度保守**的。

---

## ✅ 兩種解決方案

### 方案 1: 改回 minSdk 24（⭐ 推薦 - 最簡單）

**優點**:
- ✅ 無需修改代碼
- ✅ 立即可用
- ✅ 符合 EDC 設備現狀（大部分是 Android 7.0+）

**操作**:
```groovy
// app/build.gradle 和 core/build.gradle
defaultConfig {
    minSdk 24  // 改回 24
}
```

然後執行：
1. Gradle Sync
2. Rebuild Project
3. ✅ 編譯成功！

---

### 方案 2: 保持 minSdk 22（需要修改 Core）

**適用於**: 您的 EDC 設備確實運行 Android 5.x/6.x (API 22-23)

**需要做的事**:
1. 修改 Core 子模組（10+ 個檔案）
2. 移除所有 `@RequiresApi(api = Build.VERSION_CODES.N)` 註解
3. 提交到 Core 倉庫
4. 更新主專案的 submodule 指標
5. 團隊成員同步更新

**工作量**: 高 ⚠️  
**風險**: 中等（需要測試 API 22 相容性）

---

## 🎯 我的建議

### 建議：使用 minSdk 24

**理由**:

1. **硬體現狀**
   - PAX、Castles 等 EDC 廠商的機器都是 Android 7.0+ (API 24)
   - Android 5.x/6.x 設備在 EDC 市場已經很少見
   - 新機器不會低於 Android 7.0

2. **維護成本**
   - 不需要修改 Core 模組（10+ 個檔案）
   - 不需要團隊協調和測試
   - 避免潛在的 API 22 相容性問題

3. **Google 政策**
   - Google Play 要求 minSdk 逐年提升
   - Android 7.0 已經是 8 年前的版本（2016）
   - 設定更高的 minSdk 是趨勢

---

## 📊 決策流程圖

```
您的 EDC 設備運行哪個 Android 版本？
│
├─ Android 7.0+ (API 24+)
│  └─ ✅ 使用 minSdk 24（推薦）
│     └─ 無需修改 Core，立即可用
│
├─ Android 5.x/6.x (API 22-23)
│  └─ 選項 1: 升級硬體到 Android 7.0+
│  └─ 選項 2: 修改 Core 移除 @RequiresApi 註解
│
└─ 不確定
   └─ 諮詢硬體團隊或檢查設備規格
```

---

## ⚡ 快速執行

### 如果選擇 minSdk 24（推薦）

1. **修改 app/build.gradle**
   ```groovy
   defaultConfig {
       minSdk 24
   }
   ```

2. ~~**修改 core/build.gradle**~~ (Core 已經是 22，需要改為 24)
   ```groovy
   defaultConfig {
       minSdkVersion 24
   }
   ```

3. **在 Android Studio 執行**
   ```
   File → Sync Project with Gradle Files
   Build → Rebuild Project
   ```

4. ✅ 編譯成功，可以執行！

---

### 如果選擇 minSdk 22（需要工作）

請參考詳細文檔：
📖 [API-Level衝突-AAction問題.md](./API-Level衝突-AAction問題.md)

包含：
- 詳細的技術分析
- 需要修改的檔案清單
- 測試步驟
- 風險評估

---

## 🔍 檢查您的設備

### 如何確認 EDC 設備的 Android 版本？

1. **設備設定**
   ```
   設定 → 關於裝置 → Android 版本
   ```

2. **ADB 命令**
   ```bash
   adb shell getprop ro.build.version.sdk
   # 22 = Android 5.1
   # 23 = Android 6.0
   # 24 = Android 7.0
   # 25 = Android 7.1
   ```

3. **查看設備規格**
   - PAX A920: Android 7.1 (API 25) ✅
   - PAX A80: Android 7.1 (API 25) ✅
   - Castles S1F2: Android 7.1 (API 25) ✅

大部分 EDC 設備都支援 API 24+！

---

## 📝 總結

| 問題 | 答案 |
|------|------|
| minSdk 22 會有問題嗎？ | **會！編譯錯誤** ❌ |
| 為什麼 Core 要求 API 24？ | 過度保守的 @RequiresApi 註解 |
| 實際上需要 API 24 嗎？ | 不需要（Java 8 語法可 desugar） |
| 推薦解決方案？ | **改回 minSdk 24** ⭐ |
| 如果必須用 API 22？ | 修改 Core 移除 @RequiresApi（工作量大）|

---

## 🔗 相關文檔

- 📖 **詳細分析**: [API-Level衝突-AAction問題.md](./API-Level衝突-AAction問題.md)
- 📖 **文檔索引**: [docs/README.md](./README.md)

---

## 💡 最後建議

**除非您的 EDC 設備確實運行 Android 5.x/6.x，否則使用 `minSdk 24` 是最佳選擇！**

這樣可以：
- ✅ 立即解決編譯問題
- ✅ 避免修改 Core 模組
- ✅ 符合行業標準
- ✅ 面向未來

---

**現在就執行**: 將 minSdk 改回 24，Rebuild Project，問題解決！🚀

