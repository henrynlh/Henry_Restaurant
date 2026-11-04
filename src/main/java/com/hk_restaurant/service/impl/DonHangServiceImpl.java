package com.hk_restaurant.service.impl;

import com.hk_restaurant.dao.BanAnDAO;
import com.hk_restaurant.dao.DonHangDAO;
import com.hk_restaurant.dao.HoaDonDAO;
import com.hk_restaurant.dao.MonAnDAO;
import com.hk_restaurant.dao.NhanVienDAO;
import com.hk_restaurant.exception.ValidationException;
import com.hk_restaurant.model.dto.OrderDetailInput;
import com.hk_restaurant.model.entity.BanAn;
import com.hk_restaurant.model.entity.ChiTietDonHang;
import com.hk_restaurant.model.entity.DonHang;
import com.hk_restaurant.model.entity.HoaDon;
import com.hk_restaurant.model.entity.MonAn;
import com.hk_restaurant.model.entity.NhanVien;
import com.hk_restaurant.model.enumtype.OrderStatus;
import com.hk_restaurant.model.enumtype.PaymentMethod;
import com.hk_restaurant.model.enumtype.TableStatus;
import com.hk_restaurant.service.DonHangService;
import com.hk_restaurant.util.CodeGeneratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

/**
 * Lớp cài đặt nghiệp vụ cho phân hệ đơn hàng và thanh toán.
 * Đây là lớp xử lý chính cho quy trình mở bàn, thêm món, cập nhật đơn và thanh toán hóa đơn.
 */
public class DonHangServiceImpl implements DonHangService {

    private static final Logger log = LoggerFactory.getLogger(DonHangServiceImpl.class);

    private final DonHangDAO donHangDAO;
    private final BanAnDAO banAnDAO;
    private final MonAnDAO monAnDAO;
    private final HoaDonDAO hoaDonDAO;
    private final NhanVienDAO nhanVienDAO;

    /**
     * Khởi tạo service đơn hàng với đầy đủ các DAO cần cho nghiệp vụ POS.
     *
     * @param donHangDAO DAO đơn hàng
     * @param banAnDAO DAO bàn ăn
     * @param monAnDAO DAO món ăn
     * @param hoaDonDAO DAO hóa đơn
     * @param nhanVienDAO DAO nhân viên
     */
    public DonHangServiceImpl(DonHangDAO donHangDAO,
                              BanAnDAO banAnDAO,
                              MonAnDAO monAnDAO,
                              HoaDonDAO hoaDonDAO,
                              NhanVienDAO nhanVienDAO) {
        this.donHangDAO = donHangDAO;
        this.banAnDAO = banAnDAO;
        this.monAnDAO = monAnDAO;
        this.hoaDonDAO = hoaDonDAO;
        this.nhanVienDAO = nhanVienDAO;
    }

    /**
     * Mở bàn mới để bắt đầu phục vụ khách.
     * Nếu bàn đã có đơn đang phục vụ thì trả về đơn đó để tránh tạo trùng.
     *
     * @param maBan mã bàn ăn
     * @param maNhanVien mã nhân viên phục vụ
     * @param ghiChu ghi chú chung của đơn hàng
     * @return đơn hàng đang phục vụ của bàn
     */
    @Override
    public DonHang moBanMoi(Long maBan, Long maNhanVien, String ghiChu) {
        if (maBan == null) {
            throw new ValidationException("Thiếu mã bàn ăn!");
        }
        if (maNhanVien == null) {
            throw new ValidationException("Thiếu mã nhân viên phục vụ!");
        }

        BanAn banAn = banAnDAO.findById(maBan)
                .orElseThrow(() -> new ValidationException("Không tìm thấy bàn ăn!"));

        if (Boolean.FALSE.equals(banAn.getDangSuDung())) {
            throw new ValidationException("Bàn này đang bị khóa, không thể mở đơn!");
        }

        if (banAn.getTrangThai() == TableStatus.SERVING) {
            List<DonHang> ds = donHangDAO.findByBanAnIdAndTrangThai(maBan, OrderStatus.SERVING);
            if (!ds.isEmpty()) {
                log.info("Ban {} da co don dang phuc vu, tra ve don hien tai", maBan);
                return donHangDAO.findByIdWithDetails(ds.get(0).getId())
                        .orElseThrow(() -> new ValidationException("Không tải được đơn hàng hiện tại!"));
            }

            banAn.setTrangThai(TableStatus.EMPTY);
            banAnDAO.update(banAn);
        }

        NhanVien nv = nhanVienDAO.findById(maNhanVien)
                .orElseThrow(() -> new ValidationException("Không tìm thấy nhân viên phục vụ!"));

        if (Boolean.TRUE.equals(nv.getDaNghiViec())) {
            throw new ValidationException("Nhân viên này đã nghỉ việc, không thể phục vụ bàn!");
        }

        if (Boolean.FALSE.equals(nv.getDangLamViec())) {
            throw new ValidationException("Nhân viên này đang nghỉ làm, không thể phục vụ bàn!");
        }

        banAn.setTrangThai(TableStatus.SERVING);
        banAnDAO.update(banAn);

        DonHang donHang = new DonHang();
        donHang.setSoDonHang(CodeGeneratorUtils.taoSoDonHang());
        donHang.setBanAn(banAn);
        donHang.setNhanVien(nv);
        donHang.setGhiChu(ghiChu);
        donHang.setTrangThai(OrderStatus.SERVING);
        donHang.setTongTien(BigDecimal.ZERO);
        donHang.setThoiGianLap(LocalDateTime.now());

        log.info("Mo ban moi: maBan={}, maNhanVien={}", maBan, maNhanVien);
        return donHangDAO.save(donHang);
    }

