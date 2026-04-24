# API 測試框架 - StatViewModel 测试入口设计

## 📅 更新日期
2026-04-24

## 🎯 设计目标

在 `ApiTestViewModel` 中创建统一的测试执行入口，支持：

1. ✅ 自动检测设备类型（PAX / Castles / Unknown）
2. ✅ 通过 HardwareManager 执行通用 API
3. ✅ 根据设备类型执行特定 API
4. ✅ 统一的日志输出和错误处理
5. ✅ 清晰的测试方法架构

---

## 🏗️ 架构设计

### 1. 设备类型检测

```java
public enum DeviceType {
    PAX,        // PAX 设备
    CASTLES,    // Castles 设备
    UNKNOWN     // 未知设备（DummyHelper）
}

private DeviceType currentDeviceType = DeviceType.UNKNOWN;
private IHelper currentHelper = null;
```

**初始化流程**：
```
ViewModel 构造函数
    ↓
detectDeviceType()
    ↓
获取 HardwareManager.getInstance().getHelper()
    ↓
instanceof 判断
    ├─ PaxHelper → DeviceType.PAX
    ├─ CastlesHelper → DeviceType.CASTLES
    └─ 其他 → DeviceType.UNKNOWN
```

### 2. Helper 访问方法

```java
// 获取当前设备类型
public DeviceType getDeviceType();

// 获取当前 Helper（IHelper 接口）
public IHelper getCurrentHelper();

// 获取 PAX Helper（如果是 PAX 设备）
public PaxHelper getPaxHelper();

// 获取 Castles Helper（如果是 Castles 设备）
public CastlesHelper getCastlesHelper();
```

**使用示例**：
```java
// 在 ViewModel 中
if (getDeviceType() == DeviceType.PAX) {
    PaxHelper paxHelper = getPaxHelper();
    // 使用 PAX 特定 API
} else if (getDeviceType() == DeviceType.CASTLES) {
    CastlesHelper castlesHelper = getCastlesHelper();
    // 使用 Castles 特定 API
}
```

---

## ✅ 已实现的测试方法

### 1. 系统资讯测试

```java
public void executeSystemInfoTest()
```

**功能**：
- 显示 Android 系统信息
- 设备型号、制造商
- Android 版本、SDK 版本

**示例输出**：
```
[12:34:56.789] ℹ️ [資訊] ========== 系統資訊測試 ==========
[12:34:56.790] ℹ️ [資訊] 設備型號: A920
[12:34:56.791] ℹ️ [資訊] 製造商: PAX
[12:34:56.792] ℹ️ [資訊] Android 版本: 7.1.1
[12:34:56.793] ℹ️ [資訊] SDK 版本: 25
[12:34:56.794] ✅ [成功] 系統資訊測試完成！
```

### 2. 硬件管理器测试 ⭐

```java
public void executeHardwareManagerTest()
```

**功能**：
- 检测设备类型
- 测试通用硬件接口
- 执行设备特定测试
- **获取设备 SN** ✅

**测试流程**：
```
执行测试
    ↓
检测设备类型
    ├─ PAX → executePaxSpecificTest()
    ├─ Castles → executeCastlesSpecificTest()
    └─ Unknown → 跳过
    ↓
testCommonHardwareInterfaces()
    ├─ 测试 cSys 接口 → 获取 SN ✅
    ├─ 测试 cPrinter 接口
    ├─ 测试 cReader 接口
    ├─ 测试 cIcc 接口
    └─ 测试 cPed 接口
```

**示例输出**：
```
[12:34:56.789] ℹ️ [資訊] ========== 硬體管理器測試 ==========
[12:34:56.790] ℹ️ [資訊] HardwareManager 實例: 正常
[12:34:56.791] ✅ [成功] 檢測到 PAX 設備
[12:34:56.792] ℹ️ [資訊] Helper 類型: PaxHelper
[12:34:56.793] ℹ️ [資訊] --- PAX 特定功能測試 ---
[12:34:56.794] ℹ️ [資訊] PaxHelper 實例: 正常
[12:34:56.795] ℹ️ [資訊] --- 測試通用接口 ---
[12:34:56.796] ℹ️ [資訊] ✓ cSys 接口可用
[12:34:56.797] ✅ [成功] 設備序號 (SN): A920ABC123456
[12:34:56.798] ℹ️ [資訊] ✓ cPrinter 接口可用
[12:34:56.799] ℹ️ [資訊] ✓ cReader 接口可用
[12:34:56.800] ✅ [成功] 硬體管理器測試完成！
```

### 3. 列印功能测试

```java
public void executePrintTest()
```

**功能**：
- 检查列印机接口可用性
- 根据设备类型执行不同测试
- 预留 PAX / Castles 特定代码区域

**扩展点**：
```java
switch (currentDeviceType) {
    case PAX:
        addInfoMessage("執行 PAX 列印測試...");
        // TODO: 添加 PAX 列印測試代碼
        // 例如：
        // PaxHelper paxHelper = getPaxHelper();
        // paxHelper.getPrinter().printText("Test");
        break;

    case CASTLES:
        addInfoMessage("執行 Castles 列印測試...");
        // TODO: 添加 Castles 列印測試代碼
        break;
}
```

