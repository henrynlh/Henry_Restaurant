package com.hk_restaurant.model.dto;

import java.math.BigDecimal;

/**
 * DTO dùng để chứa dữ liệu thống kê doanh thu theo nhân viên.
 *
 * @param employeeCode mã nhân viên
 * @param fullName họ tên nhân viên
 * @param revenue tổng doanh thu do nhân viên phụ trách
 */
public record EmployeeRevenueDTO(
        String employeeCode,
        String fullName,
        BigDecimal revenue
) {}