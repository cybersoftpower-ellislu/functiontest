# NeptuneLiteApi 分析報告

> **分析日期**: 2026-04-23  
> **JAR 文件**: NeptuneLiteApi_V4.15.00T_20250604.jar  
> **文件大小**: 818 KB (838,570 bytes)  
> **SDK 版本**: V4.15.00T (2025-06-04)

---

##  概述

**NeptuneLiteApi** 是 PAX（百富）提供的 Android POS 設備硬件抽象層 (HAL) SDK，用於訪問和控制 PAX Neptune Lite 系列終端設備的各種硬件功能。

### 基本信息

- **提供商**: PAX Technology (百富科技)
- **包名**: `com.pax.neptunelite.api`, `com.pax.dal`
- **類文件總數**: 230 個
- **主要包結構**: 9 個包

---

## ️ 架構結構

### 核心包結構

```
com.pax.neptunelite.api/          # Neptune Lite API 核心
├── Nepcore.class                  # 核心管理類
└── NeptuneLiteUser.class          # 用戶接口類

com.pax.dal/                       # 設備抽象層 (Device Abstraction Layer)
├── 43+ 主要接口 (I*.class)        # 硬件功能接口
├── entity/                        # 數據實體類 (104個)
├── exceptions/                    # 異常類
├── memorycard/                    # 記憶卡相關
├── pedkeyisolation/              # PED 密鑰隔離
├── proxy/                         # 代理類
└── utils/                         # 工具類

com.pax.nep.security/             # 安全相關
└── interceptor/                   # 安全攔截器
```

---

##  主要硬件接口 (43個)

### 1. 核心與設備管理

| 接口 | 功能描述 |
|------|---------|
| **IDAL** | 設備抽象層主接口 |
| **IBase** | 基礎接口 |
| **IDeviceInfo** | 設備信息查詢 |
| **ISys** | 系統功能 |
| **IDalCommManager** | 通訊管理器 |

### 2. 支付與加密

| 接口 | 功能描述 |
|------|---------|
| **IPed** | PIN 輸入設備 (PIN Entry Device) |
| **IPedAuthManager** | PED 認證管理 |
| **IPedBg** | PED 背景操作 |
| **IPedCustomization** | PED 自訂化 |
| **IPedKeyIsolationManager** | 密鑰隔離管理 |
| **IPedKeyIsolationMixedManager** | 混合密鑰隔離管理 |
| **IPedNp** | PED Neptune 專用 |
| **IPedTrSys** | PED 傳輸系統 |
| **IP2PE** | P2PE (點對點加密) |
| **IPuk** | PUK 密鑰管理 |
| **IPaymentDevice** | 支付設備 |

### 3. 讀卡器

| 接口 | 功能描述 |
|------|---------|
| **IIcc** | IC 卡讀取 (接觸式) |
| **IPicc** | PICC 卡讀取 (非接觸式) |
| **IMag** | 磁條卡讀取 |
| **ICardReaderHelper** | 讀卡器輔助 |
| **ITypeA** | Type A 卡片 |
| **ISle4442** | SLE4442 記憶卡 |

### 4. 列印

| 接口 | 功能描述 |
|------|---------|
| **IPrinter** | 列印機控制 |

### 5. 掃描與識別

| 接口 | 功能描述 |
|------|---------|
| **IScanner** | 掃描器 (條碼/QR碼) |
| **IScannerHw** | 掃描器硬件層 |
| **IScanCodec** | 掃描編解碼 |
| **IFaceDetector** | 人臉檢測 |
| **ILivenessDetector** | 活體檢測 |
| **IFingerprintReader** | 指紋識別 |
| **IIDReader** | 身份證讀取 |
| **IIDReaderEx** | 身份證讀取擴展 |
| **IOCR** | 光學字符識別 |
| **ILPR** | 車牌識別 (License Plate Recognition) |

### 6. 輸入/輸出