    /**
     * Thêm món ăn vào đơn hàng đang phục vụ.
     * Nếu món đã tồn tại trong đơn thì cộng dồn số lượng thay vì tạo dòng mới.
     *
     * @param maDonHang mã đơn hàng
     * @param input dữ liệu đầu vào của món ăn cần thêm
     * @return đơn hàng sau khi cập nhật
     */
    @Override
    public DonHang themMonVaoDon(Long maDonHang, OrderDetailInput input) {
        if (maDonHang == null) {
            throw new ValidationException("Thiếu mã đơn hàng!");
        }
        if (input == null) {
            throw new ValidationException("Dữ liệu thêm món không hợp lệ!");
        }
        if (input.getDishId() == null) {
            throw new ValidationException("Thiếu mã món ăn!");
        }
        if (input.getQuantity() <= 0) {
            throw new ValidationException("Số lượng phải lớn hơn 0!");
        }
        if (input.getUnitPrice() == null || input.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Đơn giá không hợp lệ!");
        }

        DonHang donHang = donHangDAO.findByIdWithDetails(maDonHang)
                .orElseThrow(() -> new ValidationException("Không tìm thấy đơn hàng!"));

        if (donHang.getTrangThai() != OrderStatus.SERVING) {
            throw new ValidationException("Đơn hàng này không còn ở trạng thái phục vụ!");
        }

        MonAn monAn = monAnDAO.findById(input.getDishId())
                .orElseThrow(() -> new ValidationException("Không tìm thấy món ăn!"));

        if (Boolean.FALSE.equals(monAn.getDangKinhDoanh())) {
            throw new ValidationException("Món này đã ngừng kinh doanh!");
        }

        ChiTietDonHang chiTietDaCo = null;
        for (ChiTietDonHang ct : donHang.getDanhSachChiTiet()) {
            if (ct.getMonAn() != null && ct.getMonAn().getId().equals(monAn.getId())) {
                chiTietDaCo = ct;
                break;
            }
        }

        if (chiTietDaCo != null) {
            int soLuongMoi = chiTietDaCo.getSoLuong() + input.getQuantity();
            chiTietDaCo.setSoLuong(soLuongMoi);
            chiTietDaCo.setDonGia(input.getUnitPrice());
            chiTietDaCo.setThanhTien(input.getUnitPrice().multiply(BigDecimal.valueOf(soLuongMoi)));
            if (input.getNote() != null) {
                chiTietDaCo.setGhiChu(input.getNote().trim());
            }
        } else {
            ChiTietDonHang chiTiet = new ChiTietDonHang();
            chiTiet.setMonAn(monAn);
            chiTiet.setSoLuong(input.getQuantity());
            chiTiet.setDonGia(input.getUnitPrice());
            chiTiet.setThanhTien(input.getUnitPrice().multiply(BigDecimal.valueOf(input.getQuantity())));
            chiTiet.setGhiChu(input.getNote() != null ? input.getNote().trim() : "");

            donHang.addChiTiet(chiTiet);
        }

        tinhLaiTongTien(donHang);
        log.info("Them mon vao don: maDonHang={}, maMon={}", maDonHang, input.getDishId());
        return donHangDAO.update(donHang);
    }

