package com.hk_restaurant.dao.impl;

import com.hk_restaurant.config.JpaUtil;
import com.hk_restaurant.dao.NhanVienDAO;
import com.hk_restaurant.model.dto.NhanVienDTO;
import com.hk_restaurant.model.entity.NhanVien;
import com.hk_restaurant.model.enumtype.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

/**
 * Lớp cài đặt DAO cho thực thể NhanVien.
 * Lớp này dùng để truy vấn và cập nhật dữ liệu nhân viên trong hệ thống.
 */
public class NhanVienDAOImpl implements NhanVienDAO {

    /**
     * EntityManager dùng chung khi DAO được gọi trong một transaction lớn ở tầng service.
     */
    private final EntityManager sharedEntityManager;

    /**
     * Khởi tạo DAO ở chế độ tự quản lý EntityManager.
     */
    public NhanVienDAOImpl() {
        this.sharedEntityManager = null;
    }

    /**
     * Khởi tạo DAO với EntityManager dùng chung từ service.
     *
     * @param entityManager EntityManager đang được quản lý ở tầng service
     */
    public NhanVienDAOImpl(EntityManager entityManager) {
        this.sharedEntityManager = entityManager;
    }

    /**
     * Lấy EntityManager hiện hành.
     *
     * @return EntityManager dùng cho thao tác hiện tại
     */
    private EntityManager currentEntityManager() {
        return sharedEntityManager != null ? sharedEntityManager : JpaUtil.getEntityManager();
    }

    /**
     * Kiểm tra DAO có đang dùng EntityManager do bên ngoài truyền vào hay không.
     *
     * @return true nếu EntityManager được quản lý ở ngoài DAO
     */
    private boolean isExternallyManaged() {
        return sharedEntityManager != null;
    }

    /**
     * Đóng EntityManager nếu DAO tự mở.
     *
     * @param em EntityManager cần đóng
     */
    private void closeIfNeeded(EntityManager em) {
        if (!isExternallyManaged() && em != null && em.isOpen()) {
            em.close();
        }
    }

