package com.hk_restaurant.service.impl;

import com.hk_restaurant.config.JpaUtil;
import com.hk_restaurant.dao.MonAnDAO;
import com.hk_restaurant.exception.ValidationException;
import com.hk_restaurant.model.entity.LoaiMon;
import com.hk_restaurant.model.entity.MonAn;
import com.hk_restaurant.service.MonAnService;
import com.hk_restaurant.util.ImageStorageUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp cài đặt nghiệp vụ cho phân hệ thực đơn.
 * Toàn bộ logic kiểm tra dữ liệu và lọc thực đơn được đưa xuống tầng service
 * để controller chỉ còn vai trò điều phối giữa View và Service.
 */
public class MonAnServiceImpl implements MonAnService {

    private final MonAnDAO monAnDAO;

    /**
     * Khởi tạo service thực đơn với DAO tương ứng.
     *
     * @param monAnDAO DAO truy cập dữ liệu món ăn
     */
    public MonAnServiceImpl(MonAnDAO monAnDAO) {
        this.monAnDAO = monAnDAO;
    }

    /**
     * Lấy toàn bộ danh sách món ăn trong hệ thống.
     *
     * @return danh sách món ăn
     */
    @Override
    public List<MonAn> layDanhSachMonAn() {
        return monAnDAO.findAll();
    }

    /**
     * Lấy danh sách món ăn theo loại món được chọn.
     *
     * @param loaiMon loại món cần lọc
     * @return danh sách món ăn thuộc loại tương ứng
     */
    @Override
    public List<MonAn> layDanhSachMonAnTheoLoai(LoaiMon loaiMon) {
        if (loaiMon == null || loaiMon.getId() == null) {
            throw new ValidationException("Loại món không hợp lệ!");
        }
        return monAnDAO.findByCategoryId(loaiMon.getId());
    }

    /**
     * Lấy danh sách loại món duy nhất đang tồn tại trong hệ thống.
     * Do project hiện tại chưa có phân hệ CRUD riêng cho LoaiMon,
     * service sẽ tổng hợp loại món từ danh sách MonAn hiện có.
     *
     * @return danh sách loại món không trùng lặp
     */
    @Override
    public List<LoaiMon> layDanhSachLoaiMon() {
        List<MonAn> all = monAnDAO.findAll();
        Map<Long, LoaiMon> map = new LinkedHashMap<>();

        for (MonAn monAn : all) {
            LoaiMon loaiMon = monAn.getLoaiMon();
            if (loaiMon != null && loaiMon.getId() != null) {
                map.putIfAbsent(loaiMon.getId(), loaiMon);
            }
        }

        return new ArrayList<>(map.values());
    }

    /**
     * Lấy toàn bộ món ăn đang kinh doanh để phục vụ màn hình POS.
     *
     * @return danh sách món ăn còn bán
     */
    @Override
    public List<MonAn> layDanhSachThucDonDangBan() {
        return monAnDAO.findAll().stream()
                .filter(monAn -> Boolean.TRUE.equals(monAn.getDangKinhDoanh()))
                .collect(Collectors.toList());
    }

    /**
     * Lấy thực đơn theo tên loại món.
     * Nếu người dùng chọn "Tất cả" hoặc không nhập gì,
     * service sẽ trả về toàn bộ món ăn còn kinh doanh.
     *
     * @param tenLoaiMon tên loại món
     * @return danh sách món ăn phù hợp
     */
    @Override
    public List<MonAn> layDanhSachThucDonTheoLoai(String tenLoaiMon) {
        if (tenLoaiMon == null || tenLoaiMon.trim().isEmpty()
                || "Tất cả".equalsIgnoreCase(tenLoaiMon.trim())) {
            return layDanhSachThucDonDangBan();
        }
        return monAnDAO.findByCategoryName(tenLoaiMon.trim());
    }