    /**
     * Lấy đơn hàng đang phục vụ của một bàn.
     * Nếu bàn bị lệch trạng thái với dữ liệu đơn hàng thì service sẽ tự đồng bộ lại.
     *
     * @param maBan mã bàn ăn
     * @return đơn hàng đang phục vụ hoặc null nếu chưa có đơn
     */
    @Override
    public DonHang layDonDangPhucVuTheoBan(Long maBan) {
        if (maBan == null) {
            throw new ValidationException("Thiếu mã bàn ăn!");
        }

        BanAn banAn = banAnDAO.findById(maBan)
                .orElseThrow(() -> new ValidationException("Không tìm thấy bàn ăn!"));

        List<DonHang> ds = donHangDAO.findByBanAnIdAndTrangThai(maBan, OrderStatus.SERVING);

        if (ds.isEmpty()) {
            if (banAn.getTrangThai() == TableStatus.SERVING) {
                banAn.setTrangThai(TableStatus.EMPTY);
                banAnDAO.update(banAn);
            }
            return null;
        }

        if (banAn.getTrangThai() != TableStatus.SERVING) {
            banAn.setTrangThai(TableStatus.SERVING);
            banAnDAO.update(banAn);
        }

        return donHangDAO.findByIdWithDetails(ds.get(0).getId())
                .orElseThrow(() -> new ValidationException("Không tải được đơn đang phục vụ!"));
    }

    /**
     * Cập nhật số lượng món trong đơn hàng.
     * Nếu số lượng mới nhỏ hơn hoặc bằng 0 thì xóa món ra khỏi đơn.
     *
     * @param maDonHang mã đơn hàng
     * @param maMon mã món ăn
     * @param soLuongMoi số lượng mới
     * @return đơn hàng sau khi cập nhật
     */
    @Override
    public DonHang capNhatSoLuongMon(Long maDonHang, Long maMon, int soLuongMoi) {
        if (maDonHang == null) {
            throw new ValidationException("Thiếu mã đơn hàng!");
        }
        if (maMon == null) {
            throw new ValidationException("Thiếu mã món ăn!");
        }

        DonHang donHang = donHangDAO.findByIdWithDetails(maDonHang)
                .orElseThrow(() -> new ValidationException("Không tìm thấy đơn hàng!"));

        if (donHang.getTrangThai() != OrderStatus.SERVING) {
            throw new ValidationException("Đơn hàng này không thể chỉnh sửa!");
        }

        ChiTietDonHang canSua = null;
        for (ChiTietDonHang ct : donHang.getDanhSachChiTiet()) {
            if (ct.getMonAn() != null && ct.getMonAn().getId().equals(maMon)) {
                canSua = ct;
                break;
            }
        }

        if (canSua == null) {
            throw new ValidationException("Không tìm thấy món trong đơn!");
        }

        if (soLuongMoi <= 0) {
            donHang.removeChiTiet(canSua);
        } else {
            canSua.setSoLuong(soLuongMoi);
            canSua.setThanhTien(canSua.getDonGia().multiply(BigDecimal.valueOf(soLuongMoi)));
        }

        tinhLaiTongTien(donHang);
        log.info("Cap nhat so luong mon: maDonHang={}, maMon={}, soLuongMoi={}", maDonHang, maMon, soLuongMoi);
        return donHangDAO.update(donHang);
    }

    /**
     * Thanh toán đơn hàng và phát sinh hóa đơn tương ứng.
     *
     * @param maDonHang mã đơn hàng
     * @param phuongThuc phương thức thanh toán
     * @return hóa đơn đã tạo
     */
    @Override
    public HoaDon thanhToanDonHang(Long maDonHang, PaymentMethod phuongThuc) {
        if (maDonHang == null) {
            throw new ValidationException("Thiếu mã đơn hàng!");
        }
        if (phuongThuc == null) {
            throw new ValidationException("Vui lòng chọn phương thức thanh toán!");
        }

        DonHang donHang = donHangDAO.findByIdWithDetails(maDonHang)
                .orElseThrow(() -> new ValidationException("Không tìm thấy đơn hàng!"));

        if (donHang.getTrangThai() == OrderStatus.PAID) {
            throw new ValidationException("Đơn hàng này đã được thanh toán rồi!");
        }

        if (donHang.getTrangThai() == OrderStatus.CANCELLED) {
            throw new ValidationException("Đơn hàng này đã bị hủy!");
        }

        if (donHang.getDanhSachChiTiet() == null || donHang.getDanhSachChiTiet().isEmpty()) {
            throw new ValidationException("Đơn hàng chưa có món nào, không thể thanh toán!");
        }

        tinhLaiTongTien(donHang);

        donHang.setTrangThai(OrderStatus.PAID);
        DonHang updatedOrder = donHangDAO.update(donHang);

        BanAn banAn = updatedOrder.getBanAn();
        if (banAn != null) {
            banAn.setTrangThai(TableStatus.EMPTY);
            banAnDAO.update(banAn);
        }

        HoaDon hoaDon = new HoaDon();
        hoaDon.setSoHoaDon(CodeGeneratorUtils.taoSoHoaDon());
        hoaDon.setDonHang(updatedOrder);
        hoaDon.setTongTien(updatedOrder.getTongTien());
        hoaDon.setPhuongThucThanhToan(phuongThuc);
        hoaDon.setThoiGianThanhToan(LocalDateTime.now());

        log.info("Thanh toan don hang: maDonHang={}, phuongThuc={}", maDonHang, phuongThuc);
        return hoaDonDAO.save(hoaDon);
    }

