package com.hk_restaurant.service.impl;

import com.hk_restaurant.config.JpaUtil;
import com.hk_restaurant.dao.BanAnDAO;
import com.hk_restaurant.exception.ValidationException;
import com.hk_restaurant.model.entity.BanAn;
import com.hk_restaurant.model.enumtype.TableStatus;
import com.hk_restaurant.service.BanAnService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

/**
 * Lớp cài đặt nghiệp vụ cho phân hệ bàn ăn.
 * Service này chịu trách nhiệm kiểm tra dữ liệu đầu vào, cập nhật trạng thái bàn
 * và xử lý nghiệp vụ khóa/xóa vĩnh viễn bàn ăn.
 */
public class BanAnServiceImpl implements BanAnService {

    private final BanAnDAO banAnDAO;

    /**
     * Khởi tạo service bàn ăn.
     *
     * @param banAnDAO DAO truy cập dữ liệu bàn ăn
     */
    public BanAnServiceImpl(BanAnDAO banAnDAO) {
        this.banAnDAO = banAnDAO;
    }

    /**
     * Lấy toàn bộ danh sách bàn ăn trong hệ thống.
     *
     * @return danh sách bàn ăn
     */
    @Override
    public List<BanAn> layTatCaBanAn() {
        return banAnDAO.findAll();
    }

    /**
     * Tạo mới bàn ăn.
     * Service kiểm tra mã bàn, tên bàn, sức chứa và gán trạng thái mặc định cho bàn mới.
     *
     * @param banAn bàn ăn cần tạo mới
     * @return bàn ăn sau khi lưu
     */
    @Override
    public BanAn taoMoiBanAn(BanAn banAn) {
        if (banAn.getMaBanCode() == null || banAn.getMaBanCode().trim().isEmpty()) {
            throw new ValidationException("Mã bàn không được để trống!");
        }

        if (banAn.getTenBan() == null || banAn.getTenBan().trim().isEmpty()) {
            throw new ValidationException("Tên bàn không được để trống!");
        }

        if (banAn.getSucChua() == null || banAn.getSucChua() <= 0) {
            throw new ValidationException("Sức chứa phải lớn hơn 0!");
        }

        if (banAnDAO.existsByCode(banAn.getMaBanCode())) {
            throw new ValidationException("Mã bàn '" + banAn.getMaBanCode() + "' đã tồn tại trong hệ thống!");
        }

        banAn.setTrangThai(TableStatus.EMPTY);
        banAn.setDangSuDung(true);

        return banAnDAO.save(banAn);
    }

    /**
     * Cập nhật thông tin bàn ăn.
     *
     * @param banAn bàn ăn cần cập nhật
     * @return bàn ăn sau khi cập nhật
     */
    @Override
    public BanAn capNhatBanAn(BanAn banAn) {
        if (banAn.getId() == null) {
            throw new ValidationException("Thiếu ID bàn ăn!");
        }

        Optional<BanAn> existingTable = banAnDAO.findById(banAn.getId());
        if (existingTable.isEmpty()) {
            throw new ValidationException("Không tìm thấy bàn ăn này!");
        }

        if (banAn.getMaBanCode() == null || banAn.getMaBanCode().trim().isEmpty()) {
            throw new ValidationException("Mã bàn không được để trống!");
        }

        if (banAn.getTenBan() == null || banAn.getTenBan().trim().isEmpty()) {
            throw new ValidationException("Tên bàn không được để trống!");
        }

        if (banAn.getSucChua() == null || banAn.getSucChua() <= 0) {
            throw new ValidationException("Sức chứa phải lớn hơn 0!");
        }

        if (!existingTable.get().getMaBanCode().equals(banAn.getMaBanCode())) {
            if (banAnDAO.existsByCode(banAn.getMaBanCode())) {
                throw new ValidationException("Mã bàn '" + banAn.getMaBanCode() + "' đã bị trùng với bàn khác!");
            }
        }

        if (banAn.getTrangThai() == null) {
            banAn.setTrangThai(existingTable.get().getTrangThai());
        }
        if (banAn.getDangSuDung() == null) {
            banAn.setDangSuDung(existingTable.get().getDangSuDung());
        }

        return banAnDAO.update(banAn);
    }

