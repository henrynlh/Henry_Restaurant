-- ====================================================
-- SCRIPT 03: NẠP DỮ LIỆU MẪU VÀ ĐỒNG BỘ TRẠNG THÁI
-- ====================================================

USE hk_restaurant;

-- 1. INSERT DỮ LIỆU MẪU
INSERT INTO BanAn (MaBan, MaBanCode, TenBan, SucChua, TrangThai, DangSuDung) VALUES
                                                                                 (1,  'BAN01', 'Bàn 01', 4,  'EMPTY',   TRUE),
                                                                                 (2,  'BAN02', 'Bàn 02', 4,  'EMPTY',   TRUE),
                                                                                 (3,  'BAN03', 'Bàn 03', 6,  'EMPTY',   TRUE),
                                                                                 (4,  'BAN04', 'Bàn 04', 4,  'EMPTY',   TRUE),
                                                                                 (5,  'BAN05', 'Bàn 05', 6,  'EMPTY',   TRUE),
                                                                                 (6,  'BAN06', 'Bàn 06', 8,  'SERVING', TRUE),
                                                                                 (7,  'BAN07', 'Bàn 07', 4,  'EMPTY',   TRUE),
                                                                                 (8,  'VIP01', 'VIP 01', 10, 'SERVING', TRUE),
                                                                                 (9,  'VIP02', 'VIP 02', 10, 'EMPTY',   TRUE),
                                                                                 (10, 'VIP03', 'VIP 03', 12, 'EMPTY',   TRUE),
                                                                                 (11, 'BAN08', 'Bàn 08', 2,  'EMPTY',   TRUE),
                                                                                 (12, 'BAN09', 'Bàn 09', 2,  'EMPTY',   FALSE);

INSERT INTO NhanVien (MaNhanVien, MaNhanVienCode, HoTen, SoDienThoai, TenDangNhap, MatKhau, VaiTro, DangLamViec, DaNghiViec) VALUES
                                                                                                                                 (1, 'NV001', 'Nguyễn Văn An', '0901000001', 'admin',     MD5('123456'), 'ADMIN', TRUE, FALSE),
                                                                                                                                 (2, 'NV002', 'Trần Thị Bình', '0901000002', 'thu.ngan1', MD5('123456'), 'STAFF', TRUE, FALSE),
                                                                                                                                 (3, 'NV003', 'Lê Minh Châu',  '0901000003', 'phucvu1',   MD5('123456'), 'STAFF', TRUE, FALSE),
                                                                                                                                 (4, 'NV004', 'Phạm Gia Huy',  '0901000004', 'phucvu2',   MD5('123456'), 'STAFF', TRUE, FALSE),
                                                                                                                                 (5, 'NV005', 'Đỗ Thu Hà',     '0901000005', 'thu.ngan2', MD5('123456'), 'STAFF', TRUE, FALSE),
                                                                                                                                 (6, 'NV006', 'Phạm Đăng Khoa','0901000006', 'manager1',  MD5('123456'), 'ADMIN', TRUE, FALSE);

INSERT INTO LoaiMon (MaLoaiMon, TenLoaiMon, MoTa) VALUES
                                                      (1, 'Lẩu',         'Các món lẩu'),
                                                      (2, 'Nướng',       'Các món nướng'),
                                                      (3, 'Đồ uống',     'Nước và bia'),
                                                      (4, 'Tráng miệng', 'Món tráng miệng'),
                                                      (5, 'Món chính',   'Các món ăn chính');

