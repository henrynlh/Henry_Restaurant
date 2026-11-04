package com.hk_restaurant;

import com.formdev.flatlaf.FlatLightLaf;
import com.hk_restaurant.controller.BanAnController;
import com.hk_restaurant.controller.DonHangController;
import com.hk_restaurant.controller.MonAnController;
import com.hk_restaurant.controller.NhanVienController;
import com.hk_restaurant.controller.ReportController;
import com.hk_restaurant.dao.BanAnDAO;
import com.hk_restaurant.dao.DonHangDAO;
import com.hk_restaurant.dao.HoaDonDAO;
import com.hk_restaurant.dao.MonAnDAO;
import com.hk_restaurant.dao.NhanVienDAO;
import com.hk_restaurant.dao.ReportDAO;
import com.hk_restaurant.dao.impl.BanAnDAOImpl;
import com.hk_restaurant.dao.impl.DonHangDAOImpl;
import com.hk_restaurant.dao.impl.HoaDonDAOImpl;
import com.hk_restaurant.dao.impl.MonAnDAOImpl;
import com.hk_restaurant.dao.impl.NhanVienDAOImpl;
import com.hk_restaurant.dao.impl.ReportDAOImpl;
import com.hk_restaurant.service.BanAnService;
import com.hk_restaurant.service.DonHangService;
import com.hk_restaurant.service.MonAnService;
import com.hk_restaurant.service.NhanVienService;
import com.hk_restaurant.service.ReportService;
import com.hk_restaurant.service.impl.BanAnServiceImpl;
import com.hk_restaurant.service.impl.DonHangServiceImpl;
import com.hk_restaurant.service.impl.MonAnServiceImpl;
import com.hk_restaurant.service.impl.NhanVienServiceImpl;
import com.hk_restaurant.service.impl.ReportServiceImpl;
import com.hk_restaurant.view.BanAnPanel;
import com.hk_restaurant.view.DonHangPanel;
import com.hk_restaurant.view.LoginFrame;
import com.hk_restaurant.view.MainFrame;
import com.hk_restaurant.view.MonAnPanel;
import com.hk_restaurant.view.NhanVienPanel;
import com.hk_restaurant.view.ReportPanel;

import javax.swing.*;

/**
 * Lớp khởi động ứng dụng.
 * Lớp này chịu trách nhiệm cấu hình Look and Feel, khởi tạo các tầng DAO, Service, Controller, View
 * và nối toàn bộ thành phần của hệ thống theo đúng mô hình nhiều lớp.
 */
public class AppLauncher {

    /**
     * Điểm vào chính của chương trình.
     *
     * @param args tham số dòng lệnh của ứng dụng
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();

                UIManager.put("Button.arc", 15);
                UIManager.put("Component.arc", 15);
                UIManager.put("TextComponent.arc", 15);
                UIManager.put("Component.focusWidth", 1);
                UIManager.put("Table.showVerticalLines", true);
                UIManager.put("Table.showHorizontalLines", true);

                BanAnDAO banAnDAO = new BanAnDAOImpl();
                MonAnDAO monAnDAO = new MonAnDAOImpl();
                DonHangDAO donHangDAO = new DonHangDAOImpl();
                HoaDonDAO hoaDonDAO = new HoaDonDAOImpl();
                NhanVienDAO nhanVienDAO = new NhanVienDAOImpl();
                ReportDAO reportDAO = new ReportDAOImpl();

                BanAnService banAnService = new BanAnServiceImpl(banAnDAO);
                MonAnService monAnService = new MonAnServiceImpl(monAnDAO);
                NhanVienService nhanVienService = new NhanVienServiceImpl(nhanVienDAO);
                DonHangService donHangService = new DonHangServiceImpl(
                        donHangDAO,
                        banAnDAO,
                        monAnDAO,
                        hoaDonDAO,
                        nhanVienDAO
                );
                ReportService reportService = new ReportServiceImpl(reportDAO);

                BanAnController baCtrl = new BanAnController(banAnService, nhanVienService, donHangService);
                MonAnController maCtrl = new MonAnController(monAnService);
                DonHangController dhCtrl = new DonHangController(donHangService, monAnService, nhanVienService);
                ReportController rpCtrl = new ReportController(reportService);
                NhanVienController nvCtrl = new NhanVienController(nhanVienService);

                DonHangPanel dhPanel = new DonHangPanel(dhCtrl, baCtrl);
                BanAnPanel baPanel = new BanAnPanel(baCtrl, dhPanel::reloadPOSData);
                dhPanel.setBanAnReloadCallback(baPanel::reloadBanAnData);
                MonAnPanel maPanel = new MonAnPanel(maCtrl);
                ReportPanel rpPanel = new ReportPanel(rpCtrl);
                NhanVienPanel nvPanel = new NhanVienPanel(nvCtrl, () -> {
                    dhPanel.reloadPOSData();
                    baPanel.reloadBanAnData();
                });

                LoginFrame loginFrame = new LoginFrame(nvCtrl, currentUser -> {
                    MainFrame frame = new MainFrame(baPanel, maPanel, dhPanel, rpPanel, nvPanel, currentUser);
                    frame.setVisible(true);
                });

                loginFrame.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Khởi động ứng dụng thất bại: " + e.getMessage(),
                        "Lỗi khởi động",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
