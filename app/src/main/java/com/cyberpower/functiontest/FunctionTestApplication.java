package com.cyberpower.functiontest;

import android.app.Application;

import com.cyberpower.edc.core.Core;
import com.cyberpower.edc.core.base.BaseApplication;
import com.cyberpower.edc.core.basis.AppManager;
import com.cyberpower.edc.core.basis.LogUtils;

/**
 * FunctionTestApplication
 * 應用程式入口，負責初始化 Core 和相關服務
 */
public class FunctionTestApplication extends Application {
    private static final String TAG = "FunctionTestApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化 BaseApplication（Stetho debug 工具）
        BaseApplication.setApplication(this);

        // 初始化 Core（XLog、HardwareManager、PAX libraries）
        Core.init(this);

        // 初始化完成後才能使用 LogUtils（因為 XLog 在 Core.init 中初始化）
        LogUtils.d(TAG, "Application onCreate - 初始化完成");

        // 初始化 AppManager（Activity 堆疊管理）
        AppManager.getAppManager();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtils.d(TAG, "Application onTerminate");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtils.w(TAG, "Application onLowMemory - 系統記憶體不足");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        LogUtils.w(TAG, "Application onTrimMemory - level: " + level);

        // 根據記憶體壓力等級進行清理
        if (level >= TRIM_MEMORY_MODERATE) {
            // 中等記憶體壓力，清理一些快取
            LogUtils.w(TAG, "清理快取以釋放記憶體");
        }
    }
}

