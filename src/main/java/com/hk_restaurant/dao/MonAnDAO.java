package com.hk_restaurant.dao;

import com.hk_restaurant.model.entity.MonAn;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO cho thực thể MonAn.
 * Khai báo các phương thức thao tác dữ liệu thực đơn.
 */
public interface MonAnDAO {

    /**
     * Lấy toàn bộ danh sách món ăn trong hệ thống.
     *
     * @return danh sách món ăn
     */
    List<MonAn> findAll();

    /**
     * Tìm món ăn theo khóa chính.
     *
     * @param id mã món ăn
     * @return Optional chứa món ăn nếu tìm thấy
     */
    Optional<MonAn> findById(Long id);

    /**
     * Tìm món ăn theo mã món nghiệp vụ.
     *
     * @param maMonCode mã món ăn
     * @return Optional chứa món ăn nếu tìm thấy
     */
    Optional<MonAn> findByCode(String maMonCode);

    /**
     * Tìm kiếm món ăn theo tên món.
     *
     * @param keyword từ khóa tìm kiếm
     * @return danh sách món ăn phù hợp
     */
    List<MonAn> searchByName(String keyword);

    /**
     * Lấy danh sách món ăn theo mã loại món.
     *
     * @param categoryId mã loại món
     * @return danh sách món ăn thuộc loại tương ứng
     */
    List<MonAn> findByCategoryId(Long categoryId);

    /**
     * Lấy danh sách món ăn theo tên loại món.
     *
     * @param tenLoaiMon tên loại món
     * @return danh sách món ăn theo loại
     */
    List<MonAn> findByCategoryName(String tenLoaiMon);

    /**
     * Lưu mới một món ăn vào hệ thống.
     *
     * @param monAn món ăn cần lưu
     * @return món ăn sau khi lưu
     */
    MonAn save(MonAn monAn);

    /**
     * Cập nhật thông tin món ăn.
     *
     * @param monAn món ăn cần cập nhật
     * @return món ăn sau khi cập nhật
     */
    MonAn update(MonAn monAn);

    /**
     * Xóa món ăn theo mã định danh.
     *
     * @param id mã món ăn cần xóa
     */
    void deleteById(Long id);

    /**
     * Kiểm tra mã món đã tồn tại hay chưa.
     *
     * @param maMonCode mã món cần kiểm tra
     * @return true nếu đã tồn tại, ngược lại trả về false
     */
    boolean existsByCode(String maMonCode);
}