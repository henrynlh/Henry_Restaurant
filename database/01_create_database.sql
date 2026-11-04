-- ====================================================
-- SCRIPT 01: KHỞI TẠO CƠ SỞ DỮ LIỆU
-- ====================================================

-- 1. Xóa database cũ nếu tồn tại để làm sạch hoàn toàn
DROP DATABASE IF EXISTS hk_restaurant;

-- 2. Tạo database mới với chuẩn Unicode hỗ trợ tiếng Việt
CREATE DATABASE hk_restaurant CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. Chọn database để sử dụng
USE hk_restaurant;