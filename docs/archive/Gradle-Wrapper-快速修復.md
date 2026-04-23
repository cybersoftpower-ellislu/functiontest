# Gradle Wrapper 快速修復指南

## 🎯 當前狀態

✅ **已完成**：
- `gradlew` - Unix/Linux/Mac 腳本
- `gradlew.bat` - Windows 批次檔
- `gradle/wrapper/gradle-wrapper.properties` - 配置檔案

❌ **缺少**：
- `gradle/wrapper/gradle-wrapper.jar` - 核心 wrapper 檔案（需要約 60KB）

## 💡 最簡單的解決方案

### ✅ 方法 1：使用 Android Studio（強烈推薦）

這是**最快速且最可靠**的方法：

1. **開啟 Android Studio**

2. **開啟專案**
   - File → Open
   - 選擇：`C:\DevAndroid\functiontest`

3. **等待自動同步**
   - Android Studio 會自動檢測並下載 `gradle-wrapper.jar`
   - 通常在 5-30 秒內完成
   - 看到 "Gradle sync finished" 即完成

4. **驗證**
   ```powershell
   cd C:\DevAndroid\functiontest
   .\gradlew.bat --version
   ```

**為什麼推薦這個方法？**
- ✅ 自動化，不會出錯
- ✅ 確保版本正確
- ✅ 處理所有依賴
- ✅ 5 秒內完成

---

### 🔧 方法 2：手動下載（如果 Android Studio 不可用）

#### 選項 A：從 Services Gradle 下載

```powershell
# 1. 進入專案目錄
cd C:\DevAndroid\functiontest

# 2. 下載 wrapper jar
$url = "https://services.gradle.org/distributions/gradle-8.0-bin.zip"
$zipPath = "$env:TEMP\gradle-8.0-bin.zip"
$extractPath = "$env:TEMP\gradle-8.0"

# 下載 Gradle 完整發行版
Invoke-WebRequest -Uri $url -OutFile $zipPath

# 解壓縮
Expand-Archive -Path $zipPath -DestinationPath $extractPath -Force

# 複製 wrapper jar
Copy-Item "$extractPath\gradle-8.0\lib\plugins\gradle-wrapper-8.0.jar" `
          "gradle\wrapper\gradle-wrapper.jar" -Force

# 清理
Remove-Item $zipPath -Force
Remove-Item $extractPath -Recurse -Force

Write-Host "✅ 完成！" -ForegroundColor Green
```

#### 選項 B：從其他 Gradle 專案複製

如果您有其他 Android 專案：

```powershell
# 假設其他專案在 C:\DevAndroid\other-project
$sourcePath = "C:\DevAndroid\other-project\gradle\wrapper\gradle-wrapper.jar"
$destPath = "C:\DevAndroid\functiontest\gradle\wrapper\gradle-wrapper.jar"

if (Test-Path $sourcePath) {
    Copy-Item $sourcePath $destPath -Force
    Write-Host "✅ 複製成功！" -ForegroundColor Green
} else {
    Write-Host "❌ 找不到來源檔案" -ForegroundColor Red
}
```

#### 選項 C：使用瀏覽器手動下載

1. **下載網址**（約 119MB）：
   ```
   https://services.gradle.org/distributions/gradle-8.0-bin.zip
   ```

2. **解壓縮**後找到檔案：
   ```
   gradle-8.0\lib\plugins\gradle-wrapper-8.0.jar
   ```

3. **重命名並複製到**：
   ```
   C:\DevAndroid\functiontest\gradle\wrapper\gradle-wrapper.jar
   ```

---

## ⚡ 測試 Gradle Wrapper

完成後，測試是否正常運作：

```powershell
cd C:\DevAndroid\functiontest

# 檢查版本
.\gradlew.bat --version

# 列出可用任務
.\gradlew.bat tasks

# 清理專案
.\gradlew.bat clean

# 編譯 Debug 版本
.\gradlew.bat assembleDebug
```

## 🎓 關於環境變數

### ❌ 不需要設定 Gradle 環境變數

Gradle Wrapper 的設計就是為了**不需要安裝 Gradle**！

您只需要：
- ✅ JAVA_HOME（Android Studio 已包含）
- ✅ 專案中的 wrapper 檔案

### ✅ 如果需要檢查 JAVA_HOME

```powershell
# 檢查 JAVA_HOME
$env:JAVA_HOME

# 如果是空的，通常 Android Studio 的 Java 在這裡：
# C:\Program Files\Android\Android Studio\jbr
```

---

## 📋 常見問題

### Q1: 為什麼專案沒有 gradle-wrapper.jar？

**A:** 可能原因：
- Git 忽略了這個檔案（應該加入版控）
- 專案是從不完整的來源複製的
- 第一次建立專案時漏掉了

### Q2: 我可以使用不同版本的 gradle-wrapper.jar 嗎？

**A:** 可以！wrapper jar 是向後相容的。任何 Gradle 6.0+ 的 wrapper jar 都可以用。

### Q3: 執行 gradlew.bat 時說找不到 Java？

**A:** 設定 JAVA_HOME：
```powershell
# 臨時設定（當前視窗有效）
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"

# 永久設定（需要重新開啟 PowerShell）
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", 
    "C:\Program Files\Android\Android Studio\jbr", 
    [System.EnvironmentVariableTarget]::User)
```

---

## 🚀 推薦動作

**現在立即執行：**

1. 開啟 Android Studio
2. File → Open → 選擇 `C:\DevAndroid\functiontest`
3. 等待 Gradle Sync 完成（會自動下載 gradle-wrapper.jar）
4. 在 Terminal 執行：`.\gradlew.bat --version`

**預期結果：**
```
------------------------------------------------------------
Gradle 8.0
------------------------------------------------------------

Build time:   2023-02-03 17:00:35 UTC
Revision:     xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

Kotlin:       1.7.10
Groovy:       3.0.13
Ant:          Apache Ant(TM) version 1.10.11 compiled on July 10 2021
JVM:          17.0.x (JetBrains s.r.o. ...)
OS:           Windows 11 10.0 amd64
```

---

## 📦 檔案檢查清單

執行此腳本檢查所有必要檔案：

```powershell
$files = @(
    "gradlew",
    "gradlew.bat",
    "gradle\wrapper\gradle-wrapper.jar",
    "gradle\wrapper\gradle-wrapper.properties"
)

Write-Host "`n檢查 Gradle Wrapper 檔案：`n" -ForegroundColor Cyan

foreach ($file in $files) {
    $path = "C:\DevAndroid\functiontest\$file"
    if (Test-Path $path) {
        $size = (Get-Item $path).Length
        Write-Host "✅ $file" -ForegroundColor Green -NoNewline
        Write-Host " ($size bytes)" -ForegroundColor Gray
    } else {
        Write-Host "❌ $file (缺少)" -ForegroundColor Red
    }
}

Write-Host ""
```

---

## 🆘 如果還是不行

告訴我以下資訊，我會提供更具體的幫助：

1. Android Studio 版本
2. 執行 `.\gradlew.bat --version` 的完整錯誤訊息
3. Java 安裝位置（`$env:JAVA_HOME`）
4. 是否能正常開啟並同步專案

---

**結論**：最簡單的方式就是用 Android Studio 打開專案，它會自動處理一切！

