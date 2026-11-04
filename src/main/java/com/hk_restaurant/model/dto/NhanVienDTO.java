package com.hk_restaurant.model.dto;

/**
 * DTO dùng để truyền dữ liệu nhân viên từ tầng DAO/Service lên View.
 * Lớp này chỉ chứa các thông tin cần hiển thị, không dùng để thao tác trực tiếp với entity.
 */
public class NhanVienDTO {
    private Long id;
    private String maNhanVienCode;
    private String hoTen;
    private String soDienThoai;
    private String tenDangNhap;
    private String vaiTro;
    private Boolean dangLamViec;
    private Boolean daNghiViec;

    /**
     * Constructor mặc định.
     */
    public NhanVienDTO() {
    }

    /**
     * Constructor dùng để khởi tạo đầy đủ thông tin nhân viên DTO.
     *
     * @param id mã định danh của nhân viên
     * @param maNhanVienCode mã nghiệp vụ của nhân viên
     * @param hoTen họ tên nhân viên
     * @param soDienThoai số điện thoại nhân viên
     * @param tenDangNhap tên đăng nhập của nhân viên
     * @param vaiTro vai trò của nhân viên trong hệ thống
     * @param dangLamViec trạng thái đang làm việc
     * @param daNghiViec trạng thái đã nghỉ việc
     */
    public NhanVienDTO(Long id, String maNhanVienCode, String hoTen, String soDienThoai,
                       String tenDangNhap, String vaiTro, Boolean dangLamViec, Boolean daNghiViec) {
        this.id = id;
        this.maNhanVienCode = maNhanVienCode;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.tenDangNhap = tenDangNhap;
        this.vaiTro = vaiTro;
        this.dangLamViec = dangLamViec;
        this.daNghiViec = daNghiViec;
    }

    /**
     * Lấy id của nhân viên.
     *
     * @return id nhân viên
     */
    public Long getId() {
        return id;
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
     * Lấy họ tên nhân viên.
     *
     * @return họ tên nhân viên
     */
    public String getHoTen() {
        return hoTen;
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
     * Lấy tên đăng nhập của nhân viên.
     *
     * @return tên đăng nhập
     */
    public String getTenDangNhap() {
        return tenDangNhap;
    }

    /**
     * Lấy vai trò của nhân viên.
     *
     * @return vai trò nhân viên
     */
    public String getVaiTro() {
        return vaiTro;
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
     * Kiểm tra trạng thái đã nghỉ việc của nhân viên.
     *
     * @return true nếu đã nghỉ việc, ngược lại trả về false
     */
    public Boolean getDaNghiViec() {
        return daNghiViec;
    }

    /**
     * Cập nhật id của nhân viên.
     *
     * @param id id mới của nhân viên
     */
    public void setId(Long id) {
        this.id = id;
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
     * Cập nhật họ tên nhân viên.
     *
     * @param hoTen họ tên mới
     */
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
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
     * Cập nhật tên đăng nhập của nhân viên.
     *
     * @param tenDangNhap tên đăng nhập mới
     */
    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    /**
     * Cập nhật vai trò của nhân viên.
     *
     * @param vaiTro vai trò mới
     */
    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    /**
     * Cập nhật trạng thái đang làm việc của nhân viên.
     *
     * @param dangLamViec trạng thái đang làm việc mới
     */
    public void setDangLamViec(Boolean dangLamViec) {
        this.dangLamViec = dangLamViec;
    }

    /**
     * Cập nhật trạng thái đã nghỉ việc của nhân viên.
     *
     * @param daNghiViec trạng thái nghỉ việc mới
     */
    public void setDaNghiViec(Boolean daNghiViec) {
        this.daNghiViec = daNghiViec;
    }
}