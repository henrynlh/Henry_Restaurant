package com.hk_restaurant.model.entity;

import com.hk_restaurant.model.enumtype.Role;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity biểu diễn thông tin nhân viên trong hệ thống.
 * Nhân viên có thể đăng nhập, được phân vai trò và tham gia lập đơn hàng.
 */
@Entity
@Table(name = "NhanVien")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaNhanVien")
    private Long id;

    @Column(name = "MaNhanVienCode", unique = true, nullable = false, length = 20)
    private String maNhanVienCode;

    @Column(name = "HoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "SoDienThoai", length = 20)
    private String soDienThoai;

    @Column(name = "TenDangNhap", unique = true, nullable = false, length = 50)
    private String tenDangNhap;

    @Column(name = "MatKhau", nullable = false)
    private String matKhau;

    @Enumerated(EnumType.STRING)
    @Column(name = "VaiTro", nullable = false, length = 50)
    private Role vaiTro;

    @Column(name = "DangLamViec", nullable = false)
    private Boolean dangLamViec = true;

    @Column(name = "DaNghiViec", nullable = false)
    private Boolean daNghiViec = false;

    /**
     * Quan hệ một - nhiều với DonHang:
     * một nhân viên có thể phụ trách nhiều đơn hàng khác nhau.
     */
    @OneToMany(mappedBy = "nhanVien", fetch = FetchType.LAZY)
    private List<DonHang> danhSachDonHang = new ArrayList<>();

    /**
     * Lấy mã định danh của nhân viên.
     *
     * @return id nhân viên
     */
    public Long getId() {
        return id;
    }

    /**
     * Cập nhật mã định danh của nhân viên.
     *
     * @param id id mới của nhân viên
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Lấy mã nghiệp vụ của nhân viên.
     *
     * @return mã nhân viên
     */
    public String getMaNhanVienCode() {
        return maNhanVienCode;
    }

    /**
     * Cập nhật mã nghiệp vụ của nhân viên.
     *
     * @param maNhanVienCode mã nhân viên mới
     */
    public void setMaNhanVienCode(String maNhanVienCode) {
        this.maNhanVienCode = maNhanVienCode;
    }

    /**
     * Lấy họ tên nhân viên.
     *
     * @return họ tên nhân viên
     */
    public String getHoTen() {
        return hoTen;
    }

    /**
     * Cập nhật họ tên nhân viên.
     *
     * @param hoTen họ tên mới
     */
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    /**
     * Lấy số điện thoại của nhân viên.
     *
     * @return số điện thoại
     */
    public String getSoDienThoai() {
        return soDienThoai;
    }

    /**
     * Cập nhật số điện thoại của nhân viên.
     *
     * @param soDienThoai số điện thoại mới
     */
    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    /**
     * Lấy tên đăng nhập của nhân viên.
     *
     * @return tên đăng nhập
     */
    public String getTenDangNhap() {
        return tenDangNhap;
    }

    /**
     * Cập nhật tên đăng nhập của nhân viên.
     *
     * @param tenDangNhap tên đăng nhập mới
     */
    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    /**
     * Lấy mật khẩu đăng nhập của nhân viên.
     *
     * @return mật khẩu đã lưu
     */
    public String getMatKhau() {
        return matKhau;
    }

    /**
     * Cập nhật mật khẩu đăng nhập của nhân viên.
     *
     * @param matKhau mật khẩu mới
     */
    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    /**
     * Lấy vai trò của nhân viên trong hệ thống.
     *
     * @return vai trò nhân viên
     */
    public Role getVaiTro() {
        return vaiTro;
    }

    /**
     * Cập nhật vai trò của nhân viên trong hệ thống.
     *
     * @param vaiTro vai trò mới
     */
    public void setVaiTro(Role vaiTro) {
        this.vaiTro = vaiTro;
    }

    /**
     * Kiểm tra trạng thái đang làm việc của nhân viên.
     *
     * @return true nếu đang làm việc, ngược lại trả về false
     */
    public Boolean getDangLamViec() {
        return dangLamViec;
    }

    /**
     * Cập nhật trạng thái đang làm việc của nhân viên.
     *
     * @param dangLamViec trạng thái mới
     */
    public void setDangLamViec(Boolean dangLamViec) {
        this.dangLamViec = dangLamViec;
    }

    /**
     * Kiểm tra trạng thái đã nghỉ việc của nhân viên.
     *
     * @return true nếu đã nghỉ việc, ngược lại trả về false
     */
    public Boolean getDaNghiViec() {
        return daNghiViec;
    }

    /**
     * Cập nhật trạng thái đã nghỉ việc của nhân viên.
     *
     * @param daNghiViec trạng thái mới
     */
    public void setDaNghiViec(Boolean daNghiViec) {
        this.daNghiViec = daNghiViec;
    }

    /**
     * Lấy lịch sử các đơn hàng mà nhân viên đã phụ trách.
     *
     * @return danh sách đơn hàng của nhân viên
     */
    public List<DonHang> getDanhSachDonHang() {
        return danhSachDonHang;
    }

    /**
     * Cập nhật danh sách đơn hàng của nhân viên.
     *
     * @param danhSachDonHang danh sách đơn hàng mới
     */
    public void setDanhSachDonHang(List<DonHang> danhSachDonHang) {
        this.danhSachDonHang = danhSachDonHang;
    }

    /**
     * Trả về chuỗi đại diện của nhân viên để hiển thị trên giao diện.
     *
     * @return chuỗi gồm họ tên và mã nhân viên
     */
    @Override
    public String toString() {
        return hoTen + " (" + maNhanVienCode + ")";
    }
}
