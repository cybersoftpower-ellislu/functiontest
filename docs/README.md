# 開發文檔目錄

本目錄包含專案開發期間產生的各類文檔和指南。

---

## 📋 文檔清單

### MainActivity 實作相關

#### [MainActivity_Setup.md](./MainActivity_Setup.md)
**完整的 MainActivity 實作指南**

包含內容：
- ✅ 已創建的文件結構
- ✅ 配置更新說明
- ✅ MVVM 架構實作詳解
- ✅ 兩層 Drawer 導航設計
- ✅ 權限處理機制
- ✅ 在 Android Studio 中的設置步驟
- ✅ 功能擴展指南
- ✅ 疑難排解

**適用對象**: 所有開發者  
**閱讀時機**: 首次設置專案或需要了解 MainActivity 實作細節時

---

### 編譯問題解決

#### [Core模組RequiresApi移除-完成報告.md](./Core模組RequiresApi移除-完成報告.md) ⭐ NEW
**Core 模組 API Level 修改完成報告**

支援 Android 5.x/6.x 設備的完整修改報告：
- ✅ 已修改的 10 個文件清單
- ✅ Git 雙重提交步驟（Core + 主專案）
- ✅ 測試驗證清單
- ✅ 技術說明和後續維護建議

**適用對象**: 需要支援 minSdk 22 的開發者  
**閱讀時機**: 執行 API Level 修改後的驗證和提交

---

#### [立即執行-Git提交指南.md](./立即執行-Git提交指南.md) ⭐ 必讀
**快速操作指南 - Git 提交步驟**

修改完成後的立即行動指南：
- ✅ 詳細的 Git 提交步驟（GitHub Desktop + 命令列）
- ✅ Android Studio 編譯測試步驟
- ✅ 問題排查和檢查清單
- ✅ 預期時間和成功標誌

**適用對象**: 所有需要提交 Core 模組修改的開發者  
**閱讀時機**: Core 代碼修改完成後，立即閱讀並執行

---

#### [API-Level衝突-AAction問題.md](./API-Level衝突-AAction問題.md)
**API Level 衝突深入分析**

詳細的技術分析文檔：
- 受影響類別清單和原因分析
- Java 8 desugaring 機制說明
- 兩種解決方案對比
- 決策矩陣和建議

**適用對象**: 需要了解技術細節的開發者  
**閱讀時機**: 深入了解 API Level 問題時

---

#### [minSdk-22-vs-24-決策指南.md](./minSdk-22-vs-24-決策指南.md)
**minSdk 版本決策指南**

快速決策文檔：
- 兩種方案對比（minSdk 24 vs 22）
- 決策流程圖
- EDC 設備版本檢查方法
- 快速執行步驟

**適用對象**: 所有開發者  
**閱讀時機**: 決定 minSdk 版本時

---

#### [Kotlin版本衝突解決.md](./Kotlin版本衝突解決.md)
**Kotlin 標準庫版本衝突問題**

解決 Duplicate class kotlin.* 錯誤，包含：
- ✅ 問題原因分析（kotlin-stdlib-jdk7/jdk8 衝突）
- ✅ 解決方案（統一版本管理 + 排除舊版依賴）
- ✅ 執行步驟（Sync、Clean、Rebuild）
- ✅ 依賴版本說明和注意事項

**適用對象**: 遇到 Kotlin 相關編譯錯誤的開發者  
**閱讀時機**: 出現 Duplicate class kotlin.* 錯誤時

---

#### [編譯錯誤修復指南.md](./編譯錯誤修復指南.md)
**詳細的編譯錯誤修復步驟**

包含內容：
- ✅ 已修復的問題列表（API Level、LogUtils 初始化）
- ✅ DataBinding 類別生成問題解決
- ✅ 詳細的操作步驟（Gradle Sync、Rebuild Project）
- ✅ 常見問題 Q&A
- ✅ 編譯檢查清單
- ✅ 提示和建議

**適用對象**: 遇到編譯錯誤的開發者  
**閱讀時機**: 專案無法編譯或出現 DataBinding 相關錯誤時

---

#### [COMPILE_FIX_SUMMARY.md](./COMPILE_FIX_SUMMARY.md)
**快速修復總結**

簡潔版的編譯問題修復指南，適合快速參考。包含已修復的問題和需要手動操作的步驟。

