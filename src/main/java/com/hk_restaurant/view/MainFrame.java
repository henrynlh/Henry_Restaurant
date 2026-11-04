package com.hk_restaurant.view;

import com.hk_restaurant.model.entity.NhanVien;
import com.hk_restaurant.model.enumtype.Role;

import javax.swing.*;
import java.awt.*;

/**
 * Cửa sổ chính của hệ thống HK Restaurant.
 * Đây là MainForm, chứa các tab chức năng như POS, quản lý bàn, thực đơn, nhân viên và báo cáo.
 * Hệ thống phân quyền hiển thị tab theo vai trò của nhân viên đăng nhập.
 */
public class MainFrame extends JFrame {
    private final BanAnPanel banAnPanel;
    private final MonAnPanel monAnPanel;
    private final DonHangPanel donHangPanel;
    private final ReportPanel reportPanel;
    private final NhanVienPanel nhanVienPanel;
    private final NhanVien currentUser;

    /**
     * Khởi tạo cửa sổ chính của hệ thống.
     *
     * @param banAnPanel panel quản lý bàn ăn
     * @param monAnPanel panel quản lý thực đơn
     * @param donHangPanel panel bán hàng POS
     * @param reportPanel panel báo cáo
     * @param nhanVienPanel panel quản lý nhân viên
     * @param currentUser nhân viên đang đăng nhập
     */
    public MainFrame(BanAnPanel banAnPanel,
                     MonAnPanel monAnPanel,
                     DonHangPanel donHangPanel,
                     ReportPanel reportPanel,
                     NhanVienPanel nhanVienPanel,
                     NhanVien currentUser) {
        this.banAnPanel = banAnPanel;
        this.monAnPanel = monAnPanel;
        this.donHangPanel = donHangPanel;
        this.reportPanel = reportPanel;
        this.nhanVienPanel = nhanVienPanel;
        this.currentUser = currentUser;
        initUI();
    }

    /**
     * Khởi tạo giao diện chính và các tab chức năng.
     * Nếu là ADMIN thì xem toàn bộ.
     * Nếu là STAFF thì chỉ xem tab POS.
     */
    private void initUI() {
        setTitle("HK Restaurant - POS System");
        setSize(1450, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 14));

        boolean isAdmin = currentUser != null && currentUser.getVaiTro() == Role.ADMIN;

        tabs.addTab("Bán Hàng (POS)", donHangPanel);

        if (isAdmin) {
            tabs.addTab("Quản lý Bàn", banAnPanel);
            tabs.addTab("Quản lý Thực Đơn", monAnPanel);
            tabs.addTab("Quản lý Nhân Viên", nhanVienPanel);
            tabs.addTab("Báo Cáo", reportPanel);
        }

        tabs.addChangeListener(e -> {
            int index = tabs.getSelectedIndex();
            if (index < 0) {
                return;
            }

            String title = tabs.getTitleAt(index);

            if ("Bán Hàng (POS)".equals(title)) {
                donHangPanel.reloadPOSData();
            }

            if ("Quản lý Bàn".equals(title)) {
                banAnPanel.reloadBanAnData();
            }
        });

        add(tabs, BorderLayout.CENTER);
    }
}
