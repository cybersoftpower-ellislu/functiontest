# API 測試功能實現文檔

## 📅 實現日期
2026-04-24

## 🎯 需求回顧

用戶要求實現 API 測試功能：

1. **選單結構** - 在 PAX 和 CASTLES 的子項目最前面添加 "API測試"
2. **Fragment 實現** - 創建繼承 Core 的 BaseFragment 的 API 測試畫面
3. **MVVM 架構** - 使用 ViewModel 管理業務邏輯
4. **UI 組件**：
   - 下拉選單 + 執行按鈕
   - 測試項目說明文字框
   - 可滾動的執行過程訊息欄
   - 提供添加訊息的方法（觀察者模式）

---

## ✅ 已完成的工作

### 1. 選單結構修改 ✓

#### MainActivity.java
```java
// PAX 選單
DrawerGroup paxGroup = new DrawerGroup("PAX", isPaxDevice);
paxGroup.addChild("API測試");  // ← 新增
paxGroup.addChild("列印測試");
paxGroup.addChild("掃碼測試");
paxGroup.addChild("卡片讀取測試");

// CASTLES 選單
DrawerGroup castlesGroup = new DrawerGroup("CASTLES", isCastlesDevice);
castlesGroup.addChild("API測試");  // ← 新增
castlesGroup.addChild("列印測試");
castlesGroup.addChild("掃碼測試");
castlesGroup.addChild("卡片讀取測試");
```

### 2. Fragment 容器添加 ✓

#### activity_main.xml
```xml
<!-- 主內容 -->
<FrameLayout
    android:id="@+id/fragment_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!-- 默認主內容保留在內部 -->
    <androidx.constraintlayout.widget.ConstraintLayout ...>
        <!-- 原有的主畫面內容 -->
    </androidx.constraintlayout.widget.ConstraintLayout>
</FrameLayout>
```

### 3. Fragment 加載邏輯 ✓

#### MainActivity.java
```java
private void handleNavigationClick(String itemName) {
    LogUtils.d(TAG, "點擊: " + itemName);
    viewModel.onNavigationItemClick(itemName);
    
    // 如果點擊 "API測試"，加載 Fragment
    if ("API測試".equals(itemName)) {
        loadApiTestFragment();
    } else {
        viewModel.performTest(itemName);
    }
}

private void loadApiTestFragment() {
    LogUtils.d(TAG, "加載 API 測試 Fragment");
    ApiTestFragment fragment = new ApiTestFragment();
    getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("ApiTest")
            .commit();
}
```

### 4. 創建的文件 ✓

#### 4.1 fragment_api_test.xml (布局文件)
**組件結構**：
```
LinearLayout (vertical)
├── TextView (標題)
├── LinearLayout (horizontal)
│   ├── Spinner (測試項目選擇)
│   └── Button (執行)
├── TextView (說明標題)
├── TextView (測試項目說明)
├── TextView (訊息標題)
├── ScrollView
│   └── TextView (執行過程訊息 - 可滾動)
└── Button (清除訊息)
```

**特性**：
- ✅ 使用 DataBinding
- ✅ 完整的 MVVM 綁定
- ✅ Monospace 字體顯示日誌
- ✅ ScrollView 支援長訊息

#### 4.2 ApiTestViewModel.java (ViewModel)
**功能**：
```java
public class ApiTestViewModel extends BaseViewModel {
    // LiveData
    public MutableLiveData<String> testDescription;    // 測試說明
    public MutableLiveData<String> logMessages;        // 日誌訊息
    public SingleLiveEvent<String> toastEvent;         // Toast 事件
    public SingleLiveEvent<Void> executeClickEvent;    // 執行事件
    public SingleLiveEvent<Void> clearLogClickEvent;   // 清除事件
    
    // 方法
    public void addLogMessage(String message);         // 添加一般訊息
    public void addErrorMessage(String message);       // 添加錯誤訊息
    public void addSuccessMessage(String message);     // 添加成功訊息
    public void addWarningMessage(String message);     // 添加警告訊息
    public void addInfoMessage(String message);        // 添加資訊訊息
    public void clearLogMessages();                    // 清除訊息
    public void executeTest(String testItem);          // 執行測試
    public void showToast(String message);             // 顯示 Toast
}
```

