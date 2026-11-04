package com.hk_restaurant.model.dto;

import java.math.BigDecimal;

/**
 * DTO dùng để chứa dữ liệu thống kê món ăn bán chạy.
 *
 * @param sku mã món ăn
 * @param dishName tên món ăn
 * @param totalQty tổng số lượng đã bán
 * @param revenue tổng doanh thu của món ăn
 */
public record DishSalesDTO(
        String sku,
        String dishName,
        Long totalQty,
        BigDecimal revenue
) {}