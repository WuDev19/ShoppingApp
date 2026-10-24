# Nguyên tắc code cho ShoppingApp

File này quy định cách viết, sửa và kiểm tra code trong dự án ShoppingApp. Agent phải đọc và tuân thủ trước khi thay đổi mã nguồn. Những quy tắc dưới đây áp dụng cho code mới và phần code được chỉnh sửa; không yêu cầu sửa toàn bộ code cũ chỉ để đồng bộ phong cách.

## Quy trình làm việc và phê duyệt cho Android

Trước khi sửa, tạo mới hoặc xóa bất kỳ file nào:

1. Đọc code liên quan và phân tích luồng trên ứng dụng: điểm vào màn hình, thao tác người dùng, điều hướng, ViewModel/use case, API và cache cục bộ. Với lỗi, xác định điều kiện tái hiện, phiên bản Android và trạng thái ứng dụng nếu có thông tin. Có thể kiểm tra chỉ đọc trước khi xin phê duyệt.
2. Trình bày kế hoạch, nêu rõ:
    - File/thư mục dự kiến sửa, tạo mới hoặc xóa.
    - Logic hoặc cấu trúc thay đổi và lý do cho từng thay đổi.
    - Thay đổi package, dependency, SDK, Manifest, permission, navigation/deep link, API hoặc schema Room nếu có.
    - Ảnh hưởng đến vòng đời màn hình, trạng thái UI, dữ liệu đã lưu và hành vi khi mạng chậm hoặc mất kết nối, nếu liên quan.
    - Cách kiểm tra bằng build, test, lint và thao tác trên emulator/thiết bị phù hợp với thay đổi. Với task không tầm thường (từ 3 bước hoặc có quyết định kiến trúc), viết rõ phạm vi, hành vi mong đợi và tiêu chí hoàn thành trên ứng dụng.
3. Dừng lại và hỏi: **"Bạn có đồng ý với kế hoạch này không?"**
4. Chỉ thực hiện sau khi người dùng trả lời **"đồng ý"** hoặc **"ok"**. Không hỏi lại cho các bước đã nằm trong kế hoạch được duyệt.

Nếu người dùng yêu cầu sửa kế hoạch, cập nhật và xin xác nhận lại trước khi làm. Nếu phát sinh thay đổi ngoài phạm vi đã duyệt, trình bày phần bổ sung và chờ xác nhận trước khi thực hiện phần đó.

Ưu tiên thay đổi tối thiểu, giải quyết nguyên nhân gốc. Không tự ý refactor diện rộng, đổi tên package/entity, nâng phiên bản thư viện hoặc sửa file không liên quan. Giữ nguyên thay đổi sẵn có của người dùng. Khi được người dùng sửa cách làm, ghi nhận quy tắc để tránh lặp lại; nếu cần ghi vào file, đưa việc đó vào kế hoạch phê duyệt. 

## Phạm vi và cách làm việc

- Áp dụng cho toàn bộ repository; nếu một thư mục có `AGENTS.md` riêng, áp dụng thêm hướng dẫn cụ thể tại đó.
- Trao đổi và tóm tắt kết quả bằng tiếng Việt, trừ khi người dùng yêu cầu ngôn ngữ khác. Giữ tên lớp, API và thuật ngữ trong mã theo quy ước hiện có.
- Đọc mã liên quan trước khi sửa. Ưu tiên thay đổi nhỏ, đúng phạm vi yêu cầu; không tự ý tái cấu trúc toàn dự án, đổi kiến trúc hoặc nâng cấp thư viện.
- Giữ nguyên những thay đổi có sẵn của người dùng. Không tự commit, push hay sửa repository bên cạnh nếu chưa được yêu cầu.
- Phân biệt hiện trạng và quy tắc cho mã mới: dự án còn một số chỗ phụ thuộc chéo giữa các lớp; không mở rộng các phụ thuộc đó hoặc sửa hàng loạt ngoài phạm vi công việc.

## Nguyên tắc làm việc với ứng dụng mobile

