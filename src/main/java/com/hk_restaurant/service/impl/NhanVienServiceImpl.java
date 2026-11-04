package com.hk_restaurant.service.impl;

import com.hk_restaurant.config.JpaUtil;
import com.hk_restaurant.constant.AppConstants;
import com.hk_restaurant.dao.NhanVienDAO;
import com.hk_restaurant.exception.AuthenticationException;
import com.hk_restaurant.exception.ValidationException;
import com.hk_restaurant.model.dto.NhanVienDTO;
import com.hk_restaurant.model.entity.NhanVien;
import com.hk_restaurant.service.NhanVienService;
import com.hk_restaurant.util.security.PasswordUtils;
import com.hk_restaurant.util.validation.ValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Lớp cài đặt nghiệp vụ cho phân hệ nhân viên và đăng nhập.
 * Service này xử lý kiểm tra dữ liệu, bảo mật mật khẩu và thay đổi trạng thái nhân sự.
 */
public class NhanVienServiceImpl implements NhanVienService {
    private static final Logger log = LoggerFactory.getLogger(NhanVienServiceImpl.class);

    private final NhanVienDAO nhanVienDAO;

    /**
     * Khởi tạo service nhân viên.
     *
     * @param nhanVienDAO DAO truy cập dữ liệu nhân viên
     */
    public NhanVienServiceImpl(NhanVienDAO nhanVienDAO) {
        this.nhanVienDAO = nhanVienDAO;
    }

    /**
     * Lấy danh sách nhân viên dưới dạng DTO để hiển thị trên giao diện.
     *
     * @return danh sách nhân viên DTO
     */
    @Override
    public List<NhanVienDTO> layDanhSachNhanVien() {
        return nhanVienDAO.findAllDTO();
    }

