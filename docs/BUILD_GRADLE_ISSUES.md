# 編譯與 Gradle 問題解決指南

> **文档说明**: 本文档整合所有编译、Gradle、依赖冲突等构建相关问题的解决方案
> 
> **最后更新**: 2026-04-23

---

## 📋 目录

- [Gradle Wrapper 问题](#gradle-wrapper-问题)
- [Kotlin 版本冲突](#kotlin-版本冲突)
- [API Level 冲突问题](#api-level-冲突问题)
- [DataBinding 编译错误](#databinding-编译错误)
- [通用编译问题排查](#通用编译问题排查)

---

## Gradle Wrapper 问题

### 问题描述
专案缺少 `gradlew`、`gradlew.bat` 和 `gradle-wrapper.jar` 文件，或执行时报错。

### 核心概念
**Gradle Wrapper** 的设计目标：
- ✅ 不需要预先安装 Gradle
- ✅ 不需要设定 GRADLE_HOME 环境变量
- ✅ 专案自带正确的 Gradle 版本
- ✅ 确保所有开发者使用相同版本

### 快速解决方案

#### 方案 A：使用 Android Studio（强烈推荐）⭐

这是**最快速且最可靠**的方法：

1. **开启专案**
   ```
   File → Open → C:\DevAndroid\functiontest
   ```

2. **等待自动同步**
   - Android Studio 会自动检测并下载 `gradle-wrapper.jar`
   - 通常在 5-30 秒内完成
   - 看到 "Gradle sync finished" 即完成

3. **验证**
   ```powershell
   cd C:\DevAndroid\functiontest
   .\gradlew.bat --version
   ```

#### 方案 B：手动下载 wrapper jar

如果 Android Studio 不可用：

```powershell
# 1. 下载 Gradle 完整发行版
$url = "https://services.gradle.org/distributions/gradle-8.0-bin.zip"
$zipPath = "$env:TEMP\gradle-8.0-bin.zip"
$extractPath = "$env:TEMP\gradle-8.0"

Invoke-WebRequest -Uri $url -OutFile $zipPath
Expand-Archive -Path $zipPath -DestinationPath $extractPath -Force

# 2. 复制 wrapper jar
Copy-Item "$extractPath\gradle-8.0\lib\plugins\gradle-wrapper-8.0.jar" `
          "gradle\wrapper\gradle-wrapper.jar" -Force

# 3. 清理
Remove-Item $zipPath -Force
Remove-Item $extractPath -Recurse -Force
```

### 环境变量说明

**❌ 不需要设定 Gradle 环境变量！**

唯一需要的是 **JAVA_HOME**（Android Studio 已包含）:
```powershell
# 检查 JAVA_HOME
$env:JAVA_HOME

# 如果需要设定（使用 Android Studio 的 JDK）
[System.Environment]::SetEnvironmentVariable(
    "JAVA_HOME", 
    "C:\Program Files\Android\Android Studio\jbr", 
    [System.EnvironmentVariableTarget]::User
)
```

### 文件检查清单

执行此脚本检查所有必要文件：

```powershell
$files = @(
    "gradlew",
    "gradlew.bat",
    "gradle\wrapper\gradle-wrapper.jar",
    "gradle\wrapper\gradle-wrapper.properties"
)

Write-Host "`n检查 Gradle Wrapper 文件：`n" -ForegroundColor Cyan

foreach ($file in $files) {
    $path = "C:\DevAndroid\functiontest\$file"
    if (Test-Path $path) {
        $size = (Get-Item $path).Length
        Write-Host "✅ $file" -ForegroundColor Green -NoNewline
        Write-Host " ($size bytes)" -ForegroundColor Gray
    } else {
        Write-Host "❌ $file (缺少)" -ForegroundColor Red
    }
}
```

---

## Kotlin 版本冲突

### 问题描述

编译时出现大量 Duplicate class 错误：
```
Duplicate class kotlin.collections.jdk8.CollectionsJDK8Kt found in modules 
jetified-kotlin-stdlib-1.8.10 and jetified-kotlin-stdlib-jdk8-1.6.21
```

### 错误原因

1. **版本冲突**: 专案中同时存在不同版本的 Kotlin 标准库
   - `kotlin-stdlib:1.8.10` (新版)
   - `kotlin-stdlib-jdk7:1.6.21` (旧版)
   - `kotlin-stdlib-jdk8:1.6.21` (旧版)

2. **库整合**: 从 Kotlin 1.8.0 开始，`kotlin-stdlib-jdk7` 和 `kotlin-stdlib-jdk8` 的功能已经整合到 `kotlin-stdlib` 主库中

3. **传递依赖**: 某些第三方库（如 RxJava、Retrofit 等）可能依赖旧版本

### 解决方案（已修复）

在根目录 `build.gradle` 中添加统一的依赖版本管理：

```groovy
// 统一管理所有子专案的依赖版本
subprojects {
    configurations.all {
        resolutionStrategy {
            // 强制使用统一的 Kotlin 版本（1.8+ 已整合 jdk7/jdk8）
            force 'org.jetbrains.kotlin:kotlin-stdlib:1.8.10'
        }
        
        // 排除旧版的 jdk7/jdk8 标准库（已整合到 kotlin-stdlib 1.8+）
        exclude group: 'org.jetbrains.kotlin', module: 'kotlin-stdlib-jdk7'
        exclude group: 'org.jetbrains.kotlin', module: 'kotlin-stdlib-jdk8'
    }
}
```

### 执行步骤

在 Android Studio 中：

1. **Gradle Sync**
   ```
   File → Sync Project with Gradle Files
   ```

2. **Clean Project**
   ```
   Build → Clean Project
   ```

3. **Rebuild Project** (最重要)
   ```
   Build → Rebuild Project
   ```

4. **确认成功**
   - Build 视窗应显示 `BUILD SUCCESSFUL`
   - 所有 "Duplicate class kotlin.*" 错误消失

### 如果问题持续

**方案 1: 清除缓存**
```
File → Invalidate Caches / Restart...
选择 "Invalidate and Restart"
```

**方案 2: 手动删除 build 目录**
关闭 Android Studio，然后删除：
```
functiontest/app/build/
functiontest/core/build/
functiontest/build/
functiontest/.gradle/
```

重新打开专案，执行 Gradle Sync → Rebuild

---

## API Level 冲突问题

### 问题描述

如果将 minSdk 改为 22，会遇到编译错误：
```
❌ Extending BaseActivity requires API level 24 (current min is 22)
```

### 问题原因

Core 模組中以下類別都標註了 `@RequiresApi(api = Build.VERSION_CODES.N)` (API 24):
- BaseActivity, BaseFragment, BaseTask
- AAction, ActionBind, ActionContext
- ActionMonitor, ActionResultDispatcher
- BaseNoUiFragment, BaseDialogFragment

### 为什么标注 API 24？

这些类别使用了：
- `@FunctionalInterface` 注解
- Lambda 表达式 (如 `() -> {...}`)

**但是**: 这些都是 Java 8 语法，透过 Gradle desugar 可以在 API 22 运行！所以 `@RequiresApi(24)` 注解是**过度保守**的。

### 解决方案

#### 方案 1: 保持 minSdk 24（推荐 - 最简单）⭐

**操作**:
```groovy
// app/build.gradle 和 core/build.gradle
defaultConfig {
    minSdk 24  // 保持 24
}
```

**优点**:
- ✅ 无需修改代码
- ✅ 立即可用
- ✅ 符合 EDC 设备现状（大部分是 Android 7.0+）

**理由**:
- PAX、Castles 等 EDC 厂商的机器都是 Android 7.0+ (API 24)
- Android 5.x/6.x 设备在 EDC 市场已经很少见
- Google Play 要求 minSdk 逐年提升

#### 方案 2: 改为 minSdk 22（需要修改 Core）

如果您的 EDC 设备确实运行 Android 5.x/6.x，需要：

1. **修改 Core 模块** - 移除 10+ 个文件的 `@RequiresApi` 注解
2. **提交到 Core 仓库** - Git 双重提交（Core + 主专案）
3. **团队同步** - 其他开发者需要更新

**修改的文件清单**:
- AAction.java, ActionBind.java, ActionContext.java
- ActionMonitor.java, ActionResultDispatcher.java
- BaseTask.java, BaseFragment.java, BaseNoUiFragment.java
- BaseDialogFragment.java, BaseActivity.java

**Git 双重提交步骤**:
```bash
# 1. 在 Core 仓库提交
cd core
git add .
git commit -m "移除 @RequiresApi(API 24) 注解以支援 Android 5.x/6.x

- 移除 AAction、ActionBind 等 10 个类别的 @RequiresApi 注解
- Java 8 语法透过 desugar 支援 API 22+"
git push origin main

# 2. 在主专案更新 submodule
cd ..
git add core
git commit -m "更新 core submodule: 支援 minSdk 22"
git push origin main
```

### Java 8 Desugar 确认

项目的 build.gradle 已经设定：
```groovy
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```

✅ 这个配置会自动启用 **D8/R8 desugaring**，将 Java 8 语法转换为 API 19+ 兼容的字节码。

| 特性 | 需要 API | Desugar 后 |
|------|---------|-----------|
| Lambda 表达式 | Java 8 | API 19+ ✅ |
| @FunctionalInterface | Java 8 | API 19+ ✅ |
| Stream API | API 24 | API 21+ ✅ (需额外设定) |
| java.time | API 26 | API 21+ ✅ (需额外设定) |

---

## DataBinding 编译错误

### 问题描述

遇到以下错误：
- `Cannot resolve symbol 'databinding'`
- `Cannot resolve symbol 'ActivityMainBinding'`
- `Cannot resolve symbol 'BR'`

### 原因

DataBinding 类别是在编译时自动生成的，可能尚未执行编译或编译失败。

### 解决步骤

#### 1. 确认 DataBinding 已启用

检查 `app/build.gradle`:
```groovy
android {
    buildFeatures {
        dataBinding true
    }
}
```

#### 2. Gradle Sync

```
File → Sync Project with Gradle Files
```

#### 3. Clean Project

```
Build → Clean Project
```

#### 4. Rebuild Project（最关键）

```
Build → Rebuild Project
```

这会生成：
- `ActivityMainBinding` 类别
- `BR` 类别（包含 `BR.viewModel`）
- 所有 R.id 资源

#### 5. 如果仍然失败

```
File → Invalidate Caches / Restart
```

### 预期结果

编译成功后会生成以下类别：

**ActivityMainBinding**:
```
app/build/generated/data_binding_base_class_source_out/debug/out/
com/cyberpower/functiontest/databinding/ActivityMainBinding.java
```

**BR 类别**:
```
app/build/generated/source/buildConfig/debug/
com/cyberpower/functiontest/BR.java
```

---

## 通用编译问题排查

### 编译检查清单

- [ ] `app/build.gradle` 中 `minSdk 24` 已设定
- [ ] `app/build.gradle` 中 `dataBinding true` 已设定
- [ ] `core/` 子模块已正常载入（资料夾不是空的）
- [ ] 执行 Gradle Sync 成功
- [ ] 执行 Clean Project 成功
- [ ] 执行 Rebuild Project 成功
- [ ] `ActivityMainBinding` 类别已生成
- [ ] `BR.viewModel` 可以解析
- [ ] 所有 R.id 资源可以解析
- [ ] Build 视窗显示 "BUILD SUCCESSFUL"

### Core 模块问题

如果 BaseActivity 或其他 core 类别找不到：

1. **确认 core submodule 已初始化**
   ```bash
   # 在专案根目录执行
   git submodule update --init
   ```

2. **确认 settings.gradle 包含 core**
   ```groovy
   include ':app'
   include ':core'
   ```

3. **Gradle Sync**
   ```
   File → Sync Project with Gradle Files
   ```

### 快速修复命令序列

```powershell
# Android Studio Terminal 中执行
cd C:\DevAndroid\functiontest

# 1. 确认 core 存在
git submodule update --init

# 2. Gradle Sync（在 IDE 中执行）
# File → Sync Project with Gradle Files

# 3. Clean & Rebuild（在 IDE 中执行）
# Build → Clean Project
# Build → Rebuild Project

# 4. 验证编译
.\gradlew.bat assembleDebug
```

### 常见错误参考

| 错误 | 原因 | 解决方案 |
|------|------|---------|
| `requires API level XX` | minSdk 版本不符 | 设定 minSdk 24 |
| `Cannot resolve symbol 'databinding'` | DataBinding 未生成 | Clean → Rebuild |
| `Cannot find BaseActivity` | Core 未载入 | git submodule update --init |
| `Duplicate class kotlin.*` | Kotlin 版本冲突 | 添加 resolutionStrategy |
| `gradle-wrapper.jar` 错误 | Wrapper 文件损坏 | 使用 Android Studio Sync |

### 编译时间参考

- 第一次编译: 约 3-5 分钟（需要下载依赖）
- 后续编译: 约 30-60 秒
- Clean + Rebuild: 约 1-2 分钟

---

## 📚 相关资源

### 专案文档
- `README.md` - 专案主文档
- `AGENTS.md` - 专案架构说明

### 官方文档
- [Gradle Documentation](https://docs.gradle.org/)
- [Android Gradle Plugin](https://developer.android.com/studio/build)
- [Kotlin Documentation](https://kotlinlang.org/docs/)

---

**最后更新**: 2026-04-23  
**维护者**: Development Team