    /**
     * Lấy toàn bộ danh sách nhân viên.
     *
     * @return danh sách nhân viên
     */
    @Override
    public List<NhanVien> findAll() {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery("SELECT n FROM NhanVien n ORDER BY n.id", NhanVien.class)
                    .getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Lấy danh sách nhân viên đang làm việc và có vai trò STAFF.
     *
     * @return danh sách nhân viên đang làm
     */
    @Override
    public List<NhanVien> findAllActiveStaff() {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery("""
                    SELECT n FROM NhanVien n
                    WHERE n.dangLamViec = true
                      AND n.daNghiViec = false
                      AND n.vaiTro = :role
                    ORDER BY n.hoTen
                    """, NhanVien.class)
                    .setParameter("role", Role.STAFF)
                    .getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Lấy toàn bộ danh sách nhân viên dưới dạng DTO để hiển thị lên giao diện.
     *
     * @return danh sách DTO nhân viên
     */
    @Override
    public List<NhanVienDTO> findAllDTO() {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery("""
                    SELECT new com.hk_restaurant.model.dto.NhanVienDTO(
                        n.id,
                        n.maNhanVienCode,
                        n.hoTen,
                        n.soDienThoai,
                        n.tenDangNhap,
                        CAST(n.vaiTro as string),
                        n.dangLamViec,
                        n.daNghiViec
                    )
                    FROM NhanVien n
                    ORDER BY n.id
                    """, NhanVienDTO.class).getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Tìm kiếm nhân viên dưới dạng DTO theo mã hoặc họ tên.
     *
     * @param keyword từ khóa tìm kiếm
     * @return danh sách DTO nhân viên phù hợp
     */
    @Override
    public List<NhanVienDTO> searchDTO(String keyword) {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery("""
                    SELECT new com.hk_restaurant.model.dto.NhanVienDTO(
                        n.id,
                        n.maNhanVienCode,
                        n.hoTen,
                        n.soDienThoai,
                        n.tenDangNhap,
                        CAST(n.vaiTro as string),
                        n.dangLamViec,
                        n.daNghiViec
                    )
                    FROM NhanVien n
                    WHERE LOWER(n.maNhanVienCode) LIKE LOWER(:kw)
                       OR LOWER(n.hoTen) LIKE LOWER(:kw)
                    ORDER BY n.id
                    """, NhanVienDTO.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Tìm nhân viên theo khóa chính.
     *
     * @param id mã nhân viên
     * @return Optional chứa nhân viên nếu tồn tại
     */
    @Override
    public Optional<NhanVien> findById(Long id) {
        EntityManager em = currentEntityManager();
        try {
            return Optional.ofNullable(em.find(NhanVien.class, id));
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Tìm nhân viên theo tên đăng nhập.
     *
     * @param tenDangNhap tên đăng nhập cần tìm
     * @return Optional chứa nhân viên nếu tồn tại
     */
    @Override
    public Optional<NhanVien> findByUsername(String tenDangNhap) {
        EntityManager em = currentEntityManager();
        try {
            List<NhanVien> results = em.createQuery("""
                    SELECT n FROM NhanVien n
                    WHERE n.tenDangNhap = :username
                    """, NhanVien.class)
                    .setParameter("username", tenDangNhap)
                    .getResultList();

            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Tìm nhân viên theo mã nghiệp vụ.
     *
     * @param maNhanVienCode mã nhân viên cần tìm
     * @return Optional chứa nhân viên nếu tồn tại
     */
    @Override
    public Optional<NhanVien> findByCode(String maNhanVienCode) {
        EntityManager em = currentEntityManager();
        try {
            List<NhanVien> results = em.createQuery("""
                    SELECT n FROM NhanVien n
                    WHERE n.maNhanVienCode = :code
                    """, NhanVien.class)
                    .setParameter("code", maNhanVienCode)
                    .getResultList();

            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Lưu mới một nhân viên vào cơ sở dữ liệu.
     *
     * @param nhanVien nhân viên cần lưu
     * @return nhân viên sau khi lưu
     */
    @Override
    public NhanVien save(NhanVien nhanVien) {
        EntityManager em = currentEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean startedHere = !tx.isActive();

        try {
            if (startedHere) {
                tx.begin();
            }

            em.persist(nhanVien);
            em.flush();

            if (startedHere) {
                tx.commit();
            }
            return nhanVien;
        } catch (Exception e) {
            if (startedHere && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Cập nhật thông tin nhân viên.
     *
     * @param nhanVien nhân viên cần cập nhật
     * @return nhân viên sau khi cập nhật
     */
    @Override
    public NhanVien update(NhanVien nhanVien) {
        EntityManager em = currentEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean startedHere = !tx.isActive();

        try {
            if (startedHere) {
                tx.begin();
            }

            NhanVien merged = em.merge(nhanVien);
            em.flush();

            if (startedHere) {
                tx.commit();
            }
            return merged;
        } catch (Exception e) {
            if (startedHere && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Xóa nhân viên theo mã định danh.
     *
     * @param id mã nhân viên cần xóa
     */
    @Override
    public void deleteById(Long id) {
        EntityManager em = currentEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean startedHere = !tx.isActive();

        try {
            if (startedHere) {
                tx.begin();
            }

            NhanVien nhanVien = em.find(NhanVien.class, id);
            if (nhanVien != null) {
                em.remove(nhanVien);
            }

            if (startedHere) {
                tx.commit();
            }
        } catch (Exception e) {
            if (startedHere && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Kiểm tra mã nhân viên đã tồn tại hay chưa.
     *
     * @param maNhanVienCode mã nhân viên cần kiểm tra
     * @return true nếu đã tồn tại, ngược lại trả về false
     */
    @Override
    public boolean existsByCode(String maNhanVienCode) {
        EntityManager em = currentEntityManager();
        try {
            Long count = em.createQuery("""
                    SELECT COUNT(n) FROM NhanVien n
                    WHERE n.maNhanVienCode = :code
                    """, Long.class)
                    .setParameter("code", maNhanVienCode)
                    .getSingleResult();
            return count > 0;
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Kiểm tra tên đăng nhập đã tồn tại hay chưa.
     *
     * @param tenDangNhap tên đăng nhập cần kiểm tra
     * @return true nếu đã tồn tại, ngược lại trả về false
     */
    @Override
    public boolean existsByUsername(String tenDangNhap) {
        EntityManager em = currentEntityManager();
        try {
            Long count = em.createQuery("""
                    SELECT COUNT(n) FROM NhanVien n
                    WHERE n.tenDangNhap = :username
                    """, Long.class)
                    .setParameter("username", tenDangNhap)
                    .getSingleResult();
            return count > 0;
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Kiểm tra nhân viên có đang phục vụ bàn nào hay không.
     *
     * @param maNhanVien mã nhân viên cần kiểm tra
     * @return true nếu đang phục vụ ít nhất một bàn
     */
    @Override
    public boolean isServingAnyTable(Long maNhanVien) {
        EntityManager em = currentEntityManager();
        try {
            Long count = em.createQuery("""
                    SELECT COUNT(d) FROM DonHang d
                    WHERE d.nhanVien.id = :maNhanVien
                      AND d.trangThai = com.hk_restaurant.model.enumtype.OrderStatus.SERVING
                    """, Long.class)
                    .setParameter("maNhanVien", maNhanVien)
                    .getSingleResult();
            return count > 0;
        } finally {
            closeIfNeeded(em);
        }
    }
}
