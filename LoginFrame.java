import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Màn hình đăng nhập hệ thống.
 */
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    private final AccountRepository accountRepository = new AccountRepository();

    // Chỉ đúng tài khoản này mới được cấp quyền ADMIN
    private static final String ADMIN_EMAIL = "admin123@gmail.com";
    private static final String ADMIN_PASSWORD = "mochirangrua";

    public LoginFrame() {
        setTitle("Đăng nhập hệ thống quản lý phòng học");
        setSize(440, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        UITheme.BackgroundPanel root = new UITheme.BackgroundPanel(new BorderLayout(10, 10));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setOpaque(false);
        formWrapper.add(buildFormCard());
        root.add(formWrapper, BorderLayout.CENTER);

        root.add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(28, 10, 10, 10));

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", JLabel.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Quản lý đặt phòng học", JLabel.CENTER);
        lblSubtitle.setFont(UITheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(UITheme.TEXT_MUTED);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        header.add(lblTitle);
        header.add(lblSubtitle);
        return header;
    }

    private JPanel buildFormCard() {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(
                new GridLayout(2, 1, 0, 16), 20, UITheme.GLASS_PANEL, UITheme.PRIMARY);
        card.setBorder(BorderFactory.createEmptyBorder(26, 28, 26, 28));
        card.setPreferredSize(new Dimension(340, 150));

        JPanel rowUser = new JPanel(new BorderLayout(0, 6));
        rowUser.setOpaque(false);
        rowUser.add(UITheme.fieldLabel("Tài khoản / Email", IconFactory.of(IconFactory.Type.USER, UITheme.PRIMARY, 16)), BorderLayout.NORTH);
        txtUsername = UITheme.roundedTextField();
        rowUser.add(txtUsername, BorderLayout.CENTER);

        JPanel rowPass = new JPanel(new BorderLayout(0, 6));
        rowPass.setOpaque(false);
        rowPass.add(UITheme.fieldLabel("Mật khẩu", IconFactory.of(IconFactory.Type.LOCK, UITheme.PRIMARY, 16)), BorderLayout.NORTH);
        txtPassword = UITheme.roundedPasswordField();
        rowPass.add(txtPassword, BorderLayout.CENTER);

        card.add(rowUser);
        card.add(rowPass);
        return card;
    }

    private JPanel buildFooter() {
        JPanel panelSouth = new JPanel();
        panelSouth.setOpaque(false);
        panelSouth.setLayout(new BoxLayout(panelSouth, BoxLayout.Y_AXIS));
        panelSouth.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));

        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panelButton.setOpaque(false);
        panelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnLogin = new UITheme.RoundedButton("Đăng nhập", UITheme.PRIMARY_DARK,
                IconFactory.of(IconFactory.Type.CHECK, UITheme.WHITE, 16));
        JButton btnExit = new UITheme.RoundedButton("Thoát", UITheme.DANGER,
                IconFactory.of(IconFactory.Type.LOGOUT, UITheme.WHITE, 16));

        panelButton.add(btnLogin);
        panelButton.add(btnExit);

        JButton btnRegisterLink = new JButton("Chưa có tài khoản? Đăng ký ngay",
                IconFactory.of(IconFactory.Type.ADD_USER, UITheme.PRIMARY, 14));
        btnRegisterLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegisterLink.setFont(UITheme.FONT_LINK);
        btnRegisterLink.setForeground(UITheme.PRIMARY);
        btnRegisterLink.setBorderPainted(false);
        btnRegisterLink.setContentAreaFilled(false);
        btnRegisterLink.setFocusPainted(false);
        btnRegisterLink.setIconTextGap(6);
        btnRegisterLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelSouth.add(panelButton);
        panelSouth.add(Box.createVerticalStrut(10));
        panelSouth.add(btnRegisterLink);

        btnRegisterLink.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thucHienDangNhap();
            }
        });

        btnExit.addActionListener(e -> System.exit(0));

        return panelSouth;
    }

    // ĐÃ SỬA: bỏ 2 lối tắt cũ (admin/123 và user/123). Giờ chỉ đúng
    // ADMIN_EMAIL + ADMIN_PASSWORD mới được cấp quyền Admin; mọi tài khoản
    // khác đăng nhập bình thường qua danh sách đã đăng ký (AccountRepository).
    private void thucHienDangNhap() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (user.equalsIgnoreCase(ADMIN_EMAIL) && pass.equals(ADMIN_PASSWORD)) {
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công với quyền ADMIN!");
            new MainFrame(true).setVisible(true);
            dispose();
            return;
        }

        Account acc = accountRepository.dangNhap(user, pass);
        if (acc != null) {
            JOptionPane.showMessageDialog(this, "Xin chào " + acc.getHoTen() + "! Đăng nhập thành công.");
            new MainFrame(false).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tài khoản hoặc mật khẩu không đúng!\n\nVui lòng đăng ký tài khoản nếu chưa có.",
                    "Lỗi Đăng Nhập",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
