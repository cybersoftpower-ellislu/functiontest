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

**GitHub Desktop：**
1. 點選上方 **Fetch origin**
2. 出現 **Pull origin** 後點擊

**命令列：**
```bash
git pull
```

### 更新 core submodule

> GitHub Desktop 不支援 submodule 更新，需使用 AS 底部 Terminal：

```bash
git submodule update --remote
```

更新後在 AS 執行 `File → Sync Project with Gradle Files`。

---

## 提交修改

### 修改 functiontest（app 程式碼）

**GitHub Desktop：**
1. 左側會列出所有變更的檔案
2. 勾選要提交的檔案
3. 左下角填寫 commit 訊息，點 **Commit to main**
4. 點上方 **Push origin** 推上 GitHub

**Android Studio：**
1. `Git → Commit`
2. 勾選變更的檔案，填寫訊息，點 **Commit and Push**

---

### 修改 core 模組

core 是獨立的 repository，需要分別對兩個 repo 各做一次 commit/push。

**步驟一：push core 的修改**

GitHub Desktop：
1. 左上角切換 Repository → 選 **core**
2. 勾選變更檔案，填寫 commit 訊息
3. **Commit to main** → **Push origin**

**步驟二：push functiontest 的 submodule 指標更新**

GitHub Desktop：
1. 切換 Repository → 選 **functiontest**
2. 左側會出現 `core` 的變更（submodule 指標更新）
3. 填寫 commit 訊息，**Commit to main** → **Push origin**
