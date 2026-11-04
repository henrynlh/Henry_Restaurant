package com.hk_restaurant.model.enumtype;

/**
 * Enum biểu diễn trạng thái hoạt động của bàn ăn trong nhà hàng.
 * Trạng thái này hỗ trợ theo dõi bàn trống và bàn đang phục vụ.
 */
public enum TableStatus {
    EMPTY,      // Bàn đang trống, chưa có khách
    SERVING     // Bàn đang có khách và đang phục vụ
}