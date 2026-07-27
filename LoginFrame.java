import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame() {
        // Cấu hình giao diện cửa sổ Đăng nhập
        setTitle("ĐĂNG NHẬP HỆ THỐNG QUẢN LÝ PHÒNG HỌC");
        setSize(380, 230);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Tiêu đề Màn hình Đăng nhập
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.BLUE);
        add(lblTitle, BorderLayout.NORTH);

        // Form nhập tài khoản và mật khẩu
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        panelForm.add(new JLabel("Tài khoản:"));
        txtUsername = new JTextField();
        panelForm.add(txtUsername);

        panelForm.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        panelForm.add(txtPassword);

        add(panelForm, BorderLayout.CENTER);

        // Thanh chứa nút bấm
        JPanel panelButton = new JPanel();
        JButton btnLogin = new JButton("Đăng nhập");
        JButton btnExit = new JButton("Thoát");
        
        panelButton.add(btnLogin);
        panelButton.add(btnExit);
        add(panelButton, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN NÚT BẤM ---

        // Nút Đăng nhập: Kiểm tra tài khoản & Chuyển sang MainFrame
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = txtUsername.getText().trim();
                String pass = new String(txtPassword.getPassword()).trim();

                // 1. Kiểm tra tài khoản ADMIN
                if (user.equals("admin") && pass.equals("123")) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Đăng nhập thành công với quyền ADMIN!");
                    
                    // Mở giao diện chính với quyền Admin (Mở thêm nút Duyệt phòng)
                    new MainFrame(true).setVisible(true); 
                    
                    dispose(); // Đóng màn hình Đăng nhập
                } 
                // 2. Kiểm tra tài khoản USER (Sinh viên/Giảng viên)
                else if (user.equals("user") && pass.equals("123")) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Đăng nhập thành công với quyền USER!");
                    
                    // Mở giao diện chính với quyền User thường
                    new MainFrame(false).setVisible(true); 
                    
                    dispose(); // Đóng màn hình Đăng nhập
                } 
                // 3. Nhập sai
                else {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                        "Tài khoản hoặc mật khẩu không đúng!\n\nGợi ý đăng nhập:\n- Quyền Admin: admin / 123\n- Quyền User: user / 123", 
                        "Lỗi Đăng Nhập", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Nút Thoát
        btnExit.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
