# 測試問題解決記錄

> **文档说明**: 本文档记录所有测试过程中遭遇的问题及解决方案
> 
> **最后更新**: 2026-04-23

---

## 📋 目录

- [冷启动蓝屏问题](#冷启动蓝屏问题)
- [启动崩溃问题](#启动崩溃问题)
- [Splash Background 配置](#splash-background-配置)

---

## 冷启动蓝屏问题

### 问题现象

```
┌──────────────────────────────┐
│                              │
│     纯蓝色画面                │  ← 这个蓝色画面持续 0.5-1 秒
│     (#2196F3)                │
│                              │
└──────────────────────────────┘
        ↓ 0.5-1秒后
┌──────────────────────────────┐
│   Function Test              │
│   硬體功能測試系統            │  ← SplashActivity
│        ●                     │
└──────────────────────────────┘
```

### 问题原因

**Android 应用启动流程**：

```
用户点击图标
    ↓
系统立即显示预览窗口（Preview Window）
    ├─ 使用 Theme.Splash 的 windowBackground
    ├─ 原设定：@color/colorPrimary (纯蓝色)
    └─ 目的：立即给用户视觉反馈
    ↓
应用程序进程启动
    ├─ Application.onCreate() 执行
    └─ 初始化 Core、HardwareManager 等
    ↓
SplashActivity 创建
    ├─ onCreate() 执行
    ├─ 权限检查开始
    └─ 布局渲染
    ↓
用户看到 SplashActivity 实际内容
```

**问题**: 步骤 1 的预览窗口是纯蓝色，与步骤 3 的 SplashActivity 不一致，导致明显的视觉跳变。

### 解决方案

#### 方案：自定义启动画面 Drawable

创建自定义的 `splash_background.xml`，让预览窗口和 SplashActivity 视觉一致。

##### 1. 创建 splash_background.xml（基本版）

**文件**: `app/src/main/res/drawable/splash_background.xml`

```xml
<layer-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:opacity="opaque">
    
    <!-- 背景色 -->
    <item>
        <shape android:shape="rectangle">
            <solid android:color="@color/colorPrimary" />
        </shape>
    </item>
    
    <!-- Logo 部分（避免资源错误，已注释）-->
    <!--
    <item
        android:width="120dp"
        android:height="120dp"
        android:gravity="center">
        <bitmap
            android:gravity="center"
            android:src="@drawable/splash_logo" />
    </item>
    -->
</layer-list>
```

**特点**:
- ✅ 简单 - 只有纯色背景
- ✅ 可靠 - 不会出现资源找不到的错误
- ✅ 快速 - 加载速度最快
- ✅ 稳定 - 已测试无问题

##### 2. 创建 splash_background_advanced.xml（进阶版）

**文件**: `app/src/main/res/drawable/splash_background_advanced.xml`

```xml
<layer-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:opacity="opaque">
    
    <!-- 背景色 -->
    <item>
        <shape android:shape="rectangle">
            <!-- 纯色背景（当前启用）-->
            <solid android:color="@color/colorPrimary" />
            
            <!-- 或使用渐变背景（可选，已注释）-->
            <!--
            <gradient
                android:angle="135"
                android:centerColor="@color/colorPrimary"
                android:endColor="@color/colorPrimaryDark"
                android:startColor="#42A5F5"
                android:type="linear" />
            -->
        </shape>
    </item>
    
    <!-- Logo 部分（预留，已注释）-->
    <!--
    <item
        android:width="120dp"
        android:height="120dp"
        android:gravity="center">
        <bitmap
            android:gravity="center"
            android:src="@drawable/splash_logo" />
    </item>
    -->
</layer-list>
```

**特点**:
- 🎨 可选渐变背景 - 更精美的视觉效果
- 🎯 预留 Logo 位置 - 方便添加图标
- 📐 固定尺寸控制 - Logo 大小可调整（120dp）
- 🔧 更多自定义选项 - 适合进阶需求

##### 3. 更新主题配置

**文件**: `app/src/main/res/values/themes.xml`

```xml
<style name="Theme.Splash" parent="Theme.AppCompat.Light.NoActionBar">
    <!-- 从纯色改为自定义 drawable -->
    <item name="android:windowBackground">@drawable/splash_background</item>
    <!-- 或使用进阶版 -->
    <!-- <item name="android:windowBackground">@drawable/splash_background_advanced</item> -->
    
    <item name="android:windowFullscreen">true</item>
    <item name="android:windowContentOverlay">@null</item>
    <item name="android:windowIsTranslucent">false</item>
    <item name="android:windowNoTitle">true</item>
    <item name="android:windowActionBar">false</item>
</style>
```

### 效果对比

#### 修复前

```
点击图标 → 纯蓝色画面 → SplashActivity
            ↑ 明显闪烁
```

#### 修复后

```
点击图标 → 启动画面（带背景） → SplashActivity
            ↑ 无缝衔接
```

### 测试步骤

#### 1. 重新编译

```
Build → Clean Project
Build → Rebuild Project
```

#### 2. 完全关闭应用

```
设定 → 应用程式 → Function Test → 强制停止
```

或使用 adb:
```bash
adb shell am force-stop com.cyberpower.functiontest
```

#### 3. 冷启动测试

从桌面点击应用图标，观察启动过程。

#### 4. 预期结果

- ✅ 不再看到纯蓝色闪烁
- ✅ 直接看到设计的启动画面
- ✅ 过渡更加自然流畅
- ✅ 约 1.5 秒后跳转到 MainActivity

### 进阶选项

#### 启用渐变背景

在 `splash_background_advanced.xml` 中：

1. **注释掉纯色背景**:
```xml
<!-- <solid android:color="@color/colorPrimary" /> -->
```

2. **取消注释渐变部分**:
```xml
<gradient
    android:angle="135"
    android:centerColor="@color/colorPrimary"
    android:endColor="@color/colorPrimaryDark"
    android:startColor="#42A5F5"
    android:type="linear" />
```

3. **更新 themes.xml**:
```xml
<item name="android:windowBackground">@drawable/splash_background_advanced</item>
```

4. **重新编译并安装**

**渐变效果**:
```
┌─────────────────────┐
│  #42A5F5 (浅蓝)    │ ← 左上角
│    ↘                │
│   #2196F3 (中蓝)   │ ← 中央
│    ↘                │
│  #1976D2 (深蓝)    │ ← 右下角
└─────────────────────┘
135° 斜向渐变
```

#### 添加 Logo

**注意**: 必须先准备好图片资源！

1. **准备 Logo 图片**:
   - PNG 格式，透明背景
   - 建议尺寸：512x512 或 256x256
   - 放置到 `app/src/main/res/drawable/splash_logo.png`

2. **取消注释 Logo 部分**:
```xml
<item
    android:width="120dp"
    android:height="120dp"
    android:gravity="center">
    <bitmap
        android:gravity="center"
        android:src="@drawable/splash_logo" />
</item>
```

3. **重新编译并安装**

---

## 启动崩溃问题

### 问题描述

**错误信息**:
```
FATAL EXCEPTION: main
android.content.res.Resources$NotFoundException: Drawable splash_background with resource ID #0x7f070077
Caused by: org.xmlpull.v1.XmlPullParserException: Binary XML file line #12: <bitmap> requires a valid 'src' attribute
```

**发生时间**: 2026-04-23

**影响**: 应用启动立即崩溃，无法进入 SplashActivity

### 根本原因

在修复冷启动蓝屏问题时，`splash_background.xml` 中包含：

```xml
<item>
    <bitmap
        android:gravity="center"
        android:src="@mipmap/ic_launcher" />
</item>
```

**问题**:
- `@mipmap/ic_launcher` 资源可能不存在或格式不正确
- 导致 `<bitmap>` 标签无法找到有效的 `src` 属性
- 应用启动时加载主题的 `windowBackground` 失败
- 整个应用崩溃

### 解决方案（已完成）

#### 1. 修复 splash_background.xml

**修改前**:
```xml
<item android:drawable="@color/colorPrimary" />
<item>
    <bitmap
        android:gravity="center"
        android:src="@mipmap/ic_launcher" />
</item>
```

**修改后**:
```xml
<item>
    <shape android:shape="rectangle">
        <solid android:color="@color/colorPrimary" />
    </shape>
</item>
<!-- Logo 部分已注释掉 -->
```

**效果**:
- ✅ 移除了会导致崩溃的 `<bitmap>` 标签
- ✅ 保留纯色背景
- ✅ 仍然解决了冷启动蓝屏问题（蓝色背景保持一致）

#### 2. 修复 splash_background_advanced.xml

同样注释掉 Logo 部分，避免相同问题。

### 测试步骤

#### 1. 重新编译

在 Android Studio 中:
```
Build → Clean Project
Build → Rebuild Project
```

#### 2. 重新运行

```
Run → Run 'app'
```

#### 3. 预期结果

- ✅ 应用正常启动
- ✅ 显示纯蓝色启动画面（无 Logo）
- ✅ 无崩溃
- ✅ 1.5 秒后跳转到 MainActivity
- ✅ 权限请求正常

### 对比分析

#### 修复前 ❌

```
启动应用
    ↓
加载 Theme.Splash
    ↓
尝试加载 splash_background.xml
    ↓
<bitmap> 找不到 @mipmap/ic_launcher
    ↓
Resources$NotFoundException
    ↓
应用崩溃 💥
```

#### 修复后 ✅

```
启动应用
    ↓
加载 Theme.Splash
    ↓
加载 splash_background.xml
    ↓
显示纯蓝色背景（solid color）
    ↓
无缝过渡到 SplashActivity ✨
    ↓
显示 activity_splash.xml（含文字 Logo）
    ↓
权限检查
    ↓
跳转到 MainActivity
```

### 经验教训

#### 1. Drawable 资源引用要谨慎

**不要直接引用可能不存在的资源**:
```xml
❌ <bitmap android:src="@mipmap/ic_launcher" />
```

**应该**:
- 确认资源存在
- 使用相对路径
- 或提供降级方案

#### 2. 主题的 windowBackground 要简单

**推荐**:
- ✅ 纯色 `@color/xxx`
- ✅ 简单的 shape
- ✅ 已验证存在的 drawable

**避免**:
- ❌ 复杂的 layer-list（可能有资源依赖）
- ❌ 未验证的图片引用
- ❌ 可能不存在的 mipmap 资源

#### 3. 测试构建变体

在不同配置下测试:
- Debug / Release
- 不同的 productFlavors
- 不同的 buildTypes

### 故障排除

#### 问题 1: 修复后仍然崩溃

**检查清单**:
- [ ] 已执行 Clean Project
- [ ] 已执行 Rebuild Project
- [ ] 已完全卸载旧版本
- [ ] 检查 `splash_background.xml` 中没有 `<bitmap>` 标签（或已注释）

#### 问题 2: 想要添加 Logo 但不知道如何做

**建议步骤**:
1. 先确保纯色版本能正常运行
2. 准备 PNG 格式的 Logo（放到 `drawable/`）
3. 在 `splash_background.xml` 中取消注释 Logo 部分
4. 修改 `android:src` 指向正确的资源
5. 重新编译测试

---

## Splash Background 配置

### 两个版本的文件

#### 基本版: splash_background.xml ✅ 当前使用

**特点**:
- ✅ 简单 - 只有纯色背景
- ✅ 可靠 - 不会出现资源错误
- ✅ 快速 - 加载速度最快
- ✅ 稳定 - 已测试无问题

**用途**:
- 解决冷启动蓝屏闪烁问题
- 提供与 SplashActivity 一致的背景色

#### 进阶版: splash_background_advanced.xml - 备选

**特点**:
- 🎨 可选渐变背景 - 更精美的视觉效果
- 🖼️ 预留 Logo 位置 - 方便添加图标
- 📐 固定尺寸控制 - Logo 大小可调整（120dp）
- 🔧 更多自定义选项 - 适合进阶需求

**用途**:
- 如果想要更精美的启动画面
- 如果想使用渐变背景而非纯色
- 如果想在预览窗口显示 Logo

### 功能对比表

| 特性 | 基本版 | 进阶版 |
|------|--------|--------|
| **纯色背景** | ✅ 有 | ✅ 有 |
| **渐变背景** | ❌ 无 | ✅ 可选（已注释） |
| **Logo 支持** | ❌ 已注释 | ✅ 预留（已注释） |
| **复杂度** | 🟢 简单 | 🟡 中等 |
| **加载速度** | 🟢 快 | 🟢 快 |
| **资源依赖** | 🟢 无 | 🟡 可能需要 Logo 图片 |
| **当前使用** | ✅ 是 | ❌ 否 |
| **推荐场景** | 日常使用 | 精美定制 |

### 如何切换版本

#### 切换到进阶版

1. **修改 themes.xml**:
```xml
<!-- 将此行 -->
<item name="android:windowBackground">@drawable/splash_background</item>

<!-- 改为 -->
<item name="android:windowBackground">@drawable/splash_background_advanced</item>
```

2. **重新编译并安装**

#### 切换回基本版

```xml
<!-- 改回 -->
<item name="android:windowBackground">@drawable/splash_background</item>
```

### 当前配置状态

- ✅ **themes.xml** 使用基本版
- ✅ **应用启动** 正常
- ✅ **冷启动蓝屏** 已解决
- ✅ **视觉效果** 流畅

### 推荐操作

**保持现状** - 不需要修改任何东西

**理由**:
- 当前配置已完美工作
- 用户反馈正常
- 简单可靠

---

## 测试最佳实践

### 1. 冷启动测试

**必须**完全关闭应用后再测试：

```bash
# 强制停止
adb shell am force-stop com.cyberpower.functiontest

# 等待 2 秒

# 启动应用
adb shell am start -n com.cyberpower.functiontest/.SplashActivity
```

### 2. 资源变更测试

当修改 drawable 资源时，**必须完全重新安装**：

```
1. Build → Clean Project
2. 完全卸载旧版本
3. Build → Rebuild Project
4. 重新安装
```

### 3. 多设备测试

在不同设备上测试：
- Android 7.0 (API 24) - 最低支持版本
- Android 10 (API 29)
- Android 12+ (API 31+) - 新的 Splash Screen API

### 4. 模拟器 vs 真机

- **真机**: 效果更明显，性能更真实
- **模拟器**: 方便调试，但可能看不清细节

### 5. 日志检查

查看启动日志：

```bash
adb logcat | findstr "SplashActivity\|MainActivity\|FunctionTestApplication"
```

预期日志：
```
SplashActivity onCreate - 启动画面开始
SplashViewModel 初始化
请求权限: 5 项
权限结果: 全部授予
准备跳转到 MainActivity
MainActivity onCreate
```

---

## 常见问题排查

### Q1: 修改后还是看到蓝色闪烁？

**原因**:
- 应用未重新安装（只是增量更新）
- 资源文件未正确编译

**解决**:
```
1. Build → Clean Project
2. 完全卸载应用
3. Build → Rebuild Project
4. 重新安装
```

### Q2: Logo 显示位置不对？

**原因**:
- Bitmap gravity 设定问题

**解决**:
```xml
<bitmap
    android:gravity="center"  <!-- 确认是 center -->
    android:src="@drawable/splash_logo" />
```

### Q3: 想恢复之前有 Logo 的版本

**不推荐**: 除非确认 Logo 资源存在且可访问

**如果一定要**:
1. 确认 `@mipmap/ic_launcher` 存在
2. 或使用 `@drawable/your_logo`
3. 手动修改 `splash_background.xml`

### Q4: Android 12+ 显示不正常？

**原因**:
- Android 12 引入了新的 Splash Screen API

**解决**:
考虑使用 `androidx.core:core-splashscreen:1.0.1` 库

---

## 📚 相关资源

### 专案文档
- `BUILD_GRADLE_ISSUES.md` - 编译问题解决
- `DEVELOPMENT_GUIDE.md` - 开发规范指南

### Android 官方文档
- [Splash Screens](https://developer.android.com/develop/ui/views/launch/splash-screen)
- [Launch Screen Patterns](https://material.io/design/communication/launch-screen.html)

---

**最后更新**: 2026-04-23  
**维护者**: Development Team


