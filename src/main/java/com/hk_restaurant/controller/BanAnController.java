package com.hk_restaurant.controller;

import com.hk_restaurant.model.entity.BanAn;
import com.hk_restaurant.model.entity.DonHang;
import com.hk_restaurant.model.entity.NhanVien;
import com.hk_restaurant.service.BanAnService;
import com.hk_restaurant.service.DonHangService;
import com.hk_restaurant.service.NhanVienService;

import java.util.List;

/**
 * Controller cho phân hệ bàn ăn.
 * Lớp này tiếp nhận thao tác từ giao diện và điều phối xuống các service liên quan.
 */
public class BanAnController {

    private final BanAnService banAnService;
    private final NhanVienService nhanVienService;
    private final DonHangService donHangService;

    /**
     * Khởi tạo controller bàn ăn.
     *
     * @param banAnService service xử lý nghiệp vụ bàn ăn
     * @param nhanVienService service xử lý dữ liệu nhân viên
     * @param donHangService service xử lý đơn hàng đang phục vụ theo bàn
     */
    public BanAnController(BanAnService banAnService,
                           NhanVienService nhanVienService,
                           DonHangService donHangService) {
        this.banAnService = banAnService;
        this.nhanVienService = nhanVienService;
        this.donHangService = donHangService;
    }

    /**
     * Lấy toàn bộ danh sách bàn ăn.
     *
     * @return danh sách bàn ăn
     */
    public List<BanAn> layDanhSachBanAn() {
        return banAnService.layTatCaBanAn();
    }

    /**
     * Thêm mới một bàn ăn.
     *
     * @param banAn bàn ăn cần thêm
     * @return bàn ăn sau khi lưu
     */
    public BanAn themBanAn(BanAn banAn) {
        return banAnService.taoMoiBanAn(banAn);
    }

    /**
     * Cập nhật thông tin bàn ăn.
     *
     * @param banAn bàn ăn cần cập nhật
     * @return bàn ăn sau khi cập nhật
     */
    public BanAn capNhatBanAn(BanAn banAn) {
        return banAnService.capNhatBanAn(banAn);
    }

    /**
     * Khóa mềm một bàn ăn.
     *
     * @param id mã bàn ăn cần khóa
     */
    public void xoaBanAn(Long id) {
        banAnService.xoaBanAn(id);
    }

    /**
     * Xóa vĩnh viễn một bàn ăn.
     *
     * @param id mã bàn ăn cần xóa
     */
    public void xoaVinhVienBanAn(Long id) {
        banAnService.xoaVinhVienBanAn(id);
    }

    /**
     * Lấy danh sách nhân viên đang làm việc để gán phục vụ bàn.
     *
     * @return danh sách nhân viên đang hoạt động
     */
    public List<NhanVien> layDanhSachNhanVienDangLam() {
        return nhanVienService.layDanhSachNhanVienDangLam();
    }

    /**
     * Lấy nhân viên đang phục vụ cho bàn hiện tại.
     *
     * @param maBan mã bàn ăn
     * @return nhân viên đang phục vụ hoặc null nếu bàn chưa có đơn
     */
    public NhanVien layNhanVienDangPhucVu(Long maBan) {
        DonHang donHang = donHangService.layDonDangPhucVuTheoBan(maBan);
        if (donHang == null) {
            return null;
        }
        return donHang.getNhanVien();
    }

    /**
     * Lấy tên nhân viên đang phục vụ để hiển thị nhanh trên bảng giao diện.
     *
     * @param maBan mã bàn ăn
     * @return tên nhân viên hoặc chuỗi rỗng nếu chưa có người phục vụ
     */
    public String layTenNhanVienDangPhucVu(Long maBan) {
        NhanVien nv = layNhanVienDangPhucVu(maBan);
        return nv != null ? nv.getHoTen() : "";
    }

    /**
     * Đổi nhân viên đang phục vụ cho bàn hiện tại.
     *
     * @param maBan mã bàn ăn
     * @param maNhanVien mã nhân viên mới
     */
    public void capNhatNhanVienPhucVu(Long maBan, Long maNhanVien) {
        donHangService.capNhatNhanVienPhucVuTheoBan(maBan, maNhanVien);
    }
}