INSERT INTO MonAn (MaMon, MaLoaiMon, MaMonCode, TenMon, DonGia, HinhAnh, MoTa, DangKinhDoanh) VALUES
                                                                                                  (1,  1, 'MON001', 'Lẩu Thái Hải Sản',        250000.00, NULL, 'Lẩu thái chua cay',         TRUE),
                                                                                                  (2,  1, 'MON002', 'Lẩu Bò Nhúng Dấm',        200000.00, NULL, 'Lẩu bò nhúng dấm',          TRUE),
                                                                                                  (3,  1, 'MON003', 'Lẩu Nấm Gia Đình',        180000.00, NULL, 'Lẩu nấm thanh ngọt',        TRUE),
                                                                                                  (4,  2, 'MON004', 'Bò Tảng Sốt Phô Mai',     150000.00, NULL, 'Bò nướng sốt phô mai',      TRUE),
                                                                                                  (5,  2, 'MON005', 'Ba Chỉ Heo Nướng Sa Tế',   90000.00, NULL, 'Ba chỉ heo sa tế',          TRUE),
                                                                                                  (6,  2, 'MON006', 'Bạch Tuộc Nướng Cay',     120000.00, NULL, 'Bạch tuộc nướng',           TRUE),
                                                                                                  (7,  3, 'MON007', 'Nước ngọt Coca Cola',      20000.00, NULL, 'Lon Coca Cola',             TRUE),
                                                                                                  (8,  3, 'MON008', 'Trà Đá',                    5000.00, NULL, 'Trà đá',                    TRUE),
                                                                                                  (9,  4, 'MON009', 'Dưa Hấu Tráng Miệng',      30000.00, NULL, 'Dưa hấu',                   TRUE),
                                                                                                  (10, 3, 'MON010', 'Bia Heineken',             25000.00, NULL, 'Bia chai/lon',              TRUE),
                                                                                                  (11, 5, 'MON011', 'Khoai Tây Chiên',          45000.00, NULL, 'Khoai tây chiên giòn',      TRUE),
                                                                                                  (12, 5, 'MON012', 'Cơm Chiên Hải Sản',        85000.00, NULL, 'Cơm chiên hải sản',         TRUE),
                                                                                                  (13, 5, 'MON013', 'Mì Xào Bò',                75000.00, NULL, 'Mì xào bò',                 TRUE),
                                                                                                  (14, 3, 'MON014', 'Nước Cam Ép',              35000.00, NULL, 'Cam ép tươi',               TRUE),
                                                                                                  (15, 4, 'MON015', 'Chè Khúc Bạch',            40000.00, NULL, 'Chè khúc bạch',             TRUE),
                                                                                                  (16, 5, 'MON016', 'Cá Hồi Nướng Muối Ớt',    110000.00, NULL, 'Cá hồi nướng',              TRUE),
                                                                                                  (17, 5, 'MON017', 'Cánh Gà Chiên Mắm',        65000.00, NULL, 'Cánh gà',                   TRUE);

INSERT INTO GhiChuMon (MaGhiChu, MaMon, NoiDungGhiChu) VALUES
                                                           (1, 1,  'Ít cay'),
                                                           (2, 1,  'Không hành'),
                                                           (3, 4,  'Thêm phô mai'),
                                                           (4, 7,  'Ít đá'),
                                                           (5, 10, 'Ướp lạnh');

INSERT INTO DonHang (MaDonHang, SoDonHang, MaBan, MaNhanVien, ThoiGianLap, TongTien, TrangThai, GhiChu) VALUES
                                                                                                            (1,  'ORD-20260105001',  1, 2, '2026-01-05 11:30:00', 295000.00, 'PAID',      NULL),
                                                                                                            (2,  'ORD-20260112001',  2, 3, '2026-01-12 18:40:00', 390000.00, 'PAID',      'Khách ngồi ngoài trời'),
                                                                                                            (3,  'ORD-20260120001', 11, 4, '2026-01-20 12:00:00', 165000.00, 'PAID',      NULL),
                                                                                                            (4,  'ORD-20260203001',  5, 2, '2026-02-03 19:20:00', 450000.00, 'PAID',      NULL),
                                                                                                            (5,  'ORD-20260210001',  4, 5, '2026-02-10 11:10:00', 260000.00, 'PAID',      NULL),
                                                                                                            (6,  'ORD-20260218001',  8, 3, '2026-02-18 20:05:00', 535000.00, 'PAID',      'Khách VIP'),
                                                                                                            (7,  'ORD-20260301001',  7, 4, '2026-03-01 18:30:00', 330000.00, 'PAID',      NULL),
                                                                                                            (8,  'ORD-20260308001',  9, 2, '2026-03-08 12:15:00', 210000.00, 'PAID',      NULL),
                                                                                                            (9,  'ORD-20260315001', 10, 5, '2026-03-15 19:00:00', 485000.00, 'PAID',      NULL),
                                                                                                            (10, 'ORD-20260320001', 11, 3, '2026-03-20 17:30:00', 175000.00, 'PAID',      NULL),
                                                                                                            (11, 'ORD-20260328001',  6, 4, '2026-03-28 10:50:00', 325000.00, 'PAID',      NULL),
                                                                                                            (12, 'ORD-20260328002',  6, 4, '2026-03-28 12:10:00', 455000.00, 'SERVING',   'Bàn đang ăn'),
                                                                                                            (13, 'ORD-20260328003',  8, 2, '2026-03-28 12:20:00', 180000.00, 'SERVING',   'Khách VIP đang dùng món'),
                                                                                                            (14, 'ORD-20260327001',  3, 3, '2026-03-27 17:00:00', 220000.00, 'CANCELLED', 'Khách hủy bàn');

