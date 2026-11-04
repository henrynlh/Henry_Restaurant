package com.hk_restaurant.exception;

/**
 * Exception dung cho cac loi xac thuc va dang nhap trong he thong.
 *
 * <p>Lop nay thuong duoc nem ra khi nguoi dung nhap sai ten dang nhap,
 * sai mat khau, tai khoan khong hop le hoac khong du dieu kien de truy cap
 * vao chuc nang cua chuong trinh.</p>
 */
public class AuthenticationException extends BusinessException {

    /**
     * Khoi tao ngoai le xac thuc voi thong diep mo ta loi.
     *
     * @param message noi dung thong bao loi xac thuc
     */
    public AuthenticationException(String message) {
        super(message);
    }
}
