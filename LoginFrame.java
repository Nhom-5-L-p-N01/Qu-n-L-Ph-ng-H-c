import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    // Bảng màu chủ đạo
    private final Color COLOR_PRIMARY = new Color(41, 98, 255);
    private final Color COLOR_PRIMARY_DARK = new Color(25, 70, 190);
    private final Color COLOR_BG = new Color(240, 244, 255);
    private final Color COLOR_DANGER = new Color(220, 60, 60);

    public LoginFrame() {
        setTitle("ĐĂNG NHẬP HỆ THỐNG QUẢN LÝ PHÒNG HỌC");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COLOR_BG);

        // ---- Tiêu đề ----
        JLabel lblTitle = new JLabel("🔐  ĐĂNG NHẬP HỆ THỐNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_PRIMARY_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(lblTitle, BorderLayout.NORTH);

        // ---- Form nhập ----
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 12, 15));
        panelForm.setBackground(COLOR_BG);
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 35, 15, 35));

        JLabel lblUser = new JLabel("👤 Tài khoản:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelForm.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        panelForm.add(txtUsername);

        JLabel lblPass = new JLabel("🔑 Mật khẩu:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelForm.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        panelForm.add(txtPassword);

        add(panelForm, BorderLayout.CENTER);

        // ---- Nút bấm ----
        JPanel panelButton = new JPanel();
        panelButton.setBackground(COLOR_BG);
        panelButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton btnLogin = createStyledButton("➜ Đăng nhập", COLOR_PRIMARY);
        JButton btnExit = createStyledButton("✕ Thoát", COLOR_DANGER);

        panelButton.add(btnLogin);
        panelButton.add(btnExit);
        add(panelButton, BorderLayout.SOUTH);

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

    // Tạo nút bấm có màu nền, chữ trắng, bo góc nhẹ
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
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
