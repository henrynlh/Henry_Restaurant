# Kết quả kiểm tra web — 04/11/2026

Build `mvn package`: PASS. JavaScript `node --check`: PASS. Python test scripts compile: PASS.

Chạy JAR thật trên cổng 18080 với schema PostgreSQL riêng `hk_web_check_20261104_full`. Schema thử đã được xóa sau kiểm tra; không chạy test CRUD lên `public`.

## HTTP integration — PASS

- Đăng nhập sai/đúng, session, đăng xuất.
- STAFF bị chặn API quản trị; thao tác thiếu CSRF bị từ chối.
- Đọc 17 món, 5 loại món và 12 bàn mẫu.
- Tạo/sửa bàn; chặn tạm ngưng bàn đang phục vụ; tạm ngưng/xóa bàn sau thanh toán.
- Mở bàn lặp trả cùng một đơn; STAFF không gán nhân viên khác khi mở bàn.
- Thêm món, tăng/giảm số lượng, xóa món, ghi chú tiếng Việt, đổi nhân viên.
- Giá gửi giả từ client bị bỏ qua, tổng tiền lấy theo giá database.
- Chặn thanh toán đơn rỗng; thanh toán chuyển khoản thành công; chặn thanh toán lại.
- Hóa đơn xuất PDF, kiểm tra chữ ký `%PDF-` và có nội dung; lịch sử trả hóa đơn vừa tạo.
- Thêm/sửa/ngừng bán/xóa món.
- Thêm/sửa nhân viên, nghỉ tạm, đi làm lại, đặt lại mật khẩu, nghỉ việc và xóa.
- Tài khoản nghỉ tạm không đăng nhập được; session đang đăng nhập bị thu hồi sau khi nghỉ việc.
- Báo cáo doanh thu tháng, món bán chạy, doanh thu nhân viên và trung bình hóa đơn trả dữ liệu.

## HTTP đồng thời — PASS

- Hai phiên mở cùng bàn chỉ tạo một đơn.
- Hai phiên cùng thêm món không mất số lượng.
- Hai phiên cùng thanh toán chỉ có một yêu cầu thành công.

## Bản chạy chính

- `http://localhost:8080/`, `/app.css`, `/app.js`: HTTP 200.
- Đọc database Render `test_db_o340`, schema mặc định: 8 bảng và 17 món ăn.
- JAR: `target/HK_Restaurant-1.0.0.jar`.
- Server khởi chạy cho người dùng: PID 35688. PID chỉ có giá trị tại thời điểm kiểm tra.

## Giới hạn kiểm tra

Browser access host và native Computer Use pipe không khả dụng trong phiên này. Chưa kiểm tra trực quan bố cục hoặc thao tác DOM bằng trình duyệt thật. Upload ảnh chưa được kiểm tra qua HTTP. Dockerfile và cấu hình Render đã được chuẩn bị nhưng chưa build Docker hoặc triển khai dịch vụ công khai. PDF đã được kiểm tra phản hồi HTTP; chưa render để kiểm tra bố cục.
