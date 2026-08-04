import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterPanel extends JPanel {
    private JTextField txtRegUser;     // Đảm bảo là JTextField thường
    private JPasswordField txtRegPass; // Dành cho mật khẩu
    private JPasswordField txtRegConfirm;

    public RegisterPanel(JPanel cardContainer, CardLayout cardLayout) {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 25, 6, 25);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // --- Tiêu đề (Đã đổi Font sang Arial để tránh lỗi gãy nét) ---
        JLabel lblSignUp = new JLabel("Sign up");
        lblSignUp.setFont(new Font("Arial", Font.BOLD, 24));
        lblSignUp.setForeground(new Color(40, 40, 40));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 25, 10, 25); // Giảm bớt lề trên để có không gian
        add(lblSignUp, gbc);

        gbc.insets = new Insets(6, 25, 6, 25);

        // --- Form nhập liệu ---

        // 1. Ô Tài khoản: Khởi tạo bằng JTextField (không bị dấu sao)
        txtRegUser = new JTextField();
        // Sửa Font cho text field để tránh lỗi gãy nét khi nhập liệu
        txtRegUser.setFont(new Font("Arial", Font.PLAIN, 14));
        add(LoginFrame.createInputPanel("👤", "Tên tài khoản mới", txtRegUser), gbc);

        // 2. Ô Mật khẩu
        gbc.gridy = 1;
        txtRegPass = new JPasswordField();
        txtRegPass.setFont(new Font("Arial", Font.PLAIN, 14));
        add(LoginFrame.createInputPanel("🔒", "Mật khẩu", txtRegPass), gbc);

        // 3. Ô Xác nhận mật khẩu
        gbc.gridy = 2;
        txtRegConfirm = new JPasswordField();
        txtRegConfirm.setFont(new Font("Arial", Font.PLAIN, 14));
        add(LoginFrame.createInputPanel("🔄", "Xác nhận mật khẩu", txtRegConfirm), gbc);

        // --- Nút Đăng ký ---
        JButton btnRegister = LoginFrame.createStyledButton("Tạo tài khoản", LoginFrame.COLOR_PRIMARY, Color.WHITE, true);
        // Đảm bảo Font của nút cũng được dùng font an toàn
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 3;
        gbc.insets = new Insets(15, 25, 5, 25);
        add(btnRegister, gbc);

        // --- Chuyển về Đăng nhập ---
        JPanel pnlLoginLink = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pnlLoginLink.setBackground(Color.WHITE);

        JLabel lblText = new JLabel("Đã có tài khoản?");
        lblText.setFont(new Font("Arial", Font.PLAIN, 12));
        lblText.setForeground(Color.GRAY);

        JLabel lblGoToLogin = new JLabel("Đăng nhập ngay");
        lblGoToLogin.setFont(new Font("Arial", Font.BOLD, 12));
        lblGoToLogin.setForeground(LoginFrame.COLOR_PRIMARY);
        lblGoToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlLoginLink.add(lblText);
        pnlLoginLink.add(lblGoToLogin);

        gbc.gridy = 4;
        gbc.insets = new Insets(5, 25, 10, 25);
        add(pnlLoginLink, gbc);

        // --- XỬ LÝ SỰ KIỆN ---
        // Lật bài về lại form Đăng nhập
        lblGoToLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Clear data cũ trước khi sang trang login
                txtRegUser.setText("");
                txtRegPass.setText("");
                txtRegConfirm.setText("");
                cardLayout.show(cardContainer, "LOGIN");
            }
        });

        // Xử lý nút Đăng ký
        btnRegister.addActionListener(e -> {
            String user = txtRegUser.getText().trim();
            String pass = new String(txtRegPass.getPassword());
            String confirm = new String(txtRegConfirm.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, "Đăng ký thành công tài khoản: " + user + "\nVui lòng đăng nhập để tiếp tục.");

            // Xóa rỗng form và tự động quay về trang đăng nhập
            txtRegUser.setText("");
            txtRegPass.setText("");
            txtRegConfirm.setText("");
            cardLayout.show(cardContainer, "LOGIN");
        });
    }
}