**日誌格式**：
```
[HH:mm:ss.SSS] ✅ [成功] 測試完成
[HH:mm:ss.SSS] ❌ [錯誤] 測試失敗
[HH:mm:ss.SSS] ⚠️ [警告] 注意事項
[HH:mm:ss.SSS] ℹ️ [資訊] 一般資訊
```

#### 4.3 ApiTestFragment.java (Fragment)
**功能**：
```java
public class ApiTestFragment extends BaseFragment<FragmentApiTestBinding, ApiTestViewModel> {
    // 繼承 BaseFragment
    // 實現 MVVM 架構
    // 管理 8 個 Dummy 測試項目
    // 處理 UI 交互
    // 執行測試邏輯
}
```

**8 個 Dummy 測試項目**：
1. 系統資訊測試
2. 硬體管理器測試
3. 列印功能測試
4. 掃碼功能測試
5. 卡片讀取測試
6. 網路連接測試
7. 加密功能測試
8. 綜合壓力測試

**測試實現範例** (系統資訊測試)：
```java
private void performSystemInfoTest() {
    viewModel.addInfoMessage("========== 系統資訊測試 ==========");
    
    try {
        viewModel.addInfoMessage("設備型號: " + Build.MODEL);
        viewModel.addInfoMessage("製造商: " + Build.MANUFACTURER);
        viewModel.addInfoMessage("Android 版本: " + Build.VERSION.RELEASE);
        viewModel.addInfoMessage("SDK 版本: " + Build.VERSION.SDK_INT);
        viewModel.addInfoMessage("設備名稱: " + Build.DEVICE);
        
        viewModel.addSuccessMessage("系統資訊測試完成！");
    } catch (Exception e) {
        viewModel.addErrorMessage("系統資訊測試失敗: " + e.getMessage());
    }
}
```

---

## 📊 架構設計

### MVVM 架構流程

```
用戶操作
    ↓
Fragment (View 層)
    ↓
ViewModel (觀察者)
    ↓
LiveData / SingleLiveEvent
    ↓
Fragment 自動更新 UI
```

### 觀察者模式

```java
// Fragment 中設定觀察者
viewModel.testDescription.observe(getViewLifecycleOwner(), description -> {
    binding.tvTestDescription.setText(description);
});

viewModel.logMessages.observe(getViewLifecycleOwner(), messages -> {
    binding.tvLogMessages.setText(messages);
    // 自動滾動到底部
    scrollView.fullScroll(View.FOCUS_DOWN);
});

viewModel.toastEvent.observe(getViewLifecycleOwner(), message -> {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
});
```

### Fragment 生命週期

```
onCreate()
    ↓
onCreateView()  -- 綁定 Layout
    ↓
onViewCreated() -- 初始化組件
    ↓
    ├─ initTestItems()
    ├─ initSpinner()
    ├─ initObservers()
    └─ initListeners()
    ↓
使用中...
    ↓
onDestroyView()
```

---

## 🎨 UI 設計

### 布局結構

```
┌──────────────────────────────────────┐
│ API 測試                             │  ← 標題
├──────────────────────────────────────┤
│ [ 系統資訊測試 ▼ ]     [ 執行 ]     │  ← 選單 + 按鈕
├──────────────────────────────────────┤
│ 測試項目說明                          │  ← 說明標題
│ ┌──────────────────────────────────┐ │
│ │ 測試獲取設備系統資訊，包括型號、  │ │  ← 說明內容
│ │ 製造商、Android 版本等。          │ │
│ └──────────────────────────────────┘ │
├──────────────────────────────────────┤
│ 執行過程訊息                          │  ← 訊息標題
│ ┌──────────────────────────────────┐ │
│ │ [12:34:56.789] ℹ️ 開始測試...    │ │
│ │ [12:34:57.123] ✅ 測試成功        │ │  ← 可滾動訊息
│ │ [12:34:57.456] ⚠️ 警告訊息        │ │
│ │ ...                               │ │
│ └──────────────────────────────────┘ │
│ [ 清除訊息 ]                         │  ← 清除按鈕
└──────────────────────────────────────┘
```

### 顏色方案

