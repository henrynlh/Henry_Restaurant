package com.hk_restaurant.service;

import com.hk_restaurant.model.dto.NhanVienDTO;
import com.hk_restaurant.model.entity.NhanVien;

import java.util.List;
import java.util.Optional;

/**
 * Interface service cho phân hệ nhân viên và đăng nhập.
 * Tầng này chịu trách nhiệm xử lý nghiệp vụ nhân sự,
 * xác thực tài khoản và thay đổi trạng thái làm việc của nhân viên.
 */
public interface NhanVienService {

    /**
     * Lấy danh sách nhân viên dưới dạng DTO để hiển thị trên giao diện.
     *
     * @return danh sách nhân viên DTO
     */
    List<NhanVienDTO> layDanhSachNhanVien();

    /**
     * Tìm kiếm nhân viên theo từ khóa mã hoặc họ tên.
     *
     * @param keyword từ khóa tìm kiếm
     * @return danh sách nhân viên phù hợp
     */
    List<NhanVienDTO> timKiemNhanVien(String keyword);

    /**
     * Lấy danh sách nhân viên đang làm việc để phục vụ các combobox trên giao diện.
     *
     * @return danh sách nhân viên đang hoạt động
     */
    List<NhanVien> layDanhSachNhanVienDangLam();

    /**
     * Tìm nhân viên theo id.
     *
     * @param id mã nhân viên
     * @return Optional chứa nhân viên nếu tồn tại
     */
    Optional<NhanVien> timNhanVienTheoId(Long id);

    /**
     * Thêm mới nhân viên.
     *
     * @param nv nhân viên cần thêm
     * @param password mật khẩu gốc
     * @param confirmPassword mật khẩu xác nhận
     * @return nhân viên sau khi lưu
     */
    NhanVien themNhanVien(NhanVien nv, String password, String confirmPassword);

    /**
     * Cập nhật thông tin nhân viên.
     *
     * @param nv nhân viên cần cập nhật
     * @param currentPassword mật khẩu hiện tại để xác nhận
     * @return nhân viên sau khi cập nhật
     */
    NhanVien capNhatNhanVien(NhanVien nv, String currentPassword);

    /**
     * Chuyển nhân viên sang trạng thái nghỉ làm tạm thời.
     *
     * @param id mã nhân viên
     * @param currentPassword mật khẩu hiện tại để xác nhận
     */
    void choNghiLam(Long id, String currentPassword);

    /**
     * Chuyển nhân viên quay lại trạng thái đang làm việc.
     *
     * @param id mã nhân viên
     */
    void choDiLamLai(Long id);

    /**
     * Chuyển nhân viên sang trạng thái nghỉ việc chính thức.
     *
     * @param id mã nhân viên
     * @param currentPassword mật khẩu hiện tại để xác nhận
     */
    void choNghiViec(Long id, String currentPassword);

    /**
     * Xóa cứng dữ liệu nhân viên khỏi hệ thống.
     *
     * @param id mã nhân viên
     * @param currentPassword mật khẩu hiện tại để xác nhận
     */
    void xoaNhanVien(Long id, String currentPassword);

    /**
     * Đặt lại mật khẩu bằng quyền root demo của hệ thống.
     *
     * @param id mã nhân viên
     * @param rootPassword mật khẩu root
     * @param newPassword mật khẩu mới
     * @param confirmPassword mật khẩu xác nhận
     */
    void doiMatKhauBangRoot(Long id, String rootPassword, String newPassword, String confirmPassword);

    /**
     * Xác thực đăng nhập cho nhân viên.
     *
     * @param tenDangNhap tên đăng nhập
     * @param matKhau mật khẩu gốc
     * @return nhân viên đăng nhập thành công
     */
    NhanVien dangNhap(String tenDangNhap, String matKhau);
}