INSERT INTO ChiTietDonHang (MaChiTiet, MaDonHang, MaMon, SoLuong, DonGia, ThanhTien, GhiChu) VALUES
                                                                                                 (1,  1, 11, 1,  45000.00,  45000.00, NULL),
                                                                                                 (2,  1, 12, 2,  85000.00, 170000.00, NULL),
                                                                                                 (3,  1,  8, 4,   5000.00,  20000.00, NULL),
                                                                                                 (4,  1,  9, 2,  30000.00,  60000.00, NULL),
                                                                                                 (5,  2,  1, 1, 250000.00, 250000.00, 'Ít cay'),
                                                                                                 (6,  2,  7, 2,  20000.00,  40000.00, NULL),
                                                                                                 (7,  2, 10, 4,  25000.00, 100000.00, NULL),
                                                                                                 (8,  3, 13, 1,  75000.00,  75000.00, NULL),
                                                                                                 (9,  3, 14, 2,  35000.00,  70000.00, NULL),
                                                                                                 (10, 3,  8, 4,   5000.00,  20000.00, NULL),
                                                                                                 (11, 4,  2, 1, 200000.00, 200000.00, NULL),
                                                                                                 (12, 4,  4, 1, 150000.00, 150000.00, NULL),
                                                                                                 (13, 4,  7, 3,  20000.00,  60000.00, NULL),
                                                                                                 (14, 4, 15, 1,  40000.00,  40000.00, NULL),
                                                                                                 (15, 5,  5, 2,  90000.00, 180000.00, NULL),
                                                                                                 (16, 5,  8, 4,   5000.00,  20000.00, NULL),
                                                                                                 (17, 5,  9, 2,  30000.00,  60000.00, NULL),
                                                                                                 (18, 6,  1, 1, 250000.00, 250000.00, NULL),
                                                                                                 (19, 6,  6, 1, 120000.00, 120000.00, NULL),
                                                                                                 (20, 6, 10, 5,  25000.00, 125000.00, NULL),
                                                                                                 (21, 6, 14, 1,  35000.00,  35000.00, NULL),
                                                                                                 (22, 6,  8, 1,   5000.00,   5000.00, NULL),
                                                                                                 (23, 7,  3, 1, 180000.00, 180000.00, NULL),
                                                                                                 (24, 7,  7, 3,  20000.00,  60000.00, NULL),
                                                                                                 (25, 7, 15, 1,  40000.00,  40000.00, NULL),
                                                                                                 (26, 7,  8,10,   5000.00,  50000.00, NULL),
                                                                                                 (27, 8,  4, 1, 150000.00, 150000.00, NULL),
                                                                                                 (28, 8, 11, 1,  45000.00,  45000.00, NULL),
                                                                                                 (29, 8,  8, 3,   5000.00,  15000.00, NULL),
                                                                                                 (30, 9,  2, 1, 200000.00, 200000.00, NULL),
                                                                                                 (31, 9, 16, 1, 110000.00, 110000.00, NULL),
                                                                                                 (32, 9, 17, 1,  65000.00,  65000.00, NULL),
                                                                                                 (33, 9,  7, 3,  20000.00,  60000.00, NULL),
                                                                                                 (34, 9,  8,10,   5000.00,  50000.00, NULL),
                                                                                                 (35, 10, 13, 1,  75000.00,  75000.00, NULL),
                                                                                                 (36, 10, 14, 2,  35000.00,  70000.00, NULL),
                                                                                                 (37, 10,  8, 6,   5000.00,  30000.00, NULL),
                                                                                                 (38, 11,  3, 1, 180000.00, 180000.00, NULL),
                                                                                                 (39, 11,  7, 2,  20000.00,  40000.00, NULL),
                                                                                                 (40, 11, 10, 3,  25000.00,  75000.00, NULL),
                                                                                                 (41, 11,  8, 6,   5000.00,  30000.00, NULL),
                                                                                                 (42, 12,  1, 1, 250000.00, 250000.00, NULL),
                                                                                                 (43, 12,  6, 1, 120000.00, 120000.00, NULL),
                                                                                                 (44, 12, 14, 1,  35000.00,  35000.00, NULL),
                                                                                                 (45, 12, 15, 1,  40000.00,  40000.00, NULL),
                                                                                                 (46, 12,  8, 2,   5000.00,  10000.00, NULL),
                                                                                                 (47, 13,  3, 1, 180000.00, 180000.00, NULL),
                                                                                                 (48, 14,  4, 1, 150000.00, 150000.00, NULL),
                                                                                                 (49, 14, 14, 2,  35000.00,  70000.00, NULL);

