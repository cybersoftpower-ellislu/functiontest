package com.cyberpower.functiontest;

import android.app.Application;

import androidx.annotation.NonNull;

import com.cyberpower.edc.core.base.BaseViewModel;
import com.cyberpower.edc.core.basis.LogUtils;
import com.cyberpower.edc.core.customobject.SingleLiveEvent;

/**
 * SplashViewModel
 * Splash 畫面的 ViewModel，處理初始化和權限
 */
public class SplashViewModel extends BaseViewModel {
    private static final String TAG = "SplashViewModel";

    // 權限檢查完成事件
    public SingleLiveEvent<Boolean> permissionCheckCompleteEvent = new SingleLiveEvent<>();

    // 跳轉到主畫面事件
    public SingleLiveEvent<Void> navigateToMainEvent = new SingleLiveEvent<>();

    public SplashViewModel(@NonNull Application application) {
        super(application);
        LogUtils.d(TAG, "SplashViewModel 初始化");
    }

    /**
     * 處理權限請求結果
     * @param allGranted 是否全部授予
     */
    public void onPermissionResult(boolean allGranted) {
        LogUtils.d(TAG, "權限結果: " + (allGranted ? "全部授予" : "部分拒絕"));
        permissionCheckCompleteEvent.setValue(allGranted);

        if (allGranted) {
            // 權限已授予，準備跳轉
            toastEvent.setValue("權限已授予，正在啟動...");
        } else {
            toastEvent.setValue("部分權限未授予，可能影響功能使用");
        }
    }

    /**
     * 開始跳轉到主畫面
     */
    public void startNavigation() {
        LogUtils.d(TAG, "準備跳轉到 MainActivity");
        navigateToMainEvent.call();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        LogUtils.d(TAG, "SplashViewModel 清理");
    }
}

