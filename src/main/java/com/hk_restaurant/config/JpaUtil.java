package com.hk_restaurant.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.function.Function;

/**
 * Lớp tiện ích dùng để khởi tạo và quản lý EntityManagerFactory của ứng dụng.
 * EntityManagerFactory được tạo duy nhất một lần và tái sử dụng trong toàn bộ hệ thống.
 */
public final class JpaUtil {

    /**
     * Tên "restaurantPU" phải khớp chính xác với persistence-unit
     * đã khai báo trong file persistence.xml.
     */
    private static final EntityManagerFactory EMF =
            DatabaseConfig.createEntityManagerFactory();

    /**
     * Constructor private để ngăn không cho tạo đối tượng từ lớp tiện ích này.
     */
    private JpaUtil() {
    }

    /**
     * Tạo và trả về một EntityManager mới để thao tác với cơ sở dữ liệu.
     *
     * @return EntityManager dùng cho các thao tác CRUD và truy vấn JPA
     */
    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /**
     * Thực thi một khối nghiệp vụ trong cùng một transaction duy nhất.
     * Hàm này phù hợp cho các service có nhiều bước ghi dữ liệu liên tiếp
     * như mở bàn, thanh toán, cập nhật nhiều bảng liên quan,...
     *
     * @param action khối xử lý cần chạy trong transaction
     * @param <T> kiểu kết quả trả về
     * @return kết quả sau khi transaction hoàn tất thành công
     */
    public static <T> T executeInTransaction(Function<EntityManager, T> action) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            T result = action.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Đóng EntityManagerFactory khi ứng dụng kết thúc
     * nhằm giải phóng tài nguyên kết nối đến cơ sở dữ liệu.
     */
    public static void shutdown() {
        if (EMF.isOpen()) {
            EMF.close();
        }
    }
}
