# WealthTrack+ AGENT Guide

## Tech stack
- Android app written in **Kotlin**
- UI built with **Jetpack Compose (Material3)**
- Local data only:
  - Room Database for user data (accounts, assets, transactions, etc.)
  - DataStore for simple flags (e.g. first run, onboarding)
- No cloud backend. Do NOT send any user financial data to external services.

## Architecture conventions
- Package name: `com.liangyu.wealthtrackplus`
- Use **MVVM**:
  - `ViewModel` + `UiState` data classes
  - Repository layer talks to Room / DataStore
- New screens:
  - Go under `ui.feature.*` or a specific subpackage (e.g. `ui.dashboard`, `ui.cash`, `ui.riskprofile`).
- Prefer small Composables and small files instead of one huge function.

## Navigation
- There is a central `AppNavHost` handling navigation.
- When adding a new screen:
  - Define a new route object (e.g. `AppRoute.AssetDashboard`)
  - Add a `composable` entry in `AppNavHost`
  - Wire it from drawer / menu / buttons using existing patterns.

## UI / design style
- Brand style: **deep green + gold accent**, “金綠質感立體風”.
- Use existing theme colors and typography where possible.
- Avoid introducing random new colors if a similar one exists in theme.
- Prefer:
  - White / light backgrounds for cards
  - Soft shadows, rounded corners
  - Clear hierarchy: title > subtitle > body text

## Privacy & security
- All financial data must stay **on device**.
- The app may use fingerprint / device lock for local protection.
- Do NOT:
  - Upload accounts, transactions, or balances to any external API.
  - Generate code that sends financial data to remote servers.

## Build & checks
- Before finishing a task, run:
  - `./gradlew :app:assembleDebug`
- If you add logic-heavy code, prefer adding unit tests where reasonable.

## Asset dashboard (example feature)
- Screen name: "資產儀表板" (Asset Dashboard).
- Main content:
  - Total asset amount
  - Today’s P&L
  - Overall return rate
  - Breakdown by asset category (stock, bond, cash, etc.)
  - Top holdings list
- Focus on clear layout and readability instead of overly complex graphics.