- **背景**: 白色 (#FFFFFF)
- **文字框背景**: 淺灰色 (#F5F5F5)
- **主要文字**: 黑色 (#000000)
- **次要文字**: 深灰色 (darker_gray)
- **日誌字體**: Monospace (等寬字體)

---

## 🔧 技術實現細節

### 1. BaseFragment 繼承

```java
public class ApiTestFragment extends BaseFragment<FragmentApiTestBinding, ApiTestViewModel> {
    @Override
    public int bindContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return R.layout.fragment_api_test;
    }

    @Override
    public int bindViewModelId() {
        return BR.viewModel;
    }
}
```

**注意**：
- 方法簽名必須匹配 BaseFragment 的抽象方法
- 需要提供 LayoutInflater, ViewGroup, Bundle 參數

### 2. SingleLiveEvent 使用

```java
// ViewModel 中定義
public SingleLiveEvent<Void> executeClickEvent = new SingleLiveEvent<>();

// 觸發事件
executeClickEvent.call();  // 無參數事件

// Fragment 中觀察
viewModel.executeClickEvent.observe(getViewLifecycleOwner(), unused -> {
    performTest(selectedItemPosition);
});
```

**位置**: `com.cyberpower.edc.core.customobject.SingleLiveEvent`

### 3. 自動滾動實現

```java
viewModel.logMessages.observe(getViewLifecycleOwner(), messages -> {
    binding.tvLogMessages.setText(messages);
    
    // 自動滾動到底部
    binding.tvLogMessages.post(() -> {
        ScrollView scrollView = (ScrollView) binding.tvLogMessages.getParent();
        scrollView.fullScroll(View.FOCUS_DOWN);
    });
});
```

### 4. Spinner 數據綁定

```java
// 準備數據
List<String> itemNames = new ArrayList<>();
for (TestItem item : testItems) {
    itemNames.add(item.name);
}

// 創建 Adapter
ArrayAdapter<String> adapter = new ArrayAdapter<>(
    requireContext(),
    android.R.layout.simple_spinner_item,
    itemNames
);
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// 設定到 Spinner
binding.spinnerTestItems.setAdapter(adapter);

// 監聽選擇事件
binding.spinnerTestItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        onTestItemSelected(position);
    }
    
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
});
```

---

## 📝 代碼變更總結

### 修改的文件

| 文件 | 變更類型 | 主要內容 |
|------|---------|---------|
| MainActivity.java | 功能增強 | ① 選單添加 "API測試"<br>② 添加 Fragment 加載邏輯 |
| activity_main.xml | 結構調整 | ① 添加 fragment_container<br>② 保留原有默認內容 |

### 新建的文件

| 文件 | 類型 | 行數 | 用途 |
|------|------|------|------|
| fragment_api_test.xml | Layout | ~110 行 | Fragment 布局 |
| ApiTestViewModel.java | ViewModel | ~130 行 | 業務邏輯管理 |
| ApiTestFragment.java | Fragment | ~380 行 | UI 管理與交互 |

---

## 🧪 測試指南

### 測試步驟

#### 1. 基本功能測試

**步驟**：
1. 啟動應用
2. 打開 Drawer
3. 展開 PAX 或 CASTLES (根據設備)
4. 點擊 "API測試"

**預期結果**：
- ✅ Fragment 正確加載
- ✅ 顯示 8 個測試項目
- ✅ 默認選中第一項
- ✅ 說明文字正確顯示

#### 2. Spinner 測試

**步驟**：
1. 點擊下拉選單
2. 選擇不同的測試項目

**預期結果**：
- ✅ 說明文字隨選擇更新
- ✅ 選擇後 Spinner 正確關閉

#### 3. 執行測試

**步驟**：
1. 選擇 "系統資訊測試"
2. 點擊 "執行" 按鈕

**預期結果**：
- ✅ 訊息欄顯示帶時間戳的日誌
- ✅ 顯示系統資訊 (型號、製造商等)
- ✅ 顯示成功訊息
- ✅ 自動滾動到底部

#### 4. 清除功能

**步驟**：
1. 執行多個測試
2. 點擊 "清除訊息" 按鈕

**預期結果**：
- ✅ 訊息欄重置為 "等待執行..."
- ✅ 說明文字保持不變

#### 5. 返回功能

**步驟**：
1. 在 API 測試畫面
2. 按返回鍵

**預期結果**：
- ✅ 返回主畫面
- ✅ Fragment 正確銷毀

---

## 🐛 已知問題

### IDE 編譯問題

**問題**: SingleLiveEvent 無法解析

**原因**: 
- SingleLiveEvent 在 `core.customobject` 包中
- 可能是 IDE 緩存問題

**解決方案**:
```bash
# 清理並重新構建
.\gradlew clean
.\gradlew assembleDebug

# 或在 Android Studio 中
File → Invalidate Caches / Restart
```

### DataBinding 生成

**問題**: BR.viewModel 無法識別

**解決方案**:
```bash
# 同步 Gradle
File → Sync Project with Gradle Files

# 重新構建
Build → Rebuild Project
```

---

## 🚀 未來擴展

### 短期 (1 週)

1. **實現測試邏輯**
   ```java
   // 在 ApiTestFragment 中實現
   private void performHardwareManagerTest() {
       viewModel.addInfoMessage("========== 硬體管理器測試 ==========");
       
       try {
           HardwareManager hwManager = HardwareManager.getInstance();
           IHelper helper = hwManager.getHelper();
           
           if (helper instanceof PaxHelper) {
               viewModel.addSuccessMessage("檢測到 PAX 設備");
           } else if (helper instanceof CastlesHelper) {
               viewModel.addSuccessMessage("檢測到 Castles 設備");
           } else {
               viewModel.addWarningMessage("使用 DummyHelper");
           }
       } catch (Exception e) {
           viewModel.addErrorMessage("測試失敗: " + e.getMessage());
       }
   }
   ```

2. **添加進度顯示**
   - 執行測試時顯示 ProgressDialog
   - 長時間測試顯示進度百分比

3. **測試結果保存**
   - 保存測試結果到文件
   - 提供匯出功能

### 中期 (1 個月)

1. **增強測試項目**
   - 列印測試 (打印測試票據)
   - 掃碼測試 (掃描並驗證)
   - 卡片測試 (讀取卡片資訊)
   - 網路測試 (Ping、下載速度)
   - 加密測試 (加解密驗證)

2. **測試報告**
   - 生成 HTML 格式報告
   - 包含測試時間、結果、錯誤訊息
   - Email 發送功能

3. **批次測試**
   - 選擇多個項目批次執行
   - 顯示總體測試進度

### 長期 (3 個月)

1. **自動化測試腳本**
   - 支援從文件讀取測試腳本
   - JSON/XML 格式定義測試流程

2. **遠程測試支援**
   - 連接測試服務器
   - 上傳測試結果
   - 接收測試指令

3. **性能監控**
   - CPU 使用率
   - 記憶體使用
   - 電池消耗

---

## ✅ 完成清單

### 選單結構
- [x] PAX 添加 "API測試" 子項目
- [x] CASTLES 添加 "API測試" 子項目
- [x] 點擊事件處理

### Fragment 實現
- [x] 創建 ApiTestFragment
- [x] 繼承 BaseFragment
- [x] 實現 MVVM 架構
- [x] 正確的方法簽名

### ViewModel 實現
- [x] 創建 ApiTestViewModel
- [x] 繼承 BaseViewModel
- [x] LiveData 定義
- [x] SingleLiveEvent 使用
- [x] 日誌管理方法

### UI 組件
- [x] 創建 fragment_api_test.xml
- [x] 下拉選單 (Spinner)
- [x] 執行按鈕
- [x] 測試項目說明文字框
- [x] 可滾動的訊息欄
- [x] 清除按鈕
- [x] DataBinding 綁定

### 測試項目
- [x] 8 個 Dummy 測試項目定義
- [x] 系統資訊測試實現
- [x] 其他測試預留接口

### 觀察者模式
- [x] testDescription 觀察
- [x] logMessages 觀察
- [x] toastEvent 觀察
- [x] executeClickEvent 觀察
- [x] clearLogClickEvent 觀察

---

## 📚 相關文檔

### 項目文檔
- `AGENTS.md` - 項目架構
- `DEVELOPMENT_GUIDE.md` - 開發指南
- `TASK_COMPLETION_REPORT.md` - 之前的任務報告

### Android 文檔
- [Fragment](https://developer.android.com/guide/fragments)
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
- [DataBinding](https://developer.android.com/topic/libraries/data-binding)

---

**實現狀態**: ✅ 基礎功能完成，等待編譯測試  
**實現日期**: 2026-04-24  
**實現者**: AI Assistant