- Bắt đầu từ hành vi người dùng trên màn hình và luồng nghiệp vụ hiện có. Khi sửa một tính năng, kiểm tra các điểm vào liên quan như bottom navigation, nút Back, deep link và quay lại từ ứng dụng bên ngoài; không chỉ kiểm tra màn hình khi mở lần đầu.
- Xem vòng đời Android là một phần của yêu cầu: tác vụ và observer phải có scope phù hợp; tránh gọi API lặp, mất dữ liệu nhập hoặc xử lý lại sự kiện khi tạo lại view, xoay màn hình hay đưa ứng dụng về foreground. Với trạng thái cần khôi phục sau process death, dùng cơ chế lưu phù hợp, không chỉ dựa vào ViewModel.
- Thiết kế rõ hành vi khi mạng chậm, mất mạng, timeout và thử lại. Tận dụng cache hiện có khi phù hợp; phân biệt dữ liệu rỗng với lỗi tải. Không tự động gửi lại thao tác tạo đơn, thanh toán hoặc thao tác ghi khác nếu chưa có cơ chế chống trùng.
- Ưu tiên độ mượt và tài nguyên thiết bị: không chặn main thread, không tải toàn bộ danh sách khi đã có Paging, giới hạn kích thước ảnh theo nhu cầu hiển thị, giải phóng listener và tài nguyên theo vòng đời. Không thêm polling hoặc tác vụ nền liên tục khi chưa có yêu cầu.
- Chỉ yêu cầu permission khi tính năng cần dùng; xử lý trường hợp bị từ chối, từ chối vĩnh viễn hoặc dịch vụ vị trí bị tắt. Kiểm tra khác biệt permission và API giữa các phiên bản Android được hỗ trợ; không tăng `minSdk` để né xử lý tương thích.
- Với thay đổi giao diện, kiểm tra kích thước màn hình, mật độ hiển thị, cỡ chữ hệ thống, bàn phím, system bar/insets và theme sáng/tối ở phần bị ảnh hưởng. Dùng `dp` cho kích thước, `sp` cho chữ; bảo đảm vùng chạm phù hợp và nhãn truy cập cho các nút chỉ có biểu tượng.
- Với dữ liệu tài khoản, giỏ hàng và đơn hàng, kiểm tra chuyển tài khoản, đăng xuất và cache theo người dùng khi sửa luồng liên quan. Không để dữ liệu của phiên trước hiển thị cho tài khoản khác.
- Với thanh toán, upload ảnh và chọn vị trí, kiểm tra đường quay về ứng dụng sau khi mở trình duyệt, thư viện ảnh hoặc hộp thoại hệ thống; xử lý hủy thao tác và kết quả trả về nhiều lần khi có thể xảy ra.
- Chọn kiểm tra theo rủi ro thực tế. Thay đổi UI/vòng đời cần xác minh trên emulator hoặc thiết bị nếu có; thay đổi logic cần test hành vi liên quan. Build thành công chưa đủ để kết luận trải nghiệm trên thiết bị hoạt động đúng. Nếu chưa kiểm tra được, ghi rõ kịch bản còn thiếu khi bàn giao.

## Tổng quan kỹ thuật

- Ứng dụng Android mua sắm cho người mua và người bán; module duy nhất là `:app`, package gốc `com.example.shoppingapp`.
- Kotlin, Gradle Kotlin DSL và version catalog `gradle/libs.versions.toml`.
- Giao diện dùng Activity/Fragment, XML, ViewBinding, RecyclerView và Navigation Component với Safe Args. Tiếp tục dùng hệ thống này cho các màn hình hiện có.
- MVVM với use case và repository; Hilt để inject dependency, KSP để sinh mã.
- Coroutines/Flow, LiveData, Paging 3; Retrofit/OkHttp và Moshi cho API; Room cho dữ liệu cục bộ.
- Tích hợp Firebase Auth/Firestore, Cloudinary, MapLibre, Google Location, vận chuyển GHN và thanh toán.
- Cấu hình tại thời điểm viết: Gradle Wrapper 8.7, AGP 8.6.0, Kotlin 2.0.21; `compileSdk = 35`, `minSdk = 24`, `targetSdk = 34`; bytecode Java/Kotlin target 1.8. Khi có khác biệt, lấy file build làm nguồn chính xác.

