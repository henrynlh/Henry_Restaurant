package com.hk_restaurant.model.enumtype;

/**
 * Enum biểu diễn trạng thái của đơn hàng trong hệ thống.
 * Trạng thái này được sử dụng để theo dõi quá trình phục vụ và thanh toán.
 */
public enum OrderStatus {
    SERVING,    // Đơn hàng đang được phục vụ
    PAID,       // Đơn hàng đã thanh toán xong
    CANCELLED   // Đơn hàng đã bị hủy
}