package com.hk_restaurant.view;

import com.hk_restaurant.constant.AppConstants;
import com.hk_restaurant.model.entity.ChiTietDonHang;
import com.hk_restaurant.model.entity.DonHang;
import com.hk_restaurant.model.entity.HoaDon;
import com.hk_restaurant.util.HoaDonPdfExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * Hộp thoại hiển thị hóa đơn thanh toán.
 *
 * <p>Giao diện này cho phép người dùng xem chi tiết hóa đơn vừa thanh toán
 * và xuất nội dung hóa đơn ra file PDF.</p>
 */
public class HoaDonDialog extends JDialog {

    private final HoaDon hoaDon;
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Khởi tạo hộp thoại hiển thị hóa đơn.
     *
     * @param owner cửa sổ cha của dialog
     * @param hoaDon hóa đơn cần hiển thị
     */
    public HoaDonDialog(Window owner, HoaDon hoaDon) {
        super(owner, "Hóa đơn thanh toán", ModalityType.APPLICATION_MODAL);
        this.hoaDon = hoaDon;

        setSize(560, 700);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    /**
     * Khởi tạo toàn bộ giao diện chi tiết hóa đơn.
     */
    private void initUI() {
        DonHang donHang = hoaDon.getDonHang();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblShop = new JLabel(AppConstants.SHOP_NAME);
        lblShop.setFont(new Font("Arial", Font.BOLD, 20));
        lblShop.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblShop);

        content.add(Box.createVerticalStrut(6));

        JLabel lblAddr1 = new JLabel(AppConstants.SHOP_BRANCH);
        lblAddr1.setFont(new Font("Arial", Font.PLAIN, 13));
        lblAddr1.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblAddr1);

