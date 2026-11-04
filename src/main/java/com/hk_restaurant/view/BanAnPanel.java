package com.hk_restaurant.view;

import com.hk_restaurant.controller.BanAnController;
import com.hk_restaurant.model.entity.BanAn;
import com.hk_restaurant.model.entity.NhanVien;
import com.hk_restaurant.util.validation.ValidationUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel giao diện quản lý bàn ăn của hệ thống.
 *
 * <p>Màn hình này cho phép người dùng thực hiện các thao tác quản trị
 * bàn ăn như thêm mới, cập nhật, khóa mềm, xóa vĩnh viễn và gán nhân viên
 * phục vụ cho từng bàn.</p>
 */
public class BanAnPanel extends JPanel {

    private final BanAnController controller;
    private final Runnable reloadCallback;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtId;
    private JTextField txtMaBan;
    private JTextField txtTenBan;
    private JTextField txtSucChua;
    private JComboBox<NhanVien> cbxNhanVien;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnKhoa;
    private JButton btnXoaVinhVien;
    private JButton btnGanNhanVien;
    private JButton btnLamMoi;

    /**
     * Khởi tạo panel quản lý bàn ăn.
     *
     * @param controller controller xử lý nghiệp vụ bàn ăn
     * @param reloadCallback callback dùng để đồng bộ dữ liệu với panel khác
     */
    public BanAnPanel(BanAnController controller, Runnable reloadCallback) {
        this.controller = controller;
        this.reloadCallback = reloadCallback;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initUI();
        loadNhanVienToComboBox();
        loadDataToTable();
    }

    /**
     * Tải lại dữ liệu bàn ăn và danh sách nhân viên phục vụ trên giao diện.
     */
    public void reloadBanAnData() {
        loadNhanVienToComboBox();
        loadDataToTable();
    }

