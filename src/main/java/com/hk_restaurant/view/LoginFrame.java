package com.hk_restaurant.view;

import com.hk_restaurant.controller.NhanVienController;
import com.hk_restaurant.model.entity.NhanVien;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Giao diện đăng nhập của hệ thống HK Restaurant.
 * Màn hình này dùng để xác thực tài khoản nhân viên trước khi truy cập vào hệ thống chính.
 */
public class LoginFrame extends JFrame {

    private final NhanVienController nhanVienController;
    private final Consumer<NhanVien> onLoginSuccess;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkRemember;
    private JButton btnLogin;

    /**
     * Khởi tạo cửa sổ đăng nhập.
     *
     * @param nhanVienController controller xử lý đăng nhập nhân viên
     * @param onLoginSuccess hàm callback được gọi khi đăng nhập thành công, nhận về nhân viên đăng nhập
     */
    public LoginFrame(NhanVienController nhanVienController, Consumer<NhanVien> onLoginSuccess) {
        this.nhanVienController = nhanVienController;
        this.onLoginSuccess = onLoginSuccess;
        initUI();
    }

    /**
     * Khởi tạo toàn bộ giao diện đăng nhập và gắn các sự kiện chính.
     */
    private void initUI() {
        setTitle("Đăng nhập hệ thống - HK Restaurant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new GridLayout(1, 2, 0, 0));

        add(buildLeftPanel());
        add(buildRightPanel());

        btnLogin.addActionListener(e -> handleLogin());

        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);
    }