        JLabel lblAddr2 = new JLabel(AppConstants.SHOP_ADDRESS);
        lblAddr2.setFont(new Font("Arial", Font.PLAIN, 13));
        lblAddr2.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblAddr2);

        JLabel lblPhone = new JLabel("Hotline: " + AppConstants.SHOP_PHONE);
        lblPhone.setFont(new Font("Arial", Font.PLAIN, 13));
        lblPhone.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblPhone);

        content.add(Box.createVerticalStrut(14));

        JLabel lblTitle = new JLabel("HÓA ĐƠN THANH TOÁN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblTitle);

        JLabel lblSoHd = new JLabel("Số HĐ: " + hoaDon.getSoHoaDon());
        lblSoHd.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSoHd.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblSoHd);

        content.add(Box.createVerticalStrut(14));

        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 6, 6));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        addInfo(infoPanel, "Ngày:", hoaDon.getThoiGianThanhToan().format(dtf));
        addInfo(infoPanel, "Bàn:", donHang.getBanAn() != null ? donHang.getBanAn().getTenBan() : "");
        addInfo(infoPanel, "Phục vụ:", donHang.getNhanVien() != null ? donHang.getNhanVien().getHoTen() : "");
        addInfo(infoPanel, "Thu ngân:", donHang.getNhanVien() != null ? donHang.getNhanVien().getHoTen() : "");
        addInfo(infoPanel, "Phương thức TT:", formatPaymentMethod());

        content.add(infoPanel);
        content.add(Box.createVerticalStrut(14));

        String[] cols = {"Tên món", "SL", "Đơn giá", "T.Tiền"};
        Object[][] data = buildTableData();

        JTable table = new JTable(data, cols);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setEnabled(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(220);
        table.getColumnModel().getColumn(1).setPreferredWidth(35);
        table.getColumnModel().getColumn(2).setPreferredWidth(85);
        table.getColumnModel().getColumn(3).setPreferredWidth(85);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(480, 185));
        tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 185));
        content.add(tableScroll);

        content.add(Box.createVerticalStrut(14));

        JPanel totalPanel = new JPanel(new GridLayout(5, 2, 6, 6));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));

        addTotal(totalPanel, "Thành tiền", formatMoney(hoaDon.getTongTien()), false);
        addTotal(totalPanel, "Khuyến mại", "0đ", false);
        addTotal(totalPanel, "Tổng thanh toán", formatMoney(hoaDon.getTongTien()), true);
        addTotal(totalPanel, "Tiền mặt", formatMoney(hoaDon.getTongTien()), false);
        addTotal(totalPanel, "Trả lại khách", "0đ", false);

        content.add(totalPanel);
        content.add(Box.createVerticalStrut(12));

        JLabel lblPromo = new JLabel("HĐ đã áp dụng KM: Không");
        lblPromo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblPromo.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblPromo);

        content.add(Box.createVerticalStrut(16));

        JPanel thanksPanel = new JPanel();
        thanksPanel.setLayout(new BoxLayout(thanksPanel, BoxLayout.Y_AXIS));
        thanksPanel.setBackground(Color.WHITE);
        thanksPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblThanks = new JLabel("Trân trọng cảm ơn!");
        lblThanks.setFont(new Font("Arial", Font.BOLD, 16));
        lblThanks.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblVat = new JLabel("(Hóa đơn chưa bao gồm thuế GTGT)");
        lblVat.setFont(new Font("Arial", Font.PLAIN, 12));
        lblVat.setAlignmentX(Component.CENTER_ALIGNMENT);

        thanksPanel.add(lblThanks);
        thanksPanel.add(Box.createVerticalStrut(4));
        thanksPanel.add(lblVat);

        content.add(thanksPanel);
        content.add(Box.createVerticalStrut(18));

        JButton btnExportPdf = new JButton("In hóa đơn");
        JButton btnClose = new JButton("Đóng");

        btnExportPdf.setFont(new Font("Arial", Font.PLAIN, 12));
        btnClose.setFont(new Font("Arial", Font.PLAIN, 12));

        btnExportPdf.setPreferredSize(new Dimension(120, 34));
        btnClose.setPreferredSize(new Dimension(120, 34));

        btnExportPdf.addActionListener(e -> exportPdf());
        btnClose.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnExportPdf);
        buttonPanel.add(btnClose);

        content.add(buttonPanel);

        JScrollPane mainScroll = new JScrollPane(content);
        mainScroll.getVerticalScrollBar().setUnitIncrement(14);
        root.add(mainScroll, BorderLayout.CENTER);

        setContentPane(root);
    }

    /**
     * Mở hộp thoại chọn nơi lưu và xuất hóa đơn ra file PDF.
     */
    private void exportPdf() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu hóa đơn PDF");
        chooser.setSelectedFile(new java.io.File(hoaDon.getSoHoaDon() + ".pdf"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                path += ".pdf";
            }

            HoaDonPdfExporter.exportToPdf(hoaDon, path);
            JOptionPane.showMessageDialog(this, "Xuất PDF thành công:\n" + path);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Xuất PDF thất bại: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Thêm một cặp thông tin dạng nhãn - giá trị vào panel thông tin hóa đơn.
     *
     * @param panel panel cần thêm dữ liệu
     * @param left nhãn bên trái
     * @param right giá trị bên phải
     */
    private void addInfo(JPanel panel, String left, String right) {
        JLabel l = new JLabel(left);
        l.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel r = new JLabel(right);
        r.setFont(new Font("Arial", Font.PLAIN, 13));

        panel.add(l);
        panel.add(r);
    }

    /**
     * Thêm một dòng tổng kết thanh toán vào panel cuối hóa đơn.
     *
     * @param panel panel cần thêm dữ liệu
     * @param left nhãn bên trái
     * @param right giá trị bên phải
     * @param bold xác định có hiển thị đậm hay không
     */
    private void addTotal(JPanel panel, String left, String right, boolean bold) {
        JLabel l = new JLabel(left);
        JLabel r = new JLabel(right, SwingConstants.RIGHT);

        if (bold) {
            l.setFont(new Font("Arial", Font.BOLD, 15));
            r.setFont(new Font("Arial", Font.BOLD, 15));
        } else {
            l.setFont(new Font("Arial", Font.PLAIN, 13));
            r.setFont(new Font("Arial", Font.PLAIN, 13));
        }

        panel.add(l);
        panel.add(r);
    }

    /**
     * Tạo mảng dữ liệu hiển thị danh sách món ăn trong hóa đơn.
     *
     * @return dữ liệu dạng hai chiều dùng cho JTable
     */
    private Object[][] buildTableData() {
        DonHang donHang = hoaDon.getDonHang();
        if (donHang.getDanhSachChiTiet() == null) {
            return new Object[0][0];
        }

        Object[][] rows = new Object[donHang.getDanhSachChiTiet().size()][4];
        int i = 0;
        for (ChiTietDonHang ct : donHang.getDanhSachChiTiet()) {
            rows[i][0] = ct.getMonAn() != null ? ct.getMonAn().getTenMon() : "";
            rows[i][1] = ct.getSoLuong();
            rows[i][2] = formatMoneyNoSuffix(ct.getDonGia());
            rows[i][3] = formatMoneyNoSuffix(ct.getThanhTien());
            i++;
        }
        return rows;
    }

    /**
     * Định dạng tiền tệ kèm hậu tố đơn vị đồng.
     *
     * @param value giá trị tiền
     * @return chuỗi tiền đã định dạng
     */
    private String formatMoney(BigDecimal value) {
        return formatMoneyNoSuffix(value) + "đ";
    }

    /**
     * Định dạng tiền tệ không kèm hậu tố đơn vị.
     *
     * @param value giá trị tiền
     * @return chuỗi tiền đã định dạng
     */
    private String formatMoneyNoSuffix(BigDecimal value) {
        if (value == null) return "0";
        return moneyFormat.format(value);
    }

    /**
     * Chuyển phương thức thanh toán sang chuỗi tiếng Việt để hiển thị.
     *
     * @return chuỗi mô tả phương thức thanh toán
     */
    private String formatPaymentMethod() {
        if (hoaDon.getPhuongThucThanhToan() == null) return "";
        return switch (hoaDon.getPhuongThucThanhToan()) {
            case CASH -> "Tiền mặt";
            case BANK_TRANSFER -> "Chuyển khoản";
        };
    }
}