package com.hk_restaurant.dao.impl;

import com.hk_restaurant.config.JpaUtil;
import com.hk_restaurant.dao.BanAnDAO;
import com.hk_restaurant.model.entity.BanAn;
import com.hk_restaurant.model.enumtype.TableStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

/**
 * Lớp cài đặt DAO cho thực thể BanAn.
 * Lớp này chịu trách nhiệm thao tác CRUD và truy vấn dữ liệu bàn ăn bằng JPA.
 */
public class BanAnDAOImpl implements BanAnDAO {

    /**
     * Tiền tố mã dùng cho các bàn hệ thống lưu lịch sử sau khi xóa cứng.
     * Các bàn dạng này sẽ được ẩn khỏi màn hình quản lý bàn ăn.
     */
    private static final String DELETED_TABLE_CODE_PREFIX = "DEL_BAN_%";

    /**
     * Lấy toàn bộ danh sách bàn ăn đang hiển thị trên giao diện.
     * Hệ thống ẩn các bàn lưu vết nội bộ dùng để giữ lịch sử đơn hàng.
     *
     * @return danh sách bàn ăn, sắp xếp theo id tăng dần
     */
    @Override
    public List<BanAn> findAll() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("""
                    SELECT b FROM BanAn b
                    WHERE b.maBanCode NOT LIKE :deletedPrefix
                    ORDER BY b.id
                    """, BanAn.class)
                    .setParameter("deletedPrefix", DELETED_TABLE_CODE_PREFIX)
                    .getResultList();
        }
    }

    /**
     * Tìm bàn ăn theo khóa chính.
     *
     * @param id mã bàn ăn cần tìm
     * @return Optional chứa bàn ăn nếu tìm thấy
     */
    @Override
    public Optional<BanAn> findById(Long id) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return Optional.ofNullable(em.find(BanAn.class, id));
        }
    }

    /**
     * Tìm bàn ăn theo mã nghiệp vụ của bàn.
     *
     * @param maBanCode mã bàn cần tìm
     * @return Optional chứa bàn ăn nếu tồn tại
     */
    @Override
    public Optional<BanAn> findByCode(String maBanCode) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            List<BanAn> results = em.createQuery(
                            "SELECT b FROM BanAn b WHERE b.maBanCode = :code",
                            BanAn.class
                    )
                    .setParameter("code", maBanCode)
                    .getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        }
    }

    /**
     * Lấy danh sách bàn theo trạng thái.
     * Hệ thống bỏ qua các bàn lưu vết nội bộ.
     *
     * @param trangThai trạng thái bàn
     * @return danh sách bàn phù hợp
     */
    @Override
    public List<BanAn> findByTrangThai(TableStatus trangThai) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("""
                    SELECT b FROM BanAn b
                    WHERE b.trangThai = :status
                      AND b.maBanCode NOT LIKE :deletedPrefix
                    ORDER BY b.id
                    """, BanAn.class)
                    .setParameter("status", trangThai)
                    .setParameter("deletedPrefix", DELETED_TABLE_CODE_PREFIX)
                    .getResultList();
        }
    }

    /**
     * Lưu mới một bàn ăn vào cơ sở dữ liệu.
     *
     * @param banAn bàn ăn cần lưu
     * @return bàn ăn sau khi lưu
     */
    @Override
    public BanAn save(BanAn banAn) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(banAn);
            tx.commit();
            return banAn;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật thông tin bàn ăn.
     *
     * @param banAn bàn ăn cần cập nhật
     * @return bàn ăn sau khi cập nhật
     */
    @Override
    public BanAn update(BanAn banAn) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            BanAn merged = em.merge(banAn);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Xóa vĩnh viễn bàn ăn theo mã định danh.
     *
     * @param id mã bàn ăn cần xóa
     */
    @Override
    public void deleteById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            BanAn banAn = em.find(BanAn.class, id);
            if (banAn != null) {
                em.remove(banAn);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Kiểm tra mã bàn đã tồn tại trong hệ thống hay chưa.
     *
     * @param maBanCode mã bàn cần kiểm tra
     * @return true nếu mã đã tồn tại, ngược lại false
     */
    @Override
    public boolean existsByCode(String maBanCode) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            Long count = em.createQuery(
                            "SELECT COUNT(b) FROM BanAn b WHERE b.maBanCode = :code",
                            Long.class
                    )
                    .setParameter("code", maBanCode)
                    .getSingleResult();
            return count > 0;
        }
    }
}