    /**
     * Cập nhật ghi chú riêng của một món trong đơn hàng.
     *
     * @param maDonHang mã đơn hàng
     * @param maMon mã món ăn
     * @param ghiChuMoi ghi chú mới
     * @return đơn hàng sau khi cập nhật
     */
    @Override
    public DonHang capNhatGhiChuMon(Long maDonHang, Long maMon, String ghiChuMoi) {
        if (maDonHang == null) {
            throw new ValidationException("Thiếu mã đơn hàng!");
        }
        if (maMon == null) {
            throw new ValidationException("Thiếu mã món ăn!");
        }

        DonHang donHang = donHangDAO.findByIdWithDetails(maDonHang)
                .orElseThrow(() -> new ValidationException("Không tìm thấy đơn hàng!"));

        if (donHang.getTrangThai() != OrderStatus.SERVING) {
            throw new ValidationException("Chỉ được cập nhật ghi chú khi đơn đang phục vụ!");
        }

        boolean found = false;
        for (ChiTietDonHang ct : donHang.getDanhSachChiTiet()) {
            if (ct.getMonAn() != null && ct.getMonAn().getId().equals(maMon)) {
                ct.setGhiChu(ghiChuMoi != null ? ghiChuMoi.trim() : "");
                found = true;
                break;
            }
        }

        if (!found) {
            throw new ValidationException("Không tìm thấy món trong đơn hàng!");
        }

        log.info("Cap nhat ghi chu mon: maDonHang={}, maMon={}", maDonHang, maMon);
        return donHangDAO.update(donHang);
    }

    /**
     * Đổi nhân viên đang phục vụ cho bàn hiện tại.
     *
     * @param maBan mã bàn ăn
     * @param maNhanVienMoi mã nhân viên mới
     * @return đơn hàng sau khi cập nhật nhân viên phục vụ
     */
    @Override
    public DonHang capNhatNhanVienPhucVuTheoBan(Long maBan, Long maNhanVienMoi) {
        if (maBan == null) {
            throw new ValidationException("Thiếu mã bàn ăn!");
        }
        if (maNhanVienMoi == null) {
            throw new ValidationException("Thiếu mã nhân viên mới!");
        }

        BanAn banAn = banAnDAO.findById(maBan)
                .orElseThrow(() -> new ValidationException("Không tìm thấy bàn ăn!"));

        if (Boolean.FALSE.equals(banAn.getDangSuDung())) {
            throw new ValidationException("Bàn này đang bị khóa!");
        }

        DonHang donDangPhucVu = layDonDangPhucVuTheoBan(maBan);
        if (donDangPhucVu == null) {
            throw new ValidationException("Bàn này hiện chưa có đơn đang phục vụ để đổi nhân viên!");
        }

        NhanVien nvMoi = nhanVienDAO.findById(maNhanVienMoi)
                .orElseThrow(() -> new ValidationException("Không tìm thấy nhân viên mới!"));

        if (Boolean.TRUE.equals(nvMoi.getDaNghiViec())) {
            throw new ValidationException("Nhân viên này đã nghỉ việc, không thể gán phục vụ!");
        }

        if (Boolean.FALSE.equals(nvMoi.getDangLamViec())) {
            throw new ValidationException("Nhân viên này đang nghỉ làm, không thể gán phục vụ!");
        }

        donDangPhucVu.setNhanVien(nvMoi);

        log.info("Doi nhan vien phuc vu: maBan={}, maNhanVienMoi={}", maBan, maNhanVienMoi);
        return donHangDAO.update(donDangPhucVu);
    }

    /**
     * Tính lại tổng tiền đơn hàng từ toàn bộ các dòng chi tiết món ăn.
     *
     * @param donHang đơn hàng cần tính lại tổng tiền
     */
    private void tinhLaiTongTien(DonHang donHang) {
        BigDecimal tong = BigDecimal.ZERO;
        if (donHang.getDanhSachChiTiet() != null) {
            for (ChiTietDonHang ct : donHang.getDanhSachChiTiet()) {
                if (ct.getThanhTien() != null) {
                    tong = tong.add(ct.getThanhTien());
                }
            }
        }
        donHang.setTongTien(tong);
    }
}
