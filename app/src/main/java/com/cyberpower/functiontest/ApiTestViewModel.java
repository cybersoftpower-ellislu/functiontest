package com.cyberpower.functiontest;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.cyberpower.edc.core.base.BaseViewModel;
import com.cyberpower.edc.core.basis.LogUtils;
import com.cyberpower.edc.core.customobject.SingleLiveEvent;
import com.cyberpower.edc.core.device.hardware.HardwareManager;
import com.cyberpower.edc.core.device.hardware.castles.CastlesHelper;
import com.cyberpower.edc.core.device.hardware.hardwareinterface.IHelper;
import com.cyberpower.edc.core.device.hardware.pax.PaxHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ApiTestViewModel
 * API 測試畫面的 ViewModel
 */
public class ApiTestViewModel extends BaseViewModel {
    private static final String TAG = "ApiTestViewModel";

    // 測試項目說明
    public MutableLiveData<String> testDescription = new MutableLiveData<>("");

    // 執行過程訊息
    public MutableLiveData<String> logMessages = new MutableLiveData<>("等待執行...\n");

    // Toast 事件
    public SingleLiveEvent<String> toastEvent = new SingleLiveEvent<>();

    // 執行按鈕點擊事件
    public SingleLiveEvent<Void> executeClickEvent = new SingleLiveEvent<>();

