# Drawer 選單 - 設備類型動態啟用功能

## 📅 更新日期
2026-04-24

## 🎯 需求

實現基於設備類型的動態選單啟用/禁用功能：

1. **重構選單結構** - 將選單改為：PAX、CASTLES、設定、關於
2. **動態啟用控制** - 根據 HardwareManager 的 mHelper 類型：
   - `PaxHelper` → PAX 可點擊，CASTLES 禁用
   - `CastlesHelper` → CASTLES 可點擊，PAX 禁用
   - `DummyHelper` → 兩者都禁用
3. **視覺回饋** - 禁用的選單項目顯示為灰色且半透明

---

## ✅ 已完成的修改

### 1. MainActivity.java 修改

#### 1.1 添加導入
```java
import com.cyberpower.edc.core.device.hardware.HardwareManager;
import com.cyberpower.edc.core.device.hardware.castles.CastlesHelper;
import com.cyberpower.edc.core.device.hardware.pax.PaxHelper;
```

#### 1.2 重構 initDrawerData() 方法

**新選單結構：**
```java
private void initDrawerData() {
    drawerData = new ArrayList<>();

    // 檢測當前設備類型
    boolean isPaxDevice = false;
    boolean isCastlesDevice = false;

    try {
        HardwareManager hwManager = HardwareManager.getInstance();
        if (hwManager != null && hwManager.getHelper() != null) {
            if (hwManager.getHelper() instanceof PaxHelper) {
                isPaxDevice = true;
                LogUtils.d(TAG, "檢測到 PAX 設備");
            } else if (hwManager.getHelper() instanceof CastlesHelper) {
                isCastlesDevice = true;
                LogUtils.d(TAG, "檢測到 Castles 設備");
            } else {
                LogUtils.d(TAG, "檢測到未知設備（使用 DummyHelper）");
            }
        }
    } catch (Exception e) {
        LogUtils.e(TAG, "檢測設備類型時發生錯誤", e);
    }

    // 第一層：PAX（根據設備類型設定啟用狀態）
    DrawerGroup paxGroup = new DrawerGroup("PAX", isPaxDevice);
    paxGroup.addChild("列印測試");
    paxGroup.addChild("掃碼測試");
    paxGroup.addChild("卡片讀取測試");
    drawerData.add(paxGroup);

    // 第一層：CASTLES（根據設備類型設定啟用狀態）
    DrawerGroup castlesGroup = new DrawerGroup("CASTLES", isCastlesDevice);
    castlesGroup.addChild("列印測試");
    castlesGroup.addChild("掃碼測試");
    castlesGroup.addChild("卡片讀取測試");
    drawerData.add(castlesGroup);

    // 第一層：設定（無子項目，始終啟用）
    DrawerGroup settingsGroup = new DrawerGroup("設定", true);
    drawerData.add(settingsGroup);

    // 第一層：關於（無子項目，始終啟用）
    DrawerGroup aboutGroup = new DrawerGroup("關於", true);
    drawerData.add(aboutGroup);
}
```

**技術要點：**
- 使用 `instanceof` 檢查 Helper 類型
- 根據設備類型設置 enabled 狀態
- 設定和關於始終啟用

#### 1.3 修改 DrawerGroup 類

**添加 enabled 屬性：**
```java
public static class DrawerGroup {
    public String name;
    public List<String> children;
    public boolean enabled; // 是否啟用（根據設備類型）

    public DrawerGroup(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
        this.children = new ArrayList<>();
    }

    public void addChild(String child) {
        this.children.add(child);
    }
}
```

#### 1.4 修改點擊事件處理

**群組點擊事件：**
```java
binding.expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
    DrawerGroup group = drawerData.get(groupPosition);
    
    // 如果項目被禁用，不處理點擊
    if (!group.enabled) {
        LogUtils.d(TAG, "群組 [" + group.name + "] 已禁用，不處理點擊");
        return true; // 返回 true 表示已處理，不執行其他操作
    }
    
    if (group.children.isEmpty()) {
        // 沒有子項目，直接處理點擊
        handleNavigationClick(group.name);
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    return false; // 有子項目，展開/收合
});
```

