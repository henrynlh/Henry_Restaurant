package com.hk_restaurant.service;

import com.hk_restaurant.model.dto.OrderDetailInput;
import com.hk_restaurant.model.entity.DonHang;
import com.hk_restaurant.model.entity.HoaDon;
import com.hk_restaurant.model.enumtype.PaymentMethod;

/**
 * Interface service cho phân hệ đơn hàng và thanh toán.
 * Khai báo các nghiệp vụ chính phục vụ quá trình gọi món, cập nhật đơn và thanh toán.
 */
public interface DonHangService {

    DonHang moBanMoi(Long maBan, Long maNhanVien, String ghiChu);

    DonHang themMonVaoDon(Long maDonHang, OrderDetailInput input);

    DonHang capNhatSoLuongMon(Long maDonHang, Long maMon, int soLuongMoi);

    DonHang capNhatGhiChuMon(Long maDonHang, Long maMon, String ghiChuMoi);

    DonHang layDonDangPhucVuTheoBan(Long maBan);

    HoaDon thanhToanDonHang(Long maDonHang, PaymentMethod phuongThuc);

    DonHang capNhatNhanVienPhucVuTheoBan(Long maBan, Long maNhanVienMoi);
}