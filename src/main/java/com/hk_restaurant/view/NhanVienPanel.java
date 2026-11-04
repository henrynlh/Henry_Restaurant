package com.hk_restaurant.view;

import com.hk_restaurant.controller.NhanVienController;
import com.hk_restaurant.model.dto.NhanVienDTO;
import com.hk_restaurant.model.entity.NhanVien;
import com.hk_restaurant.model.enumtype.Role;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel giao diện quản lý nhân viên.
 * Màn hình này hỗ trợ thêm, sửa, cho nghỉ làm, đi làm lại, nghỉ việc, xóa dữ liệu và đặt lại mật khẩu.
 */
public class NhanVienPanel extends JPanel {

    private final NhanVienController controller;
    private final Runnable reloadCallback;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtId;
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtSDT;
    private JTextField txtTenDangNhap;
    private JTextField txtSearch;
    private JComboBox<Role> cbxVaiTro;

    /**
     * Khởi tạo panel quản lý nhân viên.
     *
     * @param controller controller xử lý nghiệp vụ nhân viên
     * @param reloadCallback callback dùng để panel khác reload nếu dữ liệu nhân viên thay đổi
     */
    public NhanVienPanel(NhanVienController controller, Runnable reloadCallback) {
        this.controller = controller;
        this.reloadCallback = reloadCallback;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initUI();
        loadData();
    }

    /**
     * Tải lại dữ liệu nhân viên trên giao diện.
     */
    public void reloadNhanVienData() {
        loadData();
    }

    /**
     * Thông báo cho các panel khác reload dữ liệu khi cần.
     */
    private void notifyReloadOtherPanels() {
        if (reloadCallback != null) {
            reloadCallback.run();
        }
    }

