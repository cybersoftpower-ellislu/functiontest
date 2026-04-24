# Device Package 快速修復指南

> **最后更新**: 2026-04-23  
> **適用對象**: 開發者立即執行  
> **預計時間**: 2-4 小時

---

##  必須立即修復的問題 (Critical)

### 修復 1: HardwareManager 添加缺少的方法 ⭐⭐⭐

**文件**: `core/src/main/java/com/cyberpower/edc/core/device/hardware/HardwareManager.java`

**問題**: DeviceUtils.eraseKeys() 調用了不存在的方法

**在第 131 行後添加**:

```java
    public cPed getPed() {
        if (mHelper == null) {
            return null;
        }
        return mHelper.getPed();
    }

    public cCrypto getCrypto() {
        if (mHelper == null) {
            return null;
        }
        return mHelper.getCrypto();
    }
```

---

### 修復 2: 移除靜態 mDal 避免內存洩漏 ⭐⭐⭐

**文件**: `core/src/main/java/com/cyberpower/edc/core/device/hardware/pax/PaxHelper.java`

**修改第 31 行**:

```java
// 修改前
public static IDAL mDal;

// 修改後
private IDAL mDal;  // ✅ 改為非靜態
```

---

### 修復 3: 改善 bindService 異常處理 ⭐⭐⭐

**文件**: `core/src/main/java/com/cyberpower/edc/core/device/hardware/pax/PaxHelper.java`

**替換 bindService 方法** (第 52-56 行):

```java
@Override
public void bindService(Context context) throws Exception {
    if (context == null) {
        throw new IllegalArgumentException("Context cannot be null");
    }
    
    mContext = context;
    try {
        mDal = NeptuneLiteUser.getInstance().getDal(mContext);
        if (mDal == null) {
            throw new IllegalStateException("Failed to initialize IDAL - NeptuneLiteUser returned null");
        }
        LogUtils.d(TAG, "PAX service bound successfully");
    } catch (Exception e) {
        LogUtils.e(TAG, "Failed to bind PAX service", e);
        throw new IllegalStateException("PAX service binding failed", e);
    }
}
```

---

##  建議盡快修復的問題 (High Priority)

### 修復 4: 添加線程安全保護

**文件**: `core/src/main/java/com/cyberpower/edc/core/device/hardware/pax/PaxHelper.java`

**將所有 get 方法改為 synchronized**:

```java
@Override
public synchronized cSys getSys() {
    if (null == mSys) {
        if (mDal == null) {
            throw new IllegalStateException("Service not bound. Call bindService() first.");
        }
        mSys = new PaxSys(mDal.getSys());
    }
    return mSys;
}

@Override
public synchronized cKeyboard getKeyboard() {
    if (null == mKeyBoard) {
        if (mDal == null) {
            throw new IllegalStateException("Service not bound. Call bindService() first.");
        }
        mKeyBoard = new PaxKeyboard(mDal.getKeyBoard());
    }
    return mKeyBoard;
}

// ... 其他方法類似處理
```

**同樣修改 CastlesHelper**:

```java
// 文件: CastlesHelper.java
@Override
public synchronized cSys getSys() {
    if(null == mSys) {
        mSys = new CastlesSys(mMap);
    }
    return mSys;
}

// ... 其他方法類似
```

---

### 修復 5: 改善設備檢測邏輯

**文件**: `core/src/main/java/com/cyberpower/edc/core/device/hardware/HardwareManager.java`

**替換 InitDevice 方法** (第 47-61 行):

```java
private static void InitDevice() {
    Log.d(TAG, "Devices實例化: [" + mManufacturer + " - "+ mModel + "]");
    
    try {
        switch (mManufacturer) {
            case "PAX":
            case "lephone":  // PAX A50 設備使用 "lephone" 作為 Build.MANUFACTURER
                             // 這是 PAX 的特殊配置，並非錯誤
                mHelper = new PaxHelper();
                LogUtils.d(TAG, "使用 PaxHelper 處理設備");
                break;
                
            case "Castles":
                mHelper = new CastlesHelper();
                LogUtils.d(TAG, "使用 CastlesHelper 處理設備");
                break;
                
            default:
                LogUtils.w(TAG, "未支援的設備製造商: [" + mManufacturer + "], 型號: [" + mModel + "], 使用 DummyHelper");
                mHelper = new DummyHelper();
        }
        
        mHelper.init();
        
    } catch (Exception e) {
        LogUtils.e(TAG, "設備初始化失敗", e);
        mHelper = new DummyHelper();
        try {
            mHelper.init();
        } catch (Exception ex) {
            LogUtils.e(TAG, "DummyHelper 初始化也失敗", ex);
        }
    }
}
```

