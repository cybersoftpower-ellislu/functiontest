package com.cyberpower.functiontest;

import android.os.Bundle;
import android.view.View;

import androidx.core.view.GravityCompat;

import com.cyberpower.edc.core.base.BaseActivity;
import com.cyberpower.edc.core.basis.AppManager;
import com.cyberpower.edc.core.basis.LogUtils;
import com.cyberpower.functiontest.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity
 * 主畫面，包含兩層 Drawer 導航
 * 注意：權限檢查已在 SplashActivity 中完成
 */
public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    private static final String TAG = "MainActivity";

    private List<DrawerGroup> drawerData;

    @Override
    public int bindViewModelId() {
        return BR.viewModel;
    }

    @Override
    public int bindContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "MainActivity onCreate");

        // 添加到 Activity 堆疊管理
        AppManager.getAppManager().addActivity(this);

        initViews();
        initObservers();
    }

    /**
     * 初始化畫面元件
     */
    private void initViews() {
        // 設定工具列標題為"功能測試"（使用自定義 TextView 置中顯示）
        binding.toolbarTitle.setText(R.string.function_test_title);

        // 隱藏 Toolbar 預設標題
        binding.toolbar.setTitle("");

        binding.toolbar.setNavigationIcon(R.drawable.ic_menu);
        binding.toolbar.setNavigationOnClickListener(v -> {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
                // 打開選單時恢復預設標題
                binding.toolbarTitle.setText(R.string.function_test_title);
            } else {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // 初始化 Drawer 資料
        initDrawerData();

        // 設定 ExpandableListView
        DrawerAdapter drawerAdapter = new DrawerAdapter(this, drawerData);
        binding.expandableListView.setAdapter(drawerAdapter);

        // 設定群組點擊事件
        binding.expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            DrawerGroup group = drawerData.get(groupPosition);
            if (group.children.isEmpty()) {
                // 沒有子項目，直接處理點擊
                handleNavigationClick(group.name);
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            return false; // 有子項目，展開/收合
        });

        // 設定子項目點擊事件
        binding.expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            DrawerGroup group = drawerData.get(groupPosition);
            String childName = group.children.get(childPosition);
            handleNavigationClick(childName);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // 設定主畫面按鈕點擊事件
        binding.btnTest.setOnClickListener(this);

        // 設定底部"離開"按鈕點擊事件
        binding.btnExit.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            exitApp();
        });
    }

    /**
     * 初始化 Drawer 資料（兩層結構）
     */
    private void initDrawerData() {
        drawerData = new ArrayList<>();

        // 第一層：硬體測試
        DrawerGroup hardwareGroup = new DrawerGroup("硬體測試");
        hardwareGroup.addChild("列印測試");
        hardwareGroup.addChild("掃碼測試");
        hardwareGroup.addChild("卡片讀取測試");
        drawerData.add(hardwareGroup);

        // 第一層：系統測試
        DrawerGroup systemGroup = new DrawerGroup("系統測試");
        systemGroup.addChild("網路測試");
        systemGroup.addChild("儲存測試");
        systemGroup.addChild("螢幕測試");
        drawerData.add(systemGroup);

        // 第一層：設定（無子項目）
        DrawerGroup settingsGroup = new DrawerGroup("設定");
        drawerData.add(settingsGroup);

        // 第一層：關於（無子項目）
        DrawerGroup aboutGroup = new DrawerGroup("關於");
        drawerData.add(aboutGroup);
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

        // 觀察 Drawer 事件
        viewModel.drawerEvent.observe(this, shouldOpen -> {
            if (shouldOpen != null && shouldOpen) {
                if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        // 觀察導航事件
        viewModel.navigationEvent.observe(this, itemName -> {
            if (itemName != null) {
                binding.tvContent.setText("當前功能: " + itemName);
            }
        });

        // 觀察標題更新事件
        viewModel.titleUpdateEvent.observe(this, title -> {
            if (title != null) {
                binding.toolbarTitle.setText(title);
                LogUtils.d(TAG, "更新 Action Bar 標題: " + title);
            }
        });

        // 觀察結束事件
        viewModel.finishEvent.observe(this, unused -> finish());
    }

    /**
     * 處理導航項目點擊
     */
    private void handleNavigationClick(String itemName) {
        LogUtils.d(TAG, "點擊: " + itemName);

        viewModel.onNavigationItemClick(itemName);
        viewModel.performTest(itemName);
    }

    @Override
    protected void onClickProtected(View v) {
        super.onClickProtected(v);
        if (v.getId() == R.id.btn_test) {
            viewModel.performTest("測試按鈕");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "MainActivity onDestroy");
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // 按返回鍵時恢復預設標題
            binding.toolbarTitle.setText(R.string.function_test_title);
            super.onBackPressed();
        }
    }

    /**
     * 結束應用程式
     */
    private void exitApp() {
        LogUtils.d(TAG, "使用者選擇離開應用程式");

        // 顯示確認對話框
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("離開應用程式")
                .setMessage("確定要離開嗎？")
                .setPositiveButton("確定", (dialog, which) -> {
                    // 關閉所有 Activity 並結束應用程式
                    AppManager.getAppManager().AppExit();
                    finish();
                    // 完全終止應用程式進程
                    System.exit(0);
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    /**
     * Drawer 群組資料類別
     */
    public static class DrawerGroup {
        public String name;
        public List<String> children;

        public DrawerGroup(String name) {
            this.name = name;
            this.children = new ArrayList<>();
        }

        public void addChild(String child) {
            this.children.add(child);
        }
    }
}
