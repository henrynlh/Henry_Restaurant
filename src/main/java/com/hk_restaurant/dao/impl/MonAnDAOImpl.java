package com.hk_restaurant.dao.impl;

import com.hk_restaurant.config.JpaUtil;
import com.hk_restaurant.dao.MonAnDAO;
import com.hk_restaurant.model.entity.MonAn;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

/**
 * Lớp cài đặt DAO cho thực thể MonAn.
 * Lớp này chịu trách nhiệm thao tác dữ liệu thực đơn bằng JPA.
 */
public class MonAnDAOImpl implements MonAnDAO {

    /**
     * EntityManager dùng chung khi DAO được gọi trong một transaction lớn ở tầng service.
     */
    private final EntityManager sharedEntityManager;

    /**
     * Khởi tạo DAO ở chế độ tự quản lý EntityManager.
     */
    public MonAnDAOImpl() {
        this.sharedEntityManager = null;
    }

    /**
     * Khởi tạo DAO với EntityManager dùng chung từ service.
     *
     * @param entityManager EntityManager đang được quản lý ở tầng service
     */
    public MonAnDAOImpl(EntityManager entityManager) {
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
     * Lấy toàn bộ danh sách món ăn.
     * Sử dụng JOIN FETCH để lấy luôn thông tin loại món,
     * tránh lỗi lazy loading khi hiển thị lên bảng Swing.
     *
     * @return danh sách món ăn
     */
    @Override
    public List<MonAn> findAll() {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery(
                            "SELECT m FROM MonAn m JOIN FETCH m.loaiMon ORDER BY m.id",
                            MonAn.class)
                    .getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Tìm món ăn theo khóa chính.
     *
     * @param id mã món ăn cần tìm
     * @return Optional chứa món ăn nếu tìm thấy
     */
    @Override
    public Optional<MonAn> findById(Long id) {
        EntityManager em = currentEntityManager();
        try {
            return Optional.ofNullable(em.find(MonAn.class, id));
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Tìm món ăn theo mã món nghiệp vụ.
     *
     * @param maMonCode mã món cần tìm
     * @return Optional chứa món ăn nếu tồn tại
     */
    @Override
    public Optional<MonAn> findByCode(String maMonCode) {
        EntityManager em = currentEntityManager();
        try {
            List<MonAn> results = em.createQuery(
                            "SELECT m FROM MonAn m JOIN FETCH m.loaiMon WHERE m.maMonCode = :code",
                            MonAn.class)
                    .setParameter("code", maMonCode)
                    .getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Tìm món ăn theo từ khóa tên món.
     *
     * @param keyword từ khóa cần tìm
     * @return danh sách món ăn phù hợp
     */
    @Override
    public List<MonAn> searchByName(String keyword) {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery(
                            "SELECT m FROM MonAn m JOIN FETCH m.loaiMon " +
                                    "WHERE LOWER(m.tenMon) LIKE LOWER(:kw)",
                            MonAn.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Lấy danh sách món ăn theo mã loại món.
     *
     * @param categoryId mã loại món
     * @return danh sách món ăn thuộc loại tương ứng
     */
    @Override
    public List<MonAn> findByCategoryId(Long categoryId) {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery(
                            "SELECT m FROM MonAn m JOIN FETCH m.loaiMon WHERE m.loaiMon.id = :catId",
                            MonAn.class)
                    .setParameter("catId", categoryId)
                    .getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Lưu mới một món ăn vào hệ thống.
     *
     * @param monAn món ăn cần lưu
     * @return món ăn sau khi lưu
     */
    @Override
    public MonAn save(MonAn monAn) {
        EntityManager em = currentEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean startedHere = !tx.isActive();

        try {
            if (startedHere) {
                tx.begin();
            }

            em.persist(monAn);
            em.flush();

            if (startedHere) {
                tx.commit();
            }
            return monAn;
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
     * Cập nhật thông tin món ăn.
     *
     * @param monAn món ăn cần cập nhật
     * @return món ăn sau khi cập nhật
     */
    @Override
    public MonAn update(MonAn monAn) {
        EntityManager em = currentEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean startedHere = !tx.isActive();

        try {
            if (startedHere) {
                tx.begin();
            }

            MonAn merged = em.merge(monAn);
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
     * Xóa món ăn theo mã định danh.
     *
     * @param id mã món ăn cần xóa
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

            MonAn monAn = em.find(MonAn.class, id);
            if (monAn != null) {
                em.remove(monAn);
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
     * Kiểm tra mã món đã tồn tại trong hệ thống hay chưa.
     *
     * @param maMonCode mã món cần kiểm tra
     * @return true nếu đã tồn tại, ngược lại trả về false
     */
    @Override
    public boolean existsByCode(String maMonCode) {
        EntityManager em = currentEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(m) FROM MonAn m WHERE m.maMonCode = :code",
                            Long.class)
                    .setParameter("code", maMonCode)
                    .getSingleResult();
            return count > 0;
        } finally {
            closeIfNeeded(em);
        }
    }

    /**
     * Lấy danh sách món ăn theo tên loại món và trạng thái đang kinh doanh.
     *
     * @param tenLoaiMon tên loại món cần lọc
     * @return danh sách món ăn phù hợp
     */
    @Override
    public List<MonAn> findByCategoryName(String tenLoaiMon) {
        EntityManager em = currentEntityManager();
        try {
            return em.createQuery("""
                SELECT m FROM MonAn m
                JOIN FETCH m.loaiMon l
                WHERE LOWER(l.tenLoaiMon) = LOWER(:tenLoaiMon)
                  AND m.dangKinhDoanh = true
                ORDER BY m.id
                """, MonAn.class)
                    .setParameter("tenLoaiMon", tenLoaiMon)
                    .getResultList();
        } finally {
            closeIfNeeded(em);
        }
    }
}