    /**
     * Khởi tạo toàn bộ giao diện quản lý bàn ăn.
     */
    private void initUI() {
        JPanel panelForm = new JPanel(new GridLayout(5, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Thông tin Bàn Ăn"));

        panelForm.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelForm.add(txtId);

        panelForm.add(new JLabel("Mã Bàn (*):"));
        txtMaBan = new JTextField();
        panelForm.add(txtMaBan);

        panelForm.add(new JLabel("Tên Bàn (*):"));
        txtTenBan = new JTextField();
        panelForm.add(txtTenBan);

        panelForm.add(new JLabel("Sức Chứa (*):"));
        txtSucChua = new JTextField();
        panelForm.add(txtSucChua);

        panelForm.add(new JLabel("Nhân viên phục vụ:"));
        cbxNhanVien = new JComboBox<>();
        panelForm.add(cbxNhanVien);

        add(panelForm, BorderLayout.NORTH);

        String[] columns = {
                "ID", "Mã Bàn", "Tên Bàn", "Sức Chứa",
                "Trạng Thái", "Hoạt Động", "Nhân viên phục vụ"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            /**
             * Khóa chức năng chỉnh sửa trực tiếp trên bảng dữ liệu.
             */
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtId.setText(table.getValueAt(row, 0).toString());
                txtMaBan.setText(table.getValueAt(row, 1).toString());
                txtTenBan.setText(table.getValueAt(row, 2).toString());
                txtSucChua.setText(table.getValueAt(row, 3).toString());

                try {
                    Long maBan = Long.parseLong(table.getValueAt(row, 0).toString());
                    NhanVien nv = controller.layNhanVienDangPhucVu(maBan);
                    if (nv != null) {
                        chonNhanVienTrongCombo(nv.getId());
                    } else {
                        cbxNhanVien.setSelectedIndex(-1);
                    }
                } catch (Exception ex) {
                    cbxNhanVien.setSelectedIndex(-1);
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnThem = new JButton("Thêm Bàn");
        btnSua = new JButton("Cập Nhật");
        btnKhoa = new JButton("Khóa Bàn");
        btnXoaVinhVien = new JButton("Xóa Bàn");
        btnGanNhanVien = new JButton("Đổi Nhân Viên");
        btnLamMoi = new JButton("Làm Mới Form");

        panelButtons.add(btnThem);
        panelButtons.add(btnSua);
        panelButtons.add(btnKhoa);
        panelButtons.add(btnXoaVinhVien);
        panelButtons.add(btnGanNhanVien);
        panelButtons.add(btnLamMoi);

        add(panelButtons, BorderLayout.SOUTH);

        btnLamMoi.addActionListener(e -> clearForm());
        btnThem.addActionListener(e -> actionThem());
        btnSua.addActionListener(e -> actionSua());
        btnKhoa.addActionListener(e -> actionKhoa());
        btnXoaVinhVien.addActionListener(e -> actionXoaVinhVien());
        btnGanNhanVien.addActionListener(e -> actionGanNhanVien());
    }

    /**
     * Nạp danh sách nhân viên đang làm việc vào combobox chọn phục vụ.
     */
    private void loadNhanVienToComboBox() {
        cbxNhanVien.removeAllItems();
        List<NhanVien> ds = controller.layDanhSachNhanVienDangLam();
        for (NhanVien nv : ds) {
            cbxNhanVien.addItem(nv);
        }
        cbxNhanVien.setSelectedIndex(-1);
    }

    /**
     * Chọn đúng nhân viên trong combobox dựa trên mã nhân viên.
     *
     * @param maNhanVien mã nhân viên cần chọn
     */
    private void chonNhanVienTrongCombo(Long maNhanVien) {
        for (int i = 0; i < cbxNhanVien.getItemCount(); i++) {
            NhanVien nv = cbxNhanVien.getItemAt(i);
            if (nv.getId().equals(maNhanVien)) {
                cbxNhanVien.setSelectedIndex(i);
                return;
            }
        }
        cbxNhanVien.setSelectedIndex(-1);
    }

    /**
     * Nạp toàn bộ dữ liệu bàn ăn lên bảng hiển thị.
     */
    private void loadDataToTable() {
        tableModel.setRowCount(0);

        List<BanAn> list = controller.layDanhSachBanAn();
        for (BanAn b : list) {
            String nhanVienPhucVu = controller.layTenNhanVienDangPhucVu(b.getId());

            tableModel.addRow(new Object[]{
                    b.getId(),
                    b.getMaBanCode(),
                    b.getTenBan(),
                    b.getSucChua(),
                    b.getTrangThai() == com.hk_restaurant.model.enumtype.TableStatus.EMPTY ? "Trống" : "Đang phục vụ",
                    b.getDangSuDung() ? "Đang sử dụng" : "Đã khóa",
                    nhanVienPhucVu
            });
        }
    }

    /**
     * Xóa trắng toàn bộ dữ liệu đang nhập trên form.
     */
    private void clearForm() {
        txtId.setText("");
        txtMaBan.setText("");
        txtTenBan.setText("");
        txtSucChua.setText("");
        cbxNhanVien.setSelectedIndex(-1);
        table.clearSelection();
    }

    /**
     * Xử lý chung sau khi thay đổi dữ liệu thành công.
     *
     * <p>Phương thức này nạp lại bảng dữ liệu, xóa form và gọi callback
     * để các giao diện liên quan đồng bộ lại dữ liệu.</p>
     */
    private void afterChangeSuccess() {
        loadDataToTable();
        clearForm();

        if (reloadCallback != null) {
            reloadCallback.run();
        }
    }

    /**
     * Xử lý sự kiện thêm mới bàn ăn.
     */
    private void actionThem() {
        try {
            BanAn banAn = new BanAn();
            banAn.setMaBanCode(txtMaBan.getText().trim());
            banAn.setTenBan(txtTenBan.getText().trim());

            int sucChua = ValidationUtils.parsePositiveInt(
                    txtSucChua.getText(),
                    "Sức chứa phải là số nguyên dương!"
            );
            banAn.setSucChua(sucChua);

            controller.themBanAn(banAn);
            JOptionPane.showMessageDialog(this, "Thêm bàn thành công!");
            afterChangeSuccess();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nghiệp vụ", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý sự kiện cập nhật thông tin bàn ăn.
     */
    private void actionSua() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một bàn để sửa!");
                return;
            }

            BanAn banAn = new BanAn();
            banAn.setId(Long.parseLong(txtId.getText().trim()));
            banAn.setMaBanCode(txtMaBan.getText().trim());
            banAn.setTenBan(txtTenBan.getText().trim());

            int sucChua = ValidationUtils.parsePositiveInt(
                    txtSucChua.getText(),
                    "Sức chứa phải là số nguyên dương!"
            );
            banAn.setSucChua(sucChua);

            controller.capNhatBanAn(banAn);
            JOptionPane.showMessageDialog(this, "Cập nhật bàn thành công!");
            afterChangeSuccess();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý khóa mềm một bàn ăn khỏi trạng thái sử dụng.
     */
    private void actionKhoa() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để khóa!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn khóa bàn này?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                Long id = Long.parseLong(txtId.getText().trim());
                controller.xoaBanAn(id);
                JOptionPane.showMessageDialog(this, "Khóa bàn thành công!");
                afterChangeSuccess();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý xóa vĩnh viễn một bàn ăn khỏi hệ thống.
     */
    private void actionXoaVinhVien() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để xóa vĩnh viễn!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Xóa vĩnh viễn bàn này khỏi hệ thống?\nHành động này không thể hoàn tác.",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                Long id = Long.parseLong(txtId.getText().trim());
                controller.xoaVinhVienBanAn(id);
                JOptionPane.showMessageDialog(this, "Xóa vĩnh viễn bàn thành công!");
                afterChangeSuccess();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi xóa", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý gán hoặc đổi nhân viên phục vụ cho bàn ăn đang chọn.
     */
    private void actionGanNhanVien() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn trước!");
                return;
            }

            NhanVien nv = (NhanVien) cbxNhanVien.getSelectedItem();
            if (nv == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
                return;
            }

            Long maBan = Long.parseLong(txtId.getText().trim());
            controller.capNhatNhanVienPhucVu(maBan, nv.getId());
            JOptionPane.showMessageDialog(this, "Đổi nhân viên phục vụ thành công!");
            afterChangeSuccess();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi gán nhân viên", JOptionPane.ERROR_MESSAGE);
        }
    }
}