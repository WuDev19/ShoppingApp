# Product review integration

Product detail displays rating statistics and paged review rows with author, rating, comment, timestamp and image previews. The section has independent loading/error/empty states and retries; review failures do not block the product/cart sections.

Write/edit sheets support ratings without photos, at most 5 JPEG/PNG images up to 5 MB each, and comments up to 2,000 characters. ReviewImageReader validates size, format and dimensions for both upload implementations. Draft text/rating/URIs/deleted-image IDs are saved in Fragment state.

The document picker uses OpenMultipleDocuments and retains URI read permission, instead of depending on a temporary GetContent grant. Camera images use an app-private FileProvider cache path and therefore do not require external-storage permission on Android 24-28. Cancelled captures and captured files removed from a draft or submitted successfully are discarded. Gallery duplicates and the image cap are handled before submission. Other application camera helpers are unchanged.

The selected production review flow uploads directly to Cloudinary through the existing unsigned preset, then submits the returned HTTPS URLs to the backend. The repository now uses the ImageReviewUploader binding consistently with the backend's direct URL support. It does not require Cloudinary server API keys. BackendReviewImageUploader and the optional authenticated backend upload endpoint remain available for a future managed-media setup.

Cloudinary upload has a 45-second deadline. Rescheduled/offline SDK work is cancelled and surfaced as a retryable error, and cancellation of the view model also cancels the request. This prevents an uncancelable review sheet from staying in loading state indefinitely. Successfully uploaded URLs remain cached for explicit retries within the ViewModel lifetime.

Only backend-managed assets participate in server-side remote cleanup. Direct/legacy shared assets have no verified ownership record on the backend, so deleting a review removes database image references but does not delete those remote files.

## Verification

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

The final debug APK, all five JVM tests and lint passed after the uploader timeout/URI changes. The APK was rebuilt without the test URL override. Lint settings are unchanged.

Pixel 8 Pro emulator validation passed these scenarios:

1. Create/edit/delete a rating-only review against live backend/SQL Server.
2. Select a photo, recreate the Activity, upload through the real Cloudinary Android SDK, create/read/edit/delete the review, and read the uploaded CDN image (200).
3. Preserve the draft rating/text across Activity recreation.
4. Handle document selection, cancellation of camera and restored selected images using the private URI provider.
5. Reject corrupted image data and payloads exceeding 5 MB before upload.
6. Preserve the draft and re-enable submit after an API network failure.
7. Fail an offline photo upload within the configured deadline (about 45 seconds), cancel its deferred SDK work and allow explicit retry.

The normal device class run intentionally skips its two offline-specific methods; each offline method was then run separately with its required network setup and passed. Gallery/camera Activity results are supplied by instrumentation monitors. This is not a verification of physical camera hardware or an actual operating-system process kill. Persisted document permissions and the private camera file mechanism are implemented, while third-party provider revocation and full process restoration remain manual checks.

## Reproducing device tests

ReviewTestHostActivity and its manifest registration exist only in src/debug, are not exported, and do not ship in release. The production backend URL is unchanged. A debug-only opt-in base URL makes local instrumentation reproducible:

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest -PreviewTestBaseUrl=http://127.0.0.1:18081/api/
adb reverse tcp:18081 tcp:18081
```

Install both APKs and run ReviewDeviceTest with AndroidJUnitRunner arguments reviewToken (a short-lived token for an isolated buyer), reviewApiUrl, reviewProductId and reviewOrderItemId. The backend fixture must be a completed order item not already reviewed. For a real photo round trip, select realBackendCreateEditDeleteFromAndroid with reviewWithPhoto=true. To execute the API offline case, remove the adb reverse mapping, select networkFailureKeepsDraftAndReenablesSubmit with reviewOffline=true, and then restore the mapping. For the photo upload offline case, disable emulator Wi-Fi and mobile data, run directPhotoUploadFailsWithinDeadlineWhenOffline with reviewUploadOffline=true, then restore the original network settings. Without fixture arguments the live backend case is explicitly skipped.

Rebuild without reviewTestBaseUrl after testing to restore the normal debug APK. Release always uses the configured production URL even when a test property is provided.

Remaining manual coverage: actual camera capture, third-party gallery access after process death, large system fonts, small displays and dark theme. No Room schema, production permission or SDK changes were introduced.
