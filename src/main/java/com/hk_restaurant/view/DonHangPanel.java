package com.hk_restaurant.view;

import com.hk_restaurant.controller.BanAnController;
import com.hk_restaurant.controller.DonHangController;
import com.hk_restaurant.model.dto.OrderDetailInput;
import com.hk_restaurant.model.entity.BanAn;
import com.hk_restaurant.model.entity.ChiTietDonHang;
import com.hk_restaurant.model.entity.DonHang;
import com.hk_restaurant.model.entity.HoaDon;
import com.hk_restaurant.model.entity.MonAn;
import com.hk_restaurant.model.entity.NhanVien;
import com.hk_restaurant.model.enumtype.PaymentMethod;
import com.hk_restaurant.model.enumtype.TableStatus;
import com.hk_restaurant.util.ImageStorageUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Panel giao diện bán hàng (POS).
 * Màn hình này hỗ trợ chọn bàn, mở đơn, gọi món, tăng giảm số lượng,
 * ghi chú món, thanh toán và in hóa đơn.
 */
public class DonHangPanel extends JPanel {
    private final DonHangController donHangController;
    private final BanAnController banAnController;

    private Runnable banAnReloadCallback;

    private BanAn selectedTable;
    private DonHang currentOrder;

    private JPanel panelTables;
    private JPanel panelMenu;
    private JTable tableCart;
    private DefaultTableModel cartModel;
    private JLabel lblTableInfo;
    private JLabel lblTotalPrice;
    private JComboBox<String> cbxLoaiMon;
    private JButton btnPay;

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    /**
     * Khởi tạo panel POS.
     *
     * @param dhCtrl controller xử lý nghiệp vụ đơn hàng
     * @param baCtrl controller xử lý nghiệp vụ bàn ăn
     */
    public DonHangPanel(DonHangController dhCtrl, BanAnController baCtrl) {
        this.donHangController = dhCtrl;
        this.banAnController = baCtrl;

        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        initUI();
        loadTables();
        loadMenu();
    }

    /**
     * Gán callback để thông báo cho màn hình quản lý bàn reload dữ liệu khi trạng thái bàn thay đổi.
     *
     * @param banAnReloadCallback hàm callback reload bàn ăn
     */
    public void setBanAnReloadCallback(Runnable banAnReloadCallback) {
        this.banAnReloadCallback = banAnReloadCallback;
    }

    /**
     * Gọi callback reload bàn ăn nếu đã được thiết lập.
     */
    private void notifyBanAnChanged() {
        if (banAnReloadCallback != null) {
            banAnReloadCallback.run();
        }
    }

