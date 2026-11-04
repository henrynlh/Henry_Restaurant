package com.hk_restaurant.model.entity;

import com.hk_restaurant.model.enumtype.PaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity biểu diễn hóa đơn thanh toán của nhà hàng.
 * Mỗi hóa đơn được tạo ra từ đúng một đơn hàng đã thanh toán.
 */
@Entity
@Table(name = "HoaDon")
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaHoaDon")
    private Long id;

    @Column(name = "SoHoaDon", unique = true, nullable = false, length = 50)
    private String soHoaDon;

    /**
     * Quan hệ một - một với DonHang:
     * một đơn hàng sau khi thanh toán sẽ sinh ra đúng một hóa đơn.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDonHang", unique = true, nullable = false)
    private DonHang donHang;

    @Column(name = "ThoiGianThanhToan", nullable = false)
    private LocalDateTime thoiGianThanhToan = LocalDateTime.now();

    @Column(name = "TongTien", nullable = false)
    private BigDecimal tongTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "PhuongThucThanhToan", nullable = false, length = 50)
    private PaymentMethod phuongThucThanhToan;

    /**
     * Lấy mã định danh của hóa đơn.
     *
     * @return id hóa đơn
     */
    public Long getId() {
        return id;
    }

    /**
     * Cập nhật mã định danh của hóa đơn.
     *
     * @param id id mới của hóa đơn
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Lấy số hóa đơn nghiệp vụ.
     *
     * @return số hóa đơn
     */
    public String getSoHoaDon() {
        return soHoaDon;
    }

    /**
     * Cập nhật số hóa đơn nghiệp vụ.
     *
     * @param soHoaDon số hóa đơn mới
     */
    public void setSoHoaDon(String soHoaDon) {
        this.soHoaDon = soHoaDon;
    }

    /**
     * Lấy đơn hàng liên kết với hóa đơn.
     *
     * @return đơn hàng liên kết
     */
    public DonHang getDonHang() {
        return donHang;
    }

    /**
     * Gán đơn hàng cho hóa đơn.
     * Đồng thời đồng bộ liên kết ngược từ DonHang sang HoaDon.
     *
     * @param donHang đơn hàng liên kết
     */
    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
        if (donHang != null && donHang.getHoaDon() != this) {
            donHang.setHoaDon(this);
        }
    }

    /**
     * Lấy thời gian thanh toán của hóa đơn.
     *
     * @return thời gian thanh toán
     */
    public LocalDateTime getThoiGianThanhToan() {
        return thoiGianThanhToan;
    }

    /**
     * Cập nhật thời gian thanh toán của hóa đơn.
     *
     * @param thoiGianThanhToan thời gian thanh toán mới
     */
    public void setThoiGianThanhToan(LocalDateTime thoiGianThanhToan) {
        this.thoiGianThanhToan = thoiGianThanhToan;
    }

    /**
     * Lấy tổng tiền của hóa đơn.
     *
     * @return tổng tiền hóa đơn
     */
    public BigDecimal getTongTien() {
        return tongTien;
    }

    /**
     * Cập nhật tổng tiền của hóa đơn.
     *
     * @param tongTien tổng tiền mới
     */
    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    /**
     * Lấy phương thức thanh toán của hóa đơn.
     *
     * @return phương thức thanh toán
     */
    public PaymentMethod getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    /**
     * Cập nhật phương thức thanh toán của hóa đơn.
     *
     * @param phuongThucThanhToan phương thức thanh toán mới
     */
    public void setPhuongThucThanhToan(PaymentMethod phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }
}
