package com.hk_restaurant.constant;

/**
 * Lớp chứa các hằng số dùng chung trong toàn bộ ứng dụng.
 */
public final class AppConstants {
    private AppConstants() {
    }

    public static final String APP_NAME = "HK Restaurant";
    public static final String SHOP_NAME = "HK RESTAURANT";
    public static final String SHOP_BRANCH = "Nhà hàng HK - Cơ sở 1";
    public static final String SHOP_ADDRESS = "Số 1 Võ Văn Ngân, Thủ Đức, TP HCM";
    public static final String SHOP_PHONE = "1900 1508";

    /**
     * Mật khẩu root dùng cho mục đích demo và chấm đồ án.
     * Nếu triển khai thực tế, cần chuyển sang file cấu hình hoặc biến môi trường.
     */
    public static final String DEMO_ROOT_PASSWORD = "123456";

    /**
     * Giữ lại hằng số cũ để tránh làm vỡ các đoạn mã legacy chưa refactor hết.
     */
    @Deprecated
    public static final String DEFAULT_ROOT_PASSWORD = DEMO_ROOT_PASSWORD;
}
