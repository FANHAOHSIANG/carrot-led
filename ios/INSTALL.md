# 安裝與建置

## 1. 共用準備：C4、網路、屏幕

C4 與手機／車機連到同一可信私人區網；先停車設定。LOY PLAY 選 96×16，iPixel Color 選 64×16。先斷開原廠 App、Mac、另一支手機的 BLE 連線，讓一個客戶端控制屏幕。

既有 `test/carrot-wip-upstream-20261104` 或 `carrot-wip` 已整合 LED 橋接時，沿用現有安裝與金鑰，不必因獨立儲存庫重新刷 C4。

### C4 IP 與固定金鑰

透過你已設定的 SSH 登入 C4 後執行：

```sh
ip -4 -o addr show wlan0
cat /data/iphone-led/token
```

IP 去掉 `/24` 等子網尾碼；將金鑰填入兩版 App 的「橋接連線金鑰」。這不是 Apple／Google 密碼。不要貼到公開 GitHub issue，也不要重新生成已使用中的金鑰。

若金鑰檔不存在，先確認安裝的分支含 LED 橋接。停車時在 C4 建立一次（已存在會保留）：

```sh
python3 - <<'PY'
from pathlib import Path
import secrets, os
p = Path('/data/iphone-led/token')
p.parent.mkdir(parents=True, exist_ok=True)
try:
    fd = os.open(p, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
except FileExistsError:
    print('既有金鑰保留')
else:
    with os.fdopen(fd, 'w') as f:
        f.write(secrets.token_hex(32) + '\n')
    print('已建立金鑰，請自行讀取填入 App')
PY
cat /data/iphone-led/token
```

既有整合版由 manager 啟動橋接；未整合的分支不能只建立金鑰就宣稱完成。手動測試可在 C4 的 openpilot 根目錄、其既有 Python 環境執行本專案的 `c4_carstate_bridge.py`；先確定 18765 埠沒有其他橋接。不要同時啟動兩份。這不會替你安裝開機啟動服務。

橋接：HTTP `18765/carstate`、Bearer 金鑰、私人 IPv4，資料超過 0.7 秒失效。HTTP 本身未加密，不要公開轉發這個埠到網際網路。

## 2. Android 安裝

最低 Android 8（API 26），需要可用的 BLE 硬體與系統驅動；車機內建「電話藍牙」不一定能提供 Android App BLE GATT。

1. 下載 `downloads/CarrotLED-0.1.11.apk`，透過檔案管理器開啟並允許該來源安裝。
2. 已裝 0.1.9／0.1.10：直接覆蓋；不要刪 App，設定與金鑰會保留。0.1.8 或更早簽章不同：先自行記下設定，再移除舊版並安裝。
3. 允許附近裝置與通知；Android 11 以下 BLE 掃描需要位置權限及系統定位開關。
4. 填 C4 IP／金鑰，儲存，選屏幕類型，按「開始背景連動」，掃描並選自己的屏幕。
5. 想看 GPS 速度／估計里程：先停止連動、開啟「行車 GPS 速度與估計里程」，授予精確位置權限，在 App 前景重新開始。GPS 可關閉；不影響 C4 車況來源。
6. 確認前景有新鮮車況、開屏與清屏正常，再切換導航。若沒有通知，按「3 秒後測試通知」並切出去。

ADB 安裝（已安裝 Android platform-tools、裝置開啟 USB 偵錯）：

```sh
adb devices
adb install -r CarrotLED-0.1.11.apk
```

套件：`tw.carrot.led`，versionCode `12`。發佈簽章 SHA-256：
`1cf22fc66c9b3333d93077bb40102135186732570614d754e45536e4fd2b799d`。

## 3. iPhone：免費 Personal Team

需要 Mac、Xcode 26+、iPhone（App 最低 iOS 16.2）。GPS 行車模式與 Live Activity 不等於保證無限背景執行；另有需 iOS 26 的實驗背景處理模式。

下載 iOS ZIP 解壓後進入 `iphone_led_lan`：

```sh
# 已有 Homebrew 時，首次安裝 XcodeGen
brew install xcodegen
cd ~/Downloads/iphone_led_lan
xcodegen generate
open CarrotLEDProbe.xcodeproj
```

或直接從儲存庫取得：

```sh
git clone https://github.com/FANHAOHSIANG/carrot-led.git
cd carrot-led/ios
xcodegen generate
open CarrotLEDProbe.xcodeproj
```

在 Xcode：

1. Settings → Accounts 加入 Apple ID；接上 iPhone，信任 Mac，依系統提示開 Developer Mode。
2. 主 App `CarrotLEDProbe`、extension `CarrotLEDLiveActivity` 的 Signing & Capabilities 都選同一個 Personal Team，開 Automatically manage signing。
3. 保留你原本安裝使用的主 App Bundle Identifier；extension 用相同前綴加 `.liveactivity`。首次安裝需自選唯一 ID。不要刪舊 App，以保留設定與鑰匙圈金鑰。
4. Scheme 選 `CarrotLEDProbe`、目的裝置選你的 iPhone，按 ⌘R。若系統要求信任開發者，依 iPhone 設定完成。
5. 允許藍牙、區域網路、使用期間定位；C4 與屏幕就緒後，開啟自動車況，於連線設定開始背景連動，等狀態確認後再切換 App。

免費 Personal Team 是個人裝置測試途徑，簽署期限／配額依 Xcode 與 Apple 帳號顯示；到期需用原團隊重新安裝。不是 TestFlight 或 App Store 發佈資格。發佈需另辦 Apple Developer Program、App Store Connect、簽署與審查，尚未在本專案完成。

## 4. 開發者建置

Android：JDK 17、Android SDK platform 36／build-tools 36.0.0。

```sh
cd android
export ANDROID_SDK_ROOT=/你的/Android/sdk
./test-core.sh
./build-apk.sh
```

預設產生本機測試簽章 APK，不能當既有發佈版的覆蓋更新。發佈維護者須另外提供私人簽章：

```sh
export CARROT_ANDROID_RELEASE=1
export CARROT_ANDROID_KEYSTORE=/私人路徑/carrotled-20261108.p12
export CARROT_ANDROID_KEY_ALIAS=carrotled
export CARROT_ANDROID_PASSWORD_FILE=/私人路徑/password.txt
./build-apk.sh
```

腳本會驗證 `signing-certificate.sha256`。私鑰與密碼不在 GitHub，請勿提交或改用 CI 臨時簽章發佈。

iOS：依上述 XcodeGen 步驟產生專案；CI 的完整 Swift 測試／模擬器建置指令位於 `.github/workflows/iphone-led-build.yaml`。共用資料檢查在根目錄執行 `python3 tests/check_parity.py`。