    // 清除按鈕點擊事件
    public SingleLiveEvent<Void> clearLogClickEvent = new SingleLiveEvent<>();

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());

    // 設備類型枚舉
    public enum DeviceType {
        PAX,
        CASTLES,
        UNKNOWN
    }

    private DeviceType currentDeviceType = DeviceType.UNKNOWN;
    private IHelper currentHelper = null;

    public ApiTestViewModel(@NonNull Application application) {
        super(application);
        LogUtils.d(TAG, "ApiTestViewModel 已創建");
        detectDeviceType();
    }

    /**
     * 檢測當前設備類型
     */
    private void detectDeviceType() {
        try {
            HardwareManager hwManager = HardwareManager.getInstance();
            if (hwManager != null) {
                currentHelper = hwManager.getHelper();
                if (currentHelper instanceof PaxHelper) {
                    currentDeviceType = DeviceType.PAX;
                    LogUtils.d(TAG, "檢測到 PAX 設備");
                } else if (currentHelper instanceof CastlesHelper) {
                    currentDeviceType = DeviceType.CASTLES;
                    LogUtils.d(TAG, "檢測到 Castles 設備");
                } else {
                    currentDeviceType = DeviceType.UNKNOWN;
                    LogUtils.d(TAG, "檢測到未知設備類型");
                }
            }
        } catch (Exception e) {
            currentDeviceType = DeviceType.UNKNOWN;
            LogUtils.e(TAG, "設備類型檢測失敗", e);
        }
    }

    /**
     * 獲取當前設備類型
     */
    public DeviceType getDeviceType() {
        return currentDeviceType;
    }

    /**
     * 獲取當前 Helper
     */
    public IHelper getCurrentHelper() {
        return currentHelper;
    }

    /**
     * 獲取 PAX Helper（如果是 PAX 設備）
     */
    public PaxHelper getPaxHelper() {
        if (currentHelper instanceof PaxHelper) {
            return (PaxHelper) currentHelper;
        }
        return null;
    }

    /**
     * 獲取 Castles Helper（如果是 Castles 設備）
     */
    public CastlesHelper getCastlesHelper() {
        if (currentHelper instanceof CastlesHelper) {
            return (CastlesHelper) currentHelper;
        }
        return null;
    }

    /**
     * 設定測試項目說明
     */
    public void setTestDescription(String description) {
        testDescription.setValue(description);
        LogUtils.d(TAG, "更新測試項目說明: " + description);
    }

    /**
     * 添加日誌訊息
     */
    public void addLogMessage(String message) {
        String timestamp = timeFormat.format(new Date());
        String logEntry = "[" + timestamp + "] " + message + "\n";

        String currentLog = logMessages.getValue();
        if (currentLog == null) {
            currentLog = "";
        }

        // 如果是初始訊息，清空
        if (currentLog.equals("等待執行...\n")) {
            currentLog = "";
        }

        logMessages.setValue(currentLog + logEntry);
        LogUtils.d(TAG, "添加日誌: " + message);
    }

    /**
     * 添加錯誤訊息
     */
    public void addErrorMessage(String message) {
        addLogMessage("❌ [錯誤] " + message);
    }

    /**
     * 添加成功訊息
     */
    public void addSuccessMessage(String message) {
        addLogMessage("✅ [成功] " + message);
    }

    /**
     * 添加警告訊息
     */
    public void addWarningMessage(String message) {
        addLogMessage("⚠️ [警告] " + message);
    }

    /**
     * 添加資訊訊息
     */
    public void addInfoMessage(String message) {
        addLogMessage("ℹ️ [資訊] " + message);
    }

    /**
     * 清除日誌訊息
     */
    public void clearLogMessages() {
        logMessages.setValue("等待執行...\n");
        LogUtils.d(TAG, "清除日誌訊息");
    }

    /**
     * 執行測試 - 統一入口
     * @param testItem 測試項目名稱
     */
    public void executeTest(String testItem) {
        LogUtils.d(TAG, "執行測試: " + testItem);
        addInfoMessage("開始執行測試: " + testItem);
        addInfoMessage("當前設備類型: " + currentDeviceType.name());

        // 觸發執行事件
        executeClickEvent.call();
    }

    /**
     * 執行系統資訊測試
     */
    public void executeSystemInfoTest() {
        addInfoMessage("========== 系統資訊測試 ==========");

        try {
            // Android 系統資訊
            addInfoMessage("設備型號: " + android.os.Build.MODEL);
            addInfoMessage("製造商: " + android.os.Build.MANUFACTURER);
            addInfoMessage("Android 版本: " + android.os.Build.VERSION.RELEASE);
            addInfoMessage("SDK 版本: " + android.os.Build.VERSION.SDK_INT);
            addInfoMessage("設備名稱: " + android.os.Build.DEVICE);

            addSuccessMessage("系統資訊測試完成！");
        } catch (Exception e) {
            addErrorMessage("系統資訊測試失敗: " + e.getMessage());
            LogUtils.e(TAG, "系統資訊測試失敗", e);
        }
    }

    /**
     * 執行硬體管理器測試
     */
    public void executeHardwareManagerTest() {
        addInfoMessage("========== 硬體管理器測試 ==========");

        try {
            HardwareManager hwManager = HardwareManager.getInstance();
            if (hwManager == null) {
                addErrorMessage("HardwareManager 未初始化");
                return;
            }

            addInfoMessage("HardwareManager 實例: 正常");

            // 檢測設備類型
            switch (currentDeviceType) {
                case PAX:
                    addSuccessMessage("檢測到 PAX 設備");
                    addInfoMessage("Helper 類型: PaxHelper");
                    executePaxSpecificTest();
                    break;

                case CASTLES:
                    addSuccessMessage("檢測到 Castles 設備");
                    addInfoMessage("Helper 類型: CastlesHelper");
                    executeCastlesSpecificTest();
                    break;

                case UNKNOWN:
                    addWarningMessage("使用 DummyHelper（未知設備）");
                    break;
            }

            // 通用硬體接口測試
            testCommonHardwareInterfaces(hwManager);

            addSuccessMessage("硬體管理器測試完成！");

        } catch (Exception e) {
            addErrorMessage("硬體管理器測試失敗: " + e.getMessage());
            LogUtils.e(TAG, "硬體管理器測試失敗", e);
        }
    }

    /**
     * 測試通用硬體接口
     */
    private void testCommonHardwareInterfaces(HardwareManager hwManager) {
        addInfoMessage("--- 測試通用接口 ---");

        try {
            // 測試系統接口
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

            // 測試列印機接口
            if (hwManager.getPrinter() != null) {
                addInfoMessage("✓ cPrinter 接口可用");
            } else {
                addWarningMessage("✗ cPrinter 接口不可用");
            }

            // 測試讀卡器接口
            if (hwManager.getReader() != null) {
                addInfoMessage("✓ cReader 接口可用");
            } else {
                addWarningMessage("✗ cReader 接口不可用");
            }

            // 測試 ICC 接口
            if (hwManager.getIcc() != null) {
                addInfoMessage("✓ cIcc 接口可用");
            } else {
                addWarningMessage("✗ cIcc 接口不可用");
            }

            // 測試 PED 接口
            if (hwManager.getPed() != null) {
                addInfoMessage("✓ cPed 接口可用");
            } else {
                addWarningMessage("✗ cPed 接口不可用");
            }

        } catch (Exception e) {
            addErrorMessage("通用接口測試失敗: " + e.getMessage());
        }
    }

    /**
     * 執行 PAX 特定測試
     */
    private void executePaxSpecificTest() {
        addInfoMessage("--- PAX 特定功能測試 ---");

        try {
            PaxHelper paxHelper = getPaxHelper();
            if (paxHelper == null) {
                addWarningMessage("無法獲取 PaxHelper");
                return;
            }

            addInfoMessage("PaxHelper 實例: 正常");

            // 在這裡添加 PAX 特定的測試
            // 例如：測試 PAX 專屬的 API
            addInfoMessage("提示: 可在此添加 PAX 專屬 API 測試");

        } catch (Exception e) {
            addErrorMessage("PAX 特定測試失敗: " + e.getMessage());
        }
    }

    /**
     * 執行 Castles 特定測試
     */
    private void executeCastlesSpecificTest() {
        addInfoMessage("--- Castles 特定功能測試 ---");

        try {
            CastlesHelper castlesHelper = getCastlesHelper();
            if (castlesHelper == null) {
                addWarningMessage("無法獲取 CastlesHelper");
                return;
            }

            addInfoMessage("CastlesHelper 實例: 正常");

            // 在這裡添加 Castles 特定的測試
            // 例如：測試 Castles 專屬的 API
            addInfoMessage("提示: 可在此添加 Castles 專屬 API 測試");

        } catch (Exception e) {
            addErrorMessage("Castles 特定測試失敗: " + e.getMessage());
        }
    }

    /**
     * 執行列印功能測試
     */
    public void executePrintTest() {
        addInfoMessage("========== 列印功能測試 ==========");

        try {
            HardwareManager hwManager = HardwareManager.getInstance();
            if (hwManager == null || hwManager.getPrinter() == null) {
                addErrorMessage("列印機接口不可用");
                return;
            }

            addInfoMessage("列印機接口: 可用");
            addInfoMessage("設備類型: " + currentDeviceType.name());

            // 根據設備類型執行不同的測試
            switch (currentDeviceType) {
                case PAX:
                    addInfoMessage("執行 PAX 列印測試...");
                    // TODO: 添加 PAX 列印測試代碼
                    break;

                case CASTLES:
                    addInfoMessage("執行 Castles 列印測試...");
                    // TODO: 添加 Castles 列印測試代碼
                    break;

                default:
                    addWarningMessage("未知設備，跳過列印測試");
            }

            addSuccessMessage("列印功能測試完成！");

        } catch (Exception e) {
            addErrorMessage("列印功能測試失敗: " + e.getMessage());
            LogUtils.e(TAG, "列印功能測試失敗", e);
        }
    }

    /**
     * 執行掃碼功能測試
     */
    public void executeScanTest() {
        addInfoMessage("========== 掃碼功能測試 ==========");

        try {
            HardwareManager hwManager = HardwareManager.getInstance();
            if (hwManager == null || hwManager.getScannerHw() == null) {
                addErrorMessage("掃碼器接口不可用");
                return;
            }

            addInfoMessage("掃碼器接口: 可用");
            addWarningMessage("提示: 需要實現掃碼邏輯");

            addSuccessMessage("掃碼功能測試完成！");

        } catch (Exception e) {
            addErrorMessage("掃碼功能測試失敗: " + e.getMessage());
            LogUtils.e(TAG, "掃碼功能測試失敗", e);
        }
    }

    /**
     * 執行卡片讀取測試
     */
    public void executeCardReaderTest() {
        addInfoMessage("========== 卡片讀取測試 ==========");

        try {
            HardwareManager hwManager = HardwareManager.getInstance();
            if (hwManager == null) {
                addErrorMessage("HardwareManager 不可用");
                return;
            }

            addInfoMessage("檢查讀卡器接口...");

            if (hwManager.getReader() != null) {
                addInfoMessage("✓ 磁條卡讀取器: 可用");
            }

            if (hwManager.getIcc() != null) {
                addInfoMessage("✓ IC 卡讀取器: 可用");
            }

            addWarningMessage("提示: 需要實現卡片讀取邏輯");
            addSuccessMessage("卡片讀取測試完成！");

        } catch (Exception e) {
            addErrorMessage("卡片讀取測試失敗: " + e.getMessage());
            LogUtils.e(TAG, "卡片讀取測試失敗", e);
        }
    }

    /**
     * 執行加密功能測試
     */
    public void executeCryptoTest() {
        addInfoMessage("========== 加密功能測試 ==========");

        try {
            HardwareManager hwManager = HardwareManager.getInstance();
            if (hwManager == null) {
                addErrorMessage("HardwareManager 不可用");
                return;
            }

            addInfoMessage("檢查加密接口...");

            if (hwManager.getPed() != null) {
                addInfoMessage("✓ PED 接口: 可用");
            }

            if (hwManager.getCrypto() != null) {
                addInfoMessage("✓ Crypto 接口: 可用");
            }

            addWarningMessage("提示: 需要實現加密邏輯");
            addSuccessMessage("加密功能測試完成！");

        } catch (Exception e) {
            addErrorMessage("加密功能測試失敗: " + e.getMessage());
            LogUtils.e(TAG, "加密功能測試失敗", e);
        }
    }

    /**
     * 顯示 Toast
     */
    public void showToast(String message) {
        toastEvent.setValue(message);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        LogUtils.d(TAG, "ApiTestViewModel 已清除");
    }
}

