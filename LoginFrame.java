import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("ĐĂNG NHẬP HỆ THỐNG");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // Form
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        panelForm.add(new JLabel("Tài khoản:"));
        txtUsername = new JTextField();
        panelForm.add(txtUsername);

        panelForm.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        panelForm.add(txtPassword);

        add(panelForm, BorderLayout.CENTER);

        // Nút bấm
        JPanel panelButton = new JPanel();
        JButton btnLogin = new JButton("Đăng nhập");
        panelButton.add(btnLogin);
        add(panelButton, BorderLayout.SOUTH);

        // Xử lý sự kiện đăng nhập
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = txtUsername.getText();
                String pass = new String(txtPassword.getPassword());

                // Mẫu kiểm tra tài khoản đơn giản
                if (user.equals("admin") && pass.equals("123")) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Đăng nhập thành công với quyền Admin!");
                    new MainFrame().setVisible(true); // Mở màn hình chính
                    dispose(); // Đóng màn hình login
                } else if (user.equals("user") && pass.equals("123")) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Đăng nhập thành công!");
                    new MainFrame().setVisible(true); // Mở màn hình chính
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Tài khoản hoặc mật khẩu không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