| 接口 | 功能描述 |
|------|---------|
| **IKeyBoard** | 鍵盤輸入 |
| **ISignPad** | 簽名板 |
| **ICustomerDisplay** | 客戶顯示屏 |
| **IWLCustomerDisplay** | 無線客戶顯示屏 |
| **ICashDrawer** | 錢箱控制 |

### 7. 通訊

| 接口 | 功能描述 |
|------|---------|
| **IComm** | 通訊接口 |
| **IChannel** | 通道管理 |
| **INetwork** | 網絡管理 |
| **IPaxVpn** | PAX VPN |
| **IPhoneManager** | 電話管理 |
| **IWifiProbe** | WiFi 探測 |

---

##  數據實體類 (Entity) - 104個

### 加密與密鑰

- **KeyInfo** - 密鑰信息
- **AllocatedKeyInfo** - 已分配密鑰信息
- **RSAKeyInfo** - RSA 密鑰信息
- **RSAPinKey** - RSA PIN 密鑰
- **SM2KeyPair** - SM2 密鑰對（中國國密）
- **DUKPTResult** - DUKPT 結果
- **TAEncryptedResult** - TA 加密結果
- **Tr34OutBlock** - TR-34 輸出塊
- **WriteKEKOutput** - 寫入 KEK 輸出

### 卡片操作

- **IccPara** - IC 卡參數
- **PiccPara** - PICC 卡參數
- **PiccCardInfo** - PICC 卡信息
- **TrackData** - 磁道數據
- **ApduSendInfo** - APDU 發送信息
- **ApduRespInfo** - APDU 響應信息
- **PollingResult** - 輪詢結果

### 掃描與識別

- **ScanResult** - 掃描結果
- **ScanResultRaw** - 原始掃描結果
- **DecodeResult** - 解碼結果
- **DecodeResultRaw** - 原始解碼結果
- **FaceInfo** - 人臉信息
- **FaceConfig** - 人臉配置
- **FaceAngle** - 人臉角度
- **FaceRectangle** - 人臉矩形
- **LivenessDetectResult** - 活體檢測結果
- **FingerprintResult** - 指紋結果
- **IDReadResult** - 身份證讀取結果
- **OCRResult** - OCR 結果
- **OCRMRZResult** - OCR MRZ (機讀區) 結果
- **OCRMRZExResult** - OCR MRZ 擴展結果
- **LPRResult** - 車牌識別結果
- **LPRChinaResult** - 中國車牌識別結果
- **LPRIndiaResult** - 印度車牌識別結果

### 列印

- **FontInfo** - 字體信息

### 網絡與通訊

- **LanParam** - LAN 參數
- **LanProxyInfo** - LAN 代理信息
- **MobileParam** - 移動網絡參數
- **ModemParam** - Modem 參數
- **UartParam** - UART 參數
- **ApnInfo** - APN 信息
- **NetItem** - 網絡項目
- **NtpServerParam** - NTP 服務器參數
- **OperatorInfo** - 運營商信息

### 設備信息

- **DeviceInfo** - 設備信息
- **EmmcInfo** - eMMC 信息
- **BatterySipper** - 電池消耗信息

### 簽名與顯示

- **SignPadResp** - 簽名板響應
- **CustomerDisplayInfo** - 客戶顯示屏信息
- **WLLcdDisplaySize** - 無線 LCD 顯示大小
- **WLLcdCusorPosition** - 無線 LCD 光標位置

### 其他

- **PosMenu** - POS 菜單
- **PukInfo** - PUK 信息
- **QInfo** - Q 信息
- **VisitItem** - 訪問項目

---

##  加密算法支持

### 枚舉類型

