package com.hk_restaurant.controller;

import com.hk_restaurant.model.entity.LoaiMon;
import com.hk_restaurant.model.entity.MonAn;
import com.hk_restaurant.service.MonAnService;

import java.util.List;

/**
 * Controller cho phân hệ thực đơn.
 * Lớp này chỉ đóng vai trò nhận yêu cầu từ View và chuyển xuống tầng Service,
 * không thao tác trực tiếp với DAO.
 */
public class MonAnController {

    private final MonAnService monAnService;

    /**
     * Khởi tạo controller thực đơn.
     *
     * @param monAnService service xử lý nghiệp vụ món ăn
     */
    public MonAnController(MonAnService monAnService) {
        this.monAnService = monAnService;
    }

    /**
     * Lấy toàn bộ danh sách món ăn trong hệ thống.
     *
     * @return danh sách món ăn
     */
    public List<MonAn> layDanhSachMonAn() {
        return monAnService.layDanhSachMonAn();
    }

    /**
     * Lấy danh sách món ăn theo loại món.
     *
     * @param loaiMon loại món cần lọc
     * @return danh sách món ăn thuộc loại tương ứng
     */
    public List<MonAn> layDanhSachMonAnTheoLoai(LoaiMon loaiMon) {
        return monAnService.layDanhSachMonAnTheoLoai(loaiMon);
    }

    /**
     * Lấy danh sách loại món để đổ lên combobox giao diện.
     *
     * @return danh sách loại món
     */
    public List<LoaiMon> layDanhSachLoaiMon() {
        return monAnService.layDanhSachLoaiMon();
    }

    /**
     * Thêm mới một món ăn.
     *
     * @param monAn món ăn cần thêm
     * @return món ăn sau khi lưu
     */
    public MonAn themMonAn(MonAn monAn) {
        return monAnService.themMonAn(monAn);
    }

    /**
     * Cập nhật thông tin món ăn.
     *
     * @param monAn món ăn cần cập nhật
     * @return món ăn sau khi cập nhật
     */
    public MonAn capNhatMonAn(MonAn monAn) {
        return monAnService.capNhatMonAn(monAn);
    }

    /**
     * Ngừng kinh doanh một món ăn.
     *
     * @param id mã món ăn cần ngừng bán
     */
    public void ngungKinhDoanh(Long id) {
        monAnService.ngungKinhDoanh(id);
    }

    /**
     * Xóa vĩnh viễn một món ăn khỏi hệ thống.
     *
     * @param id mã món ăn cần xóa
     */
    public void xoaVinhVienMonAn(Long id) {
        monAnService.xoaVinhVienMonAn(id);
    }
}
