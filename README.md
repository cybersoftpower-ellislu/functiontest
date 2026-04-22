# FunctionTest

Android 機器功能測試應用程式，使用 [core](https://github.com/cybersoftpower-ellislu/core) 作為共用模組。

---

## 專案資訊

| 項目 | 說明 |
|---|---|
| Package | `com.cyberpower.functiontest` |
| minSdk | 22 |
| targetSdk | 34 |
| Core 模組 | Git Submodule |

---

## 開發環境需求

- Android Studio Panda 以上
- JDK 8
- Git

---

## 取得專案

### 方法一：GitHub Desktop（推薦）

1. 安裝 [GitHub Desktop](https://desktop.github.com)
2. 登入 GitHub 帳號
3. `File → Clone Repository`
4. 選擇 `cybersoftpower-ellislu/functiontest`
5. 選擇存放路徑，點 **Clone**
6. Clone 完成後選擇 **Yes** 初始化 submodule

### 方法二：命令列

```bash
git clone --recurse-submodules https://github.com/cybersoftpower-ellislu/functiontest.git
```

若 clone 後 `core` 資料夾是空的，執行：

```bash
git submodule update --init
```

---

## 開啟專案

1. 開啟 Android Studio
2. `File → Open` → 選擇 `functiontest` 資料夾
3. 等待 Gradle sync 完成

---

## 更新專案

### 更新 functiontest
- GitHub Desktop：`Fetch origin → Pull`
- 或在 AS Terminal 執行：`git pull`

### 更新 core submodule
在 AS 底部 Terminal 執行：

```bash
git submodule update --remote
```

更新後記得在 AS 執行 `File → Sync Project with Gradle Files`。

---

## 提交修改

### 修改 functiontest（app 程式碼）
直接透過 AS 的 Git 功能或 GitHub Desktop commit/push。

### 修改 core 模組
core 是獨立的 repository，修改後需要分別 push：

1. 在 `functiontest/core` 目錄 commit 並 push → 推到 [core repo](https://github.com/cybersoftpower-ellislu/core)
2. 回到 `functiontest` 目錄 commit 並 push → 更新 submodule 指標
