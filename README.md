# Henry_Restaurant

Ứng dụng quản lý nhà hàng, gồm phiên bản web Spring Boot và phiên bản desktop Java Swing của HK_Restaurant. Dữ liệu lưu trên PostgreSQL.

## Chức năng

- Đăng nhập, phân quyền quản trị viên và nhân viên.
- POS: mở bàn, gọi món, ghi chú, cập nhật số lượng, thanh toán.
- Quản lý bàn ăn, thực đơn, hình ảnh và nhân viên.
- Lịch sử hóa đơn, tải PDF tiếng Việt.
- Báo cáo doanh thu, món bán chạy và doanh thu nhân viên.

## Chạy web

Yêu cầu Java 17 trở lên và Maven. Mở terminal tại thư mục gốc dự án. Nếu web đang chạy, nhấn **Ctrl+C trong terminal chạy web** hoặc Stop trong IntelliJ trước khi build, vì Windows không cho ghi đè JAR đang chạy.

Trong PowerShell, đặt mật khẩu database Render cho terminal hiện tại rồi build và chạy:

```powershell
$env:DB_PASSWORD = 'THAY_BANG_MAT_KHAU_DATABASE_RENDER'
mvn package
if ($LASTEXITCODE -eq 0) {
    java -jar target/HK_Restaurant-1.0.0.jar
}
```

Thay chuỗi mẫu bằng mật khẩu database thực tế. `DB_PASSWORD` trong cấu hình Run của IntelliJ **không tự truyền sang terminal**. Mật khẩu database khác mật khẩu đăng nhập web `admin / 123456`. Dùng thêm `DB_URL` và `DB_USER` nếu cần thay database mặc định.

Khi đã đặt `DB_PASSWORD` và server cũ đã dừng, hai lệnh `mvn package` và `java -jar ...` là đủ; chỉ chạy lệnh thứ hai sau khi thấy **BUILD SUCCESS**. Nếu chưa sửa source và JAR đã build thành công, chỉ cần chạy lệnh `java -jar ...`.

Trên máy đã có cấu hình AppLauncher, có thể dùng `./run-web.ps1 -Build` để script đọc lại mật khẩu trong cấu hình IntelliJ. Khi clone mới từ GitHub, cấu hình IDE cá nhân không có trong repo nên cần tự đặt `DB_PASSWORD`.

Truy cập http://localhost:8080. Trong IntelliJ, chạy `com.hk_restaurant.web.WebLauncher` với các biến môi trường tương ứng.

Tài khoản có trong script dữ liệu mẫu: `admin / 123456`. Mật khẩu này chỉ dùng với dữ liệu demo.

Xem [hướng dẫn đầy đủ và triển khai Render](README-WEB.md), [script PostgreSQL](database/postgresql/00_setup_all.sql) và [kết quả kiểm tra](tests/web-results.md).

Không commit mật khẩu database, file `.env`, cấu hình IDE cá nhân hoặc thư mục `target`.