---

### 修復 6: DeviceUtils 添加空指針保護

**文件**: `core/src/main/java/com/cyberpower/edc/core/device/DeviceUtils.java`

**修改關鍵方法**:

```java
public static void beep() {
    cSys sys = HardwareManager.getInstance().getSys();
    if (sys == null) {
        LogUtils.w(TAG, "System functions not available on this device");
        return;  // 靜默失敗
    }
    sys.beep();
}

public static void enablePowerKey(boolean enable) {
    LogUtils.d(TAG, "enablePowerKey [" + enable + "]");
    cSys sys = HardwareManager.getInstance().getSys();
    if (sys == null) {
        LogUtils.w(TAG, "Cannot enable power key: System functions not available");
        return;
    }
    sys.enablePowerKey(enable);
}

public static void eraseKeys() {
    IHelper helper = HardwareManager.getInstance().getHelper();
    if (helper == null) {
        throw new IllegalStateException("Hardware helper not initialized");
    }
    
    cPed ped = helper.getPed();
    if (ped == null) {
        throw new UnsupportedOperationException("PED not available on this device");
    }
    
    ped.eraseKey();
}
```

---

## ⚠️ 可選但建議的改進

### 改進 1: 改善 CastlesHelper 的功能缺失提示

**文件**: `core/src/main/java/com/cyberpower/edc/core/device/hardware/castles/CastlesHelper.java`

**添加標誌位避免重複日誌**:

```java
public class CastlesHelper implements IHelper {
    private final String TAG = getClass().getSimpleName();
    
    private HashMap<String, String> mMap = new HashMap<>();
    
    // ✅ 添加標誌位
    private boolean scannerWarningLogged = false;
    private boolean commManagerWarningLogged = false;
    private boolean exDevWarningLogged = false;
    
    // ... 現有字段 ...

    @Override
    public cScannerHw getScannerHw() {
        if (!scannerWarningLogged) {
            LogUtils.w(TAG, "Castles 設備不支援 ScannerHw 功能");
            scannerWarningLogged = true;
        }
        return null;
    }

    @Override
    public cCommManager getCommManager() {
        if (!commManagerWarningLogged) {
            LogUtils.w(TAG, "Castles 設備不支援 CommManager (MODEM) 功能");
            commManagerWarningLogged = true;
        }
        return null;
    }

    @Override
    public cExDev getExDev() {
        if (!exDevWarningLogged) {
            LogUtils.w(TAG, "Castles 設備不支援 ExDev 功能");
            exDevWarningLogged = true;
        }
        return null;
    }
}
```

---

### 改進 2: 改善 PaxHelper.getExDev() 的註釋

**文件**: `core/src/main/java/com/cyberpower/edc/core/device/hardware/pax/PaxHelper.java`

**替換 getExDev 方法** (第 98-106 行):

```java
@Override
public cExDev getExDev() {
    // ExDev (External Device) 功能僅在 PAX A80 設備上可用
    // 其他 PAX 設備（如 A920、A50 等）不支持外部設備擴展接口
    if(Build.MODEL.equals(A80)) {
        if (null == mExDev) {
            mExDev = new PaxExDev(mContext);
        }
        return mExDev;
    }
    
    // 非 A80 設備返回 null
    return null;
}
```

---

### 改進 3: 移除未使用的 retry 變量

**文件**: `core/src/main/java/com/cyberpower/edc/core/device/hardware/pax/PaxHelper.java`

**刪除第 33 和 49 行**:

```java
// 刪除
private int retry;

// 刪除 init() 方法中的
this.retry = 0;
```