    /**
     * Lấy danh sách tên loại món duy nhất để hiển thị trên combobox.
     *
     * @return danh sách tên loại món đã sắp xếp tăng dần
     */
    @Override
    public List<String> layDanhSachTenLoaiMon() {
        return layDanhSachLoaiMon().stream()
                .map(LoaiMon::getTenLoaiMon)
                .filter(ten -> ten != null && !ten.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Thêm mới một món ăn sau khi kiểm tra dữ liệu đầu vào.
     *
     * @param monAn món ăn cần thêm
     * @return món ăn sau khi lưu
     */
    @Override
    public MonAn themMonAn(MonAn monAn) {
        validateMonAn(monAn, true);
        monAn.setDangKinhDoanh(true);
        return monAnDAO.save(monAn);
    }

    /**
     * Cập nhật món ăn đã có trong hệ thống.
     * Service sẽ giữ lại trạng thái kinh doanh cũ nếu giao diện không truyền xuống.
     *
     * @param monAn món ăn cần cập nhật
     * @return món ăn sau khi cập nhật
     */
    @Override
    public MonAn capNhatMonAn(MonAn monAn) {
        if (monAn == null || monAn.getId() == null) {
            throw new ValidationException("Thiếu ID món ăn!");
        }

        validateMonAn(monAn, false);

        MonAn old = monAnDAO.findById(monAn.getId())
                .orElseThrow(() -> new ValidationException("Không tìm thấy món ăn!"));

        if (!old.getMaMonCode().equals(monAn.getMaMonCode())
                && monAnDAO.existsByCode(monAn.getMaMonCode())) {
            throw new ValidationException("Mã món ăn đã tồn tại!");
        }

        if (monAn.getDangKinhDoanh() == null) {
            monAn.setDangKinhDoanh(old.getDangKinhDoanh());
        }

        return monAnDAO.update(monAn);
    }

    /**
     * Ngừng kinh doanh một món ăn theo hình thức khóa mềm.
     *
     * @param id mã món ăn cần ngừng bán
     */
    @Override
    public void ngungKinhDoanh(Long id) {
        MonAn monAn = monAnDAO.findById(id)
                .orElseThrow(() -> new ValidationException("Không tìm thấy món ăn!"));

        monAn.setDangKinhDoanh(false);
        monAnDAO.update(monAn);
    }

    /**
     * Xóa vĩnh viễn món ăn khỏi hệ thống.
     *
     * <p>Quy tắc xử lý:
     * - Chỉ cho xóa khi món đã ngừng kinh doanh
     * - Xóa toàn bộ chi tiết đơn hàng liên quan đến món này
     * - Nếu đơn hàng không còn chi tiết nào thì xóa luôn hóa đơn và đơn hàng
     * - Nếu đơn hàng vẫn còn món khác thì tính lại tổng tiền đơn hàng và hóa đơn
     * - Nếu món có ảnh lưu trong images/monan thì xóa luôn file ảnh khỏi ổ đĩa</p>
     *
     * @param id mã món ăn cần xóa vĩnh viễn
     */
    @Override
    public void xoaVinhVienMonAn(Long id) {
        MonAn monAn = monAnDAO.findById(id)
                .orElseThrow(() -> new ValidationException("Không tìm thấy món ăn!"));

        if (Boolean.TRUE.equals(monAn.getDangKinhDoanh())) {
            throw new ValidationException("Chỉ được xóa vĩnh viễn khi món đã ngừng kinh doanh!");
        }

        String imagePathToDelete = monAn.getHinhAnh();

        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            MonAn managedMonAn = em.find(MonAn.class, id);
            if (managedMonAn == null) {
                throw new ValidationException("Không tìm thấy món ăn!");
            }

            List<Long> affectedOrderIds = em.createQuery(
                    "SELECT DISTINCT ct.donHang.id FROM ChiTietDonHang ct WHERE ct.monAn.id = :monId",
                    Long.class
            ).setParameter("monId", id).getResultList();

            em.createQuery("DELETE FROM ChiTietDonHang ct WHERE ct.monAn.id = :monId")
                    .setParameter("monId", id)
                    .executeUpdate();

            for (Long orderId : affectedOrderIds) {
                Long detailCount = em.createQuery(
                        "SELECT COUNT(ct) FROM ChiTietDonHang ct WHERE ct.donHang.id = :orderId",
                        Long.class
                ).setParameter("orderId", orderId).getSingleResult();

                if (detailCount == null || detailCount == 0) {
                    em.createQuery("DELETE FROM HoaDon h WHERE h.donHang.id = :orderId")
                            .setParameter("orderId", orderId)
                            .executeUpdate();

                    em.createQuery("DELETE FROM DonHang d WHERE d.id = :orderId")
                            .setParameter("orderId", orderId)
                            .executeUpdate();
                } else {
                    BigDecimal newTotal = em.createQuery(
                            "SELECT COALESCE(SUM(ct.thanhTien), 0) FROM ChiTietDonHang ct WHERE ct.donHang.id = :orderId",
                            BigDecimal.class
                    ).setParameter("orderId", orderId).getSingleResult();

                    em.createQuery("UPDATE DonHang d SET d.tongTien = :newTotal WHERE d.id = :orderId")
                            .setParameter("newTotal", newTotal)
                            .setParameter("orderId", orderId)
                            .executeUpdate();

                    em.createQuery("UPDATE HoaDon h SET h.tongTien = :newTotal WHERE h.donHang.id = :orderId")
                            .setParameter("newTotal", newTotal)
                            .setParameter("orderId", orderId)
                            .executeUpdate();
                }
            }

            em.remove(managedMonAn);
            tx.commit();

            // Chỉ xóa file ảnh sau khi transaction DB thành công
            ImageStorageUtils.deleteStoredDishImageQuietly(imagePathToDelete);

        } catch (ValidationException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new ValidationException("Không thể xóa vĩnh viễn món này. Vui lòng kiểm tra lại dữ liệu liên quan!");
        } finally {
            em.close();
        }
    }

    /**
     * Kiểm tra tính hợp lệ của dữ liệu món ăn trước khi lưu.
     *
     * @param monAn đối tượng món ăn cần kiểm tra
     * @param checkDuplicateCode có kiểm tra trùng mã món hay không
     */
    private void validateMonAn(MonAn monAn, boolean checkDuplicateCode) {
        if (monAn == null) {
            throw new ValidationException("Dữ liệu món ăn không hợp lệ!");
        }
        if (monAn.getLoaiMon() == null || monAn.getLoaiMon().getId() == null) {
            throw new ValidationException("Vui lòng chọn loại món!");
        }
        if (monAn.getMaMonCode() == null || monAn.getMaMonCode().trim().isEmpty()) {
            throw new ValidationException("Mã món không được để trống!");
        }
        if (monAn.getTenMon() == null || monAn.getTenMon().trim().isEmpty()) {
            throw new ValidationException("Tên món không được để trống!");
        }
        if (monAn.getDonGia() == null || monAn.getDonGia().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Đơn giá phải lớn hơn 0!");
        }

        if (checkDuplicateCode && monAnDAO.existsByCode(monAn.getMaMonCode())) {
            throw new ValidationException("Mã món ăn đã tồn tại!");
        }
    }
}

