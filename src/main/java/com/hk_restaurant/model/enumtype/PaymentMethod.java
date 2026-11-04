package com.hk_restaurant.model.enumtype;

/**
 * Enum biểu diễn phương thức thanh toán của hóa đơn.
 * Hệ thống hiện hỗ trợ thanh toán bằng tiền mặt và chuyển khoản.
 */
public enum PaymentMethod {
    CASH,           // Thanh toán bằng tiền mặt
    BANK_TRANSFER   // Thanh toán bằng chuyển khoản
}