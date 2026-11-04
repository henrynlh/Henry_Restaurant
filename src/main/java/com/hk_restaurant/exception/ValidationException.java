package com.hk_restaurant.exception;

/**
 * Exception dung cho cac loi kiem tra du lieu dau vao.
 *
 * <p>Lop nay duoc su dung khi du lieu nguoi dung nhap vao khong thoa man
 * cac rang buoc nghiep vu nhu de trong truong bat buoc, sai dinh dang,
 * trung ma hoac vi pham cac dieu kien kiem tra hop le khac.</p>
 */
public class ValidationException extends BusinessException {

    /**
     * Khoi tao ngoai le kiem tra du lieu voi thong diep mo ta loi.
     *
     * @param message noi dung thong bao loi du lieu
     */
    public ValidationException(String message) {
        super(message);
    }
}
