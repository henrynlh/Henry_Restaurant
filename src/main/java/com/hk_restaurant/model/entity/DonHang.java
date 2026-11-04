package com.hk_restaurant.model.entity;

import com.hk_restaurant.model.enumtype.OrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity biểu diễn đơn hàng của một bàn ăn trong nhà hàng.
 * Một đơn hàng gắn với một bàn ăn, một nhân viên và nhiều chi tiết món ăn.
 */
@Entity
@Table(name = "DonHang")
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDonHang")
    private Long id;

    @Column(name = "SoDonHang", unique = true, nullable = false, length = 50)
    private String soDonHang;

    /**
     * Quan hệ nhiều - một với BanAn:
     * nhiều đơn hàng có thể phát sinh tại cùng một bàn theo thời gian.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaBan", nullable = false)
    private BanAn banAn;

    /**
     * Quan hệ nhiều - một với NhanVien:
     * nhiều đơn hàng có thể do cùng một nhân viên phụ trách.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNhanVien", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "ThoiGianLap", nullable = false)
    private LocalDateTime thoiGianLap = LocalDateTime.now();

    @Column(name = "TongTien", nullable = false)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", nullable = false, length = 50)
    private OrderStatus trangThai = OrderStatus.SERVING;

    @Column(name = "GhiChu")
    private String ghiChu;

    /**
     * Quan hệ một - nhiều với ChiTietDonHang:
     * một đơn hàng có thể chứa nhiều món ăn khác nhau.
     */
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietDonHang> danhSachChiTiet = new ArrayList<>();

    /**
     * Quan hệ một - một với HoaDon:
     * một đơn hàng sau khi thanh toán sẽ phát sinh tối đa một hóa đơn.
     */
    @OneToOne(mappedBy = "donHang", fetch = FetchType.LAZY)
    private HoaDon hoaDon;

    /**
     * Thêm một dòng chi tiết món ăn vào đơn hàng
     * và đồng bộ quan hệ hai chiều giữa DonHang và ChiTietDonHang.
     *
     * @param chiTiet chi tiết món ăn cần thêm
     */
    public void addChiTiet(ChiTietDonHang chiTiet) {
        danhSachChiTiet.add(chiTiet);
        chiTiet.setDonHang(this);
    }

    /**
     * Xóa một dòng chi tiết món ăn khỏi đơn hàng
     * và đồng bộ lại quan hệ hai chiều.
     *
     * @param chiTiet chi tiết cần xóa
     */
    public void removeChiTiet(ChiTietDonHang chiTiet) {
        danhSachChiTiet.remove(chiTiet);
        if (chiTiet != null) {
            chiTiet.setDonHang(null);
        }
    }

    /**
     * Lấy mã định danh của đơn hàng.
     *
     * @return id đơn hàng
     */
    public Long getId() {
        return id;
    }

    /**
     * Cập nhật mã định danh của đơn hàng.
     *
     * @param id id mới của đơn hàng
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Lấy số đơn hàng nghiệp vụ.
     *
     * @return số đơn hàng
     */
    public String getSoDonHang() {
        return soDonHang;
    }

    /**
     * Cập nhật số đơn hàng nghiệp vụ.
     *
     * @param soDonHang số đơn hàng mới
     */
    public void setSoDonHang(String soDonHang) {
        this.soDonHang = soDonHang;
    }

    /**
     * Lấy bàn ăn của đơn hàng.
     *
     * @return bàn ăn liên kết
     */
    public BanAn getBanAn() {
        return banAn;
    }

    /**
     * Gán bàn ăn cho đơn hàng.
     *
     * @param banAn bàn ăn liên kết
     */
    public void setBanAn(BanAn banAn) {
        this.banAn = banAn;
    }

    /**
     * Lấy nhân viên phụ trách đơn hàng.
     *
     * @return nhân viên liên kết
     */
    public NhanVien getNhanVien() {
        return nhanVien;
    }

    /**
     * Gán nhân viên phụ trách cho đơn hàng.
     *
     * @param nhanVien nhân viên liên kết
     */
    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    /**
     * Lấy thời gian lập đơn hàng.
     *
     * @return thời gian lập đơn
     */
    public LocalDateTime getThoiGianLap() {
        return thoiGianLap;
    }

    /**
     * Cập nhật thời gian lập đơn hàng.
     *
     * @param thoiGianLap thời gian lập mới
     */
    public void setThoiGianLap(LocalDateTime thoiGianLap) {
        this.thoiGianLap = thoiGianLap;
    }

    /**
     * Lấy tổng tiền của đơn hàng.
     *
     * @return tổng tiền đơn hàng
     */
    public BigDecimal getTongTien() {
        return tongTien;
    }

    /**
     * Cập nhật tổng tiền của đơn hàng.
     *
     * @param tongTien tổng tiền mới
     */
    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    /**
     * Lấy trạng thái hiện tại của đơn hàng.
     *
     * @return trạng thái đơn hàng
     */
    public OrderStatus getTrangThai() {
        return trangThai;
    }

    /**
     * Cập nhật trạng thái của đơn hàng.
     *
     * @param trangThai trạng thái mới
     */
    public void setTrangThai(OrderStatus trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * Lấy ghi chú chung của đơn hàng.
     *
     * @return ghi chú đơn hàng
     */
    public String getGhiChu() {
        return ghiChu;
    }

    /**
     * Cập nhật ghi chú chung của đơn hàng.
     *
     * @param ghiChu ghi chú mới
     */
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    /**
     * Lấy danh sách các chi tiết món ăn trong đơn hàng.
     *
     * @return danh sách chi tiết đơn hàng
     */
    public List<ChiTietDonHang> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    /**
     * Cập nhật toàn bộ danh sách chi tiết món ăn của đơn hàng.
     *
     * @param danhSachChiTiet danh sách chi tiết mới
     */
    public void setDanhSachChiTiet(List<ChiTietDonHang> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }

    /**
     * Lấy hóa đơn đã phát sinh của đơn hàng nếu có.
     *
     * @return hóa đơn liên kết
     */
    public HoaDon getHoaDon() {
        return hoaDon;
    }

    /**
     * Gán hóa đơn cho đơn hàng.
     *
     * @param hoaDon hóa đơn liên kết
     */
    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }
}
