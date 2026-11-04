package com.hk_restaurant.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entity biểu diễn một dòng chi tiết trong đơn hàng.
 * Mỗi chi tiết đơn hàng gắn với một đơn hàng và một món ăn cụ thể.
 */
@Entity
@Table(name = "ChiTietDonHang")
public class ChiTietDonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaChiTiet")
    private Long id;

    /**
     * Quan hệ nhiều - một với DonHang:
     * nhiều chi tiết đơn hàng thuộc về một đơn hàng.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDonHang", nullable = false)
    private DonHang donHang;

    /**
     * Quan hệ nhiều - một với MonAn:
     * nhiều chi tiết đơn hàng có thể tham chiếu đến cùng một món ăn.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaMon", nullable = false)
    private MonAn monAn;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "DonGia", nullable = false)
    private BigDecimal donGia;

    @Column(name = "ThanhTien", nullable = false)
    private BigDecimal thanhTien;

    @Column(name = "GhiChu")
    private String ghiChu;

    /**
     * Lấy mã định danh của chi tiết đơn hàng.
     *
     * @return id chi tiết đơn hàng
     */
    public Long getId() {
        return id;
    }

    /**
     * Cập nhật mã định danh của chi tiết đơn hàng.
     *
     * @param id id mới
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Lấy đơn hàng mà chi tiết này thuộc về.
     *
     * @return đơn hàng liên kết
     */
    public DonHang getDonHang() {
        return donHang;
    }

    /**
     * Gán đơn hàng cho chi tiết đơn hàng.
     *
     * @param donHang đơn hàng liên kết
     */
    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
    }

    /**
     * Lấy món ăn của dòng chi tiết này.
     *
     * @return món ăn liên kết
     */
    public MonAn getMonAn() {
        return monAn;
    }

    /**
     * Gán món ăn cho dòng chi tiết đơn hàng.
     *
     * @param monAn món ăn liên kết
     */
    public void setMonAn(MonAn monAn) {
        this.monAn = monAn;
    }

    /**
     * Lấy số lượng món ăn trong dòng chi tiết.
     *
     * @return số lượng món
     */
    public Integer getSoLuong() {
        return soLuong;
    }

    /**
     * Cập nhật số lượng món ăn trong dòng chi tiết.
     *
     * @param soLuong số lượng mới
     */
    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    /**
     * Lấy đơn giá của món ăn tại thời điểm lập đơn.
     *
     * @return đơn giá món ăn
     */
    public BigDecimal getDonGia() {
        return donGia;
    }

    /**
     * Cập nhật đơn giá của món ăn.
     *
     * @param donGia đơn giá mới
     */
    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    /**
     * Lấy thành tiền của dòng chi tiết đơn hàng.
     *
     * @return thành tiền
     */
    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    /**
     * Cập nhật thành tiền của dòng chi tiết đơn hàng.
     *
     * @param thanhTien thành tiền mới
     */
    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    /**
     * Lấy ghi chú riêng cho món ăn trong đơn hàng.
     *
     * @return ghi chú món ăn
     */
    public String getGhiChu() {
        return ghiChu;
    }

    /**
     * Cập nhật ghi chú riêng cho món ăn trong đơn hàng.
     *
     * @param ghiChu ghi chú mới
     */
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}