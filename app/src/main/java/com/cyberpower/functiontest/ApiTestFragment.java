package com.cyberpower.functiontest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cyberpower.edc.core.base.BaseFragment;
import com.cyberpower.edc.core.basis.LogUtils;
import com.cyberpower.functiontest.databinding.FragmentApiTestBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * ApiTestFragment
 * API 測試畫面
 *
 * 功能：
 * 1. 提供下拉選單選擇測試項目
 * 2. 顯示測試項目說明
 * 3. 顯示可滾動的執行過程訊息
 * 4. 提供執行和清除按鈕
 */
public class ApiTestFragment extends BaseFragment<FragmentApiTestBinding, ApiTestViewModel> {
    private static final String TAG = "ApiTestFragment";

    private List<TestItem> testItems;
    private int selectedItemPosition = 0;

    @Override
    public int bindContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return R.layout.fragment_api_test;
    }

    @Override
    public int bindViewModelId() {
        return BR.viewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.d(TAG, "ApiTestFragment onViewCreated");

        initTestItems();
        initSpinner();
        initObservers();
        initListeners();
    }

    /**
     * 初始化測試項目資料
     */
    private void initTestItems() {
        testItems = new ArrayList<>();

        // 添加 Dummy 測試項目
        testItems.add(new TestItem(
            "系統資訊測試",
            "測試獲取設備系統資訊，包括型號、製造商、Android 版本等。"
        ));

        testItems.add(new TestItem(
            "硬體管理器測試",
            "測試 HardwareManager 初始化狀態和當前使用的 Helper 類型。"
        ));

        testItems.add(new TestItem(
            "列印功能測試",
            "測試列印機初始化、狀態檢查和簡單列印功能。"
        ));

        testItems.add(new TestItem(
            "掃碼功能測試",
            "測試條碼掃描器和 QR Code 掃描功能。"
        ));

        testItems.add(new TestItem(
            "卡片讀取測試",
            "測試 IC 卡、磁條卡和非接觸式卡片讀取功能。"
        ));

        testItems.add(new TestItem(
            "網路連接測試",
            "測試網路連接狀態、WiFi 和行動數據功能。"
        ));

        testItems.add(new TestItem(
            "加密功能測試",
            "測試加密模組的初始化和基本加密解密功能。"
        ));

        testItems.add(new TestItem(
            "綜合壓力測試",
            "連續執行多個測試項目，檢查系統穩定性。"
        ));

        LogUtils.d(TAG, "已初始化 " + testItems.size() + " 個測試項目");
    }

    /**
     * 初始化下拉選單
     */
    private void initSpinner() {
        // 準備顯示名稱列表
        List<String> itemNames = new ArrayList<>();
        for (TestItem item : testItems) {
            itemNames.add(item.name);
        }

        // 創建 Adapter
        ArrayAdapter<String> testItemsAdapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            itemNames
        );
        testItemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 設定到 Spinner
        binding.spinnerTestItems.setAdapter(testItemsAdapter);

        // 設定選擇事件
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

        LogUtils.d(TAG, "Spinner 初始化完成");
    }

    /**
     * 處理測試項目選擇
     */
    private void onTestItemSelected(int position) {
        if (position >= 0 && position < testItems.size()) {
            selectedItemPosition = position;
            TestItem item = testItems.get(position);
            viewModel.setTestDescription(item.description);
            LogUtils.d(TAG, "選擇測試項目: " + item.name);
        }
    }

    /**
     * 初始化觀察者
     */
    private void initObservers() {
        // 觀察測試項目說明
        viewModel.testDescription.observe(getViewLifecycleOwner(), description -> {
            if (description != null) {
                binding.tvTestDescription.setText(description);
            }
        });

        // 觀察日誌訊息
        viewModel.logMessages.observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                binding.tvLogMessages.setText(messages);

                // 自動滾動到底部
                binding.tvLogMessages.post(() -> {
                    ScrollView scrollView = (ScrollView) binding.tvLogMessages.getParent();
                    scrollView.fullScroll(View.FOCUS_DOWN);
                });
            }
        });

        // 觀察 Toast 事件
        viewModel.toastEvent.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // 觀察執行事件
        viewModel.executeClickEvent.observe(getViewLifecycleOwner(), unused ->
            performTest(selectedItemPosition)
        );

        // 觀察清除事件
        viewModel.clearLogClickEvent.observe(getViewLifecycleOwner(), unused ->
            viewModel.clearLogMessages()
        );

        LogUtils.d(TAG, "觀察者初始化完成");
    }

    /**
     * 初始化監聽器
     */
    private void initListeners() {
        // 執行按鈕
        binding.btnExecute.setOnClickListener(v -> {
            if (selectedItemPosition >= 0 && selectedItemPosition < testItems.size()) {
                TestItem item = testItems.get(selectedItemPosition);
                viewModel.executeTest(item.name);
            }
        });

        // 清除按鈕
        binding.btnClearLog.setOnClickListener(v -> viewModel.clearLogClickEvent.call());

        LogUtils.d(TAG, "監聽器初始化完成");
    }

    /**
     * 執行測試
     */
    private void performTest(int position) {
        if (position < 0 || position >= testItems.size()) {
            viewModel.addErrorMessage("無效的測試項目");
            return;
        }

        TestItem item = testItems.get(position);
        LogUtils.d(TAG, "開始執行測試: " + item.name);

        // 根據不同的測試項目執行不同的測試邏輯
        switch (position) {
            case 0: // 系統資訊測試
                performSystemInfoTest();
                break;

            case 1: // 硬體管理器測試
                performHardwareManagerTest();
                break;

            case 2: // 列印功能測試
                performPrintTest();
                break;

            case 3: // 掃碼功能測試
                performScanTest();
                break;

            case 4: // 卡片讀取測試
                performCardReaderTest();
                break;

            case 5: // 網路連接測試
                performNetworkTest();
                break;

            case 6: // 加密功能測試
                performCryptoTest();
                break;

            case 7: // 綜合壓力測試
                performStressTest();
                break;

            default:
                viewModel.addWarningMessage("測試項目尚未實現: " + item.name);
                viewModel.showToast("此測試功能開發中");
        }
    }

    /**
     * 系統資訊測試
     */
    private void performSystemInfoTest() {
        viewModel.addInfoMessage("========== 系統資訊測試 ==========");

        try {
            viewModel.addInfoMessage("設備型號: " + android.os.Build.MODEL);
            viewModel.addInfoMessage("製造商: " + android.os.Build.MANUFACTURER);
            viewModel.addInfoMessage("Android 版本: " + android.os.Build.VERSION.RELEASE);
            viewModel.addInfoMessage("SDK 版本: " + android.os.Build.VERSION.SDK_INT);
            viewModel.addInfoMessage("設備名稱: " + android.os.Build.DEVICE);

            viewModel.addSuccessMessage("系統資訊測試完成！");
        } catch (Exception e) {
            viewModel.addErrorMessage("系統資訊測試失敗: " + e.getMessage());
            LogUtils.e(TAG, "系統資訊測試失敗", e);
        }
    }

    /**
     * 硬體管理器測試
     */
    private void performHardwareManagerTest() {
        viewModel.addInfoMessage("========== 硬體管理器測試 ==========");
        viewModel.addWarningMessage("此功能需要實現 HardwareManager 檢測");
        viewModel.addInfoMessage("提示: 在這裡添加 HardwareManager 相關測試代碼");
    }

    /**
     * 列印功能測試
     */
    private void performPrintTest() {
        viewModel.addInfoMessage("========== 列印功能測試 ==========");
        viewModel.addWarningMessage("此功能需要實現列印機測試");
        viewModel.addInfoMessage("提示: 在這裡添加列印功能測試代碼");
    }

    /**
     * 掃碼功能測試
     */
    private void performScanTest() {
        viewModel.addInfoMessage("========== 掃碼功能測試 ==========");
        viewModel.addWarningMessage("此功能需要實現掃碼器測試");
        viewModel.addInfoMessage("提示: 在這裡添加掃碼功能測試代碼");
    }

    /**
     * 卡片讀取測試
     */
    private void performCardReaderTest() {
        viewModel.addInfoMessage("========== 卡片讀取測試 ==========");
        viewModel.addWarningMessage("此功能需要實現卡片讀取器測試");
        viewModel.addInfoMessage("提示: 在這裡添加卡片讀取測試代碼");
    }

    /**
     * 網路連接測試
     */
    private void performNetworkTest() {
        viewModel.addInfoMessage("========== 網路連接測試 ==========");
        viewModel.addWarningMessage("此功能需要實現網路連接測試");
        viewModel.addInfoMessage("提示: 在這裡添加網路測試代碼");
    }

    /**
     * 加密功能測試
     */
    private void performCryptoTest() {
        viewModel.addInfoMessage("========== 加密功能測試 ==========");
        viewModel.addWarningMessage("此功能需要實現加密功能測試");
        viewModel.addInfoMessage("提示: 在這裡添加加密測試代碼");
    }

    /**
     * 綜合壓力測試
     */
    private void performStressTest() {
        viewModel.addInfoMessage("========== 綜合壓力測試 ==========");
        viewModel.addWarningMessage("此功能需要實現壓力測試");
        viewModel.addInfoMessage("提示: 連續執行多個測試項目");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.d(TAG, "ApiTestFragment onDestroyView");
    }

    /**
     * 測試項目資料類別
     */
    private static class TestItem {
        String name;
        String description;

        TestItem(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}

