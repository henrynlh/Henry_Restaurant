package com.hk_restaurant.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Entity biểu diễn món ăn trong thực đơn nhà hàng.
 * Mỗi món ăn thuộc về một loại món và có thể có nhiều ghi chú mẫu.
 */
@Entity
@Table(name = "MonAn")
public class MonAn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaMon")
    private Long id;

    /**
     * Quan hệ nhiều - một với LoaiMon:
     * nhiều món ăn có thể thuộc cùng một loại món.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLoaiMon", nullable = false)
    private LoaiMon loaiMon;

    @Column(name = "MaMonCode", unique = true, nullable = false, length = 30)
    private String maMonCode;

    @Column(name = "TenMon", nullable = false, length = 150)
    private String tenMon;

    @Column(name = "DonGia", nullable = false)
    private BigDecimal donGia;

    @Column(name = "HinhAnh")
    private String hinhAnh;

    @Column(name = "MoTa", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "DangKinhDoanh", nullable = false)
    private Boolean dangKinhDoanh = true;

    /**
     * Quan hệ một - nhiều với GhiChuMon:
     * một món ăn có thể có nhiều ghi chú mẫu.
     */
    @OneToMany(mappedBy = "monAn", cascade = CascadeType.ALL)
    private List<GhiChuMon> danhSachGhiChu;

    /**
     * Lấy mã định danh của món ăn.
     *
     * @return id món ăn
     */
    public Long getId() {
        return id;
    }

    /**
     * Cập nhật mã định danh của món ăn.
     *
     * @param id id mới của món ăn
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Lấy loại món của món ăn.
     *
     * @return loại món liên kết
     */
    public LoaiMon getLoaiMon() {
        return loaiMon;
    }

    /**
     * Gán loại món cho món ăn.
     *
     * @param loaiMon loại món liên kết
     */
    public void setLoaiMon(LoaiMon loaiMon) {
        this.loaiMon = loaiMon;
    }

    /**
     * Lấy mã nghiệp vụ của món ăn.
     *
     * @return mã món ăn
     */
    public String getMaMonCode() {
        return maMonCode;
    }

    /**
     * Cập nhật mã nghiệp vụ của món ăn.
     *
     * @param maMonCode mã món mới
     */
    public void setMaMonCode(String maMonCode) {
        this.maMonCode = maMonCode;
    }

    /**
     * Lấy tên món ăn.
     *
     * @return tên món ăn
     */
    public String getTenMon() {
        return tenMon;
    }

    /**
     * Cập nhật tên món ăn.
     *
     * @param tenMon tên món mới
     */
    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    /**
     * Lấy đơn giá của món ăn.
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
     * Lấy đường dẫn hình ảnh của món ăn.
     *
     * @return đường dẫn hình ảnh
     */
    public String getHinhAnh() {
        return hinhAnh;
    }

    /**
     * Cập nhật đường dẫn hình ảnh của món ăn.
     *
     * @param hinhAnh đường dẫn hình ảnh mới
     */
    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    /**
     * Lấy mô tả của món ăn.
     *
     * @return mô tả món ăn
     */
    public String getMoTa() {
        return moTa;
    }

    /**
     * Cập nhật mô tả của món ăn.
     *
     * @param moTa mô tả mới
     */
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    /**
     * Kiểm tra trạng thái kinh doanh của món ăn.
     *
     * @return true nếu món đang kinh doanh, ngược lại trả về false
     */
    public Boolean getDangKinhDoanh() {
        return dangKinhDoanh;
    }

    /**
     * Cập nhật trạng thái kinh doanh của món ăn.
     *
     * @param dangKinhDoanh trạng thái mới
     */
    public void setDangKinhDoanh(Boolean dangKinhDoanh) {
        this.dangKinhDoanh = dangKinhDoanh;
    }

    /**
     * Lấy danh sách ghi chú mẫu của món ăn.
     *
     * @return danh sách ghi chú mẫu
     */
    public List<GhiChuMon> getDanhSachGhiChu() {
        return danhSachGhiChu;
    }

    /**
     * Cập nhật danh sách ghi chú mẫu của món ăn.
     *
     * @param danhSachGhiChu danh sách ghi chú mới
     */
    public void setDanhSachGhiChu(List<GhiChuMon> danhSachGhiChu) {
        this.danhSachGhiChu = danhSachGhiChu;
    }
}