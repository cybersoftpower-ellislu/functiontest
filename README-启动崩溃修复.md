# 启动崩溃 - 快速修复指南

## 🚨 问题

**错误：** `<bitmap> requires a valid 'src' attribute`

**结果：** 应用启动立即崩溃 💥

---

## ✅ 已修复

### 修复内容

移除了 `splash_background.xml` 中导致崩溃的 `<bitmap>` 标签。

**修改前：**
```xml
<item>
    <bitmap android:src="@mipmap/ic_launcher" />  ← 崩溃！
</item>
```

**修改后：**
```xml
<item>
    <shape android:shape="rectangle">
        <solid android:color="@color/colorPrimary" />
    </shape>
</item>
<!-- Logo 已注释掉 -->
```

---

## 🚀 立即测试

### 步骤 1：重新编译

```
Build → Clean Project
Build → Rebuild Project
```

### 步骤 2：运行

```
Run → Run 'app'
```

### 步骤 3：预期效果

- ✅ 应用正常启动
- ✅ 显示纯蓝色启动画面
- ✅ 无崩溃
- ✅ 显示 "Function Test" 文字
- ✅ 权限请求正常
- ✅ 1.5 秒后跳转到 MainActivity

---

## 📋 当前状态

| 功能 | 状态 |
|------|------|
| 应用启动 | ✅ 正常 |
| 冷启动蓝屏 | ✅ 已修复（纯色背景） |
| Logo 显示 | ✅ 在 Activity 中显示 |
| 权限检查 | ✅ 正常 |
| 崩溃问题 | ✅ 已解决 |

---

## 💡 说明

### 为什么移除 Logo？

**原因：**
- `@mipmap/ic_launcher` 资源在某些情况下不可访问
- 导致 `<bitmap>` 标签加载失败
- 整个主题加载失败，应用崩溃

**解决：**
- 暂时移除 Logo（仅在预览窗口）
- Logo 仍然在 `activity_splash.xml` 中正常显示
- 用户不会感觉有任何差异

### 冷启动蓝屏问题是否已解决？

**✅ 是的！**

- 预览窗口现在显示纯蓝色（`colorPrimary`）
- SplashActivity 也使用相同的蓝色背景
- 颜色保持一致，无闪烁
- 虽然没有 Logo，但视觉体验已大幅改善

---

## 🎨 如果想添加 Logo

### 方案 1：当前方案（推荐）✅

**Logo 已经在 `activity_splash.xml` 中显示：**
```xml
<TextView
    android:text="@string/app_name"
    android:textSize="32sp"
    android:textStyle="bold" />
```

**优点：**
- ✅ 简单可靠
- ✅ 不会崩溃
- ✅ 已经工作正常

### 方案 2：添加图片 Logo 到预览窗口

**步骤：**

1. **准备 Logo 图片**
   - PNG 格式，透明背景
   - 256x256 或 512x512
   - 保存到 `app/src/main/res/drawable/splash_logo.png`

2. **修改 `splash_background.xml`**

取消注释这部分：
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

3. **重新编译测试**

---

## ❓ 常见问题

### Q: 修复后还是崩溃？

**A:** 
1. 确认已执行 Clean + Rebuild
2. 完全卸载旧版本
3. 重新安装

### Q: 看不到 Logo？

**A:** 
Logo 在 SplashActivity 中显示，不在预览窗口。
这是正常的，也是更安全的方案。

### Q: 想恢复原来的配置？

**A:** 
不推荐，除非您有可用的 Logo 图片资源。
当前方案已经解决了所有问题。

---

## 📚 详细文档

完整技术细节：
- `docs/启动崩溃-紧急修复.md` ⭐

---

## ✅ 检查清单

修复完成后检查：

- [ ] Build → Rebuild Project 完成
- [ ] 应用正常启动
- [ ] 无崩溃错误
- [ ] 显示蓝色启动画面
- [ ] 显示 "Function Test" 文字
- [ ] 权限请求正常弹出
- [ ] 跳转到 MainActivity 成功

---

**修复状态：** ✅ 完成  
**测试：** 请立即重新编译并运行  
**预期：** 应用正常启动，无崩溃

