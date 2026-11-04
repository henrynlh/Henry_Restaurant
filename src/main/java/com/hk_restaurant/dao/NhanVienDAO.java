package com.hk_restaurant.dao;

import com.hk_restaurant.model.dto.NhanVienDTO;
import com.hk_restaurant.model.entity.NhanVien;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO cho thực thể NhanVien.
 * Khai báo các phương thức truy vấn và thao tác dữ liệu nhân viên.
 */
public interface NhanVienDAO {

    /**
     * Lấy toàn bộ danh sách nhân viên.
     *
     * @return danh sách nhân viên
     */
    List<NhanVien> findAll();

    /**
     * Lấy danh sách nhân viên đang làm việc.
     *
     * @return danh sách nhân viên đang làm
     */
    List<NhanVien> findAllActiveStaff();

    /**
     * Lấy toàn bộ danh sách nhân viên dưới dạng DTO để hiển thị lên giao diện.
     *
     * @return danh sách DTO nhân viên
     */
    List<NhanVienDTO> findAllDTO();

    /**
     * Tìm kiếm nhân viên dưới dạng DTO theo từ khóa.
     *
     * @param keyword từ khóa tìm kiếm
     * @return danh sách DTO nhân viên phù hợp
     */
    List<NhanVienDTO> searchDTO(String keyword);

    /**
     * Tìm nhân viên theo khóa chính.
     *
     * @param id mã nhân viên
     * @return Optional chứa nhân viên nếu tìm thấy
     */
    Optional<NhanVien> findById(Long id);

    /**
     * Tìm nhân viên theo tên đăng nhập.
     *
     * @param tenDangNhap tên đăng nhập
     * @return Optional chứa nhân viên nếu tìm thấy
     */
    Optional<NhanVien> findByUsername(String tenDangNhap);

    /**
     * Tìm nhân viên theo mã nghiệp vụ.
     *
     * @param maNhanVienCode mã nhân viên
     * @return Optional chứa nhân viên nếu tìm thấy
     */
    Optional<NhanVien> findByCode(String maNhanVienCode);

    /**
     * Lưu mới một nhân viên vào cơ sở dữ liệu.
     *
     * @param nhanVien nhân viên cần lưu
     * @return nhân viên sau khi lưu
     */
    NhanVien save(NhanVien nhanVien);

    /**
     * Cập nhật thông tin nhân viên.
     *
     * @param nhanVien nhân viên cần cập nhật
     * @return nhân viên sau khi cập nhật
     */
    NhanVien update(NhanVien nhanVien);

    /**
     * Xóa nhân viên theo mã định danh.
     *
     * @param id mã nhân viên cần xóa
     */
    void deleteById(Long id);

    /**
     * Kiểm tra mã nhân viên đã tồn tại hay chưa.
     *
     * @param maNhanVienCode mã nhân viên cần kiểm tra
     * @return true nếu đã tồn tại, ngược lại trả về false
     */
    boolean existsByCode(String maNhanVienCode);

    /**
     * Kiểm tra tên đăng nhập đã tồn tại hay chưa.
     *
     * @param tenDangNhap tên đăng nhập cần kiểm tra
     * @return true nếu đã tồn tại, ngược lại trả về false
     */
    boolean existsByUsername(String tenDangNhap);

    /**
     * Kiểm tra nhân viên có đang phục vụ bàn nào hay không.
     *
     * @param maNhanVien mã nhân viên cần kiểm tra
     * @return true nếu nhân viên đang phục vụ ít nhất một bàn
     */
    boolean isServingAnyTable(Long maNhanVien);
}