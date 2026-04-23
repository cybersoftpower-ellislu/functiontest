# 立即執行 - Git 提交指南

##  ✅ 代碼已修改完成！

已成功移除 Core 模組中 10 個文件的 `@RequiresApi` 註解。

---

## 🚀 現在請立即執行（分兩步）

### 📌 步驟 1: 提交 Core 子模組

#### GitHubDesktop 用戶

1. **切換到 Core 倉庫**
   - 點擊左上角 `Current Repository` 下拉選單
   - 選擇 **`core`**

2. **查看變更**
   - 左側會顯示 10 個修改的 .java 文件
   - 確認每個文件都移除了 `@RequiresApi` 相關代碼

3. **填寫 Commit 訊息**
   ```
   移除 @RequiresApi 註解支援 Android 5.x/6.x

   - 移除 10 個類別的 @RequiresApi(API 24) 註解
   - AAction, ActionBind, ActionContext, ActionMonitor, ActionResultDispatcher
   - BaseTask, BaseFragment, BaseNoUiFragment, BaseDialogFragment, BaseFragmentWithTick
   - Java 8 語法透過 desugar 支援 API 22+
   ```

4. **提交並推送**
   - 點擊 `Commit to main`
   - 點擊 `Push origin`

#### 命令列用戶

```bash
cd core

git status
git diff

git add .
git commit -m "移除 @RequiresApi 註解支援 Android 5.x/6.x

- 移除 10 個類別的 @RequiresApi(API 24) 註解
- Java 8 語法透過 desugar 支援 API 22+"

git push origin main
```

---

### 📌 步驟 2: 更新主專案 Submodule 指標

#### GitHub Desktop 用戶

1. **切換回主專案**
   - 點擊左上角 `Current Repository` 下拉選單
   - 選擇 **`functiontest`**

2. **查看變更**
   - 左側會顯示 `core` (modified)
   - 這表示 submodule 指標需要更新

3. **填寫 Commit 訊息**
   ```
   更新 core submodule：支援 minSdk 22
   ```

4. **提交並推送**
   - 點擊 `Commit to main`
   - 點擊 `Push origin`

#### 命令列用戶

```bash
cd ..  # 回到 functiontest 根目錄

git status  # 應該看到 modified: core

git add core
git commit -m "更新 core submodule：支援 minSdk 22"
git push origin main
```

---

## 🔧 步驟 3: 在 Android Studio 測試

### 1. Gradle Sync
```
File → Sync Project with Gradle Files
```
⏱️ 等待約 30 秒

### 2. Clean Project
```
Build → Clean Project
```
⏱️ 等待約 10 秒

### 3. Rebuild Project ⭐
```
Build → Rebuild Project
```
⏱️ 等待約 1-2 分鐘

### 4. 檢查結果

**成功標誌**:
```
✅ BUILD SUCCESSFUL
✅ 沒有 "requires API level 24" 錯誤
```

**如果成功**:
```
Run → Run 'app'
```

---

## ⚠️ 如果編譯失敗

### 常見問題 1: Gradle Sync 失敗

**解決方案**:
```
File → Invalidate Caches / Restart...
選擇 "Invalidate and Restart"
```

等待 IDE 重啟後，再次執行上述步驟。

### 常見問題 2: 仍然出現 API Level 錯誤

**檢查**:
1. 確認 Core 的修改已提交並推送
2. 確認主專案的 submodule 指標已更新
3. 執行 `git submodule update --remote` 確保最新

**強制更新**:
```bash
git submodule update --remote --force
```

然後在 Android Studio 重新 Sync 和 Rebuild。

### 常見問題 3: 其他編譯錯誤

參考：
- 📖 [編譯錯誤修復指南.md](./編譯錯誤修復指南.md)
- 📖 [Kotlin版本衝突解決.md](./Kotlin版本衝突解決.md)

---

## 📋 完整檢查清單

### Git 操作
- [ ] 切換到 Core 倉庫
- [ ] 確認看到 10 個修改的 .java 文件
- [ ] 提交 Core 的修改
- [ ] 推送 Core 到遠端
- [ ] 切換回 functiontest 倉庫
- [ ] 確認看到 core (modified)
- [ ] 提交 submodule 指標更新
- [ ] 推送 functiontest 到遠端

### Android Studio 操作
- [ ] Gradle Sync 成功
- [ ] Clean Project 完成
- [ ] Rebuild Project 成功
- [ ] 沒有 API Level 錯誤
- [ ] 應用程式可以執行

### 測試（可選但建議）
- [ ] 在 API 22-23 設備上測試
- [ ] 測試 AAction 相關功能
- [ ] 測試 BaseTask 功能
- [ ] 測試 Fragment 功能

---

## 💡 提示

### 為什麼需要兩次提交？

```
functiontest/          ← 主專案（Git 倉庫 1）
└── core/             ← 子模組（Git 倉庫 2）
    └── .java 文件     ← 實際修改在這裡
```

1. **Core 倉庫**: 存放實際的代碼修改
2. **主專案**: 存放 "core 現在指向哪個 commit" 的資訊

兩個都要提交，團隊成員才能拿到完整更新！

### 確認提交成功

**在 GitHub 網站檢查**:
1. 打開 Core 倉庫頁面
   - 應該看到最新的 commit
   - 10 個文件被修改

2. 打開 functiontest 倉庫頁面
   - 應該看到 submodule 更新的 commit
   - 點擊 `core` 連結應該指向 Core 的最新 commit

---

## 🎯 預期時間

| 步驟 | 時間 |
|------|------|
| Git 提交 (Core) | 2 分鐘 |
| Git 提交 (主專案) | 1 分鐘 |
| Gradle Sync | 30 秒 |
| Clean Project | 10 秒 |
| Rebuild Project | 1-2 分鐘 |
| **總計** | **約 5 分鐘** |

---

## 📞 需要幫助？

### 詳細文檔
- 📖 [Core模組RequiresApi移除-完成報告.md](./Core模組RequiresApi移除-完成報告.md)
- 📖 [README.md](../README.md) - Git Submodule 工作流程

### 遇到問題
請檢查：
1. Core 是否成功推送（在 GitHub 網站確認）
2. 主專案 submodule 指標是否更新
3. Android Studio 是否需要 Invalidate Caches

---

## ✅ 完成後

恭喜！您的專案現在可以在 **Android 5.x/6.x** (API 22-23) 設備上運行了！

**下一步**:
1. 通知團隊成員更新代碼
2. 在實體 Android 5.x/6.x 設備上測試
3. 更新相關文檔（如有需要）

---

**現在開始第一步**: 在 GitHub Desktop 或命令列提交 Core 模組！🚀

