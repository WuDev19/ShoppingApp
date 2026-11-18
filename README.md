# ShoppingApp

ShoppingApp là ứng dụng mua sắm trên Android, viết bằng Kotlin, phục vụ cả người mua và người bán. Dự án bao gồm các luồng khám phá sản phẩm, giỏ hàng, đặt hàng, thanh toán, đánh giá và quản lý cửa hàng.

Repository này chứa ứng dụng Android. Các chức năng trực tuyến cần backend API và cấu hình dịch vụ tương ứng; chỉ build APK chưa đủ để sử dụng đầy đủ các luồng nghiệp vụ.

## Mục lục

- [Chức năng chính](#chức-năng-chính)
- [Công nghệ](#công-nghệ)
- [Kiến trúc và cấu trúc thư mục](#kiến-trúc-và-cấu-trúc-thư-mục)
- [Yêu cầu môi trường](#yêu-cầu-môi-trường)
- [Cài đặt và chạy](#cài-đặt-và-chạy)
- [Lệnh build và kiểm thử](#lệnh-build-và-kiểm-thử)
- [Xử lý lỗi thường gặp](#xử-lý-lỗi-thường-gặp)
- [Tài liệu chi tiết](#tài-liệu-chi-tiết)
- [Lưu ý khi phát triển](#lưu-ý-khi-phát-triển)

## Chức năng chính

### Người mua

- Đăng ký, đăng nhập bằng email hoặc Google và đặt lại mật khẩu.
- Xem sản phẩm theo danh mục, tìm kiếm, xem chi tiết, biến thể và sản phẩm liên quan.
- Thêm sản phẩm vào giỏ hàng, mua ngay và tạo đơn hàng.
- Quản lý hồ sơ, ảnh đại diện và địa chỉ nhận hàng; chọn vị trí trên bản đồ.
- Chọn dịch vụ giao hàng, tính phí và ước tính thời gian giao qua các API GHN của backend.
- Áp dụng voucher của cửa hàng; lựa chọn thanh toán COD hoặc VNPay.
- Xem, tìm kiếm, theo dõi trạng thái, xác nhận và hủy đơn hàng theo luồng ứng dụng.
- Xem đánh giá sản phẩm; tạo, sửa, xóa đánh giá và tải ảnh đánh giá.

### Người bán

- Đăng ký người bán và xác minh OTP.
- Xem và chỉnh sửa thông tin cửa hàng.
- Đăng sản phẩm, tải ảnh và nhập thông tin chi tiết sản phẩm.
- Xem sản phẩm của cửa hàng, tìm kiếm và phân loại theo danh mục.
- Tạo và xem voucher của cửa hàng.
- Xem dashboard thống kê đơn hàng, giá trị đơn hoàn tất, khoản thanh toán online, số lượng bán và tồn kho; lọc theo khoảng ngày và xem biểu đồ theo năm.
- Xem sản phẩm bán chạy.

Danh sách trên mô tả các luồng có trong mã nguồn, chưa phải cam kết mọi luồng đã được kiểm thử đầu cuối. Phần chat còn có mã khung/TODO, chưa nên coi là tính năng hoàn chỉnh.

## Công nghệ

| Thành phần | Công nghệ |
| --- | --- |
| Ngôn ngữ và build | Kotlin, Gradle Kotlin DSL, Version Catalog |
| Giao diện | XML Layout, Activity/Fragment, ViewBinding, Material Components, RecyclerView |
| Điều hướng | AndroidX Navigation Component, Safe Args |
| Kiến trúc | MVVM, use case, repository, phân lớp `ui` / `domain` / `data` |
| Dependency injection | Hilt, KSP |
| Bất đồng bộ và trạng thái | Coroutines, Flow, LiveData, ViewModel |
| Giao tiếp API | Retrofit, OkHttp, Moshi |
| Dữ liệu cục bộ | Room, SharedPreferences |
| Phân trang | Paging 3, PagingSource, RemoteMediator |
| Ảnh và media | Glide, Cloudinary |
| Xác thực và dữ liệu Firebase | Firebase Auth, Firestore, Credential Manager / Google ID |
| Bản đồ và vị trí | MapLibre, Google Play Services Location |
| Biểu đồ | MPAndroidChart |
| Kiểm thử | JUnit 4, AndroidX Test, Espresso |

## Kiến trúc và cấu trúc thư mục

Luồng xử lý chủ đạo:

```text
Activity / Fragment
        ↓
     ViewModel
        ↓
      UseCase
        ↓
Repository interface (domain)
        ↓
Repository implementation (data)
        ↓
Backend API / Room / Firebase / Cloudinary
```

DTO và entity được chuyển đổi qua mapper để phục vụ các model của ứng dụng. Hilt cung cấp dependency; một số danh sách kết hợp Paging và cache Room thông qua RemoteMediator. Code hiện tại còn có một số phụ thuộc chéo giữa các lớp, nên sơ đồ mô tả hướng tổ chức chính.

```text
ShoppingApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/shoppingapp/
│   │   │   ├── data/
│   │   │   │   ├── local/          # Room, DAO, entity, mapper, migration
│   │   │   │   ├── remote/         # API, DTO, interceptor, mapper
│   │   │   │   ├── repository/     # Implementation và RemoteMediator
│   │   │   │   └── source/         # PagingSource và Cloudinary uploader
│   │   │   ├── domain/            # Model, repository interface, use case
│   │   │   ├── di/                # Hilt module và qualifier
│   │   │   ├── ui/                # Activity, Fragment, adapter, ViewModel, state
│   │   │   └── utils/             # Hằng số, session/token, tiện ích, Application
│   │   ├── res/                   # Layout, navigation, drawable, strings, theme
│   │   └── AndroidManifest.xml
│   ├── src/test/                  # Unit test JVM
│   ├── src/androidTest/           # Test trên Android
│   └── build.gradle.kts
├── gradle/libs.versions.toml      # Phiên bản và alias dependency
├── gradle/wrapper/                # Gradle Wrapper
├── docs/                         # Tài liệu dashboard và tích hợp đánh giá
├── build.gradle.kts              # Plugin và cấu hình build gốc
├── settings.gradle.kts           # Module và repository dependency
└── AGENTS.md                     # Nguyên tắc code và quy trình làm việc
```

## Yêu cầu môi trường

| Cấu hình | Giá trị hiện tại |
| --- | --- |
| Module | `:app` |
| Application ID / namespace | `com.example.shoppingapp` |
| Android tối thiểu | API 24 (Android 7.0) |
| Compile SDK | 35 |
| Target SDK | 34 |
| Kotlin | 2.0.21 |
| Android Gradle Plugin | 8.6.0 |
| Gradle Wrapper | 8.7 |
| JVM target của ứng dụng | 1.8 |

Cần Android Studio hỗ trợ cấu hình trên, JDK 17 để chạy Gradle, Android SDK Platform 35 và emulator hoặc thiết bị Android. JDK chạy Gradle và JVM target của ứng dụng là hai cấu hình khác nhau.

Thiết bị/emulator nên có Google Play Services để kiểm tra đăng nhập Google và các luồng vị trí liên quan. Lần đồng bộ đầu cần mạng để tải Gradle và dependency từ các repository đã khai báo, gồm Google Maven, Maven Central và JitPack.

## Cài đặt và chạy

1. Clone repository hoặc mở bản mã nguồn hiện có trong Android Studio bằng thư mục chứa `settings.gradle.kts`.
2. Cài SDK Platform 35 trong SDK Manager và cấu hình Gradle JDK là JDK 17.
3. Để Android Studio tạo `local.properties` với đường dẫn SDK của máy. File này không được commit.
4. Kiểm tra các cấu hình backend, Firebase và Cloudinary bên dưới.
5. Thực hiện Gradle Sync, chọn cấu hình chạy module `app`, chọn emulator/thiết bị và nhấn **Run**.

### Backend API

Địa chỉ API hiện được khai báo tại `BASE_URL` trong [StringConstants.kt](app/src/main/java/com/example/shoppingapp/utils/StringConstants.kt). Cấu hình hiện dùng một địa chỉ ngrok; cần xác nhận địa chỉ còn hoạt động và trỏ tới backend tương thích trước khi chạy các chức năng trực tuyến.

Khi cấu hình môi trường phát triển riêng, dùng URL mà emulator/thiết bị truy cập được, giữ đường dẫn gốc API và dấu `/` cuối URL. Retrofit và client có/không xác thực được cung cấp trong [APIModule.kt](app/src/main/java/com/example/shoppingapp/di/APIModule.kt).

Backend cần cung cấp các API mà ứng dụng khai báo trong `data/remote/api/`, gồm tài khoản, sản phẩm, giỏ hàng, cửa hàng, đơn hàng, đánh giá, vận chuyển và thanh toán. Hướng dẫn khởi động backend thuộc repository backend tương ứng.

Để kiểm thử với backend riêng trên bản debug, dự án hỗ trợ Gradle property `reviewTestBaseUrl`. Ví dụ khi backend chạy trên máy phát triển và lắng nghe cổng `18081`:

```powershell
# Chuyển tiếp cổng từ thiết bị/emulator về máy phát triển
adb reverse tcp:18081 tcp:18081

# Build và cài bản debug dùng backend thử nghiệm
.\gradlew.bat :app:installDebug -PreviewTestBaseUrl=http://127.0.0.1:18081/api/
```

Ví dụ này yêu cầu backend tương thích đang chạy và thiết bị đã kết nối qua ADB. Property chỉ có hiệu lực trên bản debug; bản release dùng `BASE_URL`. Sau khi kiểm thử, build/cài lại không truyền property để quay về URL mặc định. Xem [hướng dẫn kiểm thử đánh giá](docs/product-reviews.md) nếu cần dữ liệu fixture và tham số instrumentation.

### Firebase

- Kiểm tra `app/google-services.json` thuộc đúng Firebase project và Android application ID `com.example.shoppingapp`.
- Cấu hình Firebase Authentication cho các phương thức email/mật khẩu và Google được sử dụng trong ứng dụng; kiểm tra thông tin chứng chỉ ký cho môi trường đăng nhập Google.
- Kiểm tra Firestore và quyền truy cập phù hợp với luồng dữ liệu tài khoản trong mã nguồn.

Nếu dùng Firebase project riêng, cần cấu hình đồng bộ cả phía ứng dụng và backend liên quan. Không ghi đè cấu hình dịch vụ dùng chung khi chưa xác định môi trường cần chạy.

### Cloudinary

Ứng dụng khởi tạo Cloudinary trong `utils/MyApplication.kt`; cloud name, upload preset và tên thư mục được khai báo trong `StringConstants.kt`. Môi trường chạy cần có cloud và upload preset phù hợp với các uploader tại `data/source/cloudinary/`.

Không đặt API secret, token hoặc mật khẩu vào README hay hằng số trong ứng dụng.

### Vận chuyển và thanh toán

Ứng dụng gọi các API GHN và tạo URL thanh toán qua backend. Cần cấu hình dịch vụ tương ứng ở backend để kiểm tra phí vận chuyển, thời gian giao hàng và VNPay.

Callback thanh toán quay về ứng dụng qua deep link:

```text
shoppingapp://payment-result
```

Deep link được khai báo trong Manifest cho `MainActivity`. Khi kiểm tra thanh toán, cần xác nhận URL trả về từ backend khớp với luồng xử lý của ứng dụng; ưu tiên môi trường thử nghiệm.

## Lệnh build và kiểm thử

Chạy tại thư mục gốc bằng PowerShell:

```powershell
# Xem phiên bản Gradle và JVM đang dùng
.\gradlew.bat --version

# Build APK debug
.\gradlew.bat :app:assembleDebug

# Cài bản debug lên thiết bị/emulator đã kết nối
.\gradlew.bat :app:installDebug

# Chạy unit test
.\gradlew.bat :app:testDebugUnitTest

# Chạy Android Lint
.\gradlew.bat :app:lintDebug

# Chạy instrumented test trên thiết bị/emulator
.\gradlew.bat :app:connectedDebugAndroidTest
```

Trên macOS/Linux thay `.\gradlew.bat` bằng `./gradlew`.

Sau khi build thành công, APK debug mặc định nằm tại:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Các nhóm test hiện có:

| Nhóm | Phạm vi |
| --- | --- |
| `ReviewContractTest` | Hợp đồng dữ liệu đánh giá, mapping và truyền tiếp cancellation |
| `DashboardContractTest` | Bộ lọc ngày/năm, giới hạn khoảng ngày, mapping số tiền lớn và xử lý dữ liệu dashboard |
| `ReviewDeviceTest` | Luồng đánh giá trên Android; một số ca cần backend, tài khoản và dữ liệu fixture riêng |
| `DashboardDeviceTest` | Trạng thái tải/lỗi/thử lại, đổi bộ lọc, tách dữ liệu tài khoản và hiển thị dashboard với repository giả |
| `ExampleUnitTest`, `ExampleInstrumentedTest` | Test mẫu phép cộng và application ID |

`ClientTest.kt` chỉ có hàm `main()` rỗng. Bộ test hiện tại chưa bao phủ đầy đủ các luồng mua hàng và bán hàng. Các ca kiểm thử đánh giá cần fixture có thể được bỏ qua khi thiếu tham số; xem tài liệu chi tiết trước khi chạy để hiểu phạm vi thực tế.

Các lệnh trên là hướng dẫn chạy; README này không xác nhận build, test hoặc tích hợp dịch vụ đã thành công trong môi trường hiện tại.

## Xử lý lỗi thường gặp

| Hiện tượng | Cách kiểm tra |
| --- | --- |
| Gradle báo thiếu Java hoặc JVM không phù hợp | Chạy `.\gradlew.bat --version`, kiểm tra `JAVA_HOME` và Gradle JDK trong Android Studio; dùng JDK 17 cho cấu hình dự án. |
| Không tìm thấy Android SDK hoặc Platform 35 | Cài Android SDK Platform 35 trong SDK Manager, kiểm tra `sdk.dir` trong `local.properties` của máy. |
| Gradle Sync không tải được dependency | Kiểm tra kết nối mạng, proxy và quyền truy cập Google Maven, Maven Central, JitPack; xem dependency cụ thể trong log lỗi. |
| Build được nhưng không tải được dữ liệu | Kiểm tra `BASE_URL`, tình trạng backend/ngrok và khả năng truy cập từ thiết bị; `localhost` trên thiết bị không tự trỏ về máy phát triển. |
| Đăng nhập Google thất bại | Kiểm tra Google Play Services, Firebase project, application ID, phương thức đăng nhập và chứng chỉ ký SHA của bản đang chạy. |
| Upload ảnh thất bại | Kiểm tra kết nối mạng, cloud name, upload preset và giới hạn ảnh theo luồng chức năng; với đánh giá, xem tài liệu tích hợp bên dưới. |
| Thanh toán không quay về ứng dụng | Kiểm tra URL callback backend và deep link `shoppingapp://payment-result` trong Manifest; xác minh trên môi trường thử nghiệm. |
| Không chạy được instrumented test | Dùng `adb devices` kiểm tra thiết bị đã kết nối/được cấp quyền, rồi kiểm tra fixture và tham số mà nhóm test yêu cầu. |

## Tài liệu chi tiết

- [Dashboard người bán](docs/seller-dashboard.md): ý nghĩa số liệu, bộ lọc, trạng thái UI, cách kiểm thử và phạm vi đã xác minh.
- [Tích hợp đánh giá sản phẩm](docs/product-reviews.md): upload ảnh, quyền truy cập URI, khôi phục bản nháp, fixture và cách chạy test trên thiết bị.

Các tài liệu này hiện được viết bằng tiếng Anh. Kết quả kiểm thử ghi trong đó gắn với thời điểm và môi trường được mô tả.

## Lưu ý khi phát triển

- Đọc [AGENTS.md](AGENTS.md) trước khi thay đổi code để tuân thủ quy trình phê duyệt, kiến trúc, quy tắc Kotlin và kiểm tra phù hợp với mobile.
- Khai báo hằng số tập trung và import để sử dụng; ưu tiên tái sử dụng `StringConstants.kt`. Chuỗi hiển thị, màu và kích thước UI dùng Android resource tương ứng.
- Khi thay đổi schema Room, bổ sung migration và đăng ký trong `di/DatabaseModule.kt`; không xóa dữ liệu người dùng để né lỗi migration.
- Không commit cấu hình máy cá nhân, thông tin bí mật hoặc artifact build.
- Kiểm tra thêm trên thiết bị các trạng thái mất mạng, quay lại màn hình, quyền bị từ chối và callback thanh toán khi sửa những luồng liên quan.
