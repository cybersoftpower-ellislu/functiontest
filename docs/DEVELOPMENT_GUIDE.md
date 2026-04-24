# 開發規範與指南

> **文档说明**: 本文档整合所有开发规范、功能实现、架构设计等开发相关指南
> 
> **最后更新**: 2026-04-23

---

## 📋 目录

- [专案架构](#专案架构)
- [MainActivity 实现](#mainactivity-实现)
- [SplashActivity 实现](#splashactivity-实现)
- [UI 组件规范](#ui-组件规范)
- [Git 工作流程](#git-工作流程)

---

## 专案架构

### 模块结构

```
functiontest/
├── app/              # 最小测试应用 (com.cyberpower.functiontest)
└── core/             # 共享库 (com.cyberpower.edc.core) - Git submodule
```

### MVVM 架构模式

所有 Activities/Fragments 使用 MVVM 架构：

```java
// Activity/Fragment 继承 Base 类别
BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel>
BaseFragment<V extends ViewDataBinding, VM extends BaseViewModel>

// ViewModel 使用 SingleLiveEvent 处理一次性事件
public class MainViewModel extends BaseViewModel {
    public SingleLiveEvent<String> toastEvent = new SingleLiveEvent<>();
    public SingleLiveEvent<String> navigationEvent = new SingleLiveEvent<>();
}

// Activity 观察 ViewModel 事件
viewModel.toastEvent.observe(this, message -> {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
});
```

### 硬件抽象层

Multi-vendor 设备支援透过策略模式：

```
HardwareManager.getInstance() // Singleton 在 Core.init() 初始化
  -> IHelper 实现:
     - PaxHelper (PAX, lephone 制造商)
     - CastlesHelper (Castles 制造商)  
     - DummyHelper (未知设备的后备方案)
```

设备检测透过 `Build.MANUFACTURER` 在 `HardwareManager.InitDevice()` 中进行。

---

## MainActivity 实现

### 文件结构

已创建的文件：
- `MainActivity.java` - 主 Activity，继承 BaseActivity
- `MainViewModel.java` - ViewModel，处理业务逻辑
- `DrawerAdapter.java` - 两层 Drawer 导航适配器
- `activity_main.xml` - 主画面 layout (DataBinding + DrawerLayout)
- `drawer_group_item.xml` - Drawer 群组项目 layout
- `drawer_child_item.xml` - Drawer 子项目 layout
- `ic_menu.xml` - 选单图标

### 功能特性

#### 1. MVVM 架构
```java
public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    @Override
    protected int bindContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected int bindViewModelId() {
        return BR.viewModel;
    }
}
```

#### 2. 两层 Drawer 导航

导航结构：
```
硬體測試
├── 列印測試
├── 掃碼測試
└── 卡片讀取測試
系統測試
├── 網路測試
├── 儲存測試
└── 螢幕測試
設定
關於
─────────────── (分隔线)
離開 (固定在底部，红色)
```

实现方式：
```xml
<!-- ExpandableListView 佔據中間空間 -->
<ExpandableListView
    android:id="@+id/expandable_list_view"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="1" />  <!-- 关键：使用 weight -->

<!-- 分隔線 -->
<View
    android:layout_width="match_parent"
    android:layout_height="1dp"
    android:background="#E0E0E0" />

<!-- 離開按鈕（固定在底部）-->
<TextView
    android:id="@+id/btn_exit"
    android:layout_width="match_parent"
    android:layout_height="56dp"
    android:text="@string/exit_app"
    android:textColor="@android:color/holo_red_dark" />
```

#### 3. 动态 Action Bar 标题

实现流程：
```
点击选单项目 → ViewModel.onNavigationItemClick()
              → titleUpdateEvent.setValue(itemName)
              → MainActivity 观察者收到通知
              → 更新 Toolbar 标题
```

代码实现：
```java
// MainViewModel.java
public SingleLiveEvent<String> titleUpdateEvent = new SingleLiveEvent<>();

public void onNavigationItemClick(String itemName) {
    navigationEvent.setValue(itemName);
    titleUpdateEvent.setValue(itemName);  // 触发标题更新
}

// MainActivity.java
viewModel.titleUpdateEvent.observe(this, title -> {
    if (title != null) {
        binding.toolbarTitle.setText(title);  // 使用自定义 TextView
    }
});
```

#### 4. Toolbar 标题置中显示

实现方法：
```xml
<androidx.appcompat.widget.Toolbar>
    <!-- 自定義標題 TextView（置中顯示）-->
    <TextView
        android:id="@+id/toolbar_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:text="@string/function_test_title"
        android:textColor="@android:color/white"
        android:textSize="20sp"
        android:textStyle="bold" />
</androidx.appcompat.widget.Toolbar>
```

更新标题：
```java
// 隐藏 Toolbar 预设标题
binding.toolbar.setTitle("");

// 使用自定义 TextView
binding.toolbarTitle.setText(R.string.function_test_title);
```

#### 5. 离开应用功能

双重确认机制：
```java
private void exitApp() {
    new AlertDialog.Builder(this)
        .setTitle("確認離開")
        .setMessage("確定要離開應用程式嗎？")
        .setPositiveButton("確定", (dialog, which) -> {
            AppManager.getAppManager().finishAllActivity();
            finish();
            System.exit(0);
        })
        .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
        .show();
}
```

#### 6. 快速点击保护

使用 Singleton 模式：
```java
protected void onClickProtected(View view, View.OnClickListener listener) {
    if (!quickClickProtection.isDoubleClick()) {
        listener.onClick(view);
    }
}
```

#### 7. 基于设备类型的动态选单 ⭐ NEW

**更新日期**: 2026-04-24

根据 HardwareManager 的 Helper 类型动态启用/禁用选单项目。

**选单结构**:
```
├── PAX (根据设备启用/禁用)
│   ├── 列印測試
│   ├── 掃碼測試
│   └── 卡片讀取測試
├── CASTLES (根据设备启用/禁用)
│   ├── 列印測試
│   ├── 掃碼測試
│   └── 卡片讀取測試
├── 設定 (始終啟用)
└── 關於 (始終啟用)
```

**设备检测逻辑**:
```java
private void initDrawerData() {
    // 检测当前设备类型
    boolean isPaxDevice = false;
    boolean isCastlesDevice = false;

    HardwareManager hwManager = HardwareManager.getInstance();
    if (hwManager.getHelper() instanceof PaxHelper) {
        isPaxDevice = true;
    } else if (hwManager.getHelper() instanceof CastlesHelper) {
        isCastlesDevice = true;
    }

    // 根据设备类型设置启用状态
    DrawerGroup paxGroup = new DrawerGroup("PAX", isPaxDevice);
    DrawerGroup castlesGroup = new DrawerGroup("CASTLES", isCastlesDevice);
}
```

**启用状态规则**:
| 设备类型 | Helper | PAX 选单 | CASTLES 选单 |
|---------|--------|---------|-------------|
| PAX/lephone | PaxHelper | ✅ 启用 | ❌ 禁用 |
| Castles | CastlesHelper | ❌ 禁用 | ✅ 启用 |
| 其他 | DummyHelper | ❌ 禁用 | ❌ 禁用 |

**视觉效果**:
- 启用: 黑色文字，不透明 (alpha = 1.0)
- 禁用: 灰色文字 (#999999)，40% 透明度 (alpha = 0.4)

**点击事件处理**:
```java
// 禁用的项目不响应点击
binding.expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
    DrawerGroup group = drawerData.get(groupPosition);
    if (!group.enabled) {
        return true; // 拦截点击
    }
    // ...处理点击
});
```

**详细文档**: 见 `docs/Drawer_Menu_Device_Based.md`

### 权限处理

在 SplashActivity 中统一检查和请求，MainActivity 无需再处理权限。

---

## SplashActivity 实现

### 功能说明

解决**冷启动白屏**问题，提供品牌展示和权限检查入口。

### 文件结构

- `SplashActivity.java` - 启动 Activity
- `SplashViewModel.java` - ViewModel，处理权限逻辑
- `activity_splash.xml` - Splash 布局
- `Theme.Splash` - 全屏主题（在 themes.xml 中）

### MVVM 架构实现

```java
public class SplashActivity extends BaseActivity<ActivitySplashBinding, SplashViewModel> {
    // 最短显示时间
    private static final long MIN_SPLASH_DISPLAY_TIME = 1500; // 1.5秒
    private long splashStartTime;

    @Override
    protected int bindContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected int bindViewModelId() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        splashStartTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        
        // 检查并请求权限
        viewModel.checkAndRequestPermissions(this);
    }
}
```

### 权限检查流程

```
应用启动
    ↓
SplashActivity (启动画面)
    ↓
检查 5 项权限并请求
    ↓
[权限授予]
    ↓
延迟 1.5 秒（确保最短显示时间）
    ↓
跳转到 MainActivity
```

需要检查的权限：
1. `CAMERA` - 相机
2. `READ_EXTERNAL_STORAGE` - 读取储存
3. `WRITE_EXTERNAL_STORAGE` - 写入储存
4. `ACCESS_FINE_LOCATION` - 精确位置
5. `ACCESS_COARSE_LOCATION` - 粗略位置

### 事件处理

```java
// SplashViewModel.java
public SingleLiveEvent<List<String>> permissionCheckCompleteEvent = new SingleLiveEvent<>();
public SingleLiveEvent<Void> navigateToMainEvent = new SingleLiveEvent<>();

public void onPermissionResult(boolean allGranted, List<String> deniedPermissions) {
    if (allGranted) {
        LogUtils.d(TAG, "权限结果: 全部授予");
    } else {
        LogUtils.w(TAG, "被拒绝的权限: " + deniedPermissions);
    }
    startNavigation();
}

private void startNavigation() {
    // 延迟后跳转到 MainActivity
    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        navigateToMainEvent.call();
    }, 500);
}
```

### 布局设计

```xml
<layout>
    <data>
        <variable name="viewModel" type="...SplashViewModel" />
    </data>
    
    <androidx.constraintlayout.widget.ConstraintLayout
        android:background="@color/colorPrimary">
        
        <TextView
            android:id="@+id/tv_app_name"
            android:text="@string/app_name"
            android:textSize="32sp"
            android:textStyle="bold" />
        
        <TextView
            android:text="@string/splash_subtitle"
            android:textSize="16sp" />
        
        <ProgressBar
            android:indeterminate="true" />
        
        <TextView
            android:text="@string/splash_version"
            android:textSize="12sp" />
    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>
```

### 主题配置

```xml
<style name="Theme.Splash" parent="Theme.AppCompat.Light.NoActionBar">
    <item name="android:windowBackground">@drawable/splash_background</item>
    <item name="android:windowFullscreen">true</item>
    <item name="android:windowContentOverlay">@null</item>
    <item name="android:windowIsTranslucent">false</item>
    <item name="android:windowNoTitle">true</item>
    <item name="android:windowActionBar">false</item>
</style>
```

### AndroidManifest 配置

```xml
<!-- Splash Screen - 启动画面 -->
<activity
    android:name=".SplashActivity"
    android:exported="true"
    android:theme="@style/Theme.Splash">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<!-- Main Activity -->
<activity
    android:name=".MainActivity"
    android:exported="false" />
```

---

## UI 组件规范

### DataBinding 使用

#### 启用 DataBinding

```groovy
// app/build.gradle
android {
    buildFeatures {
        dataBinding true
    }
}
```

#### Layout 结构

```xml
<layout xmlns:android="...">
    <data>
        <variable
            name="viewModel"
            type="com.cyberpower.functiontest.MainViewModel" />
    </data>
    
    <androidx.drawerlayout.widget.DrawerLayout>
        <!-- UI 内容 -->
    </androidx.drawerlayout.widget.DrawerLayout>
</layout>
```

#### Activity 中绑定

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // DataBinding 自动完成
    // binding 和 viewModel 已通过 BaseActivity 注入
}
```

### 快速点击保护

所有点击事件应使用保护机制：

```java
// 在 BaseActivity 中使用
protected void onClickProtected(View view, View.OnClickListener listener) {
    if (!quickClickProtection.isDoubleClick()) {
        listener.onClick(view);
    }
}

// 实际使用
binding.btnTest.setOnClickListener(v -> onClickProtected(v, view -> {
    // 点击处理逻辑
}));
```

### SingleLiveEvent 模式

用于一次性事件（避免配置变更时重复触发）：

```java
// ViewModel 中定义
public SingleLiveEvent<String> toastEvent = new SingleLiveEvent<>();
public SingleLiveEvent<Void> finishEvent = new SingleLiveEvent<>();

// 触发事件
toastEvent.setValue("操作成功");
finishEvent.call();

// Activity 中观察
viewModel.toastEvent.observe(this, message -> {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
});

viewModel.finishEvent.observe(this, unused -> {
    finish();
});
```

### 日志规范

使用 XLog 而非 Android Log：

```java
// 自动配置在 Core.init() 中
XLog.d("message");

// 或使用 LogUtils 包装
LogUtils.d(TAG, "调试信息");
LogUtils.e(TAG, "错误信息");
LogUtils.w(TAG, "警告信息");
```

日志自动保存到：`/sdcard/xlog/com.cyberpower.functiontest/`
- 自动轮转于 10MB
- 文件夹超过 500MB 时清理

### 系统 UI 控制

```java
// 在 BaseActivity.onResume() 中自动隐藏
protected void hideSystemUI() {
    getWindow().getDecorView().setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LOW_PROFILE
        | View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    );
}

// 禁用电源键
protected void enablePowerKey(boolean enable) {
    if (enable) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    } else {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }
}
```

---

## Git 工作流程

### 双重提交工作流（Core Submodule）

当修改 `core/` 目录下的代码时，需要双重提交：

#### 步骤 1: 在 Core 仓库提交

```bash
# 进入 core 目录
cd core

# 检查修改
git status
git diff

# 添加所有修改的文件
git add .

# 提交（使用有意义的讯息）
git commit -m "功能描述

详细说明：
- 修改内容 1
- 修改内容 2"

# 推送到 Core 远端仓库
git push origin main
```

#### 步骤 2: 在主专案更新 Submodule 指标

```bash
# 回到主专案根目录
cd ..

# 检查 core submodule 的变更
git status
# 应该会看到：modified: core (new commits)

# 添加 submodule 指标更新
git add core

# 提交主专案
git commit -m "更新 core submodule: 功能描述"

# 推送到主专案远端仓库
git push origin main
```

### 团队成员同步 Submodule

其他开发者需要执行：

```bash
# 拉取主专案
git pull

# 更新 submodule
git submodule update --init --recursive

# 在 Android Studio 执行
# File → Sync Project with Gradle Files
```

### 初始设定

```bash
# Clone 时包含 submodule
git clone --recurse-submodules https://github.com/cybersoftpower-ellislu/functiontest.git

# 如果 core/ 是空的
git submodule update --init
```

### 更新 Submodule 到最新版本

```bash
# 更新 core 到最新 commit
git submodule update --remote

# 然后在 Android Studio 执行
# File → Sync Project with Gradle Files
```

### 使用 GitHub Desktop

1. **切换仓库**: 在左上角下拉选单中选择 `core` 或 `functiontest`
2. **提交 Core 修改**: 
   - 选择 `core` 仓库
   - Commit & Push
3. **提交主专案**: 
   - 选择 `functiontest` 仓库
   - 会看到 "core (new commits)" 变更
   - Commit & Push

---

## 扩展功能指南

### 添加新的测试项目

在 `MainActivity.initDrawerData()` 中：

```java
DrawerGroup newGroup = new DrawerGroup("新测试类别");
newGroup.addChild("测试项目1");
newGroup.addChild("测试项目2");
drawerData.add(newGroup);
```

### 实作测试逻辑

在 `MainViewModel.performTest()` 中：

```java
public void performTest(String testName) {
    switch (testName) {
        case "列印测试":
            // 呼叫 HardwareManager 进行列印测试
            break;
        case "扫码测试":
            // 启动扫码功能
            break;
        default:
            toastEvent.setValue("测试项目: " + testName);
            break;
    }
}
```

### 导航到其他 Activity

透过 ViewModel 触发导航：

```java
// 在 MainViewModel
public SingleLiveEvent<Class<?>> navigateEvent = new SingleLiveEvent<>();

public void navigateToTest(String testName) {
    navigateEvent.setValue(TestActivity.class);
}

// 在 MainActivity
viewModel.navigateEvent.observe(this, activityClass -> {
    Intent intent = new Intent(this, activityClass);
    startActivity(intent);
});
```

---

## 开发最佳实践

### 1. 代码组织

- 所有 Activity 继承 `BaseActivity`
- 所有 Fragment 继承 `BaseFragment` 或 `RxFragment`
- ViewModel 统一使用 `SingleLiveEvent` 处理一次性事件
- 使用 DataBinding 避免 findViewById

### 2. 资源管理

- 字符串资源化（使用 `strings.xml`）
- 颜色定义在 `colors.xml`
- 尺寸定义在 `dimens.xml`
- 避免硬编码

### 3. 生命周期管理

- RxJava disposables 自动管理 via `CompositeDisposable`
- `ProgressObserver` 自动绑定到 Activity 生命周期
- 所有 Fragments 使用 `RxFragment` 实现 lifecycle-aware Rx subscriptions

### 4. 硬件访问

**必须**透过 HardwareManager 抽象：

```java
// ✅ 正确
HardwareManager.getInstance().getHelper().printer.print(data);

// ❌ 错误（直接调用厂商 SDK）
PaxPrinter.print(data);
```

### 5. 错误处理

```java
try {
    // 操作
} catch (Exception e) {
    LogUtils.e(TAG, "错误描述", e);
    toastEvent.setValue("操作失败: " + e.getMessage());
}
```

### 6. Chinese Comments

项目中的注释和字符串主要使用繁体中文，这是**有意设计**，不是 bug。

---

## 配置参考

### Build 配置

```groovy
android {
    compileSdk 34
    
    defaultConfig {
        minSdk 24        // app 模块
        // minSdk 22     // core 模块
        targetSdk 34
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    
    buildFeatures {
        dataBinding true
    }
}
```

### 依赖版本

- **RxJava**: 3.x
- **Retrofit**: 2.9+
- **OkHttp**: 4.x
- **XLog**: elvishew
- **ZXing**: 3.x
- **Guava, Gson, BouncyCastle**: latest stable

---

## 📚 相关资源

### 专案文档
- `AGENTS.md` - 完整架构说明
- `README.md` - 专案主文档
- `BUILD_GRADLE_ISSUES.md` - 编译问题解决

### Android 官方文档
- [MVVM Architecture](https://developer.android.com/jetpack/guide)
- [DataBinding Guide](https://developer.android.com/topic/libraries/data-binding)
- [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)

---

**最后更新**: 2026-04-23  
**维护者**: Development Team


