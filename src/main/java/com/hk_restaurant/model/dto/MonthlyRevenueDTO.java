package com.hk_restaurant.model.dto;

import java.math.BigDecimal;

/**
 * DTO dùng để chứa dữ liệu doanh thu theo tháng.
 *
 * @param year năm thống kê
 * @param month tháng thống kê
 * @param revenue tổng doanh thu của tháng
 */
public record MonthlyRevenueDTO(
        Integer year,
        Integer month,
        BigDecimal revenue
) {}