## Bản đồ mã nguồn

Các đường dẫn package sau nằm dưới `app/src/main/java/com/example/shoppingapp/`:

| Đường dẫn | Trách nhiệm |
| --- | --- |
| `ui/view/activity/`, `ui/view/fragment/` | Hiển thị, tương tác và vòng đời màn hình |
| `ui/view/adapter/` | Adapter cho danh sách và phân trang |
| `ui/viewmodel/`, `ui/state/` | Trạng thái UI và xử lý sự kiện qua ViewModel; trạng thái kết quả dùng `StateListener` |
| `domain/model/`, `domain/repository/`, `domain/usecase/` | Model nghiệp vụ, hợp đồng repository và use case |
| `data/remote/api/`, `data/remote/dto/`, `data/remote/mapper/` | Khai báo API, dữ liệu truyền tải và chuyển đổi model |
| `data/remote/interceptors/`, `data/remote/helper/` | Xác thực HTTP và hỗ trợ xử lý mạng |
| `data/local/` | Room entity, DAO, POJO, converter, mapper và database migration |
| `data/repository/impl/`, `data/repository/mediator/` | Hiện thực repository và đồng bộ phân trang với cache |
| `data/source/` | PagingSource và các bộ upload Cloudinary |
| `di/`, `di/qualifier/` | Hilt module và qualifier |
| `utils/` | Tiện ích, session/token, hằng số và `MyApplication` |

Các vị trí khác:

- `app/src/main/res/layout/`: giao diện XML; `res/navigation/`: `nav_home.xml`, `nav_store.xml`, `nav_profile.xml`.
- `app/src/main/res/values/`: chuỗi, màu và theme; `values-night/`: theme tối.
- `app/src/main/AndroidManifest.xml`: component, permission và deep link.
- `app/src/test/`: unit test JVM; `app/src/androidTest/`: test chạy trên Android.

## Quy tắc triển khai