**子項目點擊事件：**
```java
binding.expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
    DrawerGroup group = drawerData.get(groupPosition);
    
    // 如果父群組被禁用，不處理子項目點擊
    if (!group.enabled) {
        LogUtils.d(TAG, "群組 [" + group.name + "] 已禁用，不處理子項目點擊");
        return true; // 返回 true 表示已處理
    }
    
    String childName = group.children.get(childPosition);
    handleNavigationClick(childName);
    binding.drawerLayout.closeDrawer(GravityCompat.START);
    return true;
});
```

---

### 2. DrawerAdapter.java 修改

#### 2.1 群組視圖 - 禁用狀態視覺效果

```java
@Override
public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
    // ...ViewHolder 初始化代碼...

    MainActivity.DrawerGroup group = data.get(groupPosition);
    holder.tvGroupName.setText(group.name);

    // 根據啟用狀態設定視覺效果
    if (group.enabled) {
        // 啟用狀態：正常顯示
        holder.tvGroupName.setAlpha(1.0f);
        holder.tvIndicator.setAlpha(1.0f);
        holder.tvGroupName.setTextColor(0xFF000000); // 黑色
    } else {
        // 禁用狀態：降低透明度，改為灰色
        holder.tvGroupName.setAlpha(0.4f);
        holder.tvIndicator.setAlpha(0.4f);
        holder.tvGroupName.setTextColor(0xFF999999); // 灰色
    }

    // ...指示器設定代碼...
}
```

**視覺效果：**
- 啟用：黑色文字，不透明（alpha = 1.0）
- 禁用：灰色文字（#999999），40% 透明度（alpha = 0.4）

#### 2.2 子項目視圖 - 繼承父組狀態

```java
@Override
public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                        View convertView, ViewGroup parent) {
    // ...ViewHolder 初始化代碼...

    MainActivity.DrawerGroup group = data.get(groupPosition);
    String childName = group.children.get(childPosition);
    holder.tvChildName.setText(childName);

    // 根據父群組的啟用狀態設定視覺效果
    if (group.enabled) {
        // 啟用狀態：正常顯示
        holder.tvChildName.setAlpha(1.0f);
        holder.tvChildName.setTextColor(0xFF000000); // 黑色
    } else {
        // 禁用狀態：降低透明度，改為灰色
        holder.tvChildName.setAlpha(0.4f);
        holder.tvChildName.setTextColor(0xFF999999); // 灰色
    }

    return convertView;
}
```

---

## 📊 選單結構對比

### 修改前

```
├── 硬體測試
│   ├── 列印測試
│   ├── 掃碼測試
│   └── 卡片讀取測試
├── 系統測試
│   ├── 網路測試
│   ├── 儲存測試
│   └── 螢幕測試
├── 設定
└── 關於
```

### 修改後

```
├── PAX (根據設備啟用/禁用)
│   ├── 列印測試
│   ├── 掃碼測試
│   └── 卡片讀取測試
├── CASTLES (根據設備啟用/禁用)
│   ├── 列印測試
│   ├── 掃碼測試
│   └── 卡片讀取測試
├── 設定 (始終啟用)
└── 關於 (始終啟用)
```

---

## 🎯 設備類型檢測邏輯

### 檢測流程

```
啟動 App
    ↓
SplashActivity (Core.init())
    ↓
HardwareManager.getInstance()
    ↓
InitDevice() - 根據 Build.MANUFACTURER 選擇 Helper
    ↓
mHelper = PaxHelper / CastlesHelper / DummyHelper
    ↓
MainActivity.onCreate()
    ↓
initDrawerData() - 檢測 mHelper 類型
    ↓
設置選單項目啟用狀態
```

### 檢測代碼

```java
HardwareManager hwManager = HardwareManager.getInstance();
if (hwManager.getHelper() instanceof PaxHelper) {
    isPaxDevice = true;
} else if (hwManager.getHelper() instanceof CastlesHelper) {
    isCastlesDevice = true;
}
```

### 三種設備狀態

| 設備製造商 | Helper 類型 | PAX 選單 | CASTLES 選單 |
|-----------|------------|---------|-------------|
| PAX, lephone | PaxHelper | ✅ 啟用 | ❌ 禁用 |
| Castles | CastlesHelper | ❌ 禁用 | ✅ 啟用 |
| 其他 | DummyHelper | ❌ 禁用 | ❌ 禁用 |

---

## 🎨 視覺效果展示

### PAX 設備
```
☰ 功能測試

Drawer:
┌──────────────────────┐
│ PAX               ▶  │ ← 黑色，不透明
│ CASTLES           ▶  │ ← 灰色，半透明（禁用）
│ 設定                 │ ← 黑色，不透明
│ 關於                 │ ← 黑色，不透明
└──────────────────────┘
```

