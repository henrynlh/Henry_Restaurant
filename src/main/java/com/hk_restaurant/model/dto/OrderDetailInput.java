package com.hk_restaurant.model.dto;

import java.math.BigDecimal;

/**
 * DTO dùng để tiếp nhận dữ liệu đầu vào khi thêm món ăn vào đơn hàng.
 * Lớp này thường được sử dụng giữa View và Service trong quá trình đặt món.
 *
 * <p>Lưu ý nghiệp vụ: {@code unitPrice} chỉ mang tính chất dữ liệu tham chiếu từ giao diện.
 * Tầng service sẽ luôn lấy đơn giá chính thức từ {@code MonAn} trong cơ sở dữ liệu để tính tiền,
 * tránh trường hợp UI gửi sai giá hoặc bị chỉnh sửa ngoài ý muốn.</p>
 */
public class OrderDetailInput {
    private Long dishId;
    private int quantity;
    private BigDecimal unitPrice;
    private String note;

    /**
     * Constructor mặc định.
     */
    public OrderDetailInput() {
    }

    /**
     * Constructor dùng để khởi tạo đầy đủ thông tin chi tiết món ăn đầu vào.
     *
     * @param dishId mã món ăn
     * @param quantity số lượng món
     * @param unitPrice đơn giá tham chiếu từ giao diện
     * @param note ghi chú thêm cho món ăn
     */
    public OrderDetailInput(Long dishId, int quantity, BigDecimal unitPrice, String note) {
        this.dishId = dishId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.note = note;
    }

    /**
     * Lấy mã món ăn.
     *
     * @return mã món ăn
     */
    public Long getDishId() {
        return dishId;
    }

    /**
     * Cập nhật mã món ăn.
     *
     * @param dishId mã món ăn mới
     */
    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    /**
     * Lấy số lượng món ăn.
     *
     * @return số lượng món
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Cập nhật số lượng món ăn.
     *
     * @param quantity số lượng mới
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Lấy đơn giá tham chiếu từ giao diện.
     *
     * @return đơn giá tham chiếu
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * Cập nhật đơn giá tham chiếu từ giao diện.
     *
     * @param unitPrice đơn giá tham chiếu mới
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * Lấy ghi chú của món ăn.
     *
     * @return ghi chú món ăn
     */
    public String getNote() {
        return note;
    }

    /**
     * Cập nhật ghi chú của món ăn.
     *
     * @param note ghi chú mới
     */
    public void setNote(String note) {
        this.note = note;
    }
}