    /**
     * Khóa mềm bàn ăn.
     * Chỉ được khóa khi bàn không ở trạng thái đang phục vụ.
     *
     * @param id mã bàn cần khóa
     */
    @Override
    public void xoaBanAn(Long id) {
        Optional<BanAn> existingTable = banAnDAO.findById(id);
        if (existingTable.isPresent()) {
            if (existingTable.get().getTrangThai() == TableStatus.SERVING) {
                throw new ValidationException("Bàn này đang có khách phục vụ, không thể khóa!");
            }

            BanAn ban = existingTable.get();
            ban.setDangSuDung(false);
            banAnDAO.update(ban);
        } else {
            throw new ValidationException("Không tìm thấy bàn ăn cần khóa!");
        }
    }

    /**
     * Xóa vĩnh viễn bàn ăn khỏi hệ thống.
     *
     * <p>Quy tắc xử lý:
     * - Chỉ cho xóa khi bàn đã được khóa trước đó (DangSuDung = false)
     * - Không cho xóa nếu bàn đang ở trạng thái phục vụ
     * - Nếu bàn từng phát sinh đơn hàng/hóa đơn, hệ thống sẽ xóa toàn bộ
     *   lịch sử liên quan của riêng bàn này trước khi xóa bàn để tránh lỗi khóa ngoại</p>
     *
     * @param id mã bàn cần xóa vĩnh viễn
     */
    @Override
    public void xoaVinhVienBanAn(Long id) {
        Optional<BanAn> existingTable = banAnDAO.findById(id);
        if (existingTable.isEmpty()) {
            throw new ValidationException("Không tìm thấy bàn ăn cần xóa!");
        }

        BanAn ban = existingTable.get();

        if (ban.getTrangThai() == TableStatus.SERVING) {
            throw new ValidationException("Bàn này đang có khách, không thể xóa vĩnh viễn!");
        }

        if (Boolean.TRUE.equals(ban.getDangSuDung())) {
            throw new ValidationException("Chỉ được xóa vĩnh viễn khi bàn đã được khóa trước đó!");
        }

        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            BanAn managedBan = em.find(BanAn.class, id);
            if (managedBan == null) {
                throw new ValidationException("Không tìm thấy bàn ăn cần xóa!");
            }

            // Xóa hóa đơn của các đơn hàng thuộc bàn này
            em.createQuery("""
                    DELETE FROM HoaDon h
                    WHERE h.donHang.id IN (
                        SELECT d.id FROM DonHang d WHERE d.banAn.id = :banId
                    )
                    """)
                    .setParameter("banId", id)
                    .executeUpdate();

            // Xóa chi tiết đơn hàng của các đơn hàng thuộc bàn này
            em.createQuery("""
                    DELETE FROM ChiTietDonHang ct
                    WHERE ct.donHang.id IN (
                        SELECT d.id FROM DonHang d WHERE d.banAn.id = :banId
                    )
                    """)
                    .setParameter("banId", id)
                    .executeUpdate();

            // Xóa toàn bộ đơn hàng lịch sử của bàn này
            em.createQuery("""
                    DELETE FROM DonHang d
                    WHERE d.banAn.id = :banId
                    """)
                    .setParameter("banId", id)
                    .executeUpdate();

            // Sau khi đã xóa toàn bộ dữ liệu liên quan, tiến hành xóa bàn
            em.remove(managedBan);

            tx.commit();

        } catch (ValidationException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new ValidationException(
                    "Không thể xóa vĩnh viễn bàn này. Vui lòng kiểm tra lại dữ liệu liên quan!"
            );
        } finally {
            em.close();
        }
    }
}