    /**
     * Tạo panel bên trái của giao diện đăng nhập, hiển thị ảnh nền và thông điệp giới thiệu.
     *
     * @return panel bên trái
     */
    private JPanel buildLeftPanel() {
        ImageHeroPanel leftPanel = new ImageHeroPanel("/images/bg_restaurant.jpg");
        leftPanel.setLayout(null);
        leftPanel.setBackground(Color.BLACK);

        JPanel glass = new JPanel(null);
        glass.setOpaque(false);
        glass.setBorder(BorderFactory.createLineBorder(new Color(206, 172, 106, 100), 1));
        glass.setBounds(22, 24, 500, 500);

        JLabel lblBrand = new JLabel("HK RESTAURANT");
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 38));
        lblBrand.setForeground(new Color(222, 186, 110));
        lblBrand.setBounds(34, 118, 390, 46);

        JLabel lblDesc = new JLabel(
                "<html><div style='width:300px;'>"
                        + "Một hệ thống quản trị<br>"
                        + "chuyên nghiệp cho nhà<br>"
                        + "hàng của bạn.<br>"
                        + "Quản lý liền mạch, tối ưu<br>"
                        + "doanh thu, nâng cao trải<br>"
                        + "nghiệm khách hàng."
                        + "</div></html>"
        );
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblDesc.setForeground(new Color(241, 220, 177));
        lblDesc.setBounds(34, 208, 310, 190);

        glass.add(lblBrand);
        glass.add(lblDesc);

        leftPanel.add(glass);
        return leftPanel;
    }

    /**
     * Tạo panel bên phải của giao diện đăng nhập, bao gồm form nhập tài khoản và mật khẩu.
     *
     * @return panel bên phải
     */
    private JPanel buildRightPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(248, 246, 240));

        JPanel outer = new JPanel(null);
        outer.setOpaque(false);
        outer.setPreferredSize(new Dimension(360, 520));

        MarbleCard card = new MarbleCard();
        card.setLayout(null);
        card.setBounds(10, 10, 340, 430);

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(8, 72, 88));
        lblTitle.setBounds(45, 48, 250, 34);

        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblUser.setForeground(new Color(44, 38, 31));
        lblUser.setBounds(48, 104, 120, 20);

        txtUsername = new IconTextField();
        txtUsername.setBounds(46, 130, 250, 38);

        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblPass.setForeground(new Color(44, 38, 31));
        lblPass.setBounds(48, 194, 90, 20);

        txtPassword = new EyePasswordField();
        txtPassword.setToolTipText("Bấm vào biểu tượng con mắt để hiện hoặc ẩn mật khẩu");
        txtPassword.setBounds(46, 220, 250, 38);

        chkRemember = new JCheckBox("Ghi nhớ đăng nhập");
        chkRemember.setBounds(46, 272, 170, 24);
        chkRemember.setOpaque(false);
        chkRemember.setFocusPainted(false);
        chkRemember.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chkRemember.setForeground(new Color(31, 80, 87));
        chkRemember.setIcon(new CheckBoxIcon(false));
        chkRemember.setSelectedIcon(new CheckBoxIcon(true));

        btnLogin = new GoldLuxuryButton("ĐĂNG NHẬP");
        btnLogin.setBounds(46, 330, 250, 40);

        JLabel lblHK = new JLabel("HK", SwingConstants.CENTER);
        lblHK.setFont(new Font("Serif", Font.PLAIN, 20));
        lblHK.setForeground(new Color(191, 163, 103));
        lblHK.setBounds(150, 490, 50, 22);

        card.add(lblTitle);
        card.add(lblUser);
        card.add(txtUsername);
        card.add(lblPass);
        card.add(txtPassword);
        card.add(chkRemember);
        card.add(btnLogin);

        outer.add(card);
        outer.add(lblHK);

        wrapper.add(outer);
        return wrapper;
    }

    /**
     * Xử lý đăng nhập khi người dùng nhấn nút hoặc phím Enter.
     * Nếu thành công, cửa sổ đăng nhập đóng lại và chuyển vào màn hình chính.
     */
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            NhanVien currentUser = nhanVienController.dangNhap(username, password);

            dispose();
            if (onLoginSuccess != null) {
                onLoginSuccess.accept(currentUser);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE
            );
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    /**
     * Panel tùy biến dùng để hiển thị ảnh nền hoặc gradient ở bên trái giao diện đăng nhập.
     */
    static class ImageHeroPanel extends JPanel {
        private BufferedImage bg;

        public ImageHeroPanel(String resourcePath) {
            try {
                URL url = getClass().getResource(resourcePath);
                if (url != null) {
                    bg = ImageIO.read(url);
                }
            } catch (IOException ignored) {
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            if (bg != null) {
                g2.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            } else {
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(40, 30, 24),
                        0, getHeight(), new Color(15, 12, 10)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            GradientPaint overlay = new GradientPaint(
                    0, 0, new Color(0, 0, 0, 105),
                    getWidth(), getHeight(), new Color(22, 17, 12, 130)
            );
            g2.setPaint(overlay);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(0, 0, 0, 45));
            g2.fillRect(0, 0, 240, getHeight());

            g2.dispose();
        }
    }

    /**
     * Panel card tùy biến với hiệu ứng nền sáng và đường viền trang trí.
     */
    static class MarbleCard extends JPanel {
        public MarbleCard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            GradientPaint bg = new GradientPaint(
                    0, 0, new Color(252, 250, 245),
                    w, h, new Color(245, 241, 233)
            );
            g2.setPaint(bg);
            g2.fillRoundRect(0, 0, w, h, 20, 20);

            g2.setColor(new Color(214, 204, 186, 28));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawArc(18, 28, 120, 56, 5, 160);
            g2.drawArc(96, 58, 100, 60, 180, 120);
            g2.drawArc(24, 160, 130, 70, 210, 105);
            g2.drawArc(100, 250, 100, 70, 20, 140);

            g2.setColor(new Color(203, 176, 117));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(2, 2, w - 5, h - 5, 20, 20);

            g2.setColor(new Color(214, 193, 149));
            g2.drawRoundRect(15, 15, w - 31, h - 31, 14, 14);

            drawCorner(g2, 8, 8, false, false);
            drawCorner(g2, w - 48, 8, true, false);
            drawCorner(g2, 8, h - 48, false, true);
            drawCorner(g2, w - 48, h - 48, true, true);

            g2.dispose();
            super.paintComponent(g);
        }

        private void drawCorner(Graphics2D g2, int x, int y, boolean flipX, boolean flipY) {
            Graphics2D c = (Graphics2D) g2.create();
            c.translate(x, y);
            if (flipX || flipY) {
                c.translate(flipX ? 40 : 0, flipY ? 40 : 0);
                c.scale(flipX ? -1 : 1, flipY ? -1 : 1);
            }
            c.setColor(new Color(203, 176, 117, 140));
            c.setStroke(new BasicStroke(1f));
            c.drawArc(0, 0, 28, 28, 0, 90);
            c.drawArc(5, 4, 22, 22, 12, 78);
            c.dispose();
        }
    }

    /**
     * Ô nhập tên đăng nhập tùy biến với icon người dùng.
     */
    static class IconTextField extends JTextField {
        public IconTextField() {
            setFont(new Font("SansSerif", Font.PLAIN, 14));
            setForeground(new Color(32, 32, 32));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(7, 34, 7, 10));
            setCaretColor(new Color(145, 113, 56));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 253, 249));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            g2.setColor(new Color(133, 106, 58));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);

            drawUserIcon(g2, 12, 10);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
        }
    }

    /**
     * Ô nhập mật khẩu tùy biến với icon ổ khóa và con mắt bật tắt hiển thị mật khẩu.
     */
    static class EyePasswordField extends JPasswordField {
        private boolean passwordVisible = false;
        private final Rectangle eyeBounds = new Rectangle();

        public EyePasswordField() {
            setFont(new Font("SansSerif", Font.PLAIN, 14));
            setForeground(new Color(32, 32, 32));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(7, 34, 7, 36));
            setCaretColor(new Color(145, 113, 56));
            setEchoChar('•');

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (eyeBounds.contains(e.getPoint())) {
                        togglePasswordVisible();
                    }
                }
            });
        }

        private void togglePasswordVisible() {
            passwordVisible = !passwordVisible;
            setEchoChar(passwordVisible ? (char) 0 : '•');
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 253, 249));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            g2.setColor(new Color(133, 106, 58));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);

            drawLockIcon(g2, 12, 9);
            drawEyeIcon(g2, getWidth() - 24, 12, passwordVisible);
            eyeBounds.setBounds(getWidth() - 30, 8, 22, 22);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
        }
    }

    /**
     * Nút màu vàng tùy biến dùng cho thao tác đăng nhập chính.
     */
    static class GoldLuxuryButton extends JButton {
        public GoldLuxuryButton(String text) {
            super(text);
            setFont(new Font("SansSerif", Font.BOLD, 15));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(170, 145, 92, 35));
            g2.fillRoundRect(3, 4, getWidth() - 6, getHeight() - 6, 10, 10);

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(220, 190, 125),
                    0, getHeight(), new Color(204, 172, 105)
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 4, 10, 10);

            g2.setColor(new Color(228, 213, 176, 150));
            g2.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 5, 10, 10);

            FontMetrics fm = g2.getFontMetrics(getFont());
            int x = ((getWidth() - 3) - fm.stringWidth(getText())) / 2;
            int y = ((getHeight() - 4) - fm.getHeight()) / 2 + fm.getAscent();

            g2.setFont(getFont());
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), x, y);

            g2.dispose();
        }

        @Override
        public boolean contains(int x, int y) {
            return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10).contains(x, y);
        }
    }

    /**
     * Icon tùy biến cho checkbox ghi nhớ đăng nhập.
     */
    static class CheckBoxIcon implements Icon {
        private final boolean selected;

        public CheckBoxIcon(boolean selected) {
            this.selected = selected;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (selected) {
                g2.setColor(new Color(18, 95, 98));
                g2.fillRoundRect(x, y, 16, 16, 4, 4);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawLine(x + 4, y + 9, x + 6, y + 12);
                g2.drawLine(x + 6, y + 12, x + 12, y + 5);
            } else {
                g2.setColor(new Color(190, 190, 190));
                g2.drawRoundRect(x, y, 16, 16, 4, 4);
            }

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }

    private static void drawUserIcon(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(199, 160, 85));
        g2.fillOval(x + 4, y, 8, 8);
        g2.fillRoundRect(x, y + 10, 15, 8, 6, 6);
    }

    private static void drawLockIcon(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(199, 160, 85));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x + 3, y + 7, 12, 10, 4, 4);
        g2.drawArc(x + 5, y, 8, 10, 0, 180);
        g2.fillRect(x + 8, y + 10, 2, 4);
    }

    /**
     * Vẽ icon con mắt trong ô mật khẩu.
     * Khi hide=false thì hiển thị thêm gạch chéo để biểu diễn trạng thái ẩn.
     */
    private static void drawEyeIcon(Graphics2D g2, int x, int y, boolean visible) {
        g2.setColor(new Color(199, 160, 85));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x, y + 2, 14, 8);
        g2.fillOval(x + 5, y + 4, 4, 4);
        if (!visible) {
            g2.drawLine(x - 1, y + 12, x + 15, y);
        }
    }
}
