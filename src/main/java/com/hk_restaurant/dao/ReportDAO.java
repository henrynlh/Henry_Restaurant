package com.hk_restaurant.dao;

import com.hk_restaurant.model.dto.DishSalesDTO;
import com.hk_restaurant.model.dto.EmployeeRevenueDTO;
import com.hk_restaurant.model.dto.MonthlyRevenueDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface DAO cho phân hệ báo cáo và thống kê.
 * Khai báo các phương thức truy vấn dữ liệu tổng hợp phục vụ dashboard báo cáo.
 */
public interface ReportDAO {

    /**
     * Tính tổng doanh thu của một ngày cụ thể.
     *
     * @param date ngày cần thống kê
     * @return tổng doanh thu trong ngày
     */
    BigDecimal getDailyRevenue(LocalDate date);

    /**
     * Lấy doanh thu theo từng tháng của một năm.
     *
     * @param year năm cần thống kê
     * @return danh sách doanh thu theo tháng
     */
    List<MonthlyRevenueDTO> getRevenueByMonth(int year);

    /**
     * Lấy doanh thu của một tháng cụ thể trong năm.
     *
     * @param year năm cần thống kê
     * @param month tháng cần thống kê
     * @return danh sách doanh thu theo tháng
     */
    List<MonthlyRevenueDTO> getRevenueByMonth(int year, int month);

    /**
     * Lấy danh sách các món bán chạy nhất trong khoảng thời gian chỉ định.
     *
     * @param startDate thời điểm bắt đầu
     * @param endDate thời điểm kết thúc
     * @param limit số lượng món cần lấy
     * @return danh sách món bán chạy
     */
    List<DishSalesDTO> getTopSellingDishes(LocalDateTime startDate, LocalDateTime endDate, int limit);

    /**
     * Lấy doanh thu theo từng nhân viên trong khoảng thời gian chỉ định.
     *
     * @param startDate thời điểm bắt đầu
     * @param endDate thời điểm kết thúc
     * @return danh sách doanh thu theo nhân viên
     */
    List<EmployeeRevenueDTO> getRevenueByEmployee(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Tính giá trị trung bình của hóa đơn trong khoảng thời gian chỉ định.
     *
     * @param startDate thời điểm bắt đầu
     * @param endDate thời điểm kết thúc
     * @return giá trị hóa đơn trung bình
     */
    BigDecimal getAverageInvoiceValue(LocalDateTime startDate, LocalDateTime endDate);
    }