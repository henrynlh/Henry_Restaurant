package com.hk_restaurant.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lớp tiện ích sinh mã nghiệp vụ cho hệ thống.
 * Việc tách riêng logic sinh mã giúp service gọn hơn và dễ chuẩn hóa định dạng.
 */
public final class CodeGeneratorUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    /**
     * Constructor private để ngăn tạo instance từ lớp tiện ích.
     */
    private CodeGeneratorUtils() {
    }

    /**
     * Sinh số đơn hàng nghiệp vụ theo tiền tố ORD.
     *
     * @return số đơn hàng mới
     */
    public static String taoSoDonHang() {
        return taoMa("ORD");
    }

    /**
     * Sinh số hóa đơn nghiệp vụ theo tiền tố INV.
     *
     * @return số hóa đơn mới
     */
    public static String taoSoHoaDon() {
        return taoMa("INV");
    }

    /**
     * Hàm nội bộ dùng chung để tạo mã theo tiền tố.
     * Kết hợp timestamp và bộ đếm ngắn để hạn chế va chạm khi sinh mã liên tiếp.
     *
     * @param prefix tiền tố nghiệp vụ
     * @return mã nghiệp vụ đã định dạng
     */
    private static String taoMa(String prefix) {
        int suffix = COUNTER.updateAndGet(value -> (value + 1) % 1000);
        return prefix + "-" + LocalDateTime.now().format(FORMATTER) + String.format("%03d", suffix);
    }
}
