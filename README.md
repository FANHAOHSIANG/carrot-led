# Carrot LED

把 C4 車況轉成後窗 LED 提示的 Android／iPhone 原生 App。支援 **LOY PLAY 96×16** 與 **iPixel Color 64×16**，手機透過區域網路讀取 C4，再以 BLE 傳送文字與動畫。

C4 橋接僅讀取 `carState`，不控制油門、煞車、轉向，也不發送 CAN 指令。

## 最新版本與下載

| 平台 | 版本／檔案 | 安裝方式 |
| --- | --- | --- |
| Android 手機／車機 | [0.1.11 APK](https://raw.githubusercontent.com/FANHAOHSIANG/carrot-led/main/downloads/CarrotLED-0.1.11.apk) | 下載側載；0.1.9／0.1.10 同簽章可覆蓋 |
| iPhone | [0.32 完整 Xcode 專案 ZIP](https://raw.githubusercontent.com/FANHAOHSIANG/carrot-led/main/downloads/CarrotLED-iOS-0.32-Source.zip) | Mac + Xcode 26+，可使用免費 Personal Team 簽署 |

iOS ZIP **不是可直接點擊安裝的 IPA，也不是 TestFlight**。下載校驗見 [SHA256SUMS](downloads/SHA256SUMS)。

- **[介紹、安裝指令與 C4 金鑰取得](docs/INSTALL.md)**
- **[兩平台功能對照、差異與驗證範圍](docs/PARITY.md)**
- **[背景、通知、區網問題排查](docs/TROUBLESHOOTING.md)**
- [測試證據與待驗證項目](docs/VERIFICATION.md)
- [本次更新](CHANGELOG.md) · [第三方來源](THIRD_PARTY.md)

## 兩平台共同功能

- R 檔倒車、左右方向燈、煞車／持續動能回收、雙黃燈、感謝提示與急煞提示；共同車況測試資料驗證優先順序。
- 急煞門檻：D 檔、至少 20 km/h、加速度 ≤ −4.5 m/s² 持續至少 0.3 秒及三筆有效資料；不用先按雙黃燈。前方塞車與減速中交替至少六秒；離開 D 檔或資料失效取消。
- 七項車況個別文字／顏色／字型／字重／效果、五組自訂文字、11 類文字與圖案效果、八款眼睛動畫。
- 胡蘿蔔車開屏動畫、開關屏、清屏、全亮測試、像素設定、手動車況預覽、自動重連。
- 行車 GPS 速度／估計里程、TTF／OTF 字型匯入、連線診斷複製。
- 背景連動與背景車況顯示：iPhone 使用行車定位搭配 Live Activity／動態島；Android 使用前景服務、常駐通知與車況變更通知。

![胡蘿蔔開屏動畫](design/startup-96x16.gif)

## 功能一致的定義

兩邊覆蓋相同的主要 LED 操作與車況規則，並共用測試情境及動畫素材。**平台呈現與執行保證並不相同**：Android 沒有 iPhone 動態島；iOS 也沒有 Android 前景服務。LOY 的局部更新／整幅裁切傳送實作目前不同，兩邊皆以最高目標 20 fps 排程，不能據此保證實體屏幕達到相同幀率。詳見功能對照表。

不以假車況、假定位或假音訊保活。定位只用於可見的行程資訊，座標不保存或上傳，也不代替 C4 資料。程式不設固定連動總時限，但系統省電、強制停止、網路與藍牙斷線仍可能中斷。

## 專案結構

- `android/`：原生 Java App、建置腳本、離線測試。
- `ios/`：SwiftUI App、WidgetKit Live Activity、XcodeGen 專案與測試。
- `c4_carstate_bridge.py`：獨立橋接副本，需在 openpilot Python 環境執行。
- `diagnose_link.py`：只讀區網橋接診斷。
- `tests/check_parity.py`：檢查兩平台共同測試資料與動畫是否一致。
- `downloads/`：固定簽章 Android APK、iOS 原始碼包及校驗碼。

本專案從 [openpilot-carrot](https://github.com/FANHAOHSIANG/openpilot-carrot) 的 `tools/iphone_led` 獨立，來源提交 `acde165f9fed5141d309d76163d5d58b26371e39`。不包含整套 openpilot；既有 C4 橋接與金鑰可以沿用。

## 驗證與限制

GitHub Actions 分別編譯 Android 與 iOS／Live Activity，執行協定、車況、動畫及 GPS 規則測試。Android 發佈 APK 使用固定私有簽章，CI 臨時簽章 APK 不作為正式下載。

自動測試、編譯成功不等於實車或實體 LED 已驗證；兩手機不能同時控制同一塊 BLE 屏幕。LED 連線／協定 ACK 與 App 的最近送出內容不是實體畫面回讀。完全失去執行機會或 BLE 中斷時，無法保證清除屏幕上的舊畫面。請停車設定與驗證。