### Castles 設備
```
☰ 功能測試

Drawer:
┌──────────────────────┐
│ PAX               ▶  │ ← 灰色，半透明（禁用）
│ CASTLES           ▶  │ ← 黑色，不透明
│ 設定                 │ ← 黑色，不透明
│ 關於                 │ ← 黑色，不透明
└──────────────────────┘
```

### 未知設備（DummyHelper）
```
☰ 功能測試

Drawer:
┌──────────────────────┐
│ PAX               ▶  │ ← 灰色，半透明（禁用）
│ CASTLES           ▶  │ ← 灰色，半透明（禁用）
│ 設定                 │ ← 黑色，不透明
│ 關於                 │ ← 黑色，不透明
└──────────────────────┘
```

---

## 🔧 技術實現細節

### 1. 設備檢測時機

**在 initDrawerData() 中檢測**，確保：
- HardwareManager 已初始化（在 SplashActivity 的 Core.init() 中）
- 檢測結果即時反映在選單上
- 每次創建 Activity 時重新檢測（支持動態變化）

### 2. 異常處理

```java
try {
    HardwareManager hwManager = HardwareManager.getInstance();
    if (hwManager != null && hwManager.getHelper() != null) {
        // 檢測邏輯
    }
} catch (Exception e) {
    LogUtils.e(TAG, "檢測設備類型時發生錯誤", e);
    // 發生錯誤時，所有設備相關選單都禁用
}
```

### 3. 點擊攔截

**兩層攔截機制**：
1. **群組層級** - 檢查父組的 enabled 狀態
2. **子項目層級** - 檢查父組的 enabled 狀態

這確保禁用的項目在任何情況下都不會響應點擊。

### 4. 視覺回饋設置

**使用 alpha 和 textColor 組合**：
```java
// 禁用狀態
setAlpha(0.4f);           // 40% 透明度
setTextColor(0xFF999999);  // 灰色 #999999
```

**優點**：
- 視覺效果明顯
- 用戶一眼就能識別不可用項目
- 符合 Material Design 禁用狀態規範

---

## 📝 代碼變更總結

### 修改的文件

| 文件 | 變更類型 | 主要內容 |
|------|---------|---------|
| MainActivity.java | 重構 + 功能增強 | ① 添加 HardwareManager 導入<br>② 重構 initDrawerData()<br>③ 修改 DrawerGroup 類<br>④ 添加點擊攔截邏輯 |
| DrawerAdapter.java | 視覺增強 | ① 群組視圖禁用狀態<br>② 子項目視圖禁用狀態 |

### 新增的功能

| 功能 | 實現方式 |
|------|---------|
| 設備類型檢測 | instanceof 檢查 HardwareManager.getHelper() |
| 動態啟用控制 | DrawerGroup.enabled 屬性 |
| 視覺禁用回饋 | alpha + textColor 設置 |
| 點擊事件攔截 | 檢查 enabled 狀態 |

---

## ✅ 編譯狀態

### 無錯誤 ✅
所有修改已通過編譯檢查。

### 警告（可忽略）⚠️

**MainActivity.java**:
1. 字符串拼接警告（行 199）- 建議使用 string resource
2. Lambda 表達式簡化建議（行 268）

**DrawerAdapter.java**:
1. 字段可 final 警告 - 代碼風格建議
2. 未使用的 context 字段 - 保留以備將來使用

---

## 🧪 測試指南

### 測試場景

#### 1. PAX 設備測試

**前置條件**：
- 在 PAX 設備上運行或模擬器中 Build.MANUFACTURER = "PAX"

**測試步驟**：
1. 啟動 App
2. 打開 Drawer
3. 檢查 PAX 選單為黑色可點擊
4. 檢查 CASTLES 選單為灰色半透明
5. 嘗試點擊 PAX，應正常展開子選單
6. 嘗試點擊 CASTLES，應無反應
7. 點擊 PAX 的子項目，應正常導航

**預期結果**：
- ✅ PAX 可用，CASTLES 禁用
- ✅ 設定和關於始終可用
- ✅ 日誌顯示 "檢測到 PAX 設備"

#### 2. Castles 設備測試

**前置條件**：
- 在 Castles 設備上運行或模擬器中 Build.MANUFACTURER = "Castles"

