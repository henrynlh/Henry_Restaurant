package com.hk_restaurant.model.entity;

import jakarta.persistence.*;

/**
 * Entity biểu diễn ghi chú mẫu của món ăn.
 * Module này giúp nhà hàng chuẩn hóa các ghi chú lặp lại khi gọi món
 * như: ít đá, không hành, thêm phô mai, ít cay,... để thao tác bán hàng nhanh hơn.
 */
@Entity
@Table(name = "GhiChuMon")
public class GhiChuMon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaGhiChu")
    private Long id;

    /**
     * Quan hệ nhiều - một với MonAn:
     * nhiều ghi chú mẫu có thể thuộc cùng một món ăn.
     * Điều này cho phép mỗi món có tập ghi chú gợi ý riêng theo đặc thù chế biến.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaMon", nullable = false)
    private MonAn monAn;

    /**
     * Nội dung ghi chú mẫu hiển thị nhanh cho người bán hàng lựa chọn.
     */
    @Column(name = "NoiDungGhiChu", nullable = false, length = 100)
    private String noiDungGhiChu;

    /**
     * Lấy mã định danh của ghi chú món.
     *
     * @return id ghi chú
     */
    public Long getId() {
        return id;
    }

    /**
     * Cập nhật mã định danh của ghi chú món.
     *
     * @param id id mới
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Lấy món ăn mà ghi chú này thuộc về.
     *
     * @return món ăn liên kết
     */
    public MonAn getMonAn() {
        return monAn;
    }

    /**
     * Gán món ăn cho ghi chú mẫu.
     *
     * @param monAn món ăn liên kết
     */
    public void setMonAn(MonAn monAn) {
        this.monAn = monAn;
    }

    /**
     * Lấy nội dung ghi chú mẫu.
     *
     * @return nội dung ghi chú
     */
    public String getNoiDungGhiChu() {
        return noiDungGhiChu;
    }

    /**
     * Cập nhật nội dung ghi chú mẫu.
     *
     * @param noiDungGhiChu nội dung ghi chú mới
     */
    public void setNoiDungGhiChu(String noiDungGhiChu) {
        this.noiDungGhiChu = noiDungGhiChu;
    }

    /**
     * Trả về nội dung ghi chú để thuận tiện khi bind dữ liệu lên combobox hoặc list.
     *
     * @return nội dung ghi chú mẫu
     */
    @Override
    public String toString() {
        return noiDungGhiChu;
    }
}
