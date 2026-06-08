# VitalOS — Health Connect Dashboard

A Whoop-style health dashboard that pulls all your data from Health Connect into one place.
Reads from Samsung Health, Galaxy Watch 3, Porodo Band, Nike Run Club, Strong, and any other
app connected to Health Connect on your phone.

---

## What it tracks

- **Recovery score** — calculated from HRV and resting heart rate vs your 30-day baseline
- **Strain score** — Whoop-style 0–21 scale from today's exercise sessions
- **Sleep score** — weighted from duration, efficiency, and deep sleep %
- Resting heart rate, HRV (ms rMSSD), SpO2, skin temperature, respiratory rate
- Steps, calories (active + total), distance
- Sleep stage breakdown (Awake / Light / Deep / REM)
- Recent workouts with source app attribution
- 7-day HRV trend chart
- Connected sources panel showing every app feeding Health Connect

---

## Build requirements

You need these installed on your computer:

1. **Android Studio** (free) — https://developer.android.com/studio
   - This installs Java, Gradle, and the Android SDK automatically
2. **Android SDK** — installed automatically with Android Studio
   - Make sure API level 34 is installed (Android Studio > SDK Manager > SDK Platforms)

You do NOT need a Google account. You do NOT need to publish to the Play Store.

---

## Build steps (10 minutes)

### Step 1 — Install Android Studio
Download and install from https://developer.android.com/studio
Open it, let it install the SDK components it asks for, then close it.

### Step 2 — Open the project
1. Open Android Studio
2. Click "Open" (not "New Project")
3. Navigate to this VitalOS folder and click OK
4. Wait for Gradle sync to finish (first time takes 3–5 minutes, downloads dependencies)

### Step 3 — Build the APK
Option A — via Android Studio UI:
1. Menu: Build → Build Bundle(s) / APK(s) → Build APK(s)
2. Wait for the build (1–3 minutes)
3. Click "locate" in the popup that appears
4. The APK is at: `app/build/outputs/apk/debug/app-debug.apk`

Option B — via terminal (faster):
```bash
cd VitalOS
chmod +x gradlew
./gradlew assembleDebug
```
APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

---

## Install on your Samsung Galaxy S25 Edge

### Step 1 — Enable Unknown Sources on your phone
1. Settings → Apps → Special app access → Install unknown apps
2. Find your file manager or browser → toggle "Allow from this source"

### Step 2 — Transfer the APK
Pick any method:
- **USB cable**: Copy APK to Downloads folder on phone
- **AirDrop equivalent**: Use Samsung Quick Share (swipe down → Quick Share)
- **Email**: Email the APK to yourself, open on phone
- **Google Drive / Dropbox**: Upload APK, download on phone

### Step 3 — Install
1. Open your file manager on the phone
2. Find the APK → tap it → Install
3. If prompted about Play Protect, tap "Install anyway" (this is expected for sideloaded apps)

### Step 4 — Connect to Health Connect
1. Open VitalOS — it will detect Health Connect automatically (it's built into Android 14+)
2. Tap "Grant Permissions"
3. The Health Connect permission screen appears — grant all permissions
4. Your data loads automatically from all connected apps

---

## Health Connect data sources

VitalOS reads from Health Connect, which aggregates data from every app that writes to it.
On your phone, these apps should already be writing to Health Connect:

| App | Data it contributes |
|-----|-------------------|
| Samsung Health | Steps, HR, calories, resting HR |
| Galaxy Watch 3 | HRV, SpO2, skin temp, HR |
| Nike Run Club | Running workouts, distance, pace |
| Strong | Strength training sessions |
| Porodo Band | Sleep stages, steps, HR |

To verify what's connected:
Settings → Health Connect → App permissions → see which apps have Read/Write access

---

## Updating the app

When you make changes to the source code:
1. Rebuild: `./gradlew assembleDebug`
2. Install the new APK — Android will update the existing installation

---

## Troubleshooting

**"App not installed" error**
→ The phone has a leftover version. Go to Settings → Apps → VitalOS → Uninstall, then reinstall.

**No data showing / everything is zero**
→ Check Health Connect permissions: Settings → Health Connect → App permissions → VitalOS
→ Make sure Samsung Health / NRC / Strong have been granted Write access to Health Connect

**Build fails with "SDK not found"**
→ Open Android Studio → File → Project Structure → SDK Location
→ Copy that path into `local.properties` as `sdk.dir=/your/path/here`

**Gradle sync fails on first open**
→ Wait — it's downloading dependencies. Can take 5 minutes on first run. Just let it go.

---

## Architecture notes (for reference)

```
VitalOS/
├── app/src/main/java/com/vitalos/app/
│   ├── MainActivity.kt              — entry point, permission launcher
│   ├── data/
│   │   ├── health/Models.kt         — data classes, score algorithms
│   │   └── repository/
│   │       └── HealthConnectRepository.kt  — all Health Connect SDK calls
│   ├── viewmodel/
│   │   └── DashboardViewModel.kt    — state management, refresh logic
│   └── ui/
│       ├── theme/                   — colors, typography, Material3 theme
│       ├── components/Components.kt — reusable cards and UI elements
│       └── screens/
│           ├── MainScreen.kt        — nav scaffold + bottom bar
│           ├── TodayScreen.kt       — main dashboard
│           └── OtherScreens.kt      — Sleep, Health, Activity, Trends tabs
```

Recovery score = (currentHRV / 30-day baseline HRV) × 60 + (baseline resting HR / today's resting HR) × 40
Strain score = weighted sum of time in HR zones, scaled 0–21

---

Built with Kotlin + Jetpack Compose + Health Connect SDK 1.1.0
Target: Android 14 / API 34 | Min: Android 8.0 / API 26
