package com.hk_restaurant.controller;

import com.hk_restaurant.model.dto.DishSalesDTO;
import com.hk_restaurant.model.dto.EmployeeRevenueDTO;
import com.hk_restaurant.model.dto.MonthlyRevenueDTO;
import com.hk_restaurant.service.ReportService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller cho phân hệ báo cáo.
 * Lớp này tiếp nhận yêu cầu từ giao diện và gọi xuống tầng service thống kê.
 */
public class ReportController {

    private final ReportService reportService;

    /**
     * Khởi tạo controller báo cáo.
     *
     * @param reportService service xử lý nghiệp vụ báo cáo
     */
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Lấy doanh thu của một ngày.
     *
     * @param date ngày cần thống kê
     * @return doanh thu trong ngày
     */
    public BigDecimal layDoanhThuNgay(LocalDate date) {
        return reportService.layDoanhThuNgay(date);
    }

    /**
     * Lấy doanh thu theo từng tháng của một năm.
     *
     * @param year năm cần thống kê
     * @return danh sách doanh thu theo tháng
     */
    public List<MonthlyRevenueDTO> layDoanhThuTheoThang(int year) {
        return reportService.layDoanhThuTheoThang(year);
    }

    /**
     * Lấy doanh thu của một tháng cụ thể trong năm.
     *
     * @param year năm cần thống kê
     * @param month tháng cần thống kê
     * @return danh sách doanh thu của tháng đã chọn
     */
    public List<MonthlyRevenueDTO> layDoanhThuTheoThang(int year, int month) {
        return reportService.layDoanhThuTheoThang(year, month);
    }

    /**
     * Lấy top món bán chạy trong khoảng thời gian chỉ định.
     *
     * @param startDate thời gian bắt đầu
     * @param endDate thời gian kết thúc
     * @param limit số lượng món cần lấy
     * @return danh sách món bán chạy
     */
    public List<DishSalesDTO> layTopMonBanChay(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        return reportService.layTopMonBanChay(startDate, endDate, limit);
    }

    /**
     * Lấy doanh thu theo từng nhân viên.
     *
     * @param startDate thời gian bắt đầu
     * @param endDate thời gian kết thúc
     * @return danh sách doanh thu theo nhân viên
     */
    public List<EmployeeRevenueDTO> layDoanhThuTheoNhanVien(LocalDateTime startDate, LocalDateTime endDate) {
        return reportService.layDoanhThuTheoNhanVien(startDate, endDate);
    }

    /**
     * Tính giá trị hóa đơn trung bình trong khoảng thời gian.
     *
     * @param startDate thời gian bắt đầu
     * @param endDate thời gian kết thúc
     * @return giá trị hóa đơn trung bình
     */
    public BigDecimal layGiaTriHoaDonTrungBinh(LocalDateTime startDate, LocalDateTime endDate) {
        return reportService.layGiaTriHoaDonTrungBinh(startDate, endDate);
    }
}