    /**
     * Tìm kiếm nhân viên theo từ khóa mã hoặc họ tên.
     *
     * @param keyword từ khóa tìm kiếm
     * @return danh sách nhân viên phù hợp
     */
    @Override
    public List<NhanVienDTO> timKiemNhanVien(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return nhanVienDAO.findAllDTO();
        }
        return nhanVienDAO.searchDTO(keyword.trim());
    }

    /**
     * Lấy danh sách nhân viên đang làm việc để phục vụ các combobox trên giao diện.
     *
     * @return danh sách nhân viên đang hoạt động
     */
    @Override
    public List<NhanVien> layDanhSachNhanVienDangLam() {
        return nhanVienDAO.findAllActiveStaff();
    }

    /**
     * Tìm nhân viên theo id.
     *
     * @param id mã nhân viên
     * @return Optional chứa nhân viên nếu tồn tại
     */
    @Override
    public Optional<NhanVien> timNhanVienTheoId(Long id) {
        return nhanVienDAO.findById(id);
    }

    /**
     * Thêm mới nhân viên.
     * Mật khẩu mới sẽ được băm bằng cơ chế bảo mật nâng cấp trước khi lưu xuống DB.
     *
     * @param nv nhân viên cần thêm
     * @param password mật khẩu gốc
     * @param confirmPassword mật khẩu xác nhận
     * @return nhân viên sau khi lưu
     */
    @Override
    public NhanVien themNhanVien(NhanVien nv, String password, String confirmPassword) {
        validateThongTinCoBan(nv);

        if (ValidationUtils.isBlank(password)) {
            throw new ValidationException("Mật khẩu không được để trống!");
        }
        if (ValidationUtils.isBlank(confirmPassword)) {
            throw new ValidationException("Xác nhận mật khẩu không được để trống!");
        }
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Mật khẩu và xác nhận mật khẩu không khớp!");
        }
        if (nhanVienDAO.existsByCode(nv.getMaNhanVienCode())) {
            throw new ValidationException("Mã nhân viên đã tồn tại!");
        }
        if (nhanVienDAO.existsByUsername(nv.getTenDangNhap())) {
            throw new ValidationException("Tên đăng nhập đã tồn tại!");
        }

        nv.setMatKhau(PasswordUtils.hash(password));
        nv.setDangLamViec(true);
        nv.setDaNghiViec(false);

        log.info("Them nhan vien moi: maNV={}, username={}", nv.getMaNhanVienCode(), nv.getTenDangNhap());
        return nhanVienDAO.save(nv);
    }

    /**
     * Cập nhật thông tin nhân viên.
     * Service yêu cầu nhập đúng mật khẩu hiện tại để bảo vệ thao tác sửa dữ liệu nhạy cảm.
     *
     * @param nv nhân viên cần cập nhật
     * @param currentPassword mật khẩu hiện tại để xác nhận
     * @return nhân viên sau khi cập nhật
     */
    @Override
    public NhanVien capNhatNhanVien(NhanVien nv, String currentPassword) {
        if (nv.getId() == null) {
            throw new ValidationException("Thiếu ID nhân viên!");
        }

        NhanVien old = nhanVienDAO.findById(nv.getId())
                .orElseThrow(() -> new ValidationException("Không tìm thấy nhân viên!"));

        validateThongTinCoBan(nv);

        if (ValidationUtils.isBlank(currentPassword)) {
            throw new ValidationException("Vui lòng nhập mật khẩu hiện tại của nhân viên!");
        }
        if (!PasswordUtils.matches(currentPassword, old.getMatKhau())) {
            throw new AuthenticationException("Mật khẩu nhân viên không đúng, không thể sửa thông tin!");
        }
        if (!old.getMaNhanVienCode().equals(nv.getMaNhanVienCode())
                && nhanVienDAO.existsByCode(nv.getMaNhanVienCode())) {
            throw new ValidationException("Mã nhân viên đã tồn tại!");
        }
        if (!old.getTenDangNhap().equals(nv.getTenDangNhap())
                && nhanVienDAO.existsByUsername(nv.getTenDangNhap())) {
            throw new ValidationException("Tên đăng nhập đã tồn tại!");
        }

        nv.setDangLamViec(old.getDangLamViec());
        nv.setDaNghiViec(old.getDaNghiViec());
        nv.setMatKhau(old.getMatKhau());

        log.info("Cap nhat nhan vien: id={}", nv.getId());
        return nhanVienDAO.update(nv);
    }

    /**
     * Chuyển nhân viên sang trạng thái nghỉ làm tạm thời.
     *
     * @param id mã nhân viên
     * @param currentPassword mật khẩu hiện tại để xác nhận
     */
    @Override
    public void choNghiLam(Long id, String currentPassword) {
        NhanVien nv = nhanVienDAO.findById(id)
                .orElseThrow(() -> new ValidationException("Không tìm thấy nhân viên!"));

        if (Boolean.TRUE.equals(nv.getDaNghiViec())) {
            throw new ValidationException("Nhân viên này đã nghỉ việc!");
        }
        if (ValidationUtils.isBlank(currentPassword)) {
            throw new ValidationException("Vui lòng nhập mật khẩu nhân viên để xác nhận nghỉ làm!");
        }
        if (!PasswordUtils.matches(currentPassword, nv.getMatKhau())) {
            throw new AuthenticationException("Mật khẩu nhân viên không đúng!");
        }
        if (nhanVienDAO.isServingAnyTable(id)) {
            throw new ValidationException("Nhân viên này đang phục vụ bàn. Hãy chuyển bàn sang nhân viên khác trước khi cho nghỉ làm!");
        }

        nv.setDangLamViec(false);
        nv.setDaNghiViec(false);
        nhanVienDAO.update(nv);

        log.info("Cho nghi lam: id={}", id);
    }

    /**
     * Chuyển nhân viên quay lại trạng thái đang làm việc.
     *
     * @param id mã nhân viên
     */
    @Override
    public void choDiLamLai(Long id) {
        NhanVien nv = nhanVienDAO.findById(id)
                .orElseThrow(() -> new ValidationException("Không tìm thấy nhân viên!"));

        if (Boolean.TRUE.equals(nv.getDaNghiViec())) {
            throw new ValidationException("Nhân viên này đã nghỉ việc, không thể đi làm lại!");
        }

        nv.setDangLamViec(true);
        nhanVienDAO.update(nv);

        log.info("Cho di lam lai: id={}", id);
    }

    /**
     * Chuyển nhân viên sang trạng thái nghỉ việc chính thức.
     *
     * @param id mã nhân viên
     * @param currentPassword mật khẩu hiện tại để xác nhận
     */
    @Override
    public void choNghiViec(Long id, String currentPassword) {
        NhanVien nv = nhanVienDAO.findById(id)
                .orElseThrow(() -> new ValidationException("Không tìm thấy nhân viên!"));

        if (ValidationUtils.isBlank(currentPassword)) {
            throw new ValidationException("Vui lòng nhập mật khẩu nhân viên để xác nhận cho nghỉ việc!");
        }
        if (!PasswordUtils.matches(currentPassword, nv.getMatKhau())) {
            throw new AuthenticationException("Mật khẩu nhân viên không đúng, không thể cho nghỉ việc!");
        }
        if (Boolean.TRUE.equals(nv.getDangLamViec())) {
            throw new ValidationException("Phải cho nhân viên nghỉ làm trước rồi mới cho nghỉ việc!");
        }
        if (nhanVienDAO.isServingAnyTable(id)) {
            throw new ValidationException("Không thể cho nghỉ việc vì nhân viên đang phục vụ bàn!");
        }

        nv.setDangLamViec(false);
        nv.setDaNghiViec(true);
        nhanVienDAO.update(nv);

        log.info("Cho nghi viec: id={}", id);
    }

    /**
     * Xóa vĩnh viễn nhân viên khỏi hệ thống.
     *
     * <p>Quy tắc xử lý:
     * - Chỉ cho xóa khi nhân viên đã nghỉ việc
     * - Không cho xóa nếu nhân viên còn đang phục vụ bàn
     * - Nếu nhân viên từng phát sinh đơn hàng trong quá khứ, hệ thống sẽ xóa toàn bộ
     *   hóa đơn, chi tiết đơn hàng và đơn hàng liên quan trước khi xóa nhân viên
     *   để tránh lỗi khóa ngoại trong cơ sở dữ liệu</p>
     *
     * @param id mã nhân viên
     * @param currentPassword mật khẩu hiện tại để xác nhận
     */
    @Override
    public void xoaNhanVien(Long id, String currentPassword) {
        NhanVien nv = nhanVienDAO.findById(id)
                .orElseThrow(() -> new ValidationException("Không tìm thấy nhân viên!"));

        if (ValidationUtils.isBlank(currentPassword)) {
            throw new ValidationException("Vui lòng nhập mật khẩu nhân viên để xác nhận xóa!");
        }
        if (!PasswordUtils.matches(currentPassword, nv.getMatKhau())) {
            throw new AuthenticationException("Mật khẩu nhân viên không đúng!");
        }
        if (!Boolean.TRUE.equals(nv.getDaNghiViec())) {
            throw new ValidationException("Chỉ được xóa khi nhân viên đã nghỉ việc!");
        }
        if (nhanVienDAO.isServingAnyTable(id)) {
            throw new ValidationException("Nhân viên này đang phục vụ bàn, không thể xóa!");
        }

        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            NhanVien managedNhanVien = em.find(NhanVien.class, id);
            if (managedNhanVien == null) {
                throw new ValidationException("Không tìm thấy nhân viên!");
            }

            List<Long> affectedOrderIds = em.createQuery(
                    "SELECT d.id FROM DonHang d WHERE d.nhanVien.id = :nhanVienId",
                    Long.class
            ).setParameter("nhanVienId", id).getResultList();

            if (!affectedOrderIds.isEmpty()) {
                em.createQuery("DELETE FROM HoaDon h WHERE h.donHang.id IN :orderIds")
                        .setParameter("orderIds", affectedOrderIds)
                        .executeUpdate();

                em.createQuery("DELETE FROM ChiTietDonHang ct WHERE ct.donHang.id IN :orderIds")
                        .setParameter("orderIds", affectedOrderIds)
                        .executeUpdate();

                em.createQuery("DELETE FROM DonHang d WHERE d.id IN :orderIds")
                        .setParameter("orderIds", affectedOrderIds)
                        .executeUpdate();
            }

            em.remove(managedNhanVien);
            tx.commit();

            log.info("Xoa vinh vien nhan vien: id={}", id);

        } catch (ValidationException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            log.error("Khong the xoa vinh vien nhan vien id={}", id, e);
            throw new ValidationException("Không thể xóa vĩnh viễn nhân viên này. Vui lòng kiểm tra lại dữ liệu liên quan!");
        } finally {
            em.close();
        }
    }

    /**
     * Đổi mật khẩu cho nhân viên bằng mật khẩu root demo của hệ thống.
     *
     * @param id mã nhân viên
     * @param rootPassword mật khẩu root để xác nhận
     * @param newPassword mật khẩu mới
     * @param confirmPassword mật khẩu xác nhận
     */
    @Override
    public void doiMatKhauBangRoot(Long id, String rootPassword, String newPassword, String confirmPassword) {
        NhanVien nv = nhanVienDAO.findById(id)
                .orElseThrow(() -> new ValidationException("Không tìm thấy nhân viên!"));

        if (ValidationUtils.isBlank(rootPassword)) {
            throw new ValidationException("Vui lòng nhập mật khẩu root!");
        }
        if (!AppConstants.DEMO_ROOT_PASSWORD.equals(rootPassword)) {
            throw new AuthenticationException("Mật khẩu root không đúng!");
        }
        if (ValidationUtils.isBlank(newPassword)) {
            throw new ValidationException("Mật khẩu mới không được để trống!");
        }
        if (ValidationUtils.isBlank(confirmPassword)) {
            throw new ValidationException("Xác nhận mật khẩu không được để trống!");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("Mật khẩu mới và xác nhận mật khẩu không khớp!");
        }

        nv.setMatKhau(PasswordUtils.hash(newPassword));
        nhanVienDAO.update(nv);

        log.info("Dat lai mat khau bang root cho nhan vien id={}", id);
    }

    /**
     * Xác thực đăng nhập cho nhân viên.
     * Nếu mật khẩu trong DB vẫn đang ở dạng MD5 legacy và đăng nhập thành công,
     * service sẽ tự động nâng cấp hash sang cơ chế mới rồi lưu lại.
     *
     * @param tenDangNhap tên đăng nhập
     * @param matKhau mật khẩu gốc người dùng nhập
     * @return nhân viên đăng nhập thành công
     */
    @Override
    public NhanVien dangNhap(String tenDangNhap, String matKhau) {
        if (ValidationUtils.isBlank(tenDangNhap)) {
            throw new ValidationException("Tên đăng nhập không được để trống!");
        }
        if (ValidationUtils.isBlank(matKhau)) {
            throw new ValidationException("Mật khẩu không được để trống!");
        }

        NhanVien nv = nhanVienDAO.findByUsername(tenDangNhap.trim())
                .orElseThrow(() -> new AuthenticationException("Sai tên đăng nhập hoặc mật khẩu!"));

        if (!PasswordUtils.matches(matKhau, nv.getMatKhau())) {
            throw new AuthenticationException("Sai tên đăng nhập hoặc mật khẩu!");
        }

        if (Boolean.TRUE.equals(nv.getDaNghiViec())) {
            throw new AuthenticationException("Tài khoản này đã nghỉ việc, không thể đăng nhập!");
        }

        if (Boolean.FALSE.equals(nv.getDangLamViec())) {
            throw new AuthenticationException("Nhân viên đang ở trạng thái nghỉ làm, không thể đăng nhập!");
        }

        if (PasswordUtils.isLegacyMd5Hash(nv.getMatKhau())) {
            nv.setMatKhau(PasswordUtils.hash(matKhau));
            nhanVienDAO.update(nv);
            log.info("Da nang cap hash mat khau legacy cho username={}", tenDangNhap);
        }

        log.info("Dang nhap thanh cong: username={}", tenDangNhap);
        return nv;
    }

    /**
     * Kiểm tra các trường thông tin cơ bản trước khi thêm hoặc sửa nhân viên.
     *
     * @param nv nhân viên cần kiểm tra
     */
    private void validateThongTinCoBan(NhanVien nv) {
        if (nv == null) {
            throw new ValidationException("Dữ liệu nhân viên không hợp lệ!");
        }
        if (ValidationUtils.isBlank(nv.getMaNhanVienCode())) {
            throw new ValidationException("Mã nhân viên không được để trống!");
        }
        if (ValidationUtils.isBlank(nv.getHoTen())) {
            throw new ValidationException("Họ tên không được để trống!");
        }
        if (ValidationUtils.isBlank(nv.getTenDangNhap())) {
            throw new ValidationException("Tên đăng nhập không được để trống!");
        }
        if (nv.getVaiTro() == null) {
            throw new ValidationException("Vui lòng chọn vai trò!");
        }
        if (!ValidationUtils.isPhoneValid(nv.getSoDienThoai())) {
            throw new ValidationException("Số điện thoại không hợp lệ!");
        }
    }
}
