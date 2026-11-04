# HK Restaurant Web

Ứng dụng web Spring Boot, Thymeleaf và JavaScript dùng lại entity, DAO và service của bản Swing. Điểm khởi chạy web là `com.hk_restaurant.web.WebLauncher`; `AppLauncher` vẫn chạy desktop.

## Chạy trên IntelliJ

1. Reload Maven sau khi mở dự án, dùng JDK 17 trở lên.
2. Duplicate cấu hình chạy `AppLauncher`, đặt tên `WebLauncher` và đổi Main class thành `com.hk_restaurant.web.WebLauncher`. Giữ biến `DB_PASSWORD` đang dùng thành công với Render.
3. Working directory là thư mục gốc `HK_Restaurant`.
4. Run, chờ dòng `Started WebLauncher`, mở **http://localhost:8080**.

Tài khoản dữ liệu mẫu: `admin / 123456` (quản trị), `phucvu1 / 123456` (nhân viên). Tài khoản và dữ liệu dùng chung với desktop, thay đổi trên web sẽ ghi vào cùng database. Không cần chạy lại script seed khi đã có bảng.

## Chạy từ PowerShell

```powershell
.\run-web.ps1 -Build
```

Script ưu tiên `DB_PASSWORD` trong terminal. Nếu chưa đặt, script đọc biến này từ cấu hình AppLauncher hiện có trong `.idea/workspace.xml`, không ghi mật khẩu vào file mới. Bỏ `-Build` khi chỉ muốn chạy JAR đã build. Ctrl+C để dừng.

Hoặc build và chạy thủ công:

```powershell
mvn package
java -jar target/HK_Restaurant-1.0.0.jar
```

## Chức năng

| Màn hình | Chức năng | Quyền |
| --- | --- | --- |
| Đăng nhập | Tài khoản hiện có, session 30 phút, đăng xuất, chặn nhân viên nghỉ việc/nghỉ tạm | Tất cả |
| POS | Sơ đồ bàn, mở bàn, tìm/lọc món, thêm/bỏ món, số lượng, ghi chú, tiền mặt/chuyển khoản | ADMIN, STAFF |
| Đổi nhân viên phục vụ | Gán lại nhân viên đang làm cho bàn | ADMIN |
| Hóa đơn | 200 hóa đơn mới nhất, tải PDF tiếng Việt | ADMIN, STAFF |
| Bàn ăn | Thêm, sửa, tạm ngưng, mở lại, xóa theo quy tắc service hiện có | ADMIN |
| Thực đơn | Thêm, sửa, loại món, giá, ảnh PNG/JPEG, ngừng/mở bán, xóa theo quy tắc hiện có | ADMIN |
| Nhân viên | Thêm, sửa, nghỉ tạm, đi làm lại, nghỉ việc, xóa, đặt lại mật khẩu | ADMIN |
| Báo cáo | Doanh thu ngày/tháng/năm, món bán chạy, doanh thu nhân viên, giá trị hóa đơn trung bình | ADMIN |

Sửa nhân viên, nghỉ tạm/nghỉ việc/xóa yêu cầu mật khẩu của nhân viên đó như bản desktop. Đặt lại mật khẩu trên web yêu cầu mật khẩu của quản trị viên đang đăng nhập; không dùng khóa root demo. Không cho khóa/xóa chính tài khoản đang đăng nhập. Quyền và trạng thái tài khoản được đọc lại ở mỗi yêu cầu API.

Giá món lấy từ server. Các lệnh POS dùng một transaction và khóa bàn để tránh hai người cùng mở bàn hoặc thanh toán trùng. Web giữ CSRF bảo vệ mọi lệnh thay đổi dữ liệu; phản hồi API chỉ chứa DTO, không trả hash mật khẩu hoặc toàn bộ quan hệ entity.

## Cấu hình

| Biến môi trường | Ý nghĩa |
| --- | --- |
| `DB_PASSWORD` | Bắt buộc, mật khẩu PostgreSQL |
| `DB_URL` | Tùy chọn, JDBC URL PostgreSQL; mặc định theo persistence.xml |
| `DB_USER` | Tùy chọn, tài khoản database; mặc định theo persistence.xml |
| `PORT` | Mặc định 8080 |
| `COOKIE_SECURE` | `true` khi chạy sau HTTPS; để `false` khi dùng localhost HTTP |

Ảnh mặc định được đóng gói trong JAR. Ảnh upload lưu ở thư mục `images/` của working directory. Linux cần font DejaVu Sans để xuất PDF (Dockerfile đã cài); Windows dùng Arial có sẵn.

## Triển khai Render

Đã có `Dockerfile` và `render.yaml`. Đưa source lên repository của bạn rồi tạo Render Web Service dùng Docker, với thư mục gốc trỏ đến HK_Restaurant. Nhập `DB_PASSWORD`, `DB_USER`, `DB_URL` trong Environment của Render. `DB_URL` cần dạng `jdbc:postgresql://host:5432/database?sslmode=require`, không phải URL bắt đầu bằng `postgres://`. Đặt `COOKIE_SECURE=true` khi dùng HTTPS. Không lưu mật khẩu vào Git.

Ứng dụng nhận cổng `PORT` do Render cung cấp. Health check ở `/login`. Không chạy script xóa/tạo lại database khi deploy. Build không cần database password.

Render mặc định không giữ các file ghi mới qua restart/deploy. Muốn giữ ảnh upload lâu dài, dùng persistent disk ở `/app/images` trên gói hỗ trợ disk hoặc bổ sung object storage. Ảnh đóng gói sẵn và dữ liệu PostgreSQL không bị ảnh hưởng. Tham khảo [Render Docker](https://render.com/docs/docker), [biến môi trường](https://render.com/docs/configure-environment-variables) và [persistent disks](https://render.com/docs/disks).

## Kiểm tra tích hợp

`tests/web_smoke.py` thực hiện HTTP thật: đăng nhập sai/đúng, session, phân quyền STAFF, CSRF, đọc món, CRUD, mở bàn, chống sửa giá, thêm/xóa món, đổi nhân viên, thanh toán, chặn thanh toán lặp, PDF và báo cáo.

Chỉ chạy trên schema thử riêng, vì bài kiểm tra có ghi/xóa dữ liệu:

1. Chuẩn bị JDBC classpath: `mvn dependency:build-classpath -Dmdep.outputFile=target/web-classpath.txt`.
2. Đặt `DB_TEST_SCHEMA=hk_web_check_<tên_duy_nhất>` và `DB_PASSWORD`. Chạy Java source `tests/PrepareWebTestDatabase.java` với classpath vừa tạo để tạo schema và seed riêng.
3. Khởi chạy WebLauncher trên cổng 18080, thêm `&currentSchema=<schema thử>` vào `DB_URL` đã có `?sslmode=require`.
4. Đặt `HK_WEB_TEST_URL=http://localhost:18080` và `HK_WEB_TEST_ALLOW_WRITES=isolated-schema`, chạy `python tests/web_smoke.py`.
5. Dừng server thử; chạy PrepareWebTestDatabase với đối số `drop` để xóa riêng schema thử đó.

`PrepareWebTestDatabase` chỉ nhận tên schema bắt đầu bằng `hk_web_check_`, không xóa `public`.

Sau smoke test, chạy thêm `python tests/web_concurrency.py` để kiểm tra hai phiên cùng mở một bàn, cùng thêm món và cùng thanh toán. Chỉ một lần thanh toán được chấp nhận.
