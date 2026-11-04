package com.hk_restaurant.exception;

/**
 * Exception nghiep vu co so cua he thong.
 *
 * <p>Lop nay duoc dung lam cha cho cac ngoai le phat sinh tu tang
 * service hoac controller khi xay ra loi nghiep vu trong chuong trinh,
 * vi du nhu du lieu khong hop le, sai trang thai xu ly hoac loi xac thuc.</p>
 */
public class BusinessException extends RuntimeException {

    /**
     * Khoi tao ngoai le nghiep vu voi thong diep mo ta loi.
     *
     * @param message noi dung thong bao loi
     */
    public BusinessException(String message) {
        super(message);
    }
}
