package com.hk_restaurant.model.entity;

import com.hk_restaurant.model.enumtype.TableStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity biểu diễn thông tin bàn ăn trong nhà hàng.
 * Mỗi bàn ăn có mã riêng, tên bàn, sức chứa và trạng thái hoạt động.
 */
@Entity
@Table(name = "BanAn")
public class BanAn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaBan")
    private Long id;

    @Column(name = "MaBanCode", unique = true, nullable = false, length = 20)
    private String maBanCode;

    @Column(name = "TenBan", nullable = false, length = 50)
    private String tenBan;

    @Column(name = "SucChua", nullable = false)
    private Integer sucChua;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", nullable = false, length = 50)
    private TableStatus trangThai = TableStatus.EMPTY;

    @Column(name = "DangSuDung", nullable = false)
    private Boolean dangSuDung = true;

    /**
     * Quan hệ một - nhiều ở mức lịch sử nghiệp vụ:
     * một bàn có thể phát sinh nhiều đơn hàng theo thời gian.
     */
    @OneToMany(mappedBy = "banAn", fetch = FetchType.LAZY)
    private List<DonHang> danhSachDonHang = new ArrayList<>();

    /**
     * Lấy mã định danh của bàn ăn.
     *
     * @return id của bàn ăn
     */
    public Long getId() {
        return id;
    }

    /**
     * Cập nhật mã định danh của bàn ăn.
     *
     * @param id id mới của bàn ăn
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Lấy mã nghiệp vụ của bàn ăn.
     *
     * @return mã bàn ăn
     */
    public String getMaBanCode() {
        return maBanCode;
    }

    /**
     * Cập nhật mã nghiệp vụ của bàn ăn.
     *
     * @param maBanCode mã bàn mới
     */
    public void setMaBanCode(String maBanCode) {
        this.maBanCode = maBanCode;
    }

    /**
     * Lấy tên bàn ăn.
     *
     * @return tên bàn
     */
    public String getTenBan() {
        return tenBan;
    }

    /**
     * Cập nhật tên bàn ăn.
     *
     * @param tenBan tên bàn mới
     */
    public void setTenBan(String tenBan) {
        this.tenBan = tenBan;
    }

    /**
     * Lấy sức chứa của bàn ăn.
     *
     * @return số lượng chỗ ngồi
     */
    public Integer getSucChua() {
        return sucChua;
    }

    /**
     * Cập nhật sức chứa của bàn ăn.
     *
     * @param sucChua số chỗ ngồi mới
     */
    public void setSucChua(Integer sucChua) {
        this.sucChua = sucChua;
    }

    /**
     * Lấy trạng thái hiện tại của bàn ăn.
     *
     * @return trạng thái bàn
     */
    public TableStatus getTrangThai() {
        return trangThai;
    }

    /**
     * Cập nhật trạng thái của bàn ăn.
     *
     * @param trangThai trạng thái mới
     */
    public void setTrangThai(TableStatus trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * Kiểm tra bàn ăn còn đang được sử dụng trong hệ thống hay không.
     *
     * @return true nếu bàn đang hoạt động, ngược lại trả về false
     */
    public Boolean getDangSuDung() {
        return dangSuDung;
    }

    /**
     * Cập nhật trạng thái sử dụng của bàn ăn.
     *
     * @param dangSuDung trạng thái sử dụng mới
     */
    public void setDangSuDung(Boolean dangSuDung) {
        this.dangSuDung = dangSuDung;
    }

    /**
     * Lấy toàn bộ lịch sử đơn hàng của bàn ăn.
     *
     * @return danh sách đơn hàng của bàn
     */
    public List<DonHang> getDanhSachDonHang() {
        return danhSachDonHang;
    }

    /**
     * Cập nhật toàn bộ lịch sử đơn hàng của bàn ăn.
     *
     * @param danhSachDonHang danh sách đơn hàng mới
     */
    public void setDanhSachDonHang(List<DonHang> danhSachDonHang) {
        this.danhSachDonHang = danhSachDonHang;
    }

    /**
     * Trả về tên bàn để hỗ trợ hiển thị trên giao diện khi binding dữ liệu.
     *
     * @return chuỗi đại diện của bàn ăn
     */
    @Override
    public String toString() {
        return tenBan + " (" + maBanCode + ")";
    }
}
