package com.hk_restaurant.view;

import com.hk_restaurant.controller.MonAnController;
import com.hk_restaurant.model.entity.LoaiMon;
import com.hk_restaurant.model.entity.MonAn;
import com.hk_restaurant.util.ImageStorageUtils;
import com.hk_restaurant.util.validation.ValidationUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel quản lý thực đơn.
 * Hỗ trợ thêm, sửa, ngừng bán và quản lý ảnh món ăn.
 */
public class MonAnPanel extends JPanel {
    private final MonAnController controller;

    private JTable table;
    private DefaultTableModel tableModel;
    private List<MonAn> currentTableData = new ArrayList<>();

    private JTextField txtId;
    private JTextField txtMaMon;
    private JTextField txtTenMon;
    private JTextField txtDonGia;
    private JTextField txtHinhAnh;

    private JLabel lblPreview;

    private JComboBox<LoaiMon> cbxLoaiMon;
    private JComboBox<Object> cbxLocLoaiMon;

    private String currentOriginalImagePath;

    public MonAnPanel(MonAnController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadLoaiMonToComboBox();
        loadLoaiMonToFilter();
        loadDataToTable();
    }

    private void initUI() {
        JPanel topWrapper = new JPanel(new BorderLayout(10, 10));

        JPanel panelFormAndPreview = new JPanel(new BorderLayout(12, 0));

        JPanel panelForm = new JPanel(new GridLayout(6, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Thông tin Thực Đơn"));

        panelForm.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelForm.add(txtId);

        panelForm.add(new JLabel("Loại Món:"));
        cbxLoaiMon = new JComboBox<>();
        panelForm.add(cbxLoaiMon);

        panelForm.add(new JLabel("Mã Món (SKU):"));
        txtMaMon = new JTextField();
        panelForm.add(txtMaMon);

        panelForm.add(new JLabel("Tên Món Ăn:"));
        txtTenMon = new JTextField();
        panelForm.add(txtTenMon);

        panelForm.add(new JLabel("Đơn Giá:"));
        txtDonGia = new JTextField();
        panelForm.add(txtDonGia);

        panelForm.add(new JLabel("Ảnh món ăn:"));
        txtHinhAnh = new JTextField();
        txtHinhAnh.setEditable(false);

        JPanel imagePathPanel = new JPanel(new BorderLayout(5, 0));
        imagePathPanel.add(txtHinhAnh, BorderLayout.CENTER);

        JButton btnChonAnh = new JButton("Chọn ảnh");
        btnChonAnh.addActionListener(e -> actionChonAnh());

        JButton btnXoaAnh = new JButton("Xóa ảnh");
        btnXoaAnh.addActionListener(e -> actionXoaAnh());

        JPanel imageButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        imageButtons.add(btnChonAnh);
        imageButtons.add(btnXoaAnh);

        JPanel imageFieldWrapper = new JPanel(new BorderLayout(0, 5));
        imageFieldWrapper.add(imagePathPanel, BorderLayout.CENTER);
        imageFieldWrapper.add(imageButtons, BorderLayout.SOUTH);
        panelForm.add(imageFieldWrapper);

        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("Xem trước ảnh món"));
        previewPanel.setPreferredSize(new Dimension(180, 180));

        lblPreview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreview.setVerticalAlignment(SwingConstants.CENTER);
        lblPreview.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        previewPanel.add(lblPreview, BorderLayout.CENTER);

        panelFormAndPreview.add(panelForm, BorderLayout.CENTER);
        panelFormAndPreview.add(previewPanel, BorderLayout.EAST);

        JPanel panelFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelFilter.setBorder(BorderFactory.createTitledBorder("Lọc thực đơn"));
        panelFilter.add(new JLabel("Loại món:"));
        cbxLocLoaiMon = new JComboBox<>();
        cbxLocLoaiMon.addActionListener(e -> actionLocLoaiMon());
        panelFilter.add(cbxLocLoaiMon);

        JButton btnBoLoc = new JButton("Hiển thị tất cả");
        btnBoLoc.addActionListener(e -> {
            cbxLocLoaiMon.setSelectedIndex(0);
            loadDataToTable();
        });
        panelFilter.add(btnBoLoc);

        topWrapper.add(panelFormAndPreview, BorderLayout.CENTER);
        topWrapper.add(panelFilter, BorderLayout.SOUTH);

        add(topWrapper, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Loại", "Mã Món", "Tên Món", "Đơn Giá", "Có Ảnh", "Trạng Thái"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                if (row < 0 || row >= currentTableData.size()) {
                    return;
                }

                MonAn selected = currentTableData.get(row);

                txtId.setText(String.valueOf(selected.getId()));
                txtMaMon.setText(selected.getMaMonCode());
                txtTenMon.setText(selected.getTenMon());
                txtDonGia.setText(selected.getDonGia() != null ? selected.getDonGia().toString() : "");
                txtHinhAnh.setText(selected.getHinhAnh() == null ? "" : selected.getHinhAnh());
                currentOriginalImagePath = selected.getHinhAnh();

                if (selected.getLoaiMon() != null) {
                    chonLoaiMonTheoTen(selected.getLoaiMon().getTenLoaiMon());
                }

                updateImagePreview(selected.getHinhAnh());
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pButtons = new JPanel();
        JButton bThem = new JButton("Thêm");
        JButton bSua = new JButton("Sửa");
        JButton bXoa = new JButton("Ngừng bán");
        JButton bXoaVinhVien = new JButton("Xóa DL món");
        JButton bReset = new JButton("Làm mới");

        pButtons.add(bThem);
        pButtons.add(bSua);
        pButtons.add(bXoa);
        pButtons.add(bXoaVinhVien);
        pButtons.add(bReset);

        add(pButtons, BorderLayout.SOUTH);

        bThem.addActionListener(e -> actionThem());
        bSua.addActionListener(e -> actionSua());
        bXoa.addActionListener(e -> actionXoa());
        bXoaVinhVien.addActionListener(e -> actionXoaVinhVien());
        bReset.addActionListener(e -> clearForm());
    }

    private void actionChonAnh() {
        try {
            File selectedFile = ImageStorageUtils.chooseImageFile(this);
            if (selectedFile == null) {
                return;
            }

            String storedRelativePath = ImageStorageUtils.copyDishImageToStorage(selectedFile);
            txtHinhAnh.setText(storedRelativePath);
            updateImagePreview(storedRelativePath);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi chọn ảnh", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xóa file ảnh hiện tại khỏi thư mục images/monan và làm trống ô ảnh trên form.
     *
     * <p>Nếu người dùng đang sửa một món đã có ảnh, sau khi bấm nút này cần bấm
     * "Sửa" để cập nhật lại DB với giá trị ảnh = null.</p>
     */
    private void actionXoaAnh() {
        String storedPath = txtHinhAnh.getText() == null ? "" : txtHinhAnh.getText().trim();
        if (storedPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Món này hiện chưa có ảnh để xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa ảnh này không?",
                "Xác nhận xóa ảnh",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        ImageStorageUtils.deleteStoredDishImageQuietly(storedPath);
        txtHinhAnh.setText("");
        updateImagePreview(null);

        if (storedPath.equals(currentOriginalImagePath)) {
            currentOriginalImagePath = null;
        }

        JOptionPane.showMessageDialog(
                this,
                "Đã xóa ảnh. Hãy bấm nút Sửa để cập nhật lại dữ liệu món ăn."
        );
    }

    private void updateImagePreview(String storedPath) {
        lblPreview.setText("");
        lblPreview.setIcon(null);

        if (storedPath == null || storedPath.trim().isEmpty()) {
            lblPreview.setText("Chưa có ảnh");
            return;
        }

        try {
            String normalizedPath = storedPath.trim().replace("\\", "/");
            ImageIcon rawIcon = null;

            java.net.URL resourceUrl = getClass().getClassLoader().getResource(normalizedPath);
            if (resourceUrl != null) {
                rawIcon = new ImageIcon(resourceUrl);
            } else {
                java.io.File file = new java.io.File(storedPath);
                if (file.exists()) {
                    rawIcon = new ImageIcon(file.getAbsolutePath());
                }
            }

            if (rawIcon == null || rawIcon.getIconWidth() <= 0) {
                lblPreview.setText("Không đọc được ảnh");
                return;
            }

            Image scaled = rawIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblPreview.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            lblPreview.setText("Không đọc được ảnh");
        }
    }

    private void loadLoaiMonToComboBox() {
        cbxLoaiMon.removeAllItems();
        controller.layDanhSachLoaiMon().forEach(cbxLoaiMon::addItem);
    }

    private void loadLoaiMonToFilter() {
        cbxLocLoaiMon.removeAllItems();
        cbxLocLoaiMon.addItem("Tất cả");
        controller.layDanhSachLoaiMon().forEach(cbxLocLoaiMon::addItem);
    }

    private void loadDataToTable() {
        renderTable(controller.layDanhSachMonAn());
    }

    private void loadDataToTable(List<MonAn> ds) {
        renderTable(ds);
    }

    private void renderTable(List<MonAn> ds) {
        currentTableData = ds != null ? new ArrayList<>(ds) : new ArrayList<>();
        tableModel.setRowCount(0);

        for (MonAn m : currentTableData) {
            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getLoaiMon() != null ? m.getLoaiMon().getTenLoaiMon() : "",
                    m.getMaMonCode(),
                    m.getTenMon(),
                    m.getDonGia(),
                    (m.getHinhAnh() != null && !m.getHinhAnh().trim().isEmpty()) ? "Có" : "Không",
                    Boolean.TRUE.equals(m.getDangKinhDoanh()) ? "Đang bán" : "Ngừng bán"
            });
        }
    }

    private void actionLocLoaiMon() {
        Object selected = cbxLocLoaiMon.getSelectedItem();

        if (selected == null || "Tất cả".equals(selected.toString())) {
            loadDataToTable();
            return;
        }

        if (selected instanceof LoaiMon loaiMon) {
            loadDataToTable(controller.layDanhSachMonAnTheoLoai(loaiMon));
        }
    }

    private void actionThem() {
        try {
            MonAn m = new MonAn();
            m.setLoaiMon((LoaiMon) cbxLoaiMon.getSelectedItem());
            m.setMaMonCode(txtMaMon.getText().trim());
            m.setTenMon(txtTenMon.getText().trim());

            BigDecimal donGia = ValidationUtils.parsePositiveMoney(
                    txtDonGia.getText(),
                    "Đơn giá phải là số dương!"
            );
            m.setDonGia(donGia);
            m.setDangKinhDoanh(true);
            m.setHinhAnh(normalizeImagePath());

            controller.themMonAn(m);
            loadDataToTable();
            clearForm();

            JOptionPane.showMessageDialog(this, "Thêm món ăn thành công!");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi thêm món", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật món ăn.
     * Nếu người dùng đã chọn ảnh mới khác với ảnh cũ, sau khi cập nhật DB thành công
     * hệ thống sẽ tự động xóa file ảnh cũ trong thư mục images/monan để tránh file rác.
     */
    private void actionSua() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần sửa!");
                return;
            }

            MonAn m = new MonAn();
            m.setId(Long.parseLong(txtId.getText().trim()));
            m.setLoaiMon((LoaiMon) cbxLoaiMon.getSelectedItem());
            m.setMaMonCode(txtMaMon.getText().trim());
            m.setTenMon(txtTenMon.getText().trim());

            BigDecimal donGia = ValidationUtils.parsePositiveMoney(
                    txtDonGia.getText(),
                    "Đơn giá phải là số dương!"
            );
            m.setDonGia(donGia);
            m.setDangKinhDoanh(true);
            m.setHinhAnh(normalizeImagePath());

            controller.capNhatMonAn(m);

            String newImagePath = m.getHinhAnh();
            if (currentOriginalImagePath != null
                    && !currentOriginalImagePath.trim().isEmpty()
                    && !currentOriginalImagePath.equals(newImagePath)) {
                ImageStorageUtils.deleteStoredDishImageQuietly(currentOriginalImagePath);
            }

            actionLocLoaiMon();
            clearForm();

            JOptionPane.showMessageDialog(this, "Cập nhật món ăn thành công!");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi cập nhật món", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String normalizeImagePath() {
        String path = txtHinhAnh.getText() == null ? "" : txtHinhAnh.getText().trim();
        return path.isEmpty() ? null : path;
    }

    private void actionXoa() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần ngừng bán!");
                return;
            }

            controller.ngungKinhDoanh(Long.parseLong(txtId.getText().trim()));
            actionLocLoaiMon();
            clearForm();

            JOptionPane.showMessageDialog(this, "Đã chuyển món sang trạng thái ngừng bán!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi ngừng bán", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Xử lý xóa vĩnh viễn món ăn khỏi hệ thống.
     * Chỉ được xóa cứng khi món đã ở trạng thái ngừng bán.
     */
    private void actionXoaVinhVien() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần xóa dữ liệu!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Xóa dữ liệu món này khỏi hệ thống?\nHành động này không thể hoàn tác.",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            controller.xoaVinhVienMonAn(Long.parseLong(txtId.getText().trim()));
            actionLocLoaiMon();
            clearForm();

            JOptionPane.showMessageDialog(this, "Đã xóa dữ liệu món ăn khỏi hệ thống thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi xóa món", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtMaMon.setText("");
        txtTenMon.setText("");
        txtDonGia.setText("");
        txtHinhAnh.setText("");
        currentOriginalImagePath = null;
        updateImagePreview(null);

        if (cbxLoaiMon.getItemCount() > 0) {
            cbxLoaiMon.setSelectedIndex(0);
        }
        table.clearSelection();
    }

    private void chonLoaiMonTheoTen(String tenLoai) {
        for (int i = 0; i < cbxLoaiMon.getItemCount(); i++) {
            LoaiMon lm = cbxLoaiMon.getItemAt(i);
            if (lm != null && lm.getTenLoaiMon().equalsIgnoreCase(tenLoai)) {
                cbxLoaiMon.setSelectedIndex(i);
                break;
            }
        }
    }
}
