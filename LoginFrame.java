import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    private static final Color COLOR_ACCENT = new Color(41, 182, 246);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_PANEL = new Color(8, 14, 28, 200);
    private static final Color COLOR_DANGER = new Color(230, 90, 90);

    // Panel vẽ ảnh nền thật, co giãn theo kích thước cửa sổ + phủ mờ đen để chữ dễ đọc
    static class ImageBackgroundPanel extends JPanel {
        private Image bgImage;

        ImageBackgroundPanel(LayoutManager lm) {
            super(lm);
            setOpaque(true);
            try {
                bgImage = ImageIO.read(new File("background.jpg"));
            } catch (IOException e) {
                bgImage = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                // Lớp phủ đen mờ giúp chữ trắng nổi bật, dễ đọc hơn trên ảnh nền
                g.setColor(new Color(0, 0, 0, 115));
                g.fillRect(0, 0, getWidth(), getHeight());
            } else {
                g.setColor(new Color(6, 10, 22));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public LoginFrame() {
        setTitle("ĐĂNG NHẬP HỆ THỐNG QUẢN LÝ PHÒNG HỌC");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageBackgroundPanel root = new ImageBackgroundPanel(new BorderLayout(10, 10));
        setContentPane(root);

        // ---- Tiêu đề ----
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(24, 10, 10, 10));
        root.add(lblTitle, BorderLayout.NORTH);

        // ---- Form nhập (đặt trong khung nền mờ để dễ đọc chữ) ----
        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setOpaque(false);

        JPanel panelForm = new JPanel(new GridLayout(2, 2, 12, 15));
        panelForm.setBackground(COLOR_PANEL);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel lblUser = new JLabel("Tài khoản:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(COLOR_ACCENT);
        panelForm.add(lblUser);

        txtUsername = new JTextField();
        styleField(txtUsername);
        panelForm.add(txtUsername);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setForeground(COLOR_ACCENT);
        panelForm.add(lblPass);

        txtPassword = new JPasswordField();
        styleField(txtPassword);
        panelForm.add(txtPassword);

        formWrapper.add(panelForm);
        root.add(formWrapper, BorderLayout.CENTER);

        // ---- Nút bấm ----
        JPanel panelButton = new JPanel();
        panelButton.setOpaque(false);
        panelButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        JButton btnLogin = createStyledButton("Đăng nhập", COLOR_ACCENT);
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
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(255, 255, 255, 230));
        field.setForeground(new Color(10, 20, 40));
        field.setCaretColor(COLOR_ACCENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    private JButton createStyledButton(String text, Color accent) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(COLOR_WHITE);
        btn.setBackground(accent);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
