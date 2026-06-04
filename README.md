# FunctionTest

Android 機器功能測試應用程式。**目前僅為應用程式外殼（框架）**，實際測試功能將於後續開發時陸續添加。

共用邏輯位於 [core](https://github.com/cybersoftpower-ellislu/core) 模組，以 Git Submodule 形式引入。

---

## 專案現狀

- 目前只有 App 殼：啟動流程、抽屜選單、API 測試框架等基礎架構。
- 各項測試功能待後續逐步加入。

---

## 編譯環境（硬性需求）

> ⚠️ 正式環境的編譯主機為 **Android Studio Arctic Fox**，且經決議**不升版**。
> 以下版本為硬性綁定，請勿升級。

| 項目 | 版本 |
|---|---|
| Android Studio | **Arctic Fox** |
| Android Gradle Plugin (AGP) | **4.1.3** |
| Gradle | **6.5** |
| JDK | **11** |

---

## 版本限制與原因

| 項目 | 值 | 說明 |
|---|---|---|
| `compileSdk` / `targetSdk` | **31** | **不可升到 ≥ 32**。Arctic Fox 內建的 aapt2 4.1.3 無法解析 API ≥ 32 的 framework `resources.arsc`（含 overlayable / staged 等新 chunk），app 資源連結階段會報 `AAPT2 ... LoadedArsc.cpp Unknown chunk type '200'`。實測 API ≤ 31 可正常編譯。 |
| `minSdk` | 22 | |
| `applicationId` | `com.cyberpower.functiontest` | |
| core package | `com.cyberpower.core` | |

### core 模組相容性約束

core 需同時相容 Arctic Fox（AGP 4.1.3），故：

- 使用 manifest `package`，**不使用** `namespace` DSL。
- **不啟用** `buildFeatures { buildConfig true }`。
- 程式碼**避免直接呼叫高於 compileSdk 的新 API 符號**；如需用到（例如 Android 13 的 `RECEIVER_EXPORTED`），改以反射呼叫，並用 `Build.VERSION.SDK_INT` 守住 runtime。

---

## 取得專案

```bash
git clone --recurse-submodules https://github.com/cybersoftpower-ellislu/functiontest.git
```

若 clone 後 `core` 資料夾是空的：

```bash
git submodule update --init
```

> 註：本專案的 `gradle-wrapper.jar` 曾損壞，命令列 `./gradlew` 可能失敗；建議直接用 Android Studio 開啟與編譯（IDE 走 Gradle Tooling API，不受該檔影響）。

---

## 開啟專案

1. 開啟 Android Studio（Arctic Fox）
2. `File → Open` → 選擇 `functiontest` 資料夾
3. 等待 Gradle Sync 完成

---

## 更新與提交

### 更新

```bash
git pull
git submodule update --remote   # 更新 core 到最新
```

更新 submodule 後，在 AS 執行 `File → Sync Project with Gradle Files`。

### 提交 core 模組的修改

core 是獨立的 repository，需分別對兩個 repo 各做一次 commit / push：

1. **先 push core**：在 `core/` 目錄 commit 後 push 到 core repo 的 `main`。
2. **再更新指標**：回到 functiontest，`core` 會顯示為 submodule 指標更新，commit 後 push。

---

## 相關文件

- [AGENTS.md](./AGENTS.md) — 專案架構（AI 開發指南）
- [docs/](./docs/) — 開發文檔目錄