- Theo định dạng file lân cận; Kotlin dùng thụt lề 4 dấu cách, lớp `PascalCase`, hàm/biến `camelCase`, resource `snake_case`. Lưu file UTF-8 để giữ nguyên tiếng Việt.
- Đặt tên theo ý nghĩa nghiệp vụ; không dùng tên viết tắt khó hiểu. Hằng số dùng `UPPER_SNAKE_CASE`; không đổi tên public API hoặc resource hiện có chỉ để chuẩn hóa.
- Khi tạo hằng số mới, khai báo tập trung trong file constant chung rồi import để sử dụng; không khai báo rải rác trong Activity, Fragment, ViewModel, adapter hoặc repository, không lặp lại magic number/magic string. Ưu tiên tái sử dụng `app/src/main/java/com/example/shoppingapp/utils/StringConstants.kt` (object `StringConstants`, hiện chứa cả chuỗi và số); kiểm tra hằng số đã có trước khi thêm. Nếu cần tách theo nhóm nghiệp vụ, đặt trong file `*Constants.kt` dùng chung tại package phù hợp và tuân theo cùng quy tắc. Dùng `const val` khi giá trị là hằng số tại thời điểm biên dịch, nếu không thì dùng `val`; import object hoặc từng hằng số cần dùng, không dùng wildcard import. Chuỗi hiển thị trên UI vẫn đặt trong `strings.xml`, màu/kích thước giao diện đặt trong Android resource tương ứng; không đưa thông tin bí mật vào file constant.
- Ưu tiên `val`, kiểu dữ liệu cụ thể và phạm vi truy cập nhỏ nhất cần thiết. Không dùng `Any`, ép kiểu không an toàn hoặc `!!` để bỏ qua vấn đề thiết kế; nếu dữ liệu có thể thiếu, xử lý rõ bằng nullable type và nhánh tương ứng.
- Mỗi hàm/lớp cần có trách nhiệm rõ ràng. Tách phần xử lý khi có nhiều trách nhiệm hoặc logic lặp thực sự; không thêm abstraction, base class hay helper dùng chung khi chưa có nhu cầu cụ thể.
- Comment giải thích lý do hoặc ràng buộc nghiệp vụ; không diễn giải lại từng dòng code. Xóa import, biến và code không còn dùng trong phần đang sửa; không để code cũ dưới dạng comment.
- UI gọi ViewModel; ViewModel điều phối use case; repository xử lý nguồn dữ liệu. Với chức năng mới, không gọi trực tiếp Retrofit/DAO từ Activity, Fragment hoặc adapter. Adapter hiển thị dữ liệu và gửi callback tương tác, không thực hiện nghiệp vụ hoặc gọi mạng.
- Code nghiệp vụ mới trong `domain/` không phụ thuộc Activity, Fragment, ViewBinding, DTO/Room entity hoặc implementation trong `data/`. Model nghiệp vụ là kiểu dữ liệu trao đổi giữa các lớp; không mở rộng những phụ thuộc chéo còn tồn tại trong code cũ.
- Đặt DTO, entity và domain model đúng lớp; dùng mapper hiện có hoặc bổ sung mapper khi cần, tránh đưa chi tiết JSON/Room vào UI mới.
- Dùng constructor injection với `@Inject`; ViewModel dùng `@HiltViewModel`. Khi thêm repository hoặc dịch vụ, cập nhật binding/provider và qualifier trong `di/`.
- Tác vụ mạng, database và xử lý file không được chặn main thread. Dùng scope gắn với vòng đời; không thêm `GlobalScope` và không nuốt `CancellationException` khi xử lý lỗi coroutine.
- Trong Fragment, observe/collect theo `viewLifecycleOwner`; dùng `repeatOnLifecycle` cho Flow khi phù hợp, giải phóng binding trong `onDestroyView()`. Không giữ View/Activity trong ViewModel.
- Giữ các trạng thái tải, thành công, lỗi và danh sách rỗng rõ ràng theo pattern của màn hình; không bỏ xử lý lỗi để làm build hoặc test chạy qua.
- Kiểm tra dữ liệu đầu vào ở lớp chịu trách nhiệm trước khi thực hiện tác vụ. Không thay lỗi bằng giá trị mặc định hoặc báo thành công giả; thông báo cho người dùng phải có ý nghĩa và không lộ lỗi nội bộ hay dữ liệu nhạy cảm.
- Với Paging, kiểm tra đồng thời PagingSource/RemoteMediator, khóa truy vấn và cache Room; giữ dữ liệu tách theo người dùng, shop và bộ lọc tương ứng. Dùng `cachedIn(viewModelScope)` khi chia sẻ luồng phân trang trong ViewModel.
- Chuỗi UI mới đặt trong `strings.xml`; tái sử dụng resource màu/theme. Khi đổi ID hoặc tham số điều hướng, cập nhật XML và các nơi gọi Safe Args cùng nhau.
- Không sửa trực tiếp mã ViewBinding, Safe Args, Hilt, Room hoặc Moshi được sinh trong thư mục build.
- Dependency mới ưu tiên khai báo qua version catalog và dùng alias trong `app/build.gradle.kts`; kiểm tra cả plugin khai báo trực tiếp ở build gốc. Không thêm repository Maven hoặc đổi toolchain nếu công việc không cần.

## Dữ liệu và tích hợp