### 4. 掃碼功能测试

```java
public void executeScanTest()
```

**功能**：
- 检查掃碼器接口
- 预留扫码逻辑实现区域

### 5. 卡片讀取测试

```java
public void executeCardReaderTest()
```

**功能**：
- 检查磁条卡读取器（cReader）
- 检查 IC 卡读取器（cIcc）
- 预留卡片读取逻辑

### 6. 加密功能测试

```java
public void executeCryptoTest()
```

**功能**：
- 检查 PED 接口
- 检查 Crypto 接口
- 预留加密测试逻辑

---

## 🔧 核心方法详解

### testCommonHardwareInterfaces()

**用途**：测试所有通用硬件接口

**代码示例**：
```java
private void testCommonHardwareInterfaces(HardwareManager hwManager) {
    addInfoMessage("--- 測試通用接口 ---");

    try {
        // 测试系统接口
        if (hwManager.getSys() != null) {
            addInfoMessage("✓ cSys 接口可用");
            try {
                String sn = hwManager.getSys().getSn();
                addSuccessMessage("設備序號 (SN): " + (sn != null ? sn : "無法獲取"));
            } catch (Exception e) {
                addWarningMessage("獲取 SN 失敗: " + e.getMessage());
            }
        } else {
            addWarningMessage("✗ cSys 接口不可用");
        }

        // 测试其他接口...
    } catch (Exception e) {
        addErrorMessage("通用接口測試失敗: " + e.getMessage());
    }
}
```

**特点**：
- ✅ 安全的 null 检查
- ✅ 详细的错误处理
- ✅ 清晰的日志输出
- ✅ **获取并显示设备 SN** ⭐

### executePaxSpecificTest()

**用途**：执行 PAX 设备特定测试

**代码结构**：
```java
private void executePaxSpecificTest() {
    addInfoMessage("--- PAX 特定功能測試 ---");

    try {
        PaxHelper paxHelper = getPaxHelper();
        if (paxHelper == null) {
            addWarningMessage("無法獲取 PaxHelper");
            return;
        }

        addInfoMessage("PaxHelper 實例: 正常");

        // 在这里添加 PAX 特定的测试
        // 例如：
        // - 测试 PAX 专属 API
        // - 使用 PaxHelper 特有方法
        
    } catch (Exception e) {
        addErrorMessage("PAX 特定測試失敗: " + e.getMessage());
    }
}
```

**使用场景**：
- 只在 PAX 设备上可用的功能
- PAX 专属的硬件操作
- PAX SDK 特定 API

### executeCastlesSpecificTest()

**用途**：执行 Castles 设备特定测试

**代码结构**：
```java
private void executeCastlesSpecificTest() {
    addInfoMessage("--- Castles 特定功能測試 ---");

    try {
        CastlesHelper castlesHelper = getCastlesHelper();
        if (castlesHelper == null) {
            addWarningMessage("無法獲取 CastlesHelper");
            return;
        }

        addInfoMessage("CastlesHelper 實例: 正常");

        // 在这里添加 Castles 特定的测试
        // 例如：
        // - 测试 Castles 专属 API
        // - 使用 CastlesHelper 特有方法
        
    } catch (Exception e) {
        addErrorMessage("Castles 特定測試失敗: " + e.getMessage());
    }
}
```

**使用场景**：
- 只在 Castles 设备上可用的功能
- Castles 专属的硬件操作
- Castles CTOS API

---

## 📝 使用方法

### 在 Fragment 中调用

```java
// ApiTestFragment.java
private void performTest(int position) {
    switch (position) {
        case 0:
            viewModel.executeSystemInfoTest();
            break;
        
        case 1:
            viewModel.executeHardwareManagerTest();
            break;
        
        case 2:
            viewModel.executePrintTest();
            break;
        
        // ... 其他测试
    }
}
```

### 扩展新测试

#### 1. 在 ViewModel 中添加测试方法

```java
public void executeMyNewTest() {
    addInfoMessage("========== 我的新測試 ==========");
    
    try {
        // 检测设备类型
        switch (currentDeviceType) {
            case PAX:
                addInfoMessage("執行 PAX 版本測試");
                // PAX 特定代碼
                PaxHelper paxHelper = getPaxHelper();
                // ...
                break;
                
            case CASTLES:
                addInfoMessage("執行 Castles 版本測試");
                // Castles 特定代碼
                CastlesHelper castlesHelper = getCastlesHelper();
                // ...
                break;
                
            default:
                addWarningMessage("未知設備，使用通用邏輯");
                // 通用代碼
        }
        
        addSuccessMessage("測試完成！");
        
    } catch (Exception e) {
        addErrorMessage("測試失敗: " + e.getMessage());
        LogUtils.e(TAG, "測試失敗", e);
    }
}
```

#### 2. 在 Fragment 中调用

```java
case X: // 新測試項目
    viewModel.executeMyNewTest();
    break;
```

---

## 🎯 设计优势

