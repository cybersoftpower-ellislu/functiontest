package com.cyberpower.functiontest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cyberpower.edc.core.base.BaseActivity;
import com.cyberpower.edc.core.basis.AppManager;
import com.cyberpower.edc.core.basis.LogUtils;
import com.cyberpower.functiontest.databinding.ActivitySplashBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * SplashActivity - 應用程式啟動畫面
 *
 * 功能：
 * 1. 避免冷啟動時的空白畫面
 * 2. 顯示應用程式 Logo
 * 3. 執行權限檢查
 * 4. 執行初始化工作
 * 5. 自動跳轉到 MainActivity
 */
public class SplashActivity extends BaseActivity<ActivitySplashBinding, SplashViewModel> {
    private static final String TAG = "SplashActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 1001;

    // 啟動畫面最短顯示時間（毫秒）
    private static final long MIN_SPLASH_DISPLAY_TIME = 1500; // 1.5秒

    // 需要的權限清單
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private long startTime;
    private boolean permissionChecked = false;

    @Override
    public int bindViewModelId() {
        return BR.viewModel;
    }

    @Override
    public int bindContentView(Bundle savedInstanceState) {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "SplashActivity onCreate - 啟動畫面開始");

        // 記錄啟動時間
        startTime = System.currentTimeMillis();

        // 添加到 Activity 堆疊管理
        AppManager.getAppManager().addActivity(this);

        initObservers();
        
        // 開始權限檢查
        checkAndRequestPermissions();
    }

    /**
     * 初始化觀察者
     */
    private void initObservers() {
        // 觀察 Toast 事件
        viewModel.toastEvent.observe(this, message -> {
            if (message != null) {
                android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // 觀察權限檢查完成事件
        viewModel.permissionCheckCompleteEvent.observe(this, allGranted -> {
            if (allGranted != null) {
                permissionChecked = true;
                if (allGranted) {
                    LogUtils.d(TAG, "所有權限已授予，準備跳轉");
                    navigateToMainWithDelay();
                } else {
                    LogUtils.w(TAG, "部分權限未授予，仍然繼續");
                    // 即使權限未完全授予，也允許進入主畫面（可選策略）
                    navigateToMainWithDelay();
                }
            }
        });

        // 觀察跳轉到主畫面事件
        viewModel.navigateToMainEvent.observe(this, unused -> {
            navigateToMain();
        });
    }

    /**
     * 檢查並請求權限
     */
    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsToRequest = new ArrayList<>();

            for (String permission : REQUIRED_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }

            if (!permissionsToRequest.isEmpty()) {
                LogUtils.d(TAG, "請求權限: " + permissionsToRequest.size() + " 項");
                ActivityCompat.requestPermissions(
                        this,
                        permissionsToRequest.toArray(new String[0]),
                        REQUEST_CODE_PERMISSIONS
                );
            } else {
                LogUtils.d(TAG, "所有權限已授予");
                viewModel.onPermissionResult(true);
            }
        } else {
            // Android 6.0 以下不需要動態請求權限
            LogUtils.d(TAG, "Android 6.0 以下，跳過權限檢查");
            viewModel.onPermissionResult(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allGranted = true;
            List<String> deniedPermissions = new ArrayList<>();

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    deniedPermissions.add(permissions[i]);
                }
            }

            LogUtils.d(TAG, "權限請求結果: " + (allGranted ? "全部授予" : "部分拒絕"));
            if (!allGranted) {
                LogUtils.w(TAG, "被拒絕的權限: " + deniedPermissions);
            }

            viewModel.onPermissionResult(allGranted);

            if (!allGranted) {
                // 顯示說明對話框
                showPermissionDeniedDialog();
            }
        }
    }

    /**
     * 顯示權限被拒絕的對話框
     */
    private void showPermissionDeniedDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("權限需求")
                .setMessage("應用程式需要相關權限才能正常運作。\n某些功能可能無法使用。")
                .setPositiveButton("確定", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    /**
     * 確保最短顯示時間後再跳轉
     */
    private void navigateToMainWithDelay() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        long remainingTime = MIN_SPLASH_DISPLAY_TIME - elapsedTime;

        if (remainingTime > 0) {
            // 還沒到最短顯示時間，延遲執行
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                viewModel.startNavigation();
            }, remainingTime);
            LogUtils.d(TAG, "延遲 " + remainingTime + "ms 後跳轉");
        } else {
            // 已超過最短顯示時間，立即跳轉
            viewModel.startNavigation();
        }
    }

    /**
     * 跳轉到主畫面
     */
    private void navigateToMain() {
        LogUtils.d(TAG, "跳轉到 MainActivity");
        
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);

        // 結束 SplashActivity
        finish();

        // 添加淡入淡出轉場動畫
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        // 在啟動畫面期間禁用返回鍵
        // 不執行任何操作
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "SplashActivity onDestroy");
    }
}

