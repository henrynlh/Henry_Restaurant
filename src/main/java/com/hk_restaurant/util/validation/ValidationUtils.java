package com.hk_restaurant.util.validation;

import java.math.BigDecimal;

/**
 * Lớp tiện ích dùng để kiểm tra và chuyển đổi dữ liệu đầu vào.
 *
 * <p>Lớp này cung cấp các hàm kiểm tra chuỗi rỗng, ép kiểu số nguyên,
 * số tiền và xác thực số điện thoại nhằm hỗ trợ các form nhập liệu
 * trong chương trình.</p>
 */
public final class ValidationUtils {
    /**
     * Constructor riêng để ngăn việc khởi tạo lớp tiện ích.
     */
    private ValidationUtils() {
    }

    /**
     * Kiểm tra một chuỗi có rỗng hoặc chỉ chứa khoảng trắng hay không.
     *
     * @param value chuỗi cần kiểm tra
     * @return true nếu chuỗi rỗng hoặc null, ngược lại trả về false
     */
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Yêu cầu một chuỗi bắt buộc không được để trống.
     *
     * @param value giá trị cần kiểm tra
     * @param message thông báo lỗi nếu giá trị không hợp lệ
     */
    public static void requireNotBlank(String value, String message) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Chuyển chuỗi sang số nguyên dương.
     *
     * @param value chuỗi đầu vào
     * @param message thông báo lỗi khi dữ liệu không hợp lệ
     * @return số nguyên dương sau khi chuyển đổi thành công
     */
    public static int parsePositiveInt(String value, String message) {
        try {
            int n = Integer.parseInt(value.trim());
            if (n <= 0) {
                throw new IllegalArgumentException(message);
            }
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Chuyển chuỗi sang giá trị tiền tệ dương kiểu BigDecimal.
     *
     * @param value chuỗi đầu vào
     * @param message thông báo lỗi khi dữ liệu không hợp lệ
     * @return giá trị tiền hợp lệ
     */
    public static BigDecimal parsePositiveMoney(String value, String message) {
        try {
            BigDecimal money = new BigDecimal(value.trim());
            if (money.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(message);
            }
            return money;
        } catch (Exception e) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Kiểm tra tính hợp lệ của số điện thoại Việt Nam.
     *
     * <p>Nếu người dùng bỏ trống số điện thoại thì phương thức vẫn chấp nhận
     * để phù hợp với một số luồng nhập liệu tùy chọn.</p>
     *
     * @param phone số điện thoại cần kiểm tra
     * @return true nếu hợp lệ hoặc để trống, ngược lại trả về false
     */
    public static boolean isPhoneValid(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true;
        }
        return phone.matches("^0\\d{9,10}$");
    }
}