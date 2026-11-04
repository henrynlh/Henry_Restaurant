package com.hk_restaurant.service;

import com.hk_restaurant.model.entity.LoaiMon;
import com.hk_restaurant.model.entity.MonAn;

import java.util.List;

/**
 * Interface service cho phân hệ thực đơn.
 * Tầng này chịu trách nhiệm xử lý nghiệp vụ món ăn và loại món,
 * giúp controller không thao tác trực tiếp với DAO.
 */
public interface MonAnService {

    /**
     * Lấy toàn bộ danh sách món ăn trong hệ thống.
     *
     * @return danh sách món ăn
     */
    List<MonAn> layDanhSachMonAn();

    /**
     * Lấy danh sách món ăn theo loại món.
     *
     * @param loaiMon loại món cần lọc
     * @return danh sách món ăn thuộc loại tương ứng
     */
    List<MonAn> layDanhSachMonAnTheoLoai(LoaiMon loaiMon);

    /**
     * Lấy danh sách loại món duy nhất từ dữ liệu thực đơn.
     *
     * @return danh sách loại món
     */
    List<LoaiMon> layDanhSachLoaiMon();

    /**
     * Lấy toàn bộ món ăn đang dùng để bán hàng trên POS.
     *
     * @return danh sách món ăn còn kinh doanh
     */
    List<MonAn> layDanhSachThucDonDangBan();

    /**
     * Lấy thực đơn theo tên loại món để phục vụ màn hình POS.
     *
     * @param tenLoaiMon tên loại món
     * @return danh sách món ăn phù hợp
     */
    List<MonAn> layDanhSachThucDonTheoLoai(String tenLoaiMon);

    /**
     * Lấy danh sách tên loại món để đổ lên combobox lọc ở giao diện.
     *
     * @return danh sách tên loại món đã sắp xếp
     */
    List<String> layDanhSachTenLoaiMon();

    /**
     * Thêm mới một món ăn.
     *
     * @param monAn món ăn cần thêm
     * @return món ăn sau khi lưu
     */
    MonAn themMonAn(MonAn monAn);

    /**
     * Cập nhật thông tin món ăn.
     *
     * @param monAn món ăn cần cập nhật
     * @return món ăn sau khi cập nhật
     */
    MonAn capNhatMonAn(MonAn monAn);

    /**
     * Ngừng kinh doanh một món ăn theo hình thức khóa mềm.
     *
     * @param id mã món ăn
     */
    void ngungKinhDoanh(Long id);

    /**
     * Xóa vĩnh viễn một món ăn khỏi hệ thống.
     * Chỉ được phép xóa khi món đã ngừng kinh doanh.
     *
     * @param id mã món ăn cần xóa
     */
    void xoaVinhVienMonAn(Long id);
}
