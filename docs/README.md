# 開發文檔目錄

本目錄包含專案開發期間產生的各類文檔和指南。

> **重要更新 (2026-04-23)**: 文档已重新整理为三大类别，采用持续更新方式而非单一作业记录

---

## 📋 主要文檔（持續更新）

### 🔧 [BUILD_GRADLE_ISSUES.md](./BUILD_GRADLE_ISSUES.md) ⭐
**編譯與 Gradle 問題解決指南**

整合所有编译、Gradle、依赖冲突等构建相关问题：
- Gradle Wrapper 问题
- Kotlin 版本冲突
- API Level 冲突问题
- DataBinding 编译错误
- 通用编译问题排查

**適用對象**: 遇到编译或构建问题的开发者  
**閱讀時機**: 编译失败、Gradle 错误、依赖冲突时

---

### 📖 [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md) ⭐
**開發規範與指南**

整合所有开发规范、功能实现、架构设计：
- 专案架构（MVVM、模块结构、硬件抽象层）
- MainActivity 实现（Drawer 导航、动态标题、离开功能）
- SplashActivity 实现（权限检查、MVVM 重构）
- UI 组件规范（DataBinding、快速点击保护、日志）
- Git 工作流程（双重提交、Submodule 管理）

**適用對象**: 所有开发者  
**閱讀時機**: 开发新功能、了解架构、学习规范时

---

### 🧪 [TESTING_ISSUES.md](./TESTING_ISSUES.md) ⭐
**測試問題解決記錄**

记录测试过程中遭遇的问题及解决方案：
- 冷启动蓝屏问题（Preview Window 优化）
- 启动崩溃问题（资源引用错误）
- Splash Background 配置（基本版 vs 进阶版）

**適用對象**: 遇到测试或运行时问题的开发者  
**閱讀時機**: 应用运行异常、视觉效果问题、启动问题时

---

## 🎯 快速導航

### 新手入門
1. 閱讀 [AGENTS.md](../AGENTS.md) - 了解專案架構
2. 閱讀 [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md) - 学习开发规范
3. 閱讀 [BUILD_GRADLE_ISSUES.md](./BUILD_GRADLE_ISSUES.md) - 了解常见编译问题

### 遇到問題時

#### 编译失败
→ 查看 [BUILD_GRADLE_ISSUES.md](./BUILD_GRADLE_ISSUES.md)

具体问题：
- **Gradle Wrapper 错误** → Gradle Wrapper 问题章节
- **Kotlin 版本冲突** → Kotlin 版本冲突章节
- **API Level 错误** → API Level 冲突问题章节
- **DataBinding 错误** → DataBinding 编译错误章节

#### 开发新功能
→ 查看 [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md)

具体需求：
- **添加新测试项目** → 扩展功能指南章节
- **修改 UI** → UI 组件规范章节
- **理解架构** → 专案架构章节
- **提交代码** → Git 工作流程章节

#### 运行时问题
→ 查看 [TESTING_ISSUES.md](./TESTING_ISSUES.md)

具体问题：
- **启动白屏/蓝屏** → 冷启动蓝屏问题章节
- **应用崩溃** → 启动崩溃问题章节
- **背景配置** → Splash Background 配置章节

---

## 📁 專案文檔結構

```
functiontest/
├── README.md                    # 專案主文檔（Git、建置等）
├── AGENTS.md                    # 專案架構文檔（AI 指南）
└── docs/                        # 開發文檔目錄
    ├── README.md                          # 本文檔（文檔索引）
    │
    ├── ⭐ BUILD_GRADLE_ISSUES.md          # 編譯與 Gradle 問題（持續更新）
    ├── ⭐ DEVELOPMENT_GUIDE.md            # 開發規範與指南（持續更新）
    ├── ⭐ TESTING_ISSUES.md               # 測試問題記錄（持續更新）
    │
    └── archive/                           # 历史单一作业记录（已归档）
        ├── MainActivity_Setup.md
        ├── MainActivity-離開功能.md
        ├── SplashActivity啟動畫面說明.md
        ├── 動態ActionBar標題-實現報告.md
        ├── Title置中與Drawer標題統一-修改報告.md
        ├── Core模組RequiresApi移除-完成報告.md
        ├── API-Level衝突-AAction問題.md
        ├── minSdk-22-vs-24-決策指南.md
        ├── Kotlin版本衝突解決.md
        ├── Kotlin版本衝突-快速修復.md
        ├── Kotlin衝突修復-執行指南.md
        ├── 編譯錯誤修復指南.md
        ├── COMPILE_FIX_SUMMARY.md
        ├── Gradle-Wrapper-完整說明.md
        ├── Gradle-Wrapper-快速修復.md
        ├── Gradle-Wrapper-生成指南.md
        ├── 冷啟動藍屏-修復總結.md
        ├── 冷啟動藍屏-快速修復.md
        ├── 冷啟動藍屏問題-解決方案.md
        ├── 启动崩溃-紧急修复.md
        ├── 启用渐变背景-操作记录.md
        ├── Splash-Background-文件说明.md
        ├── SplashActivity-MVVM-快速參考.md
        ├── SplashActivity-MVVM-權限檢查.md
        ├── GitHub-推送完成报告.md
        ├── 所有問題已解決-最終報告.md
        ├── 立即執行-Git提交指南.md
        └── docs整理完成.md
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


