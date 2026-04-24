package com.cyberpower.functiontest;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.cyberpower.edc.core.base.BaseViewModel;
import com.cyberpower.edc.core.basis.LogUtils;
import com.cyberpower.edc.core.customobject.SingleLiveEvent;

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

    public ApiTestViewModel(@NonNull Application application) {
        super(application);
        LogUtils.d(TAG, "ApiTestViewModel 已創建");
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
     * 執行測試
     */
    public void executeTest(String testItem) {
        LogUtils.d(TAG, "執行測試: " + testItem);
        addInfoMessage("開始執行測試: " + testItem);

        // 這裡可以根據不同的測試項目執行不同的邏輯
        // 目前只是示範
        executeClickEvent.call();
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