    /**
     * Khởi tạo toàn bộ giao diện POS gồm:
     * sơ đồ bàn, thực đơn, giỏ hàng và khu vực thanh toán.
     */
    private void initUI() {
        // ===== LEFT: TABLES =====
        panelTables = new JPanel(new GridLayout(0, 2, 10, 10));
        panelTables.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollTables = new JScrollPane(panelTables);
        scrollTables.setPreferredSize(new Dimension(340, 0));
        scrollTables.setBorder(BorderFactory.createTitledBorder("Sơ đồ Bàn"));
        add(scrollTables, BorderLayout.WEST);

        // ===== CENTER: MENU =====
        JPanel centerWrapper = new JPanel(new BorderLayout(8, 8));

        JPanel menuTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        menuTop.setBorder(BorderFactory.createTitledBorder(""));
        menuTop.add(new JLabel("Loại món:"));

        cbxLoaiMon = new JComboBox<>();
        cbxLoaiMon.addItem("Tất cả");
        try {
            for (String tenLoai : donHangController.layDanhSachTenLoaiMon()) {
                cbxLoaiMon.addItem(tenLoai);
            }
        } catch (Exception ignored) {
        }
        cbxLoaiMon.addActionListener(e -> loadMenuByCategory());
        menuTop.add(cbxLoaiMon);

        centerWrapper.add(menuTop, BorderLayout.NORTH);

        panelMenu = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
        panelMenu.setBorder(new EmptyBorder(12, 12, 12, 12));
        panelMenu.setBackground(Color.WHITE);

        JScrollPane scrollMenu = new JScrollPane(panelMenu);
        scrollMenu.setBorder(BorderFactory.createTitledBorder("Thực Đơn"));
        scrollMenu.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        centerWrapper.add(scrollMenu, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        // ===== RIGHT: ORDER DETAIL =====
        JPanel panelRight = new JPanel(new BorderLayout(8, 8));
        panelRight.setPreferredSize(new Dimension(540, 0));
        panelRight.setBorder(BorderFactory.createTitledBorder("Chi tiết gọi món"));

        lblTableInfo = new JLabel("Chưa chọn bàn", SwingConstants.CENTER);
        lblTableInfo.setFont(new Font("Arial", Font.BOLD, 22));
        panelRight.add(lblTableInfo, BorderLayout.NORTH);

        cartModel = new DefaultTableModel(new String[]{"Mã món", "Món", "SL", "Giá", "T.Tiền", "Ghi chú"}, 0) {
            /**
             * Khóa chỉnh sửa trực tiếp trên bảng giỏ hàng.
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableCart = new JTable(cartModel);
        tableCart.setRowHeight(28);
        tableCart.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tableCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableColumn hiddenColumn = tableCart.getColumnModel().getColumn(0);
        hiddenColumn.setMinWidth(0);
        hiddenColumn.setMaxWidth(0);
        hiddenColumn.setPreferredWidth(0);

        JScrollPane scrollCart = new JScrollPane(tableCart);
        panelRight.add(scrollCart, BorderLayout.CENTER);

        JButton btnTang = new JButton("Tăng SL");
        JButton btnGiam = new JButton("Giảm SL");
        JButton btnXoaMon = new JButton("Xóa món");
        JButton btnGhiChu = new JButton("Ghi chú");
        JButton btnReload = new JButton("Làm mới");

        btnTang.addActionListener(e -> tangSoLuongMon());
        btnGiam.addActionListener(e -> giamSoLuongMon());
        btnXoaMon.addActionListener(e -> xoaMonKhoiDon());
        btnGhiChu.addActionListener(e -> actionGhiChuMon());
        btnReload.addActionListener(e -> reloadPOSData());

        JPanel actionPanel = new JPanel(new GridLayout(1, 5, 8, 8));
        actionPanel.add(btnTang);
        actionPanel.add(btnGiam);
        actionPanel.add(btnXoaMon);
        actionPanel.add(btnGhiChu);
        actionPanel.add(btnReload);

        lblTotalPrice = new JLabel("Tổng tiền: 0 VNĐ", SwingConstants.RIGHT);
        lblTotalPrice.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotalPrice.setForeground(Color.RED);

        btnPay = new JButton("THANH TOÁN & IN BILL");
        btnPay.setFont(new Font("Arial", Font.BOLD, 16));
        btnPay.setBackground(new Color(255, 215, 0));
        btnPay.setForeground(Color.BLACK);
        btnPay.setFocusPainted(false);
        btnPay.setEnabled(false);
        btnPay.addActionListener(e -> actionThanhToan());

        JPanel southBox = new JPanel(new BorderLayout(8, 8));
        southBox.add(actionPanel, BorderLayout.NORTH);
        southBox.add(lblTotalPrice, BorderLayout.CENTER);
        southBox.add(btnPay, BorderLayout.SOUTH);

        panelRight.add(southBox, BorderLayout.SOUTH);
        add(panelRight, BorderLayout.EAST);
    }

    /**
     * Tải lại toàn bộ dữ liệu POS gồm:
     * bàn ăn, đơn hàng đang chọn và danh sách thực đơn.
     */
    public void reloadPOSData() {
        try {
            if (selectedTable != null) {
                List<BanAn> dsBan = banAnController.layDanhSachBanAn();
                for (BanAn b : dsBan) {
                    if (b.getId().equals(selectedTable.getId())) {
                        selectedTable = b;
                        break;
                    }
                }

                currentOrder = donHangController.layDonDangPhucVuTheoBan(selectedTable.getId());

                if (currentOrder == null) {
                    cartModel.setRowCount(0);
                    lblTableInfo.setText("Đang chọn: " + selectedTable.getTenBan());
                    lblTotalPrice.setText("Tổng tiền: 0 VNĐ");
                    btnPay.setEnabled(false);
                } else {
                    lblTableInfo.setText("Đang chọn: " + selectedTable.getTenBan());
                    refillCartFromOrder(null);
                }
            } else {
                cartModel.setRowCount(0);
                lblTableInfo.setText("Chưa chọn bàn");
                lblTotalPrice.setText("Tổng tiền: 0 VNĐ");
                btnPay.setEnabled(false);
            }

            loadTables();
            loadMenu();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi reload POS", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Nạp sơ đồ bàn ăn lên giao diện POS.
     */
    private void loadTables() {
        panelTables.removeAll();

        List<BanAn> list = banAnController.layDanhSachBanAn();
        for (BanAn ba : list) {
            if (Boolean.FALSE.equals(ba.getDangSuDung())) {
                continue;
            }

            String statusText = (ba.getTrangThai() == TableStatus.EMPTY) ? "Trống" : "Có khách";

            JButton btn = new JButton(
                    "<html><center>"
                            + "<div style='font-size:16px; font-weight:bold;'>" + ba.getTenBan() + "</div>"
                            + "<br><div>(" + statusText + ")</div>"
                            + "</center></html>"
            );

            btn.setPreferredSize(new Dimension(130, 90));
            btn.setFocusPainted(false);
            btn.setBackground(ba.getTrangThai() == TableStatus.EMPTY
                    ? new Color(220, 255, 220)
                    : new Color(255, 220, 220));

            btn.addActionListener(e -> selectTable(ba));
            panelTables.add(btn);
        }

        panelTables.revalidate();
        panelTables.repaint();
    }

    /**
     * Nạp toàn bộ thực đơn theo bộ lọc hiện tại.
     */

    private void loadMenu() {
        loadMenuByCategory();
    }

    /**
     * Nạp thực đơn theo loại món đang chọn trong combobox.
     */
    private void loadMenuByCategory() {
        panelMenu.removeAll();

        String selectedCategory = (String) cbxLoaiMon.getSelectedItem();
        List<MonAn> foods;
        try {
            foods = donHangController.layDanhSachThucDonTheoLoai(selectedCategory);
        } catch (Exception ex) {
            foods = donHangController.layDanhSachThucDon();
        }

        for (MonAn m : foods) {
            if (Boolean.FALSE.equals(m.getDangKinhDoanh())) {
                continue;
            }

            JButton btn = createDishButton(m);
            panelMenu.add(btn);
        }

        panelMenu.revalidate();
        panelMenu.repaint();
    }

    /**
     * Tạo nút món ăn hiển thị kèm ảnh đại diện trên màn hình POS.
     *
     * @param monAn món ăn cần hiển thị
     * @return nút món ăn
     */
    private JButton createDishButton(MonAn monAn) {
        JButton btn = new JButton(buildDishButtonText(monAn));
        btn.setPreferredSize(new Dimension(250 , 170));
        btn.setBackground(new Color(255, 248, 240));
        btn.setFocusPainted(false);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.TOP);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setMargin(new Insets(10, 8, 10, 8));
        btn.setIconTextGap(10);

        // Giảm chiều cao ảnh để chừa đủ chỗ cho 2 dòng chữ bên dưới
        btn.setIcon(loadDishIcon(monAn.getHinhAnh(), 150, 90));
        btn.addActionListener(e -> addFoodToOrder(monAn));
        return btn;
    }

    /**
     * Tạo nội dung HTML cho card món ăn trong POS.
     *
     * @param monAn món ăn cần hiển thị
     * @return nội dung text của nút món ăn
     */
    private String buildDishButtonText(MonAn monAn) {
        return "<html><div style='width:145px; text-align:center;'>"
                + "<div style='font-size:11px; font-weight:bold; color:#111; margin-top:2px;'>"
                + escapeHtml(monAn.getTenMon())
                + "</div>"
                + "<div style='margin-top:8px; color:#d60000; font-size:11px;'>"
                + formatMoney(monAn.getDonGia()) + " VNĐ"
                + "</div>"
                + "</div></html>";
    }


    /**
     * Tải ảnh món ăn và scale vừa khung theo đúng tỉ lệ gốc.
     *
     * @param imagePath đường dẫn ảnh món ăn
     * @param maxWidth chiều rộng tối đa
     * @param maxHeight chiều cao tối đa
     * @return icon hiển thị trên card món ăn
     */
    private Icon loadDishIcon(String imagePath, int maxWidth, int maxHeight) {
        try {
            ImageIcon rawIcon = null;

            if (imagePath != null && !imagePath.trim().isEmpty()) {
                String normalizedPath = imagePath.trim().replace("\\", "/");

                // 1) Ưu tiên đọc từ resources/classpath
                java.net.URL resourceUrl = getClass().getClassLoader().getResource(normalizedPath);
                if (resourceUrl != null) {
                    rawIcon = new ImageIcon(resourceUrl);
                } else {
                    // 2) Fallback: đọc như file ngoài nếu có
                    java.io.File file = new java.io.File(imagePath);
                    if (file.exists()) {
                        rawIcon = new ImageIcon(file.getAbsolutePath());
                    }
                }
            }

            if (rawIcon != null && rawIcon.getIconWidth() > 0 && rawIcon.getIconHeight() > 0) {
                int originalW = rawIcon.getIconWidth();
                int originalH = rawIcon.getIconHeight();

                double scale = Math.min((double) maxWidth / originalW, (double) maxHeight / originalH);
                scale = Math.min(scale, 1.0d);

                int targetW = Math.max(1, (int) Math.round(originalW * scale));
                int targetH = Math.max(1, (int) Math.round(originalH * scale));

                Image scaled = rawIcon.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception ignored) {
        }

        return createDefaultDishIcon(maxWidth, maxHeight);
    }

    /**
     * Tạo icon mặc định khi món ăn chưa có ảnh.
     *
     * @param width chiều rộng vùng icon
     * @param height chiều cao vùng icon
     * @return icon mặc định
     */
    private Icon createDefaultDishIcon(int width, int height) {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(255, 244, 225));
        g2.fillRoundRect(2, 2, width - 4, height - 4, 16, 16);

        g2.setColor(new Color(207, 167, 102));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(2, 2, width - 5, height - 5, 16, 16);

        int iconW = Math.min(34, width - 20);
        int iconH = Math.min(16, height - 18);
        int plateX = (width - iconW) / 2;
        int plateY = Math.max(8, (height - iconH) / 2 + 2);

        g2.drawOval(plateX, plateY, iconW, Math.max(10, iconH - 2));
        g2.drawArc(plateX + Math.max(4, iconW / 6), plateY - 12, Math.max(20, iconW - 10), 18, 0, 180);

        g2.dispose();
        return new ImageIcon(image);
    }

    private void selectTable(BanAn ba) {
        try {
            this.selectedTable = ba;
            lblTableInfo.setText("Đang chọn: " + ba.getTenBan());

            currentOrder = donHangController.layDonDangPhucVuTheoBan(ba.getId());

            if (currentOrder == null) {
                cartModel.setRowCount(0);
                lblTotalPrice.setText("Tổng tiền: 0 VNĐ");
                btnPay.setEnabled(false);
            } else {
                refillCartFromOrder(null);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Thêm món ăn được chọn vào đơn hàng hiện tại của bàn.
     * Nếu bàn chưa có đơn thì hệ thống yêu cầu chọn nhân viên phục vụ và tự động mở bàn mới.
     *
     * @param monAn món ăn cần thêm
     */
    private void addFoodToOrder(MonAn monAn) {
        if (selectedTable == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn trước!");
            return;
        }

        try {
            if (currentOrder == null) {
                List<NhanVien> dsNhanVien = donHangController.layDanhSachNhanVienDangLam();
                if (dsNhanVien == null || dsNhanVien.isEmpty()) {
                    throw new Exception("Không có nhân viên nào đang làm việc để phục vụ bàn này!");
                }

                NhanVien nv = (NhanVien) JOptionPane.showInputDialog(
                        this,
                        "Chọn nhân viên phục vụ bàn " + selectedTable.getTenBan() + ":",
                        "Chọn nhân viên phục vụ",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        dsNhanVien.toArray(),
                        dsNhanVien.get(0)
                );

                if (nv == null) {
                    return;
                }

                currentOrder = donHangController.moBanMoi(
                        selectedTable.getId(),
                        nv.getId(),
                        "Gọi món tại quầy"
                );
            }

            OrderDetailInput input = new OrderDetailInput(
                    monAn.getId(),
                    1,
                    monAn.getDonGia(),
                    ""
            );

            currentOrder = donHangController.themMonVaoDon(currentOrder.getId(), input);

            refillCartFromOrder(monAn.getId());
            loadTables();
            notifyBanAnChanged();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tăng số lượng món đang chọn trong giỏ hàng.
     */
    private void tangSoLuongMon() {
        capNhatSoLuongTheoDongDangChon(1);
    }

    /**
     * Giảm số lượng món đang chọn trong giỏ hàng.
     */
    private void giamSoLuongMon() {
        capNhatSoLuongTheoDongDangChon(-1);
    }

    /**
     * Xóa món đang chọn khỏi đơn hàng.
     */
    private void xoaMonKhoiDon() {
        if (currentOrder == null) {
            JOptionPane.showMessageDialog(this, "Chưa có đơn hàng!");
            return;
        }

        int row = tableCart.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần xóa!");
            return;
        }

        try {
            Long maMon = Long.parseLong(cartModel.getValueAt(row, 0).toString());
            currentOrder = donHangController.capNhatSoLuongMon(currentOrder.getId(), maMon, 0);
            refillCartFromOrder(null);
            notifyBanAnChanged();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật ghi chú cho món đang chọn trong giỏ hàng.
     */
    private void actionGhiChuMon() {
        if (currentOrder == null) {
            JOptionPane.showMessageDialog(this, "Chưa có đơn hàng!");
            return;
        }

        int row = tableCart.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần ghi chú!");
            return;
        }

        try {
            Long maMon = Long.parseLong(cartModel.getValueAt(row, 0).toString());
            String ghiChuCu = cartModel.getValueAt(row, 5) != null ? cartModel.getValueAt(row, 5).toString() : "";

            String ghiChuMoi = JOptionPane.showInputDialog(
                    this,
                    "Nhập ghi chú cho món:",
                    ghiChuCu
            );

            if (ghiChuMoi == null) {
                return;
            }

            currentOrder = donHangController.capNhatGhiChuMon(currentOrder.getId(), maMon, ghiChuMoi.trim());
            refillCartFromOrder(maMon);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi ghi chú", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật số lượng món theo dòng đang chọn trên bảng giỏ hàng.
     *
     * @param delta giá trị thay đổi số lượng, có thể là +1 hoặc -1
     */
    private void capNhatSoLuongTheoDongDangChon(int delta) {
        if (currentOrder == null) {
            JOptionPane.showMessageDialog(this, "Chưa có đơn hàng!");
            return;
        }

        int row = tableCart.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần sửa!");
            return;
        }

        try {
            Long maMon = Long.parseLong(cartModel.getValueAt(row, 0).toString());
            int soLuongHienTai = Integer.parseInt(cartModel.getValueAt(row, 2).toString());
            int soLuongMoi = soLuongHienTai + delta;

            currentOrder = donHangController.capNhatSoLuongMon(currentOrder.getId(), maMon, soLuongMoi);
            refillCartFromOrder(maMon);
            notifyBanAnChanged();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Đổ lại dữ liệu giỏ hàng từ đơn hàng hiện tại lên bảng chi tiết.
     *
     * @param selectedDishId mã món cần chọn lại sau khi cập nhật, có thể null
     */
    private void refillCartFromOrder(Long selectedDishId) {
        cartModel.setRowCount(0);

        if (currentOrder == null || currentOrder.getDanhSachChiTiet() == null) {
            lblTotalPrice.setText("Tổng tiền: 0 VNĐ");
            btnPay.setEnabled(false);
            return;
        }

        int rowToSelect = -1;
        int currentRow = 0;

        for (ChiTietDonHang ct : currentOrder.getDanhSachChiTiet()) {
            Long maMon = ct.getMonAn().getId();

            cartModel.addRow(new Object[]{
                    maMon,
                    ct.getMonAn().getTenMon(),
                    ct.getSoLuong(),
                    formatMoney(ct.getDonGia()),
                    formatMoney(ct.getThanhTien()),
                    ct.getGhiChu() == null ? "" : ct.getGhiChu()
            });

            if (selectedDishId != null && selectedDishId.equals(maMon)) {
                rowToSelect = currentRow;
            }

            currentRow++;
        }

        lblTotalPrice.setText("Tổng tiền: " + formatMoney(currentOrder.getTongTien()) + " VNĐ");
        btnPay.setEnabled(tableCart.getRowCount() > 0);

        if (rowToSelect >= 0 && rowToSelect < tableCart.getRowCount()) {
            final int finalRow = rowToSelect;
            SwingUtilities.invokeLater(() -> tableCart.setRowSelectionInterval(finalRow, finalRow));
        }
    }

    /**
     * Xử lý thanh toán đơn hàng hiện tại và mở hộp thoại hóa đơn.
     */
    private void actionThanhToan() {
        if (selectedTable == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn trước!");
            return;
        }

        if (currentOrder == null || currentOrder.getDanhSachChiTiet() == null || currentOrder.getDanhSachChiTiet().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đơn hàng chưa có món nào!");
            return;
        }

        try {
            Object[] options = {"Tiền mặt", "Chuyển khoản"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Chọn phương thức thanh toán:",
                    "Thanh toán",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == -1) {
                return;
            }

            PaymentMethod method = (choice == 0)
                    ? PaymentMethod.CASH
                    : PaymentMethod.BANK_TRANSFER;

            HoaDon hoaDon = donHangController.thanhToanDonHang(currentOrder.getId(), method);

            JOptionPane.showMessageDialog(this,
                    "Thanh toán thành công bàn " + selectedTable.getTenBan() + "!");

            HoaDonDialog dialog = new HoaDonDialog(
                    SwingUtilities.getWindowAncestor(this),
                    hoaDon
            );
            dialog.setVisible(true);

            selectedTable = null;
            currentOrder = null;
            cartModel.setRowCount(0);
            lblTableInfo.setText("Chưa chọn bàn");
            lblTotalPrice.setText("Tổng tiền: 0 VNĐ");
            btnPay.setEnabled(false);
            loadTables();
            notifyBanAnChanged();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi thanh toán", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Định dạng tiền tệ để hiển thị trên giao diện POS.
     *
     * @param value giá trị tiền
     * @return chuỗi tiền đã định dạng
     */
    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return moneyFormat.format(value);
    }

    /**
     * Chuyển chuỗi thông thường sang chuỗi an toàn khi hiển thị HTML trong JButton.
     *
     * @param text chuỗi đầu vào
     * @return chuỗi đã escape ký tự HTML đặc biệt
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}