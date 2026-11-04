package com.hk_restaurant.service.impl;

import com.hk_restaurant.dao.ReportDAO;
import com.hk_restaurant.model.dto.DishSalesDTO;
import com.hk_restaurant.model.dto.EmployeeRevenueDTO;
import com.hk_restaurant.model.dto.MonthlyRevenueDTO;
import com.hk_restaurant.service.ReportService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Lớp cài đặt nghiệp vụ cho phân hệ báo cáo.
 * Hiện tại service chủ yếu đóng vai trò trung gian để thống nhất kiến trúc nhiều lớp,
 * đồng thời là nơi thuận lợi để mở rộng validation hay cache báo cáo trong tương lai.
 */
public class ReportServiceImpl implements ReportService {

    private final ReportDAO reportDAO;

    /**
     * Khởi tạo service báo cáo với DAO tương ứng.
     *
     * @param reportDAO DAO phục vụ truy vấn thống kê
     */
    public ReportServiceImpl(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    /**
     * Tính tổng doanh thu của một ngày.
     *
     * @param date ngày cần thống kê
     * @return doanh thu trong ngày
     */
    @Override
    public BigDecimal layDoanhThuNgay(LocalDate date) {
        return reportDAO.getDailyRevenue(date);
    }

    /**
     * Lấy doanh thu theo từng tháng của một năm.
     *
     * @param year năm cần thống kê
     * @return danh sách doanh thu theo tháng
     */
    @Override
    public List<MonthlyRevenueDTO> layDoanhThuTheoThang(int year) {
        return reportDAO.getRevenueByMonth(year);
    }

    /**
     * Lấy doanh thu theo tháng cụ thể.
     *
     * @param year năm cần thống kê
     * @param month tháng cần thống kê
     * @return danh sách doanh thu của tháng được chọn
     */
    @Override
    public List<MonthlyRevenueDTO> layDoanhThuTheoThang(int year, int month) {
        return reportDAO.getRevenueByMonth(year, month);
    }

    /**
     * Lấy top món bán chạy trong khoảng thời gian chỉ định.
     *
     * @param startDate thời gian bắt đầu
     * @param endDate thời gian kết thúc
     * @param limit số lượng món cần lấy
     * @return danh sách món bán chạy
     */
    @Override
    public List<DishSalesDTO> layTopMonBanChay(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        return reportDAO.getTopSellingDishes(startDate, endDate, limit);
    }

    /**
     * Lấy doanh thu theo nhân viên.
     *
     * @param startDate thời gian bắt đầu
     * @param endDate thời gian kết thúc
     * @return danh sách doanh thu theo nhân viên
     */
    @Override
    public List<EmployeeRevenueDTO> layDoanhThuTheoNhanVien(LocalDateTime startDate, LocalDateTime endDate) {
        return reportDAO.getRevenueByEmployee(startDate, endDate);
    }

    /**
     * Tính giá trị trung bình của hóa đơn trong khoảng thời gian.
     *
     * @param startDate thời gian bắt đầu
     * @param endDate thời gian kết thúc
     * @return giá trị hóa đơn trung bình
     */
    @Override
    public BigDecimal layGiaTriHoaDonTrungBinh(LocalDateTime startDate, LocalDateTime endDate) {
        return reportDAO.getAverageInvoiceValue(startDate, endDate);
    }
}
