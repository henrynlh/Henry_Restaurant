package com.hk_restaurant.dao;

import com.hk_restaurant.model.entity.DonHang;
import com.hk_restaurant.model.enumtype.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO cho thực thể DonHang.
 * Khai báo các phương thức truy vấn và thao tác dữ liệu đơn hàng.
 */
public interface DonHangDAO {

    /**
     * Lấy toàn bộ danh sách đơn hàng.
     *
     * @return danh sách đơn hàng
     */
    List<DonHang> findAll();

    /**
     * Tìm đơn hàng theo id và nạp kèm danh sách chi tiết món ăn.
     *
     * @param id mã đơn hàng
     * @return Optional chứa đơn hàng nếu tìm thấy
     */
    Optional<DonHang> findByIdWithDetails(Long id);

    /**
     * Tìm đơn hàng theo số đơn hàng nghiệp vụ.
     *
     * @param soDonHang số đơn hàng
     * @return Optional chứa đơn hàng nếu tìm thấy
     */
    Optional<DonHang> findBySoDonHang(String soDonHang);

    /**
     * Lấy danh sách đơn hàng theo mã bàn và trạng thái đơn.
     *
     * @param maBan mã bàn ăn
     * @param trangThai trạng thái đơn hàng
     * @return danh sách đơn hàng phù hợp điều kiện
     */
    List<DonHang> findByBanAnIdAndTrangThai(Long maBan, OrderStatus trangThai);

    /**
     * Lấy danh sách đơn hàng theo trạng thái.
     *
     * @param trangThai trạng thái đơn hàng cần lọc
     * @return danh sách đơn hàng theo trạng thái
     */
    List<DonHang> findByTrangThai(OrderStatus trangThai);

    /**
     * Lấy danh sách đơn hàng trong khoảng thời gian chỉ định.
     *
     * @param startDate thời điểm bắt đầu
     * @param endDate thời điểm kết thúc
     * @return danh sách đơn hàng trong khoảng thời gian
     */
    List<DonHang> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Tìm đơn hàng đang phục vụ hiện tại của một bàn.
     *
     * @param maBan mã bàn cần kiểm tra
     * @return Optional chứa đơn hàng đang phục vụ nếu có
     */
    Optional<DonHang> findCurrentServingByBanId(Long maBan);

    /**
     * Lưu mới một đơn hàng vào cơ sở dữ liệu.
     *
     * @param donHang đơn hàng cần lưu
     * @return đơn hàng sau khi lưu
     */
    DonHang save(DonHang donHang);

    /**
     * Cập nhật thông tin đơn hàng.
     *
     * @param donHang đơn hàng cần cập nhật
     * @return đơn hàng sau khi cập nhật
     */
    DonHang update(DonHang donHang);

    /**
     * Xóa đơn hàng theo mã định danh.
     *
     * @param id mã đơn hàng cần xóa
     */
    void deleteById(Long id);

    /**
     * Kiểm tra số đơn hàng đã tồn tại hay chưa.
     *
     * @param soDonHang số đơn hàng cần kiểm tra
     * @return true nếu đã tồn tại, ngược lại trả về false
     */
    boolean existsBySoDonHang(String soDonHang);
}