INSERT INTO HoaDon (MaHoaDon, SoHoaDon, MaDonHang, ThoiGianThanhToan, TongTien, PhuongThucThanhToan) VALUES
                                                                                                         (1,  'INV-20260105001',  1, '2026-01-05 13:00:00', 295000.00, 'CASH'),
                                                                                                         (2,  'INV-20260112001',  2, '2026-01-12 20:00:00', 390000.00, 'BANK_TRANSFER'),
                                                                                                         (3,  'INV-20260120001',  3, '2026-01-20 13:10:00', 165000.00, 'CASH'),
                                                                                                         (4,  'INV-20260203001',  4, '2026-02-03 21:05:00', 450000.00, 'BANK_TRANSFER'),
                                                                                                         (5,  'INV-20260210001',  5, '2026-02-10 12:45:00', 260000.00, 'CASH'),
                                                                                                         (6,  'INV-20260218001',  6, '2026-02-18 22:00:00', 535000.00, 'BANK_TRANSFER'),
                                                                                                         (7,  'INV-20260301001',  7, '2026-03-01 20:10:00', 330000.00, 'CASH'),
                                                                                                         (8,  'INV-20260308001',  8, '2026-03-08 13:20:00', 210000.00, 'BANK_TRANSFER'),
                                                                                                         (9,  'INV-20260315001',  9, '2026-03-15 21:30:00', 485000.00, 'CASH'),
                                                                                                         (10, 'INV-20260320001', 10, '2026-03-20 19:10:00', 175000.00, 'BANK_TRANSFER'),
                                                                                                         (11, 'INV-20260328001', 11, '2026-03-28 11:40:00', 325000.00, 'CASH');

-- 2. CẬP NHẬT TRẠNG THÁI VÀ RESET AUTO_INCREMENT
SET SQL_SAFE_UPDATES = 0;

UPDATE BanAn b
    LEFT JOIN (
    SELECT DISTINCT MaBan
    FROM DonHang
    WHERE TrangThai = 'SERVING'
    ) d ON b.MaBan = d.MaBan
    SET b.TrangThai = CASE
        WHEN d.MaBan IS NOT NULL THEN 'SERVING'
        ELSE 'EMPTY'
END
WHERE b.DangSuDung = TRUE;

SET SQL_SAFE_UPDATES = 1;

ALTER TABLE BanAn AUTO_INCREMENT = 13;
ALTER TABLE NhanVien AUTO_INCREMENT = 7;
ALTER TABLE LoaiMon AUTO_INCREMENT = 6;
ALTER TABLE MonAn AUTO_INCREMENT = 18;
ALTER TABLE GhiChuMon AUTO_INCREMENT = 6;
ALTER TABLE DonHang AUTO_INCREMENT = 15;
ALTER TABLE ChiTietDonHang AUTO_INCREMENT = 50;
ALTER TABLE HoaDon AUTO_INCREMENT = 12;