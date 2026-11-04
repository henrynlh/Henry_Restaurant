package com.hk_restaurant.dao;

import com.hk_restaurant.model.entity.BanAn;
import com.hk_restaurant.model.enumtype.TableStatus;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO cho thực thể BanAn.
 * Khai báo các phương thức truy vấn và thao tác dữ liệu bàn ăn.
 */
public interface BanAnDAO {

    /**
     * Lấy toàn bộ danh sách bàn ăn trong hệ thống.
     *
     * @return danh sách bàn ăn
     */
    List<BanAn> findAll();

    /**
     * Tìm bàn ăn theo khóa chính.
     *
     * @param id mã bàn ăn
     * @return Optional chứa bàn ăn nếu tìm thấy
     */
    Optional<BanAn> findById(Long id);

    /**
     * Tìm bàn ăn theo mã nghiệp vụ.
     *
     * @param maBanCode mã bàn ăn
     * @return Optional chứa bàn ăn nếu tìm thấy
     */
    Optional<BanAn> findByCode(String maBanCode);

    /**
     * Lấy danh sách bàn ăn theo trạng thái.
     *
     * @param trangThai trạng thái bàn cần lọc
     * @return danh sách bàn theo trạng thái
     */
    List<BanAn> findByTrangThai(TableStatus trangThai);

    /**
     * Lưu mới một bàn ăn vào cơ sở dữ liệu.
     *
     * @param banAn bàn ăn cần lưu
     * @return bàn ăn sau khi lưu
     */
    BanAn save(BanAn banAn);

    /**
     * Cập nhật thông tin bàn ăn.
     *
     * @param banAn bàn ăn cần cập nhật
     * @return bàn ăn sau khi cập nhật
     */
    BanAn update(BanAn banAn);

    /**
     * Xóa bàn ăn theo mã định danh.
     *
     * @param id mã bàn ăn cần xóa
     */
    void deleteById(Long id);

    /**
     * Kiểm tra mã bàn đã tồn tại hay chưa.
     *
     * @param maBanCode mã bàn cần kiểm tra
     * @return true nếu đã tồn tại, ngược lại trả về false
     */
    boolean existsByCode(String maBanCode);
}