# Henry_Restaurant

Ứng dụng quản lý nhà hàng, gồm phiên bản web Spring Boot và phiên bản desktop Java Swing của HK_Restaurant. Dữ liệu lưu trên PostgreSQL.

## Chức năng

- Đăng nhập, phân quyền quản trị viên và nhân viên.
- POS: mở bàn, gọi món, ghi chú, cập nhật số lượng, thanh toán.
- Quản lý bàn ăn, thực đơn, hình ảnh và nhân viên.
- Lịch sử hóa đơn, tải PDF tiếng Việt.
- Báo cáo doanh thu, món bán chạy và doanh thu nhân viên.

## Chạy web

Yêu cầu Java 17 trở lên và Maven. Cấu hình biến môi trường `DB_PASSWORD`; dùng `DB_URL` và `DB_USER` nếu cần thay database mặc định.

```sh
mvn package
java -jar target/HK_Restaurant-1.0.0.jar
```

Truy cập http://localhost:8080. Trong IntelliJ, chạy `com.hk_restaurant.web.WebLauncher` với các biến môi trường tương ứng.

Tài khoản có trong script dữ liệu mẫu: `admin / 123456`. Mật khẩu này chỉ dùng với dữ liệu demo.

Xem [hướng dẫn đầy đủ và triển khai Render](README-WEB.md), [script PostgreSQL](database/postgresql/00_setup_all.sql) và [kết quả kiểm tra](tests/web-results.md).

Không commit mật khẩu database, file `.env`, cấu hình IDE cá nhân hoặc thư mục `target`.
