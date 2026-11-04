package com.hk_restaurant.service;

import com.hk_restaurant.model.dto.DishSalesDTO;
import com.hk_restaurant.model.dto.EmployeeRevenueDTO;
import com.hk_restaurant.model.dto.MonthlyRevenueDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface service cho phân hệ báo cáo và thống kê.
 * Tầng này giúp controller không gọi trực tiếp DAO báo cáo.
 */
public interface ReportService {

    /**
     * Tính tổng doanh thu theo ngày.
     *
     * @param date ngày cần thống kê
     * @return doanh thu trong ngày
     */
    BigDecimal layDoanhThuNgay(LocalDate date);

    /**
     * Lấy doanh thu theo từng tháng của một năm.
     *
     * @param year năm cần thống kê
     * @return danh sách doanh thu theo tháng
     */
    List<MonthlyRevenueDTO> layDoanhThuTheoThang(int year);

    /**
     * Lấy doanh thu của một tháng cụ thể trong năm.
     *
     * @param year năm cần thống kê
     * @param month tháng cần thống kê
     * @return danh sách doanh thu theo tháng
     */
    List<MonthlyRevenueDTO> layDoanhThuTheoThang(int year, int month);

    /**
     * Lấy danh sách món bán chạy nhất trong khoảng thời gian.
     *
     * @param startDate thời gian bắt đầu
     * @param endDate thời gian kết thúc
     * @param limit số lượng món cần lấy
     * @return danh sách món bán chạy
     */
    List<DishSalesDTO> layTopMonBanChay(LocalDateTime startDate, LocalDateTime endDate, int limit);

    /**
     * Lấy doanh thu theo nhân viên.
     *
     * @param startDate thời gian bắt đầu
     * @param endDate thời gian kết thúc
     * @return danh sách doanh thu theo nhân viên
     */
    List<EmployeeRevenueDTO> layDoanhThuTheoNhanVien(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Tính giá trị hóa đơn trung bình.
     *
     * @param startDate thời gian bắt đầu
     * @param endDate thời gian kết thúc
     * @return giá trị hóa đơn trung bình
     */
    BigDecimal layGiaTriHoaDonTrungBinh(LocalDateTime startDate, LocalDateTime endDate);
}
