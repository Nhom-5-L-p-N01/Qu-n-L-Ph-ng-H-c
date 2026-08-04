import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    // Để public (public static) để file RegisterPanel.java có thể dùng chung
    public static final Color COLOR_PRIMARY = new Color(21, 91, 161);
    public static final Color COLOR_BG = new Color(38, 114, 201);
    public static final Color COLOR_LEFT_PANEL = new Color(19, 105, 190);
    public static final Color COLOR_WHITE = Color.WHITE;
    public static final Color COLOR_INPUT_BG = new Color(243, 245, 249);
    public static final Color COLOR_TEXT_DARK = new Color(40, 40, 40);

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
            } else {
                g.setColor(COLOR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public LoginFrame() {
        setTitle("HỆ THỐNG QUẢN LÝ PHÒNG HỌC");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageBackgroundPanel root = new ImageBackgroundPanel(new GridBagLayout());
        setContentPane(root);

        JPanel mainCard = new JPanel(new GridLayout(1, 2));
        mainCard.setPreferredSize(new Dimension(700, 420));
        mainCard.setBackground(COLOR_WHITE);
        mainCard.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1));

        // ================= CỘT TRÁI (Trang trí) =================
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_LEFT_PANEL);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(36, 128, 222));
                g2.fillOval(-80, -50, 300, 300);
                g2.fillOval(getWidth() - 100, getHeight() - 100, 150, 150);
                g2.fillOval(50, getHeight() - 120, 200, 200);
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setOpaque(true);

        JPanel leftContent = new JPanel();
        leftContent.setOpaque(false);
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));

        JLabel lblWelcome = new JLabel("WELCOME");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(COLOR_WHITE);

        JLabel lblSubtitle = new JLabel("QUẢN LÝ PHÒNG HỌC");
        lblSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));

        JLabel lblDesc = new JLabel("<html>Chào mừng bạn quay trở lại.<br>Vui lòng đăng nhập hoặc<br>đăng ký để tiếp tục.</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(255, 255, 255, 150));
        lblDesc.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        leftContent.add(lblWelcome);
        leftContent.add(lblSubtitle);
        leftContent.add(lblDesc);
        leftPanel.add(leftContent);

        // ================= CỘT PHẢI (Container chứa CardLayout) =================
        JPanel rightCardContainer = new JPanel(new CardLayout());
        CardLayout cardLayout = (CardLayout) rightCardContainer.getLayout();

        // 1. Tạo Giao diện Đăng Nhập
        JPanel pnlLogin = createLoginPanel(rightCardContainer, cardLayout);

        // 2. Lấy Giao diện Đăng Ký (từ file RegisterPanel.java)
        RegisterPanel pnlRegister = new RegisterPanel(rightCardContainer, cardLayout);

        // Thêm 2 "lá bài" vào Container
        rightCardContainer.add(pnlLogin, "LOGIN");
        rightCardContainer.add(pnlRegister, "REGISTER");

        // Ghép Trái & Phải
        mainCard.add(leftPanel);
        mainCard.add(rightCardContainer);
        root.add(mainCard);
    }

    // Hàm tạo giao diện Đăng nhập
    private JPanel createLoginPanel(JPanel cardContainer, CardLayout cardLayout) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 25, 6, 25);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblSignIn = new JLabel("Sign in");
        lblSignIn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSignIn.setForeground(COLOR_TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 25, 20, 25);
        panel.add(lblSignIn, gbc);

        gbc.insets = new Insets(6, 25, 6, 25);
        txtUsername = new JTextField();
        gbc.gridy = 1;
        panel.add(createInputPanel("👤", "Tài khoản", txtUsername), gbc);

        txtPassword = new JPasswordField();
        gbc.gridy = 2;
        panel.add(createInputPanel("🔒", "Mật khẩu", txtPassword), gbc);

        JPanel extraPanel = new JPanel(new BorderLayout());
        extraPanel.setBackground(COLOR_WHITE);
        JCheckBox chkRemember = new JCheckBox("Ghi nhớ");
        chkRemember.setBackground(COLOR_WHITE);
        chkRemember.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JLabel lblForgot = new JLabel("Quên mật khẩu?");
        lblForgot.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblForgot.setForeground(COLOR_PRIMARY);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));

        extraPanel.add(chkRemember, BorderLayout.WEST);
        extraPanel.add(lblForgot, BorderLayout.EAST);
        gbc.gridy = 3;
        panel.add(extraPanel, gbc);

        JButton btnLogin = createStyledButton("Đăng nhập", COLOR_PRIMARY, COLOR_WHITE, true);
        gbc.gridy = 4;
        gbc.insets = new Insets(15, 25, 10, 25);
        panel.add(btnLogin, gbc);

        // Liên kết sang màn hình Đăng Ký
        JPanel pnlRegisterLink = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pnlRegisterLink.setBackground(COLOR_WHITE);

        JLabel lblText = new JLabel("Chưa có tài khoản?");
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblText.setForeground(Color.GRAY);

        JLabel lblGoToRegister = new JLabel("Đăng ký ngay");
        lblGoToRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblGoToRegister.setForeground(COLOR_PRIMARY);
        lblGoToRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlRegisterLink.add(lblText);
        pnlRegisterLink.add(lblGoToRegister);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 25, 15, 25);
        panel.add(pnlRegisterLink, gbc);

        // Nút Thoát
        JButton btnExit = createStyledButton("Thoát", COLOR_WHITE, COLOR_TEXT_DARK, false);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 25, 15, 25);
        panel.add(btnExit, gbc);

        // Xử lý sự kiện lật bài sang Đăng ký
        lblGoToRegister.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardContainer, "REGISTER"); // Lật sang form Đăng ký
            }
        });

        // Xử lý sự kiện gốc
        btnLogin.addActionListener(e -> {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();

            if (user.equals("VietAdmin") && pass.equals("363636")) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Đăng nhập thành công với quyền ADMIN!");
                new MainFrame(true).setVisible(true);
                dispose();
            } else if (user.equals("VietUser") && pass.equals("636363")) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Đăng nhập thành công với quyền USER!");
                new MainFrame(false).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Tài khoản hoặc mật khẩu không đúng!\n\nGợi ý: \n- Admin: VietAdmin / 363636\n- User: VietUser / 636363",
                        "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnExit.addActionListener(e -> System.exit(0));

        return panel;
    }

    // Các hàm dùng chung (đã chuyển thành public static để file kia xài ké)
    public static JPanel createInputPanel(String iconStr, String placeholder, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(COLOR_INPUT_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel lblIcon = new JLabel(iconStr);
        lblIcon.setForeground(Color.GRAY);
        panel.add(lblIcon, BorderLayout.WEST);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(COLOR_INPUT_BG);
        field.setBorder(null);
        field.setOpaque(true);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    public static JButton createStyledButton(String text, Color bg, Color fg, boolean isSolid) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isSolid) {
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        } else {
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(9, 20, 9, 20)));
        }

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if(isSolid) btn.setBackground(bg.brighter());
                else btn.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}