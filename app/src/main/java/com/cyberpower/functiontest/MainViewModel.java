package com.cyberpower.functiontest;

import android.app.Application;

import androidx.annotation.NonNull;

import com.cyberpower.edc.core.base.BaseViewModel;
import com.cyberpower.edc.core.basis.LogUtils;
import com.cyberpower.edc.core.customobject.SingleLiveEvent;

/**
 * MainViewModel
 * 主畫面的 ViewModel，處理業務邏輯與資料
 * 注意：權限檢查已移至 SplashActivity/SplashViewModel
 */
public class MainViewModel extends BaseViewModel {
    private static final String TAG = "MainViewModel";

    // Drawer 開關事件
    public SingleLiveEvent<Boolean> drawerEvent = new SingleLiveEvent<>();
    
    // 導航事件（用於處理 drawer 項目點擊）
    public SingleLiveEvent<String> navigationEvent = new SingleLiveEvent<>();
    
    // 標題更新事件（用於更新 Action Bar 標題）
    public SingleLiveEvent<String> titleUpdateEvent = new SingleLiveEvent<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        LogUtils.d(TAG, "MainViewModel 初始化");
    }

    /**
     * 開啟或關閉 Drawer
     */
    public void toggleDrawer() {
        drawerEvent.setValue(true);
        LogUtils.d(TAG, "觸發 Drawer 切換");
    }

    /**
     * 處理導航項目點擊
     * @param itemName 項目名稱
     */
    public void onNavigationItemClick(String itemName) {
        navigationEvent.setValue(itemName);
        titleUpdateEvent.setValue(itemName);  // 更新標題
        LogUtils.d(TAG, "導航項目點擊: " + itemName);
    }


    /**
     * 執行功能測試
     * @param testName 測試名稱
     */
    public void performTest(String testName) {
        LogUtils.d(TAG, "執行測試: " + testName);
        toastEvent.setValue("開始測試: " + testName);
        
        // TODO: 在此添加實際的測試邏輯
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        LogUtils.d(TAG, "MainViewModel 清理");
    }
}