- **EAlgorithmType** - 算法類型
- **ECryptOperate** - 加密操作
- **ECryptOpt** - 加密選項
- **EAesCheckMode** - AES 校驗模式
- **ECheckMode** - 校驗模式
- **EDUKPTDesMode** - DUKPT DES 模式
- **EDUKPTMacMode** - DUKPT MAC 模式
- **EDUKPTPinMode** - DUKPT PIN 模式
- **EPinBlockMode** - PIN Block 模式
- **EPedDesMode** - PED DES 模式
- **EPedMacMode** - PED MAC 模式

### 支持的加密標準

- **DES/3DES** - 數據加密標準
- **AES** - 高級加密標準
- **RSA** - 公鑰加密
- **SM2/SM4** - 中國國密算法
- **DUKPT** - 派生唯一密鑰交易
- **TR-34** - 對稱密鑰分發

---

##  關鍵功能特性

### 1. PED (PIN Entry Device) 功能

NeptuneLiteApi 提供了完整的 PED 管理功能：

- **密鑰管理**
  - 主密鑰注入
  - 工作密鑰下載
  - 密鑰隔離
  - 多應用密鑰管理

- **PIN 處理**
  - PIN 輸入
  - PIN Block 計算
  - 多種 PIN Block 格式支持

- **加密服務**
  - MAC 計算
  - 數據加解密
  - DUKPT 密鑰派生

### 2. 讀卡器功能

- **接觸式 IC 卡 (IIcc)**
  - EMV 交易流程
  - APDU 通訊
  - 多應用選擇

- **非接觸式卡片 (IPicc)**
  - ISO 14443 Type A/B
  - M1 卡操作
  - CPU 卡交易

- **磁條卡 (IMag)**
  - 1/2/3 磁軌讀取
  - 加密磁道數據

### 3. 生物識別

- **人臉識別**
  - 人臉檢測
  - 活體檢測
  - 人臉角度檢測

- **指紋識別**
  - 指紋採集
  - 指紋驗證

- **身份證讀取**
  - 中國二代身份證
  - 身份證信息提取

### 4. OCR 與掃描

- **光學字符識別**
  - 證件 OCR
  - MRZ (護照機讀區)
  - 車牌識別

- **條碼掃描**
  - 1D 條碼
  - 2D 條碼 (QR Code)
  - 多碼識別

### 5. 列印

- **熱敏列印**
  - 文字列印
  - 圖片列印
  - 條碼/QR碼列印
  - 多字體支持

### 6. 通訊

- **網絡通訊**
  - WiFi
  - 4G/5G
  - 以太網
  - VPN

- **串口通訊**
  - UART
  - USB

---

##  技術特點

### 1. 設計模式

- **抽象工廠模式** - IDAL 作為主入口，創建各種硬件接口實例
- **單例模式** - Nepcore 核心管理類
- **監聽器模式** - 各種 Listener 接口處理異步事件

### 2. 線程安全

- 支持多線程訪問
- 異步回調機制

### 3. 錯誤處理

- 完整的異常類體系 (exceptions 包)
- 詳細的錯誤碼

### 4. 安全性

- 密鑰隔離機制
- 安全攔截器
- 符合 PCI PTS 標準

---

##  使用示例

### 初始化 DAL

```java
// 獲取 IDAL 實例
IDAL dal = NeptuneLiteUser.getInstance().getDal(context);

// 獲取各種硬件接口
IPrinter printer = dal.getPrinter();
IScanner scanner = dal.getScanner();
IPed ped = dal.getPed(EPedType.INTERNAL);
IIcc icc = dal.getIcc();
IPicc picc = dal.getPicc();
```

### 列印操作

```java
IPrinter printer = dal.getPrinter();
printer.init();
printer.printStr("Hello World", null);
printer.start();
```

### 掃碼操作

```java
IScanner scanner = dal.getScanner();
scanner.open();
scanner.start(new IScanner.IScanListener() {
    @Override
    public void onRead(ScanResult result) {
        String barcode = result.getContent();
        // 處理掃描結果
    }
    
    @Override
    public void onFinish() {
        // 掃描完成
    }
    
    @Override
    public void onCancel() {
        // 用戶取消
    }
});
```