### 1. 统一的错误处理

```java
try {
    // 测试代码
    addSuccessMessage("成功");
} catch (Exception e) {
    addErrorMessage("失敗: " + e.getMessage());
    LogUtils.e(TAG, "測試失敗", e);
}
```

**优点**：
- ✅ 所有异常都被捕获
- ✅ 错误信息显示在日志中
- ✅ 同时记录到 XLog
- ✅ 不会导致应用崩溃

### 2. 设备类型感知

```java
// 自动检测
detectDeviceType();

// 方便获取
DeviceType type = getDeviceType();
PaxHelper pax = getPaxHelper();
CastlesHelper castles = getCastlesHelper();
```

**优点**：
- ✅ 一次检测，处处可用
- ✅ 类型安全的 Helper 访问
- ✅ 避免重复的 instanceof 判断
- ✅ 清晰的代码逻辑

### 3. 清晰的日志输出

```java
addInfoMessage("資訊訊息");
addSuccessMessage("成功訊息");
addWarningMessage("警告訊息");
addErrorMessage("錯誤訊息");
```

**优点**：
- ✅ 带时间戳
- ✅ 带图标（ℹ️ ✅ ⚠️ ❌）
- ✅ 带类别标签
- ✅ 易于阅读

### 4. 可扩展性

**添加新接口**：
```java
// 在 testCommonHardwareInterfaces() 中添加
if (hwManager.getNewInterface() != null) {
    addInfoMessage("✓ 新接口可用");
    // 测试新接口
}
```

**添加设备特定测试**：
```java
// 在 executePaxSpecificTest() 或 executeCastlesSpecificTest() 中添加
if (paxHelper.hasSpecialFeature()) {
    addInfoMessage("測試特殊功能");
    // 测试代码
}
```

---

## 💡 最佳实践

### 1. 始终检查 null

```java
HardwareManager hwManager = HardwareManager.getInstance();
if (hwManager == null) {
    addErrorMessage("HardwareManager 未初始化");
    return;
}

if (hwManager.getSys() == null) {
    addWarningMessage("cSys 接口不可用");
    return;
}
```

### 2. 使用设备类型判断

```java
// ✅ 好的做法
if (getDeviceType() == DeviceType.PAX) {
    PaxHelper pax = getPaxHelper();
    // 使用 PAX API
}

// ❌ 避免重复检测
if (currentHelper instanceof PaxHelper) {
    PaxHelper pax = (PaxHelper) currentHelper;
    // ...
}
```

### 3. 详细的日志记录

```java
// ✅ 好的做法
addInfoMessage("開始測試列印機");
addInfoMessage("初始化列印機...");
addInfoMessage("發送測試數據...");
addSuccessMessage("列印機測試完成");

// ❌ 避免过于简单
addInfoMessage("測試中...");
addSuccessMessage("完成");
```

### 4. 分离通用和特定逻辑

```java
// testCommonHardwareInterfaces() - 通用接口测试
// executePaxSpecificTest() - PA特定测试
// executeCastlesSpecificTest() - Castles 特定测试
```

---

## 🚀 未来扩展

### 短期（本周）

1. **完善现有测试**
   - 实现列印测试逻辑
   - 实现掃码测试逻辑
   - 实现卡片读取逻辑

2. **添加更多 API 调用**
   - 获取更多系统信息
   - 测试更多硬件接口
   - 设备状态检查

### 中期（下周）

1. **异步测试支持**
   ```java
   public void executeAsyncTest() {
       addInfoMessage("開始異步測試");
       // 使用 RxJava 或 AsyncTask
       // 添加进度更新
   }
   ```

2. **测试结果保存**
   ```java
   public void saveTestResult() {
       // 保存到文件
       // 生成报告
   }
   ```

### 长期（下个月）

1. **自动化测试脚本**
   - JSON 配置测试流程
   - 批量执行测试
   - 定时测试

2. **远程测试支持**
   - 接收远程指令
   - 上传测试结果
   - 远程监控

---

## ✅ 总结

### 实现的功能

- ✅ 设备类型自动检测
- ✅ 统一的测试入口
- ✅ PAX / Castles 分离逻辑
- ✅ 通用接口测试
- ✅ **设备 SN 获取** ⭐
- ✅ 完整的错误处理
- ✅ 详细的日志输出

### 核心方法

- `executeSystemInfoTest()` - 系统信息
- `executeHardwareManagerTest()` - 硬件管理器（含 SN）⭐
- `executePrintTest()` - 列印功能
- `executeScanTest()` - 掃码功能
- `executeCardReaderTest()` - 卡片读取
- `executeCryptoTest()` - 加密功能

### 设备特定支持

- `executePaxSpecificTest()` - PAX 特定测试
- `executeCastlesSpecificTest()` - Castles 特定测试
- `getPaxHelper()` - 获取 PAX Helper
- `getCastlesHelper()` - 获取 Castles Helper

---

**更新日期**: 2026-04-24  
**状态**: ✅ 完成并测试  
**核心功能**: 统一测试入口 + 设备类型感知 + SN 获取