或者添加 TODO 註釋：

```java
private int retry;  // TODO: 實現服務綁定重試機制

@Override
public void init() {
    this.retry = 0;  // 預留給未來的重試邏輯
}
```

---

##  修復檢查清單

執行修復後，請檢查以下項目：

### Phase 1: 編譯檢查
- [ ] Clean Project
- [ ] Rebuild Project
- [ ] 確認無編譯錯誤
- [ ] 檢查是否有新的警告

### Phase 2: 代碼審查
- [ ] HardwareManager 新增的方法已添加
- [ ] PaxHelper.mDal 不再是 static
- [ ] 所有 bindService 都有異常處理
- [ ] get 方法都有 synchronized 或 null 檢查

### Phase 3: 功能測試
- [ ] 在 PAX 設備上測試
  - [ ] 設備初始化成功
  - [ ] 列印功能正常
  - [ ] 讀卡功能正常
  - [ ] PED 功能正常
  
- [ ] 在 Castles 設備上測試
  - [ ] 設備初始化成功
  - [ ] 列印功能正常
  - [ ] 讀卡功能正常
  - [ ] 不支持功能會正確提示

- [ ] 在不認識的設備上測試
  - [ ] 使用 DummyHelper
  - [ ] 不會崩潰
  - [ ] 日誌有清楚警告

### Phase 4: 內存測試
- [ ] 使用 Android Profiler 檢查內存
- [ ] 反復初始化不會洩漏
- [ ] Context 能被正確釋放

---

##  執行順序

1. **先修復編譯問題**
   ```
   修復 1 → 修復 2 → Rebuild → 確認編譯成功
   ```

2. **再修復運行時問題**
   ```
   修復 3 → 修復 4 → 修復 5 → 修復 6 → 測試
   ```

3. **最後做代碼優化**
   ```
   改進 1 → 改進 2 → 改進 3 → Code Review
   ```

---

##  修復後的 Git Commit 建議

```bash
# Commit 1: 修復關鍵問題
git add core/src/main/java/com/cyberpower/edc/core/device/hardware/HardwareManager.java
git add core/src/main/java/com/cyberpower/edc/core/device/hardware/pax/PaxHelper.java
git commit -m "修復 Device Package 關鍵問題

- HardwareManager: 添加缺少的 getPed() 和 getCrypto() 方法
- PaxHelper: 移除 static mDal 避免內存洩漏
- 改善 bindService() 異常處理和錯誤信息"

# Commit 2: 添加線程安全
git add core/src/main/java/com/cyberpower/edc/core/device/hardware/pax/PaxHelper.java
git add core/src/main/java/com/cyberpower/edc/core/device/hardware/castles/CastlesHelper.java
git commit -m "Device Package: 添加線程安全保護

- 所有硬件組件的 get 方法添加 synchronized
- 添加 mDal 初始化檢查避免 NPE"

# Commit 3: 改善錯誤處理
git add core/src/main/java/com/cyberpower/edc/core/device/DeviceUtils.java
git add core/src/main/java/com/cyberpower/edc/core/device/hardware/HardwareManager.java
git commit -m "Device Package: 改善錯誤處理

- DeviceUtils: 添加空指針保護
- HardwareManager: 改善設備檢測邏輯和日志
- CastlesHelper: 優化功能缺失警告"
```

---

## ⚠️ 重要提醒

1. **務必在測試環境先測試**
   - 這些修改涉及硬件抽象層
   - 必須在真實設備上測試
   - 確保 PAX 和 Castles 都能正常工作

2. **修改 core 模組需要雙重提交**
   - Core 是 Git submodule
   - 需要先在 core 倉庫提交
   - 然後在主專案更新 submodule 指標

3. **注意向後兼容性**
   - 這些修改應該不會破壞現有 API
   - 但建議通知團隊成員

---

##  相關文檔

- `Device_Package_Code_Review.md` - 完整審查報告
- `AGENTS.md` - 專案架構說明
- `README.md` - Git submodule 工作流程

---

**創建日期**: 2026-04-23  
**預計修復時間**: 2-4 小時  
**風險等級**: 中 (需要在真實設備上測試)

