# 驗證紀錄（2026-11-12）

程式與發佈檔案提交：`fb45216edd8c1bdbbd2156302afad2889d73a838`。

| 檢查 | 結果／證據 |
| --- | --- |
| Android 0.1.11 編譯 | [GitHub Actions](https://github.com/FANHAOHSIANG/carrot-led/actions/runs/43085387402) 通過；本機 JDK 17／SDK 36 發佈建置亦通過 |
| Android 發佈 APK | v2／v3 驗簽通過，固定憑證符合 `android/signing-certificate.sha256`；package `tw.carrot.led`、versionCode 12 |
| iOS 0.32 App／Live Activity | [GitHub Actions](https://github.com/FANHAOHSIANG/carrot-led/actions/runs/43085387252)：Xcode 編譯、封裝宣告、Swift 規則測試、模擬器啟動與畫面擷取通過 |
| 共用功能資料 | [GitHub Actions](https://github.com/FANHAOHSIANG/carrot-led/actions/runs/43085387272) 通過；四份共用情境資料與 11 份動畫素材逐位元一致 |
| Android 核心測試 | 42 核心檢查、6 組上游 iPixel golden frames、眼睛幾何、291 筆車況、46 個開屏切換、2204 項 LOY 編碼／畫面檢查、9 項通知、6 項路由、GPS 品質過濾通過 |
| iOS 核心測試 | 超過一小時／重連預算、GPS、動態島提醒、iPixel、LOY 點陣與差分畫布、眼睛、共用車況與開屏情境通過 |
| 下載檔一致性 | 從公開下載連結重新讀取兩檔，比對本機 SHA-256 與完整位元組一致；iOS ZIP 逐檔比對 `ios/` 原始碼與資源一致 |
| 安裝文件 | 相對 Markdown 連結查核通過；包含 C4 金鑰、Android 覆蓋安裝、Xcode 兩 Target 與 Personal Team 步驟 |

檔案版本、來源與 SHA-256 另見根目錄 `versions.json`、`downloads/SHA256SUMS`。

**尚未完成**：真實 iPhone／Android 車機安裝、實體 BLE 屏幕顯示、GPS 權限互動、匯入字型實際字形、長時間切背景／鎖屏、CP 搭子同時運作的區網對照。自動測試不代表這些情境已通過，也不證明兩平台有相同實際 fps。