### 讀卡操作

```java
// IC 卡
IIcc icc = dal.getIcc();
icc.init((byte) 0); // slot 0
icc.detect((byte) 0);

// 非接觸卡
IPicc picc = dal.getPicc();
PiccCardInfo cardInfo = picc.detect(EPiccType.ISO14443_TYPE_A.getEPiccType());
```

---

##  兼容性

### 支持的 PAX 設備型號

NeptuneLiteApi 主要支持 PAX Neptune Lite 系列設備：

- **A920/A920Pro** - Android 智能 POS
- **A80** - Android 桌面 POS
- **A77** - Android 手持 POS
- **A50** - Android 手持 POS
- 其他 Neptune Lite 系列設備

### Android 版本要求

- **最低版本**: Android 5.0 (API 21)
- **推薦版本**: Android 7.0+ (API 24+)

---

##  與其他 PAX SDK 的比較

### NeptuneLiteApi vs GLComm

| 特性 | NeptuneLiteApi | GLComm |
|------|---------------|--------|
| **目標設備** | Neptune Lite 系列 | 傳統 PROLIN 系列 |
| **架構** | 現代 Android DAL | 傳統 GL 框架 |
| **API 設計** | 接口導向 | 類導向 |
| **異步支持** | Listener 模式 | 回調函數 |
| **生物識別** | ✅ 完整支持 | ⚠️ 有限支持 |
| **文檔** | 較新 | 較舊但完整 |

---

## ⚠️ 注意事項

### 1. 權限要求

使用 NeptuneLiteApi 需要在 AndroidManifest.xml 中聲明權限：

```xml
<!-- 硬件權限 -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

<!-- PAX 專用權限 -->
<uses-permission android:name="com.pax.permission.PED" />
<uses-permission android:name="com.pax.permission.PRINTER" />
<uses-permission android:name="com.pax.permission.SCANNER" />
```

### 2. 設備檢測

並非所有功能在所有設備上都可用，使用前應檢查：

```java
IDeviceInfo deviceInfo = dal.getDeviceInfo();
if (deviceInfo.isSupported(IDeviceInfo.ESupported.ICC)) {
    // 設備支持 IC 卡讀取
}
```

### 3. 資源釋放

使用完硬件後應及時釋放資源：

```java
scanner.close();
printer.close();
```

### 4. 線程處理

某些操作可能是阻塞的，建議在後台線程執行：

```java
new Thread(() -> {
    try {
        // 執行硬件操作
    } catch (Exception e) {
        // 錯誤處理
    }
}).start();
```

---

##  進一步學習

### 推薦資源

1. **PAX 官方文檔**
   - PAX Neptune Lite 開發者指南
   - API Reference Manual

2. **示例代碼**
   - PAX Store 上的示例應用
   - GitHub 上的開源項目

3. **社區支持**
   - PAX 開發者論壇
   - PAX 技術支持

---

##  版本歷史

### V4.15.00T (2025-06-04)

當前版本的主要特性：
- ✅ 完整的 PED 功能
- ✅ 生物識別支持
- ✅ OCR 與車牌識別
- ✅ 支持國密算法 (SM2/SM4)
- ✅ TR-34 密鑰注入
- ✅ 網絡管理增強

---

## 總結

**NeptuneLiteApi** 是一個功能完整、設計現代的 Android POS 硬件抽象層 SDK。它提供了：

✅ **43+ 硬件接口** - 完整的設備控制  
✅ **104+ 數據實體** - 豐富的數據結構  
✅ **現代架構** - 接口導向、異步支持  
✅ **安全性** - PCI 合規、密鑰隔離  
✅ **擴展性** - 易於集成和擴展

對於開發 PAX Neptune Lite 系列設備的應用，這是必不可少的核心 SDK。

---

**分析完成日期**: 2026-04-23  
**分析工具**: JAR 文件結構分析  
**文檔版本**: 1.0