**測試步驟**：
1. 啟動 App
2. 打開 Drawer
3. 檢查 CASTLES 選單為黑色可點擊
4. 檢查 PAX 選單為灰色半透明
5. 嘗試點擊 CASTLES，應正常展開子選單
6. 嘗試點擊 PAX，應無反應

**預期結果**：
- ✅ CASTLES 可用，PAX 禁用
- ✅ 日誌顯示 "檢測到 Castles 設備"

#### 3. 未知設備測試（DummyHelper）

**前置條件**：
- 在非 PAX/Castles 設備上運行

**測試步驟**：
1. 啟動 App
2. 打開 Drawer
3. 檢查 PAX 和 CASTLES 都是灰色半透明
4. 嘗試點擊任一項目，應無反應
5. 檢查設定和關於仍可點擊

**預期結果**：
- ✅ PAX 和 CASTLES 都禁用
- ✅ 設定和關於可用
- ✅ 日誌顯示 "檢測到未知設備（使用 DummyHelper）"

#### 4. 視覺效果測試

**檢查項目**：
- [ ] 禁用項目為灰色（#999999）
- [ ] 禁用項目透明度約 40%
- [ ] 啟用項目為黑色（#000000）
- [ ] 啟用項目完全不透明
- [ ] 子項目隨父組狀態變化
- [ ] 指示器箭頭同樣受禁用狀態影響

---

## 🚀 未來擴展建議

### 可選改進

#### 1. 動態圖標

為不同設備類型添加圖標：
```xml
<ImageView
    android:id="@+id/iv_device_icon"
    android:src="@drawable/ic_pax" />
```

#### 2. Toast 提示

點擊禁用項目時顯示提示：
```java
if (!group.enabled) {
    Toast.makeText(context, "此功能僅適用於 " + 
        (isPaxDevice ? "PAX" : "Castles") + " 設備", 
        Toast.LENGTH_SHORT).show();
    return true;
}
```

#### 3. 設備切換模擬

開發階段添加切換設備類型的功能（僅 Debug 模式）：
```java
if (BuildConfig.DEBUG) {
    DrawerGroup debugGroup = new DrawerGroup("開發者選項", true);
    debugGroup.addChild("模擬 PAX 設備");
    debugGroup.addChild("模擬 Castles 設備");
    drawerData.add(debugGroup);
}
```

#### 4. 配置化選單

將選單結構移到配置文件或資料庫：
```json
{
  "menuItems": [
    {
      "name": "PAX",
      "requiredHelper": "PaxHelper",
      "children": ["列印測試", "掃碼測試", "卡片讀取測試"]
    }
  ]
}
```

---

## 📚 相關文檔

### 項目文檔
- `AGENTS.md` - 專案架構說明
- `DEVELOPMENT_GUIDE.md` - 開發指南
- `Device_Package_Code_Review.md` - Device 包代碼審查

### Android 文檔
- [ExpandableListView](https://developer.android.com/reference/android/widget/ExpandableListView)
- [Material Design - States](https://material.io/design/interaction/states.html)

---

## ✅ 總結

### 完成的功能

1. ✅ **選單結構重構** - PAX、CASTLES、設定、關於
2. ✅ **設備檢測** - 自動識別 PaxHelper/CastlesHelper/DummyHelper
3. ✅ **動態啟用控制** - 根據設備類型啟用/禁用選單
4. ✅ **視覺回饋** - 禁用項目顯示灰色半透明
5. ✅ **點擊攔截** - 禁用項目不響應點擊
6. ✅ **日誌記錄** - 記錄設備檢測和點擊攔截

### 技術亮點

- ✅ 使用 `instanceof` 進行類型檢測
- ✅ 擴展 DrawerGroup 類支持 enabled 狀態
- ✅ Adapter 層面實現視覺禁用效果
- ✅ 兩層攔截確保禁用項目完全不可用
- ✅ 異常處理確保穩定性

### 用戶體驗

- ✅ 一目了然的禁用狀態（灰色+半透明）
- ✅ 點擊禁用項目無反應（符合預期）
- ✅ 設定和關於始終可用
- ✅ 清晰的視覺層次

---

**修改完成！請編譯並在不同設備上測試。** 🎉

---

**修改日期：** 2026-04-24  
**修改者：** AI Assistant  
**狀態：** ✅ 已完成，等待測試

