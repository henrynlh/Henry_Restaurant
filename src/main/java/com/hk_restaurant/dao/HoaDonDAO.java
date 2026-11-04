package com.hk_restaurant.dao;

import com.hk_restaurant.model.entity.HoaDon;
import com.hk_restaurant.model.enumtype.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO cho thực thể HoaDon.
 * Khai báo các phương thức thao tác và truy vấn dữ liệu hóa đơn.
 */
public interface HoaDonDAO {

    /**
     * Lấy toàn bộ danh sách hóa đơn.
     *
     * @return danh sách hóa đơn
     */
    List<HoaDon> findAll();

    /**
     * Tìm hóa đơn theo khóa chính.
     *
     * @param id mã hóa đơn
     * @return Optional chứa hóa đơn nếu tìm thấy
     */
    Optional<HoaDon> findById(Long id);

    /**
     * Tìm hóa đơn theo số hóa đơn nghiệp vụ.
     *
     * @param soHoaDon số hóa đơn
     * @return Optional chứa hóa đơn nếu tìm thấy
     */
    Optional<HoaDon> findBySoHoaDon(String soHoaDon);

    /**
     * Tìm hóa đơn theo mã đơn hàng liên kết.
     *
     * @param maDonHang mã đơn hàng
     * @return Optional chứa hóa đơn nếu tìm thấy
     */
    Optional<HoaDon> findByMaDonHang(Long maDonHang);

    /**
     * Lấy danh sách hóa đơn theo phương thức thanh toán.
     *
     * @param phuongThuc phương thức thanh toán cần lọc
     * @return danh sách hóa đơn theo phương thức thanh toán
     */
    List<HoaDon> findByPhuongThucThanhToan(PaymentMethod phuongThuc);

    /**
     * Lấy danh sách hóa đơn trong một khoảng thời gian.
     * Phương thức này thường dùng cho thống kê doanh thu.
     *
     * @param startDate thời điểm bắt đầu
     * @param endDate thời điểm kết thúc
     * @return danh sách hóa đơn trong khoảng thời gian
     */
    List<HoaDon> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Lưu mới một hóa đơn vào cơ sở dữ liệu.
     *
     * @param hoaDon hóa đơn cần lưu
     * @return hóa đơn sau khi lưu
     */
    HoaDon save(HoaDon hoaDon);

    /**
     * Cập nhật thông tin hóa đơn.
     *
     * @param hoaDon hóa đơn cần cập nhật
     * @return hóa đơn sau khi cập nhật
     */
    HoaDon update(HoaDon hoaDon);

    /**
     * Xóa hóa đơn theo mã định danh.
     *
     * @param id mã hóa đơn cần xóa
     */
    void deleteById(Long id);

    /**
     * Kiểm tra số hóa đơn đã tồn tại hay chưa.
     *
     * @param soHoaDon số hóa đơn cần kiểm tra
     * @return true nếu đã tồn tại, ngược lại trả về false
     */
    boolean existsBySoHoaDon(String soHoaDon);
}