    /**
     * Khởi tạo toàn bộ giao diện quản lý nhân viên.
     */
    private void initUI() {
        JPanel topContainer = new JPanel(new BorderLayout(8, 8));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm nhân viên"));

        txtSearch = new JTextField();
        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnResetSearch = new JButton("Hiển thị tất cả");

        btnSearch.addActionListener(e -> actionSearch());
        btnResetSearch.addActionListener(e -> loadData());

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchButtons.add(btnSearch);
        searchButtons.add(btnResetSearch);

        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(searchButtons, BorderLayout.EAST);

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));

        form.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        form.add(txtId);

        form.add(new JLabel("Mã nhân viên:"));
        txtMaNV = new JTextField();
        form.add(txtMaNV);

        form.add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        form.add(txtHoTen);

        form.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField();
        form.add(txtSDT);

        form.add(new JLabel("Tên đăng nhập:"));
        txtTenDangNhap = new JTextField();
        form.add(txtTenDangNhap);

        form.add(new JLabel("Vai trò:"));
        cbxVaiTro = new JComboBox<>(Role.values());
        form.add(cbxVaiTro);

        topContainer.add(searchPanel, BorderLayout.NORTH);
        topContainer.add(form, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Mã NV", "Họ tên", "SĐT", "Tên đăng nhập", "Vai trò", "Đang làm việc", "Đã nghỉ việc"}, 0
        ) {
            /**
             * Khóa chỉnh sửa trực tiếp dữ liệu trên bảng.
             */
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
                txtId.setText(table.getValueAt(row, 0).toString());
                txtMaNV.setText(table.getValueAt(row, 1).toString());
                txtHoTen.setText(table.getValueAt(row, 2).toString());
                txtSDT.setText(table.getValueAt(row, 3) == null ? "" : table.getValueAt(row, 3).toString());
                txtTenDangNhap.setText(table.getValueAt(row, 4).toString());
                cbxVaiTro.setSelectedItem(Role.valueOf(table.getValueAt(row, 5).toString()));
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnNghiLam = new JButton("Cho nghỉ làm");
        JButton btnDiLamLai = new JButton("Đi làm lại");
        JButton btnNghiViec = new JButton("Đuổi việc");
        JButton btnXoa = new JButton("Xóa dữ liệu NV");
        JButton btnMoi = new JButton("Làm mới");

        btnThem.addActionListener(e -> actionThem());
        btnSua.addActionListener(e -> actionSua());
        btnNghiLam.addActionListener(e -> actionNghiLam());
        btnDiLamLai.addActionListener(e -> actionDiLamLai());
        btnNghiViec.addActionListener(e -> actionNghiViec());
        btnXoa.addActionListener(e -> actionXoa());
        btnMoi.addActionListener(e -> clearForm());

        buttons.add(btnThem);
        buttons.add(btnSua);
        buttons.add(btnNghiLam);
        buttons.add(btnDiLamLai);
        buttons.add(btnNghiViec);
        buttons.add(btnXoa);
        buttons.add(btnMoi);

        bottom.add(buttons, BorderLayout.CENTER);

        JButton btnQuenMatKhau = new JButton("Quên mật khẩu");
        btnQuenMatKhau.addActionListener(e -> actionQuenMatKhau());
        bottom.add(btnQuenMatKhau, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Hiển thị danh sách nhân viên DTO lên bảng.
     *
     * @param list danh sách nhân viên cần hiển thị
     */
    private void renderTable(List<NhanVienDTO> list) {
        tableModel.setRowCount(0);

        for (NhanVienDTO nv : list) {
            tableModel.addRow(new Object[]{
                    nv.getId(),
                    nv.getMaNhanVienCode(),
                    nv.getHoTen(),
                    nv.getSoDienThoai(),
                    nv.getTenDangNhap(),
                    nv.getVaiTro(),
                    Boolean.TRUE.equals(nv.getDangLamViec()) ? "Có" : "Không",
                    Boolean.TRUE.equals(nv.getDaNghiViec()) ? "Có" : "Không"
            });
        }
    }

    /**
     * Nạp toàn bộ danh sách nhân viên lên bảng.
     */
    private void loadData() {
        renderTable(controller.layDanhSachNhanVien());
    }

    /**
     * Tìm kiếm nhân viên theo từ khóa người dùng nhập vào.
     */
    private void actionSearch() {
        renderTable(controller.timKiemNhanVien(txtSearch.getText().trim()));
    }

    /**
     * Xóa trắng form và bỏ chọn dòng hiện tại.
     */
    private void clearForm() {
        txtId.setText("");
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtSDT.setText("");
        txtTenDangNhap.setText("");
        txtSearch.setText("");
        cbxVaiTro.setSelectedIndex(0);
        table.clearSelection();
    }

    /**
     * Tạo đối tượng nhân viên từ dữ liệu đang nhập trên form.
     *
     * @return đối tượng nhân viên
     */
    private NhanVien buildNhanVienFromForm() {
        NhanVien nv = new NhanVien();

        if (!txtId.getText().trim().isEmpty()) {
            nv.setId(Long.parseLong(txtId.getText().trim()));
        }

        nv.setMaNhanVienCode(txtMaNV.getText().trim());
        nv.setHoTen(txtHoTen.getText().trim());
        nv.setSoDienThoai(txtSDT.getText().trim());
        nv.setTenDangNhap(txtTenDangNhap.getText().trim());
        nv.setVaiTro((Role) cbxVaiTro.getSelectedItem());

        return nv;
    }

    /**
     * Hiển thị hộp thoại nhập mật khẩu một trường.
     *
     * @param title tiêu đề hộp thoại
     * @param label nhãn hiển thị
     * @return mật khẩu người dùng nhập hoặc null nếu hủy
     */
    private String askPassword(String title, String label) {
        JPasswordField pwd = new JPasswordField();
        Object[] message = {label, pwd};

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            return new String(pwd.getPassword()).trim();
        }
        return null;
    }

    /**
     * Hiển thị hộp thoại nhập cặp mật khẩu mới và xác nhận mật khẩu.
     *
     * @param title tiêu đề hộp thoại
     * @return mảng gồm 2 chuỗi mật khẩu hoặc null nếu người dùng hủy
     */
    private String[] askNewPasswordPair(String title) {
        JPasswordField pwd1 = new JPasswordField();
        JPasswordField pwd2 = new JPasswordField();

        Object[] message = {
                "Nhập mật khẩu:", pwd1,
                "Xác nhận mật khẩu:", pwd2
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            return new String[]{
                    new String(pwd1.getPassword()).trim(),
                    new String(pwd2.getPassword()).trim()
            };
        }

        return null;
    }

    /**
     * Xử lý thêm mới nhân viên.
     */
    private void actionThem() {
        try {
            NhanVien nv = buildNhanVienFromForm();

            String[] pwds = askNewPasswordPair("Thêm nhân viên mới");
            if (pwds == null) return;

            controller.themNhanVien(nv, pwds[0], pwds[1]);
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
            loadData();
            clearForm();
            notifyReloadOtherPanels();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi thêm nhân viên", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý cập nhật thông tin nhân viên.
     */
    private void actionSua() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để sửa!");
                return;
            }

            String currentPassword = askPassword(
                    "Xác thực sửa thông tin",
                    "Nhập mật khẩu hiện tại của nhân viên:"
            );
            if (currentPassword == null) return;

            controller.capNhatNhanVien(buildNhanVienFromForm(), currentPassword);
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
            loadData();
            clearForm();
            notifyReloadOtherPanels();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi sửa nhân viên", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý cho nhân viên nghỉ làm tạm thời.
     */
    private void actionNghiLam() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
                return;
            }

            String currentPassword = askPassword(
                    "Xác nhận nghỉ làm",
                    "Nhập mật khẩu hiện tại của nhân viên:"
            );
            if (currentPassword == null) return;

            controller.choNghiLam(Long.parseLong(txtId.getText().trim()), currentPassword);
            JOptionPane.showMessageDialog(this, "Đã cho nhân viên nghỉ làm!");
            loadData();
            clearForm();
            notifyReloadOtherPanels();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi cho nghỉ làm", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý cho nhân viên đi làm lại.
     */
    private void actionDiLamLai() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
                return;
            }

            controller.choDiLamLai(Long.parseLong(txtId.getText().trim()));
            JOptionPane.showMessageDialog(this, "Đã cho nhân viên đi làm lại!");
            loadData();
            clearForm();
            notifyReloadOtherPanels();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đi làm lại", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý cho nhân viên nghỉ việc chính thức.
     */
    private void actionNghiViec() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cho nghỉ việc!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn cho nhân viên này nghỉ việc?",
                    "Xác nhận cho nghỉ việc",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm != JOptionPane.YES_OPTION) return;

            String currentPassword = askPassword(
                    "Xác thực cho nghỉ việc",
                    "Nhập mật khẩu hiện tại của nhân viên:"
            );
            if (currentPassword == null) return;

            controller.choNghiViec(Long.parseLong(txtId.getText().trim()), currentPassword);
            JOptionPane.showMessageDialog(this, "Cho nhân viên nghỉ việc thành công!");
            loadData();
            clearForm();
            notifyReloadOtherPanels();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi cho nghỉ việc", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý xóa cứng dữ liệu nhân viên.
     */
    private void actionXoa() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để xóa!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn xóa nhân viên này?",
                    "Xác nhận xóa nhân viên",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm != JOptionPane.YES_OPTION) return;

            String currentPassword = askPassword(
                    "Xác thực xóa nhân viên",
                    "Nhập mật khẩu hiện tại của nhân viên:"
            );
            if (currentPassword == null) return;

            controller.xoaNhanVien(Long.parseLong(txtId.getText().trim()), currentPassword);
            JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
            loadData();
            clearForm();
            notifyReloadOtherPanels();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi xóa nhân viên", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý đặt lại mật khẩu khi quên mật khẩu bằng quyền root.
     */
    private void actionQuenMatKhau() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần đặt lại mật khẩu!");
                return;
            }

            String rootPassword = askPassword(
                    "Xác thực của chủ cửa hàng",
                    "Nhập mật khẩu root:"
            );
            if (rootPassword == null) return;

            String[] pwds = askNewPasswordPair("Đặt lại mật khẩu");
            if (pwds == null) return;

            controller.doiMatKhauBangRoot(
                    Long.parseLong(txtId.getText().trim()),
                    rootPassword,
                    pwds[0],
                    pwds[1]
            );

            JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi quên mật khẩu", JOptionPane.ERROR_MESSAGE);
        }
    }
}