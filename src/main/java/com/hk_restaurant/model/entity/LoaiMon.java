package com.hk_restaurant.model.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity biểu diễn danh mục loại món ăn trong nhà hàng.
 * Đây là thực thể danh mục gốc để nhóm các món như món chính, món khai vị,
 * đồ uống, món tráng miệng,... giúp hệ thống dễ lọc thực đơn và dễ thống kê.
 */
@Entity
@Table(name = "LoaiMon")
public class LoaiMon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaLoaiMon")
    private Long id;

    /**
     * Tên loại món hiển thị trên giao diện và dùng trong các bộ lọc thực đơn.
     */
    @Column(name = "TenLoaiMon", nullable = false, length = 100)
    private String tenLoaiMon;

    /**
     * Mô tả ngắn cho loại món.
     * Trường này hữu ích khi mở rộng sang màn hình quản trị danh mục riêng.
     */
    @Column(name = "MoTa")
    private String moTa;

    /**
     * Quan hệ một - nhiều với MonAn:
     * một loại món có thể có nhiều món ăn.
     * Danh sách này thể hiện vai trò của LoaiMon như một module danh mục trung tâm.
     */
    @OneToMany(mappedBy = "loaiMon", cascade = CascadeType.ALL)
    private List<MonAn> danhSachMonAn = new ArrayList<>();

    /**
     * Constructor mặc định của entity LoaiMon.
     */
    public LoaiMon() {
    }

    /**
     * Trả về tên loại món để hiển thị trên giao diện,
     * đặc biệt hữu ích khi binding dữ liệu lên JComboBox.
     *
     * @return tên loại món
     */
    @Override
    public String toString() {
        return tenLoaiMon;
    }

    /**
     * Lấy mã định danh của loại món.
     *
     * @return id loại món
     */
    public Long getId() {
        return id;
    }

    /**
     * Cập nhật mã định danh của loại món.
     *
     * @param id id mới
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Lấy tên loại món.
     *
     * @return tên loại món
     */
    public String getTenLoaiMon() {
        return tenLoaiMon;
    }

    /**
     * Cập nhật tên loại món.
     *
     * @param tenLoaiMon tên loại món mới
     */
    public void setTenLoaiMon(String tenLoaiMon) {
        this.tenLoaiMon = tenLoaiMon;
    }

    /**
     * Lấy mô tả của loại món.
     *
     * @return mô tả loại món
     */
    public String getMoTa() {
        return moTa;
    }

    /**
     * Cập nhật mô tả của loại món.
     *
     * @param moTa mô tả mới
     */
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    /**
     * Lấy toàn bộ danh sách món ăn thuộc loại hiện tại.
     *
     * @return danh sách món ăn
     */
    public List<MonAn> getDanhSachMonAn() {
        return danhSachMonAn;
    }

    /**
     * Cập nhật toàn bộ danh sách món ăn của loại món.
     *
     * @param danhSachMonAn danh sách món ăn mới
     */
    public void setDanhSachMonAn(List<MonAn> danhSachMonAn) {
        this.danhSachMonAn = danhSachMonAn;
    }
}
