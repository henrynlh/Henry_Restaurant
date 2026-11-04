package com.hk_restaurant.controller;

import com.hk_restaurant.model.dto.NhanVienDTO;
import com.hk_restaurant.model.entity.NhanVien;
import com.hk_restaurant.service.NhanVienService;

import java.util.List;
import java.util.Optional;

/**
 * Controller cho phân hệ nhân viên và đăng nhập.
 * Lớp này tiếp nhận thao tác từ giao diện và điều phối xuống tầng service.
 */
public class NhanVienController {

    private final NhanVienService nhanVienService;

    /**
     * Khởi tạo controller nhân viên.
     *
     * @param nhanVienService service xử lý nghiệp vụ nhân viên
     */
    public NhanVienController(NhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    /**
     * Lấy danh sách nhân viên dưới dạng DTO để hiển thị trên giao diện.
     *
     * @return danh sách nhân viên DTO
     */
    public List<NhanVienDTO> layDanhSachNhanVien() {
        return nhanVienService.layDanhSachNhanVien();
    }

    /**
     * Tìm kiếm nhân viên theo mã hoặc họ tên.
     *
     * @param keyword từ khóa tìm kiếm
     * @return danh sách nhân viên phù hợp
     */
    public List<NhanVienDTO> timKiemNhanVien(String keyword) {
        return nhanVienService.timKiemNhanVien(keyword);
    }

    /**
     * Lấy danh sách nhân viên đang làm việc.
     *
     * @return danh sách nhân viên đang hoạt động
     */
    public List<NhanVien> layDanhSachNhanVienDangLam() {
        return nhanVienService.layDanhSachNhanVienDangLam();
    }

    /**
     * Tìm nhân viên theo id.
     *
     * @param id mã nhân viên
     * @return Optional chứa nhân viên nếu tồn tại
     */
    public Optional<NhanVien> timNhanVienTheoId(Long id) {
        return nhanVienService.timNhanVienTheoId(id);
    }

    /**
     * Thêm mới nhân viên.
     *
     * @param nv nhân viên cần thêm
     * @param password mật khẩu gốc
     * @param confirmPassword mật khẩu xác nhận
     * @return nhân viên sau khi lưu
     */
    public NhanVien themNhanVien(NhanVien nv, String password, String confirmPassword) {
        return nhanVienService.themNhanVien(nv, password, confirmPassword);
    }

    /**
     * Cập nhật thông tin nhân viên.
     *
     * @param nv nhân viên cần cập nhật
     * @param currentPassword mật khẩu hiện tại để xác nhận
     * @return nhân viên sau khi cập nhật
     */
    public NhanVien capNhatNhanVien(NhanVien nv, String currentPassword) {
        return nhanVienService.capNhatNhanVien(nv, currentPassword);
    }

    /**
     * Cho nhân viên nghỉ làm tạm thời.
     *
     * @param id mã nhân viên
     * @param currentPassword mật khẩu hiện tại để xác nhận
     */
    public void choNghiLam(Long id, String currentPassword) {
        nhanVienService.choNghiLam(id, currentPassword);
    }

    /**
     * Cho nhân viên đi làm lại.
     *
     * @param id mã nhân viên
     */
    public void choDiLamLai(Long id) {
        nhanVienService.choDiLamLai(id);
    }

    /**
     * Cho nhân viên nghỉ việc chính thức.
     *
     * @param id mã nhân viên
     * @param currentPassword mật khẩu hiện tại để xác nhận
     */
    public void choNghiViec(Long id, String currentPassword) {
        nhanVienService.choNghiViec(id, currentPassword);
    }

    /**
     * Xóa cứng dữ liệu nhân viên.
     *
     * @param id mã nhân viên
     * @param currentPassword mật khẩu hiện tại để xác nhận
     */
    public void xoaNhanVien(Long id, String currentPassword) {
        nhanVienService.xoaNhanVien(id, currentPassword);
    }

    /**
     * Đặt lại mật khẩu bằng quyền root demo.
     *
     * @param id mã nhân viên
     * @param rootPassword mật khẩu root
     * @param newPassword mật khẩu mới
     * @param confirmPassword mật khẩu xác nhận
     */
    public void doiMatKhauBangRoot(Long id, String rootPassword, String newPassword, String confirmPassword) {
        nhanVienService.doiMatKhauBangRoot(id, rootPassword, newPassword, confirmPassword);
    }

    /**
     * Xác thực đăng nhập cho nhân viên.
     *
     * @param tenDangNhap tên đăng nhập
     * @param matKhau mật khẩu gốc
     * @return nhân viên đăng nhập thành công
     */
    public NhanVien dangNhap(String tenDangNhap, String matKhau) {
        return nhanVienService.dangNhap(tenDangNhap, matKhau);
    }
}
