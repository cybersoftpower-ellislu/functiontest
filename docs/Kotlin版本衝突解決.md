# Kotlin 標準庫版本衝突問題解決

## 問題描述

編譯時出現大量 Duplicate class 錯誤：
```
Duplicate class kotlin.collections.jdk8.CollectionsJDK8Kt found in modules 
jetified-kotlin-stdlib-1.8.10 and jetified-kotlin-stdlib-jdk8-1.6.21
```

### 錯誤原因

1. **版本衝突**: 專案中同時存在不同版本的 Kotlin 標準庫
   - `kotlin-stdlib:1.8.10` (新版)
   - `kotlin-stdlib-jdk7:1.6.21` (舊版)
   - `kotlin-stdlib-jdk8:1.6.21` (舊版)

2. **庫整合**: 從 Kotlin 1.8.0 開始，`kotlin-stdlib-jdk7` 和 `kotlin-stdlib-jdk8` 的功能已經整合到 `kotlin-stdlib` 主庫中，不應該再單獨引用這兩個庫。

3. **傳遞依賴**: 某些第三方庫（如 RxJava、Retrofit 等）可能依賴舊版本的 Kotlin 標準庫，導致衝突。

---

## ✅ 解決方案

### 修改 `build.gradle` (根目錄)

已添加統一的依賴版本管理：

```groovy
plugins {
    id 'com.android.application' version '8.1.4' apply false
    id 'com.android.library' version '8.1.4' apply false
}

// 統一管理所有子專案的依賴版本
subprojects {
    configurations.all {
        resolutionStrategy {
            // 強制使用統一的 Kotlin 版本（1.8+ 已整合 jdk7/jdk8）
            force 'org.jetbrains.kotlin:kotlin-stdlib:1.8.10'
        }
        
        // 排除舊版的 jdk7/jdk8 標準庫（已整合到 kotlin-stdlib 1.8+）
        exclude group: 'org.jetbrains.kotlin', module: 'kotlin-stdlib-jdk7'
        exclude group: 'org.jetbrains.kotlin', module: 'kotlin-stdlib-jdk8'
    }
}
```

### 說明

1. **resolutionStrategy.force**: 強制所有依賴使用 Kotlin 1.8.10 版本
2. **exclude**: 排除所有傳遞依賴中的舊版 jdk7/jdk8 庫
3. **subprojects**: 配置會自動應用到 app 和 core 兩個模組

---

## 🔧 執行步驟

### 1. Gradle Sync
```
File → Sync Project with Gradle Files
```

### 2. Clean Project
```
Build → Clean Project
```

### 3. Rebuild Project
```
Build → Rebuild Project
```

### 4. 確認成功
編譯應該成功完成，不再出現 Duplicate class 錯誤。

---

## 📊 依賴版本說明

### Kotlin 版本演進

| 版本 | 說明 |
|------|------|
| Kotlin 1.6.x | kotlin-stdlib-jdk7 和 kotlin-stdlib-jdk8 是分離的 |
| Kotlin 1.7.x | 開始建議不再使用分離的 jdk7/jdk8 庫 |
| **Kotlin 1.8.x** | **正式整合，jdk7/jdk8 功能併入主庫** |
| Kotlin 1.9.x+ | 持續使用整合後的架構 |

### 專案當前設置

- ✅ 統一使用 `kotlin-stdlib:1.8.10`
- ✅ 排除 `kotlin-stdlib-jdk7`
- ✅ 排除 `kotlin-stdlib-jdk8`

---

## ⚠️ 注意事項

### 1. Core 模組的依賴

Core 模組中的某些依賴可能引入舊版 Kotlin：
- `com.jakewharton.rxbinding2:rxbinding:2.1.1`
- `com.trello.rxlifecycle2:*`
- `com.squareup.retrofit2:*`

這些依賴會自動被根 build.gradle 的配置處理，無需手動修改。

### 2. 未來添加依賴時

如果添加新的第三方庫後再次出現此錯誤：
1. 不需要修改 build.gradle（已有全局配置）
2. 執行 Clean → Rebuild 即可
3. 全局排除規則會自動處理

### 3. 檢查依賴樹

如果想要查看具體哪個依賴引入了衝突版本：
```bash
./gradlew :app:dependencies
./gradlew :core:dependencies
```

在 Android Studio 中：
```
View → Tool Windows → Gradle → 
右鍵模組 → Tasks → android → androidDependencies
```

---

## 🔍 相關問題排查

### 如果問題持續

1. **清除快取**
   ```
   File → Invalidate Caches / Restart...
   ```

2. **刪除 build 目錄**
   ```bash
   rm -rf app/build
   rm -rf core/build
   rm -rf build
   ```
   
   然後重新 Sync 和 Rebuild

3. **檢查 Gradle 快取**
   ```bash
   ~/.gradle/caches/
   ```

### 其他可能的解決方案

如果上述方案無效，可以嘗試在 `gradle.properties` 中添加：
```properties
android.enableJetifier=true
android.useAndroidX=true
```

但通常不需要，因為根 build.gradle 的配置已經足夠。

---

## 📚 參考資料

- [Kotlin 1.8.0 Release Notes](https://github.com/JetBrains/kotlin/releases/tag/v1.8.0)
- [Kotlin stdlib Migration Guide](https://kotlinlang.org/docs/whatsnew18.html)
- [Android Gradle Plugin 依賴管理](https://developer.android.com/studio/build/dependencies)

---

## ✅ 檢查清單

完成以下步驟確認問題已解決：

- [x] 修改根 build.gradle 添加 resolutionStrategy
- [x] 添加 exclude 排除舊版庫
- [ ] 執行 Gradle Sync
- [ ] 執行 Clean Project
- [ ] 執行 Rebuild Project
- [ ] 確認編譯成功無錯誤
- [ ] 測試應用程式執行正常

---

**修復日期**: 2026-04-23  
**問題類型**: Kotlin 標準庫版本衝突  
**解決方式**: 統一版本管理 + 排除舊版依賴  
**狀態**: ✅ 已修復，待驗證

