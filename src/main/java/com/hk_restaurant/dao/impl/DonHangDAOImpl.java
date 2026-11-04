package com.hk_restaurant.dao.impl;

import com.hk_restaurant.config.JpaUtil;
import com.hk_restaurant.dao.DonHangDAO;
import com.hk_restaurant.model.entity.DonHang;
import com.hk_restaurant.model.enumtype.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Lớp cài đặt DAO cho thực thể DonHang.
 * Lớp này xử lý các thao tác CRUD và truy vấn đơn hàng bằng JPA/Hibernate.
 */
public class DonHangDAOImpl implements DonHangDAO {

    /**
     * EntityManager dùng chung khi DAO được gọi trong một transaction lớn ở tầng service.
     */
    private final EntityManager sharedEntityManager;

    /**
     * Khởi tạo DAO ở chế độ tự quản lý EntityManager.
     */
    public DonHangDAOImpl() {
        this.sharedEntityManager = null;
    }

    /**
     * Khởi tạo DAO với EntityManager dùng chung từ service.
     *
     * @param entityManager EntityManager đang được quản lý ở tầng service
     */
    public DonHangDAOImpl(EntityManager entityManager) {
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
     * Lấy toàn bộ danh sách đơn hàng.
     * Sử dụng JOIN FETCH để nạp sẵn thông tin bàn ăn và nhân viên,
     * tránh lỗi lazy loading khi hiển thị dữ liệu trên giao diện Swing.
     *
     * @return danh sách đơn hàng
     */
    @Override
    public List<DonHang> findAll() {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery(
                    "SELECT d FROM DonHang d " +
                            "JOIN FETCH d.banAn " +
                            "JOIN FETCH d.nhanVien " +
                            "ORDER BY d.thoiGianLap DESC",
                    DonHang.class
            ).getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Tìm đơn hàng theo id và nạp đầy đủ danh sách chi tiết món ăn.
     *
     * @param id mã đơn hàng cần tìm
     * @return Optional chứa đơn hàng nếu tồn tại
     */
    @Override
    public Optional<DonHang> findByIdWithDetails(Long id) {
        EntityManager em = currentEntityManager();
        try {
            List<DonHang> results = em.createQuery(
                            "SELECT DISTINCT d FROM DonHang d " +
                                    "JOIN FETCH d.banAn " +
                                    "JOIN FETCH d.nhanVien " +
                                    "LEFT JOIN FETCH d.danhSachChiTiet ct " +
                                    "LEFT JOIN FETCH ct.monAn " +
                                    "WHERE d.id = :id",
                            DonHang.class
                    )
                    .setParameter("id", id)
                    .getResultList();

            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Tìm đơn hàng theo số đơn hàng nghiệp vụ.
     *
     * @param soDonHang số đơn hàng cần tìm
     * @return Optional chứa đơn hàng nếu tìm thấy
     */
    @Override
    public Optional<DonHang> findBySoDonHang(String soDonHang) {
        EntityManager em = currentEntityManager();
        try {
            List<DonHang> results = em.createQuery(
                            "SELECT d FROM DonHang d " +
                                    "JOIN FETCH d.banAn " +
                                    "JOIN FETCH d.nhanVien " +
                                    "WHERE d.soDonHang = :soDonHang",
                            DonHang.class
                    )
                    .setParameter("soDonHang", soDonHang)
                    .getResultList();

            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Lấy danh sách đơn hàng theo mã bàn và trạng thái đơn.
     *
     * @param maBan mã bàn cần lọc
     * @param trangThai trạng thái đơn hàng
     * @return danh sách đơn hàng phù hợp điều kiện
     */
    @Override
    public List<DonHang> findByBanAnIdAndTrangThai(Long maBan, OrderStatus trangThai) {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery(
                            "SELECT d FROM DonHang d " +
                                    "JOIN FETCH d.banAn " +
                                    "JOIN FETCH d.nhanVien " +
                                    "WHERE d.banAn.id = :maBan AND d.trangThai = :trangThai " +
                                    "ORDER BY d.thoiGianLap DESC",
                            DonHang.class
                    )
                    .setParameter("maBan", maBan)
                    .setParameter("trangThai", trangThai)
                    .getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Lấy danh sách đơn hàng theo trạng thái.
     *
     * @param trangThai trạng thái đơn hàng cần lọc
     * @return danh sách đơn hàng theo trạng thái
     */
    @Override
    public List<DonHang> findByTrangThai(OrderStatus trangThai) {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery(
                            "SELECT d FROM DonHang d " +
                                    "JOIN FETCH d.banAn " +
                                    "JOIN FETCH d.nhanVien " +
                                    "WHERE d.trangThai = :trangThai " +
                                    "ORDER BY d.thoiGianLap DESC",
                            DonHang.class
                    )
                    .setParameter("trangThai", trangThai)
                    .getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Lấy danh sách đơn hàng trong một khoảng thời gian.
     *
     * @param startDate thời điểm bắt đầu
     * @param endDate thời điểm kết thúc
     * @return danh sách đơn hàng theo khoảng thời gian
     */
    @Override
    public List<DonHang> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery(
                            "SELECT d FROM DonHang d " +
                                    "JOIN FETCH d.banAn " +
                                    "JOIN FETCH d.nhanVien " +
                                    "WHERE d.thoiGianLap BETWEEN :startDate AND :endDate " +
                                    "ORDER BY d.thoiGianLap DESC",
                            DonHang.class
                    )
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Lưu mới một đơn hàng vào cơ sở dữ liệu.
     * Sau khi lưu, hệ thống nạp lại đơn hàng đầy đủ chi tiết để sử dụng tiếp.
     *
     * @param donHang đơn hàng cần lưu
     * @return đơn hàng sau khi lưu thành công
     */
    @Override
    public DonHang save(DonHang donHang) {
        EntityManager em = currentEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean startedHere = !tx.isActive();

        try {
            if (startedHere) {
                tx.begin();
            }

            em.persist(donHang);
            em.flush();

            if (startedHere) {
                tx.commit();
                return new DonHangDAOImpl().findByIdWithDetails(donHang.getId())
                        .orElseThrow(() -> new RuntimeException("Không thể tải lại đơn hàng sau khi lưu!"));
            }

            return findByIdWithDetails(donHang.getId())
                    .orElseThrow(() -> new RuntimeException("Không thể tải lại đơn hàng sau khi lưu!"));
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
     * Cập nhật thông tin đơn hàng.
     * Sau khi cập nhật, hệ thống nạp lại dữ liệu chi tiết mới nhất của đơn hàng.
     *
     * @param donHang đơn hàng cần cập nhật
     * @return đơn hàng sau khi cập nhật
     */
    @Override
    public DonHang update(DonHang donHang) {
        EntityManager em = currentEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean startedHere = !tx.isActive();

        try {
            if (startedHere) {
                tx.begin();
            }

            DonHang merged = em.merge(donHang);
            em.flush();

            if (startedHere) {
                tx.commit();
                return new DonHangDAOImpl().findByIdWithDetails(merged.getId())
                        .orElseThrow(() -> new RuntimeException("Không thể tải lại đơn hàng sau khi cập nhật!"));
            }

            return findByIdWithDetails(merged.getId())
                    .orElseThrow(() -> new RuntimeException("Không thể tải lại đơn hàng sau khi cập nhật!"));
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
     * Xóa đơn hàng theo mã định danh.
     *
     * @param id mã đơn hàng cần xóa
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

            DonHang donHang = em.find(DonHang.class, id);
            if (donHang != null) {
                em.remove(donHang);
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
     * Kiểm tra số đơn hàng đã tồn tại trong hệ thống hay chưa.
     *
     * @param soDonHang số đơn hàng cần kiểm tra
     * @return true nếu đã tồn tại, ngược lại trả về false
     */
    @Override
    public boolean existsBySoDonHang(String soDonHang) {
        EntityManager em = currentEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(d) FROM DonHang d WHERE d.soDonHang = :soDonHang",
                            Long.class
                    )
                    .setParameter("soDonHang", soDonHang)
                    .getSingleResult();

            return count > 0;
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Tìm đơn hàng đang phục vụ hiện tại của một bàn.
     * Truy vấn này thường dùng trong màn hình POS để lấy đơn đang hoạt động.
     *
     * @param maBan mã bàn cần kiểm tra
     * @return Optional chứa đơn hàng đang phục vụ nếu có
     */
    @Override
    public Optional<DonHang> findCurrentServingByBanId(Long maBan) {
        EntityManager em = currentEntityManager();
        try {
            List<DonHang> results = em.createQuery("""
                SELECT DISTINCT d FROM DonHang d
                JOIN FETCH d.banAn
                JOIN FETCH d.nhanVien
                LEFT JOIN FETCH d.danhSachChiTiet ct
                LEFT JOIN FETCH ct.monAn
                WHERE d.banAn.id = :maBan AND d.trangThai = :trangThai
                ORDER BY d.thoiGianLap DESC
                """, DonHang.class)
                    .setParameter("maBan", maBan)
                    .setParameter("trangThai", OrderStatus.SERVING)
                    .getResultList();

            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            closeIfNeeded(em);
        }
    }
}
