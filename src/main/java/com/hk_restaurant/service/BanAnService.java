package com.hk_restaurant.service;

import com.hk_restaurant.model.entity.BanAn;

import java.util.List;

/**
 * Interface service cho phân hệ bàn ăn.
 * Tầng này chịu trách nhiệm điều phối các nghiệp vụ quản lý bàn,
 * giúp controller không gọi trực tiếp xuống DAO.
 */
public interface BanAnService {

    /**
     * Lấy toàn bộ danh sách bàn ăn trong hệ thống.
     *
     * @return danh sách bàn ăn
     */
    List<BanAn> layTatCaBanAn();

    /**
     * Tạo mới một bàn ăn.
     *
     * @param banAn bàn ăn cần thêm
     * @return bàn ăn sau khi lưu
     */
    BanAn taoMoiBanAn(BanAn banAn);

    /**
     * Cập nhật thông tin bàn ăn.
     *
     * @param banAn bàn ăn cần cập nhật
     * @return bàn ăn sau khi cập nhật
     */
    BanAn capNhatBanAn(BanAn banAn);

    /**
     * Khóa mềm một bàn ăn để ngừng sử dụng tạm thời.
     *
     * @param id mã bàn ăn cần khóa
     */
    void xoaBanAn(Long id);

    /**
     * Xóa vĩnh viễn một bàn ăn khỏi hệ thống.
     *
     * @param id mã bàn ăn cần xóa
     */
    void xoaVinhVienBanAn(Long id);
}