- Khi sửa API, kiểm tra cả interface, DTO, mapper, repository và nơi sử dụng. Giữ tên trường JSON, kiểu dữ liệu, nullability và hợp đồng backend; không tự suy đoán endpoint hoặc response chưa có căn cứ.
- `di/APIModule.kt` tách client có xác thực và không xác thực. Giữ API đăng nhập/refresh trên luồng phù hợp để tránh vòng lặp interceptor; kiểm tra token hết hạn và đăng xuất khi sửa phần này.
- Khi thay đổi schema Room, cập nhật version trong `data/local/db/AppDatabase.kt`, bổ sung migration trong `DatabaseMigrations.kt` và đăng ký tại `di/DatabaseModule.kt`. Không dùng destructive migration để né lỗi hoặc làm mất dữ liệu người dùng.
- Giữ tương thích deep link thanh toán `shoppingapp://payment-result` với Manifest và nơi xử lý callback. Thay đổi đặt hàng, phí vận chuyển, voucher và thanh toán cần kiểm tra cả kết quả thành công, thất bại và hủy.
- Không thêm token, mật khẩu, private key hoặc thông tin tài khoản thật vào mã, log, test hay tài liệu. Không in nội dung nhạy cảm khi kiểm tra cấu hình.
- `local.properties` là cấu hình máy cá nhân; không commit hoặc thay đường dẫn SDK của người dùng. Không tự thay `app/google-services.json`, thông tin dịch vụ hay địa chỉ backend để phù hợp máy của agent.
- Không đưa artifact từ `.gradle/`, `.kotlin/`, `build/`, `app/build/` hoặc cấu hình IDE cá nhân vào thay đổi.

## Build và kiểm tra

Chạy tại thư mục gốc dự án. Trên Windows PowerShell dùng Gradle Wrapper:

```powershell
# Kiểm tra JVM và phiên bản Wrapper
.\gradlew.bat --version

# Build APK debug
.\gradlew.bat :app:assembleDebug

# Unit test JVM
.\gradlew.bat :app:testDebugUnitTest

# Android Lint
.\gradlew.bat :app:lintDebug

# Instrumented test: cần emulator hoặc thiết bị đã kết nối
.\gradlew.bat :app:connectedDebugAndroidTest
```

Trên macOS/Linux dùng `./gradlew` với cùng task. Cần JDK phù hợp AGP 8.x (JDK 17), Android SDK có platform 35 và cấu hình SDK hợp lệ. JVM chạy Gradle khác với bytecode target 1.8 của ứng dụng. Lần chạy đầu có thể cần mạng để tải Gradle/dependency.

- Chọn kiểm tra theo thay đổi: mã Kotlin/XML/DI cần build debug; logic nghiệp vụ cần test liên quan; resource/Manifest cần lint; vòng đời UI hoặc Room migration cần kiểm tra trên thiết bị khi có điều kiện.
- Với sửa lỗi logic đáng kể, ưu tiên test hồi quy kiểm tra hành vi; không gọi backend thật hoặc tạo đơn hàng/thanh toán thật trong test tự động.
- Test hiện có chủ yếu là mẫu: `ExampleUnitTest` kiểm tra phép cộng, `ExampleInstrumentedTest` kiểm tra package; `ClientTest.kt` chỉ có `main()` rỗng. Chạy qua các test này chưa chứng minh luồng nghiệp vụ hoạt động.
- Thay đổi chỉ về tài liệu không cần build Android. Kiểm tra diff, đường dẫn và lệnh được ghi trong tài liệu.
- Nếu không chạy được do thiếu JDK/SDK, mạng hoặc dependency, báo chính xác lệnh đã chạy và nguyên nhân. Không khẳng định build/test đã đạt khi chưa xác minh; không tự nâng phiên bản để xử lý lỗi môi trường ngoài phạm vi.

## Khi bàn giao

- Kiểm tra `git diff --check` và diff của các file đã sửa; tránh thay đổi định dạng không liên quan.
- Tóm tắt nội dung đã đổi, file chính, kiểm tra đã chạy và các hạn chế còn lại. Với UI hoặc tích hợp cần kiểm tra thủ công, nêu ngắn gọn các bước xác minh.
- Nếu người dùng yêu cầu commit, theo phong cách lịch sử hiện tại như `feat(review): ...` hoặc `fix(my-review-ui): ...`.
