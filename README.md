# Ad SDK Integration Demo - Android (Java)

An Android application demonstrating the integration and display of test ads from **9 major Ad SDKs**. Each SDK is integrated with its own dedicated Activity screen, showing banner, interstitial, and/or rewarded ad formats.

## Integrated Ad SDKs

| # | SDK | Ad Formats | Test Configuration |
|---|-----|------------|-------------------|
| 1 | **Google AdMob** | Banner, Interstitial, Rewarded | Google test ad unit IDs |
| 2 | **Google Ad Manager** | Banner, Interstitial | Google test ad unit IDs |
| 3 | **Meta Audience Network (Facebook)** | Banner, Interstitial | Test mode enabled |
| 4 | **Unity Ads** | Banner, Interstitial, Rewarded | Test Game ID with test mode |
| 5 | **AppLovin MAX** | Banner, Interstitial, Rewarded | Demo ad unit IDs |
| 6 | **ironSource (Unity LevelPlay)** | Interstitial, Rewarded | Demo App Key |
| 7 | **InMobi** | Banner, Interstitial | Test account & placement IDs |
| 8 | **Tapjoy** | Content Placement | Test SDK Key |
| 9 | **Chartboost** | Banner, Interstitial, Rewarded | Test App ID & Signature |

## Project Structure

```
app/src/main/java/com/example/adsdkdemo/
├── AdSDKDemoApp.java          # Application class (initializes Google Mobile Ads)
├── MainActivity.java           # Main menu with SDK selection cards
├── AdMobActivity.java          # Google AdMob demo
├── AdManagerActivity.java      # Google Ad Manager demo
├── MetaAudienceActivity.java   # Meta Audience Network demo
├── UnityAdsActivity.java       # Unity Ads demo
├── AppLovinActivity.java       # AppLovin MAX demo
├── IronSourceActivity.java     # ironSource LevelPlay demo
├── InMobiActivity.java         # InMobi demo
├── TapjoyActivity.java         # Tapjoy demo
└── ChartboostActivity.java     # Chartboost demo
```

## SDK Versions

| SDK | Version |
|-----|---------|
| Google Mobile Ads (AdMob + Ad Manager) | 23.6.0 |
| Meta Audience Network | 6.18.0 |
| Unity Ads | 4.12.5 |
| AppLovin MAX | 13.0.1 |
| ironSource Mediation SDK | 7.9.0 |
| InMobi | 10.7.8 |
| Tapjoy | 14.0.1 |
| Chartboost | 9.7.0 |

## Requirements

- **Android Studio** Hedgehog or newer
- **Min SDK**: API 23 (Android 6.0)
- **Target SDK**: API 34 (Android 14)
- **Java**: 17
- **Gradle**: 8.2
- **Android Gradle Plugin**: 8.2.2

## Setup & Build

1. Clone this repository
2. Open in Android Studio
3. Sync Gradle (dependencies will be downloaded automatically)
4. Build and run on a device or emulator

```bash
./gradlew assembleDebug
```

## How It Works

1. **Main Screen**: Displays a list of 9 SDK cards. Tap any card to open its demo screen.
2. **SDK Demo Screens**: Each screen shows:
   - SDK initialization status
   - Buttons to load and show different ad formats
   - Real-time event log showing all SDK callbacks
   - Banner ads displayed at the bottom of the screen

## Test Ad IDs Used

### Google AdMob
- Banner: `ca-app-pub-3940256099942544/6300978111`
- Interstitial: `ca-app-pub-3940256099942544/1033173712`
- Rewarded: `ca-app-pub-3940256099942544/5224354917`
- App ID: `ca-app-pub-3940256099942544~3347511713`

### Google Ad Manager
- Banner: `/6499/example/banner`
- Interstitial: `/6499/example/interstitial`

### Unity Ads
- Game ID: `14851` (test mode enabled)
- Placements: `video`, `rewardedVideo`, `banner`

### ironSource
- App Key: `85460dcd` (demo key)

### Chartboost
- App ID: `4f21c409cd1cb2fb7000001b` (test)
- App Signature: `92e2de2fd7070327d881571f904c275107e0d2c5` (test)

## Notes

- All SDKs are configured with **test mode / test ad unit IDs** to avoid policy violations during development.
- Some SDKs (AppLovin, InMobi, Tapjoy) require real dashboard credentials for full test ad delivery. The demo shows proper SDK initialization and ad request flow.
- The event log in each activity screen provides real-time feedback on SDK callbacks.
- Replace test IDs with your own production IDs before publishing.

## License

This project is for demonstration and educational purposes.
