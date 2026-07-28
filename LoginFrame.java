import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    private static final Color COLOR_BG = new Color(12, 14, 12);
    private static final Color COLOR_GREEN = new Color(0, 230, 118);
    private static final Color COLOR_GREEN_DIM = new Color(0, 140, 70);
    private static final Color COLOR_PANEL = new Color(22, 26, 22);
    private static final Color COLOR_DANGER = new Color(200, 70, 70);

    // Panel nền đen có vẽ họa tiết quyển sách mở bằng code
    static class BookBackgroundPanel extends JPanel {
        BookBackgroundPanel(LayoutManager lm) {
            super(lm);
            setOpaque(true);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_BG);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Vẽ quyển sách mở mờ ở giữa làm họa tiết trang trí
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int w = Math.min(getWidth(), 300);
            int h = w / 2;

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
            g2.setColor(COLOR_GREEN);
            g2.setStroke(new BasicStroke(2f));

            GeneralPath left = new GeneralPath();
            left.moveTo(cx, cy - h / 2);
            left.quadTo(cx - w / 2, cy - h / 2 - 15, cx - w / 2, cy);
            left.quadTo(cx - w / 2, cy + h / 2, cx, cy + h / 2 - 10);
            left.closePath();

            GeneralPath right = new GeneralPath();
            right.moveTo(cx, cy - h / 2);
            right.quadTo(cx + w / 2, cy - h / 2 - 15, cx + w / 2, cy);
            right.quadTo(cx + w / 2, cy + h / 2, cx, cy + h / 2 - 10);
            right.closePath();

            g2.draw(left);
            g2.draw(right);
            g2.drawLine(cx, cy - h / 2, cx, cy + h / 2 - 10);

            g2.dispose();
        }
    }

    public LoginFrame() {
        setTitle("ĐĂNG NHẬP HỆ THỐNG QUẢN LÝ PHÒNG HỌC");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BookBackgroundPanel root = new BookBackgroundPanel(new BorderLayout(10, 10));
        setContentPane(root);

        // ---- Tiêu đề ----
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_GREEN);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(24, 10, 10, 10));
        root.add(lblTitle, BorderLayout.NORTH);

        // ---- Form nhập ----
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 12, 15));
        panelForm.setOpaque(false);
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 35, 15, 35));

        JLabel lblUser = new JLabel("Tài khoản:");
        lblUser.setFont(new Font("Consolas", Font.PLAIN, 14));
        lblUser.setForeground(COLOR_GREEN);
        panelForm.add(lblUser);

        txtUsername = new JTextField();
        styleField(txtUsername);
        panelForm.add(txtUsername);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Consolas", Font.PLAIN, 14));
        lblPass.setForeground(COLOR_GREEN);
        panelForm.add(lblPass);

        txtPassword = new JPasswordField();
        styleField(txtPassword);
        panelForm.add(txtPassword);

        root.add(panelForm, BorderLayout.CENTER);

        // ---- Nút bấm ----
        JPanel panelButton = new JPanel();
        panelButton.setOpaque(false);
        panelButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        JButton btnLogin = createStyledButton("Đăng nhập", COLOR_GREEN);
        JButton btnExit = createStyledButton("Thoát", COLOR_DANGER);

        panelButton.add(btnLogin);
        panelButton.add(btnExit);
        root.add(panelButton, BorderLayout.SOUTH);

        // --- Xử lý sự kiện ---
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = txtUsername.getText().trim();
                String pass = new String(txtPassword.getPassword()).trim();

                if (user.equals("admin") && pass.equals("123")) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Đăng nhập thành công với quyền ADMIN!");
                    new MainFrame(true).setVisible(true);
                    dispose();
                } else if (user.equals("user") && pass.equals("123")) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Đăng nhập thành công với quyền USER!");
                    new MainFrame(false).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Tài khoản hoặc mật khẩu không đúng!\n\nGợi ý đăng nhập:\n- Quyền Admin: admin / 123\n- Quyền User: user / 123",
                            "Lỗi Đăng Nhập",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnExit.addActionListener(e -> System.exit(0));
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Consolas", Font.PLAIN, 14));
        field.setBackground(COLOR_PANEL);
        field.setForeground(COLOR_GREEN);
        field.setCaretColor(COLOR_GREEN);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_GREEN_DIM, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    private JButton createStyledButton(String text, Color accent) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.BOLD, 13));
        btn.setForeground(accent);
        btn.setBackground(COLOR_PANEL);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