**適用對象**: 熟悉專案但遇到編譯問題的開發者  
**閱讀時機**: 需要快速查看修復步驟時

---

## 🎯 快速導航

### 新手入門
1. 閱讀專案根目錄的 [README.md](../README.md) - 了解專案基本資訊
2. 閱讀 [AGENTS.md](../AGENTS.md) - 了解專案架構
3. 閱讀 [MainActivity_Setup.md](./MainActivity_Setup.md) - 了解主要功能實作

### 遇到編譯錯誤

#### 需要支援 Android 5.x/6.x (minSdk 22)
1. 閱讀 [Core模組RequiresApi移除-完成報告.md](./Core模組RequiresApi移除-完成報告.md) - 查看修改結果
2. 執行 [立即執行-Git提交指南.md](./立即執行-Git提交指南.md) - ⭐ 立即提交
3. 參考 [minSdk-22-vs-24-決策指南.md](./minSdk-22-vs-24-決策指南.md) - 了解背景

#### Duplicate class kotlin.* 錯誤
1. 閱讀 [Kotlin版本衝突解決.md](./Kotlin版本衝突解決.md) - Kotlin 標準庫衝突
2. 執行 Clean → Rebuild Project

#### DataBinding 相關錯誤
1. 閱讀 [COMPILE_FIX_SUMMARY.md](./COMPILE_FIX_SUMMARY.md) - 快速了解問題
2. 參考 [編譯錯誤修復指南.md](./編譯錯誤修復指南.md) - 詳細解決步驟

### 擴展功能
1. 參考 [MainActivity_Setup.md](./MainActivity_Setup.md) 中的「擴展功能」章節
2. 參考 [AGENTS.md](../AGENTS.md) 了解專案慣例和模式

---

## 📁 專案文檔結構

```
functiontest/
├── README.md                              # 專案主文檔（Git、建置等）
├── AGENTS.md                              # 專案架構文檔（AI 指南）
└── docs/                                  # 開發文檔目錄
    ├── README.md                          # 本文檔（文檔索引）
    ├── MainActivity_Setup.md              # MainActivity 實作指南
    ├── Core模組RequiresApi移除-完成報告.md  # ⭐ API 22 支援修改報告
    ├── 立即執行-Git提交指南.md              # ⭐ Git 提交步驟
    ├── API-Level衝突-AAction問題.md        # API Level 技術分析
    ├── minSdk-22-vs-24-決策指南.md         # minSdk 版本決策
    ├── Kotlin版本衝突解決.md                # Kotlin 標準庫衝突
    ├── Kotlin版本衝突-快速修復.md           # Kotlin 快速指南
    ├── 編譯錯誤修復指南.md                  # 詳細修復步驟
    ├── COMPILE_FIX_SUMMARY.md             # 快速修復總結
    └── docs整理完成.md                      # 文檔整理說明
```

---

## 💡 文檔維護

### 添加新文檔
當創建新的開發文檔時，請：
1. 將文檔放在 `docs/` 目錄下
2. 在本 README.md 中添加相應的索引
3. 使用清晰的檔名（中文或英文皆可）
4. 在文檔開頭說明適用對象和閱讀時機

### 文檔命名規範
- 使用描述性的檔名
- 可以使用中文或英文
- 建議格式：`功能名稱_文檔類型.md` 或 `FEATURE_TYPE.md`

### 文檔內容建議
- 使用 Markdown 格式
- 添加目錄（TOC）方便導航
- 使用表情符號（emoji）增加可讀性
- 提供具體的代碼範例
- 包含疑難排解章節

---

## 🔗 相關連結

- **專案主頁**: [README.md](../README.md)
- **架構文檔**: [AGENTS.md](../AGENTS.md)
- **Core 模組**: [GitHub - core](https://github.com/cybersoftpower-ellislu/core)

---

## 📝 更新記錄

| 日期 | 變更內容 |
|------|---------|
| 2026-04-23 | 初始化 docs 目錄，整理開發文檔 |
| 2026-04-23 | 添加 MainActivity 實作相關文檔 |
| 2026-04-23 | 添加編譯問題解決文檔 |
| 2026-04-23 | 添加 Kotlin 版本衝突解決文檔 |
| 2026-04-23 | **完成 Core 模組 API 22 支援修改** |
| 2026-04-23 | 添加 API Level 支援相關文檔（4份） |

---

**最後更新**: 2026-04-23  
**維護者**: Development Team


