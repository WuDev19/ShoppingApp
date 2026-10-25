# Seller dashboard on Android

The seller entry in Profile opens `DashBoardSellerFragment`. The screen uses the authenticated backend endpoints:

- `GET seller/dashboard?fromDate=yyyy-MM-dd&toDate=yyyy-MM-dd`
- `GET seller/dashboard/monthly?year=yyyy`

Backend identity comes from the authenticated session, not a client-supplied seller ID. Firebase account changes or a mismatch with the navigation account clear both panels; HTTP 401/403 clears all visible statistics. There is no persistent dashboard cache shared between accounts.

## Behavior

The default range is the current month through today in Asia/Ho_Chi_Minh; the default year is the current year. Ranges contain at most 366 inclusive calendar days. Dates and years must be within the backend bounds, 1900 through 9998. Filter values are stored with SavedStateHandle. Creating a fresh process reloads statistics rather than restoring account data from disk.

The date-range panel contains order counts, completed-order value, online paid amount, completed COD order value, units sold, current product/stock counts, order statuses, daily series and up to five top product names with units sold. The yearly panel shows both monthly monetary series plus exact monthly amounts and units in text. Charts use millions of VND; integer money values are retained as Long and formatted in Vietnamese currency in the text.

Completed-order value is attributed by completion date; online payments are attributed by successful payment date. They are independent series and are never added together. Inventory is current, not historical. Undated completed orders are explicitly flagged.

Each panel has its own progress, failure and retry state. Refreshing the same filter retains previous data and labels it as stale if the request fails. Changing the relevant filter clears the old panel immediately. In-flight requests are cancelled when the account or relevant filter changes. No automatic polling or write retry is introduced. Returning from product creation, voucher creation or shop settings refreshes both panels.

## Implementation

`Fragment -> Hilt ViewModel -> use case -> SellerDashboardRepository -> Retrofit API`, with DTO/domain mappers and lifecycle-scoped StateFlow collection. Uses existing MPAndroidChart and Material components. No new production permissions, dependencies, SDK changes, Room migration or backend changes.

The private `DashboardTestHostActivity` exists only in the debug source set. Production navigation and authentication remain on the actual Fragment. Device tests isolate state logic with a fake repository and test screen/adapter rendering separately; they do not call the real backend or create orders/payments.

## Verification

Run from the mobile repository:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest
.\gradlew.bat :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.shoppingapp.DashboardDeviceTest
```

JVM cases: 366-day boundary, leap/invalid dates, strict date format, year limits, date round trip, Long JSON mapping, nullable product metadata, zero months, invalid input preventing requests, cancellation propagation.

Device cases: stale data and retry, independent panel requests, rapid filter changes, account isolation, filter state restoration, forbidden response clearing both panels, actual Fragment/date-dialog recreation, adapter rendering of large amounts, error and empty states.

Actual authenticated seller navigation and values against a running backend, hardware Android 7/8 devices, and a complete OS process-death round trip need separate end-to-end verification. A recreated activity or a reconstructed SavedStateHandle does not establish full process-death coverage.

## Verified result (2026-10-24)

- Debug APK, Android test APK, unit tests and lint: BUILD SUCCESSFUL.
- JVM: 12 passed in total, including 7 new dashboard contract cases.
- Pixel 8 Pro emulator, Android API 36: 6 dashboard device tests passed on the final code in light mode and again in dark mode with system font scale 1.3.
- Final dark-mode screenshot inspected: toolbar contrast, wrapped long amounts and explanatory text remain readable at 130% font scale. Chart points remain visible for a single-day series.
- `git diff --check` passed. The normal APK retains the configured backend base URL; the debug API override is empty.
- No backend writes, orders or payments were made by these tests. Emulator theme and font scale were restored after testing.

Evidence is in ignored `app/build/dashboard-completion-checks.log`, `dashboard-device-completion.txt`, `dashboard-device-completion-dark.txt` and `dashboard-completion-dark.png`.

## UI alignment with existing seller screens

Dashboard reuses the existing `button_color` blue (#1362A0), white toolbar labels/icons, `border_under` toolbar shape, `border_view` panels and 10dp button corners/spacing from seller settings and voucher/product creation. A dashboard-only theme overlay supplies the matching chart and dialog colors. Light mode uses white and light-gray surfaces; dark mode uses scoped neutral surfaces with readable text. Disabled actions have explicit disabled colors.

Changed UI files: dashboard layouts/dimensions, scoped dashboard styles/colors, chart palette in the adapter, date-picker theme and year-dialog context. Authentication, API contracts and statistic calculations are unchanged by this UI revision.

Verified: debug APK and Android test APK build plus lint passed; all 6 dashboard device cases passed again on the final UI in light mode and dark mode at font scale 1.3. Both screenshots were inspected. Evidence: `app/build/dashboard-style-final-checks.log`, `dashboard-style-device-light.txt`, `dashboard-style-device-dark.txt`, `dashboard-style-light.png`, `dashboard-style-dark.png`. Screenshot values come from test fixtures.
