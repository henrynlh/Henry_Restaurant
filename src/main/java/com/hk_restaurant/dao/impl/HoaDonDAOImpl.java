package com.hk_restaurant.dao.impl;

import com.hk_restaurant.config.JpaUtil;
import com.hk_restaurant.dao.HoaDonDAO;
import com.hk_restaurant.model.entity.HoaDon;
import com.hk_restaurant.model.enumtype.PaymentMethod;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Lớp cài đặt DAO cho thực thể HoaDon.
 * Lớp này dùng để thao tác dữ liệu hóa đơn thanh toán trong hệ thống.
 */
public class HoaDonDAOImpl implements HoaDonDAO {

    /**
     * EntityManager dùng chung khi DAO được gọi trong một transaction lớn ở tầng service.
     */
    private final EntityManager sharedEntityManager;

    /**
     * Khởi tạo DAO ở chế độ tự quản lý EntityManager.
     */
    public HoaDonDAOImpl() {
        this.sharedEntityManager = null;
    }

    /**
     * Khởi tạo DAO với EntityManager dùng chung từ service.
     *
     * @param entityManager EntityManager đang được quản lý ở tầng service
     */
    public HoaDonDAOImpl(EntityManager entityManager) {
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
     * Lấy toàn bộ danh sách hóa đơn.
     * Sử dụng JOIN FETCH để nạp luôn thông tin đơn hàng, bàn ăn và nhân viên liên quan.
     *
     * @return danh sách hóa đơn
     */
    @Override
    public List<HoaDon> findAll() {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT h FROM HoaDon h " +
                                    "JOIN FETCH h.donHang d " +
                                    "JOIN FETCH d.banAn " +
                                    "JOIN FETCH d.nhanVien " +
                                    "LEFT JOIN FETCH d.danhSachChiTiet ct " +
                                    "LEFT JOIN FETCH ct.monAn " +
                                    "ORDER BY h.thoiGianThanhToan DESC", HoaDon.class)
                    .getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Tìm hóa đơn theo khóa chính.
     *
     * @param id mã hóa đơn cần tìm
     * @return Optional chứa hóa đơn nếu tìm thấy
     */
    @Override
    public Optional<HoaDon> findById(Long id) {
        EntityManager em = currentEntityManager();
        try {
            List<HoaDon> results = em.createQuery(
                            "SELECT DISTINCT h FROM HoaDon h " +
                                    "JOIN FETCH h.donHang d " +
                                    "JOIN FETCH d.banAn " +
                                    "JOIN FETCH d.nhanVien " +
                                    "LEFT JOIN FETCH d.danhSachChiTiet ct " +
                                    "LEFT JOIN FETCH ct.monAn " +
                                    "WHERE h.id = :id",
                            HoaDon.class)
                    .setParameter("id", id)
                    .getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Tìm hóa đơn theo số hóa đơn nghiệp vụ.
     *
     * @param soHoaDon số hóa đơn cần tìm
     * @return Optional chứa hóa đơn nếu tồn tại
     */
    @Override
    public Optional<HoaDon> findBySoHoaDon(String soHoaDon) {
        EntityManager em = currentEntityManager();
        try {
            List<HoaDon> results = em.createQuery(
                            "SELECT DISTINCT h FROM HoaDon h " +
                                    "JOIN FETCH h.donHang d " +
                                    "JOIN FETCH d.banAn " +
                                    "JOIN FETCH d.nhanVien " +
                                    "LEFT JOIN FETCH d.danhSachChiTiet ct " +
                                    "LEFT JOIN FETCH ct.monAn " +
                                    "WHERE h.soHoaDon = :soHoaDon",
                            HoaDon.class)
                    .setParameter("soHoaDon", soHoaDon)
                    .getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Tìm hóa đơn theo mã đơn hàng.
     *
     * @param maDonHang mã đơn hàng liên kết
     * @return Optional chứa hóa đơn nếu tồn tại
     */
    @Override
    public Optional<HoaDon> findByMaDonHang(Long maDonHang) {
        EntityManager em = currentEntityManager();
        try {
            List<HoaDon> results = em.createQuery(
                            "SELECT DISTINCT h FROM HoaDon h " +
                                    "JOIN FETCH h.donHang d " +
                                    "JOIN FETCH d.banAn " +
                                    "JOIN FETCH d.nhanVien " +
                                    "LEFT JOIN FETCH d.danhSachChiTiet ct " +
                                    "LEFT JOIN FETCH ct.monAn " +
                                    "WHERE d.id = :maDonHang",
                            HoaDon.class)
                    .setParameter("maDonHang", maDonHang)
                    .getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Lấy danh sách hóa đơn theo phương thức thanh toán.
     *
     * @param phuongThuc phương thức thanh toán cần lọc
     * @return danh sách hóa đơn tương ứng
     */
    @Override
    public List<HoaDon> findByPhuongThucThanhToan(PaymentMethod phuongThuc) {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT h FROM HoaDon h JOIN FETCH h.donHang d " +
                                    "JOIN FETCH d.banAn JOIN FETCH d.nhanVien " +
                                    "WHERE h.phuongThucThanhToan = :phuongThuc " +
                                    "ORDER BY h.thoiGianThanhToan DESC",
                            HoaDon.class)
                    .setParameter("phuongThuc", phuongThuc)
                    .getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Lấy danh sách hóa đơn trong một khoảng thời gian thanh toán.
     *
     * @param startDate thời điểm bắt đầu
     * @param endDate thời điểm kết thúc
     * @return danh sách hóa đơn trong khoảng thời gian
     */
    @Override
    public List<HoaDon> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT h FROM HoaDon h " +
                                    "JOIN FETCH h.donHang d " +
                                    "JOIN FETCH d.banAn " +
                                    "JOIN FETCH d.nhanVien " +
                                    "LEFT JOIN FETCH d.danhSachChiTiet ct " +
                                    "LEFT JOIN FETCH ct.monAn " +
                                    "WHERE h.thoiGianThanhToan BETWEEN :startDate AND :endDate " +
                                    "ORDER BY h.thoiGianThanhToan DESC", HoaDon.class)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Lưu mới một hóa đơn vào cơ sở dữ liệu.
     *
     * @param hoaDon hóa đơn cần lưu
     * @return hóa đơn sau khi lưu
     */
    @Override
    public HoaDon save(HoaDon hoaDon) {
        EntityManager em = currentEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean startedHere = !tx.isActive();

        try {
            if (startedHere) {
                tx.begin();
            }

            em.persist(hoaDon);
            em.flush();

            if (startedHere) {
                tx.commit();
            }
            return hoaDon;
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
     * Cập nhật thông tin hóa đơn.
     *
     * @param hoaDon hóa đơn cần cập nhật
     * @return hóa đơn sau khi cập nhật
     */
    @Override
    public HoaDon update(HoaDon hoaDon) {
        EntityManager em = currentEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean startedHere = !tx.isActive();

        try {
            if (startedHere) {
                tx.begin();
            }

            HoaDon merged = em.merge(hoaDon);
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
     * Xóa hóa đơn theo mã định danh.
     *
     * @param id mã hóa đơn cần xóa
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

            HoaDon hoaDon = em.find(HoaDon.class, id);
            if (hoaDon != null) {
                em.remove(hoaDon);
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
     * Kiểm tra số hóa đơn đã tồn tại hay chưa.
     *
     * @param soHoaDon số hóa đơn cần kiểm tra
     * @return true nếu đã tồn tại, ngược lại trả về false
     */
    @Override
    public boolean existsBySoHoaDon(String soHoaDon) {
        EntityManager em = currentEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(h) FROM HoaDon h WHERE h.soHoaDon = :soHoaDon",
                            Long.class)
                    .setParameter("soHoaDon", soHoaDon)
                    .getSingleResult();
            return count > 0;
        } finally {
            closeIfNeeded(em);
        }
    }
}
