package com.hk_restaurant.controller;

import com.hk_restaurant.model.dto.OrderDetailInput;
import com.hk_restaurant.model.entity.DonHang;
import com.hk_restaurant.model.entity.HoaDon;
import com.hk_restaurant.model.entity.MonAn;
import com.hk_restaurant.model.entity.NhanVien;
import com.hk_restaurant.model.enumtype.PaymentMethod;
import com.hk_restaurant.service.DonHangService;
import com.hk_restaurant.service.MonAnService;
import com.hk_restaurant.service.NhanVienService;

import java.util.List;

/**
 * Controller cho phân hệ đơn hàng và POS.
 * Controller chỉ điều phối yêu cầu giữa View và Service,
 * không truy cập trực tiếp xuống DAO.
 */
public class DonHangController {

    private final DonHangService donHangService;
    private final MonAnService monAnService;
    private final NhanVienService nhanVienService;

    /**
     * Khởi tạo controller đơn hàng.
     *
     * @param donHangService service xử lý nghiệp vụ đơn hàng
     * @param monAnService service xử lý dữ liệu thực đơn phục vụ POS
     * @param nhanVienService service xử lý dữ liệu nhân viên phục vụ POS
     */
    public DonHangController(DonHangService donHangService,
                             MonAnService monAnService,
                             NhanVienService nhanVienService) {
        this.donHangService = donHangService;
        this.monAnService = monAnService;
        this.nhanVienService = nhanVienService;
    }

    /**
     * Mở bàn mới và tạo đơn phục vụ đầu tiên cho bàn đó.
     *
     * @param maBan mã bàn
     * @param maNhanVien mã nhân viên phục vụ
     * @param ghiChu ghi chú chung của đơn
     * @return đơn hàng đang phục vụ
     */
    public DonHang moBanMoi(Long maBan, Long maNhanVien, String ghiChu) {
        return donHangService.moBanMoi(maBan, maNhanVien, ghiChu);
    }

    /**
     * Thêm món vào đơn đang phục vụ.
     *
     * @param maDonHang mã đơn hàng
     * @param input dữ liệu món ăn đầu vào
     * @return đơn hàng sau khi cập nhật
     */
    public DonHang themMonVaoDon(Long maDonHang, OrderDetailInput input) {
        return donHangService.themMonVaoDon(maDonHang, input);
    }

    /**
     * Cập nhật số lượng của một món trong đơn hàng.
     *
     * @param maDonHang mã đơn hàng
     * @param maMon mã món ăn
     * @param soLuongMoi số lượng mới
     * @return đơn hàng sau khi cập nhật
     */
    public DonHang capNhatSoLuongMon(Long maDonHang, Long maMon, int soLuongMoi) {
        return donHangService.capNhatSoLuongMon(maDonHang, maMon, soLuongMoi);
    }

    /**
     * Cập nhật ghi chú của một món trong đơn hàng.
     *
     * @param maDonHang mã đơn hàng
     * @param maMon mã món ăn
     * @param ghiChuMoi ghi chú mới
     * @return đơn hàng sau khi cập nhật
     */
    public DonHang capNhatGhiChuMon(Long maDonHang, Long maMon, String ghiChuMoi) {
        return donHangService.capNhatGhiChuMon(maDonHang, maMon, ghiChuMoi);
    }

    /**
     * Lấy đơn đang phục vụ hiện tại của một bàn.
     *
     * @param maBan mã bàn ăn
     * @return đơn đang phục vụ hoặc null nếu bàn chưa có đơn
     */
    public DonHang layDonDangPhucVuTheoBan(Long maBan) {
        return donHangService.layDonDangPhucVuTheoBan(maBan);
    }

    /**
     * Thanh toán đơn hàng và sinh hóa đơn.
     *
     * @param maDonHang mã đơn hàng
     * @param phuongThuc phương thức thanh toán
     * @return hóa đơn đã phát sinh
     */
    public HoaDon thanhToanDonHang(Long maDonHang, PaymentMethod phuongThuc) {
        return donHangService.thanhToanDonHang(maDonHang, phuongThuc);
    }

    /**
     * Lấy toàn bộ thực đơn đang kinh doanh cho màn hình POS.
     *
     * @return danh sách món ăn còn bán
     */
    public List<MonAn> layDanhSachThucDon() {
        return monAnService.layDanhSachThucDonDangBan();
    }

    /**
     * Lấy thực đơn theo tên loại món trên combobox lọc.
     *
     * @param tenLoaiMon tên loại món
     * @return danh sách món ăn phù hợp
     */
    public List<MonAn> layDanhSachThucDonTheoLoai(String tenLoaiMon) {
        return monAnService.layDanhSachThucDonTheoLoai(tenLoaiMon);
    }

    /**
     * Lấy danh sách tên loại món để hiển thị trên combobox.
     *
     * @return danh sách tên loại món đã sắp xếp
     */
    public List<String> layDanhSachTenLoaiMon() {
        return monAnService.layDanhSachTenLoaiMon();
    }

    /**
     * Lấy danh sách nhân viên đang làm việc để chọn phục vụ bàn.
     *
     * @return danh sách nhân viên đang hoạt động
     */
    public List<NhanVien> layDanhSachNhanVienDangLam() {
        return nhanVienService.layDanhSachNhanVienDangLam();
    }
}
