import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterFrame extends JFrame {
    private JTextField txtHoTen;
    private JTextField txtEmail;
    private JTextField txtSdt;
    private JPasswordField txtMatKhau;
    private JPasswordField txtXacNhanMatKhau;

    private static final Color COLOR_ACCENT = new Color(41, 182, 246);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_PANEL = new Color(8, 14, 28, 200);
    private static final Color COLOR_DANGER = new Color(230, 90, 90);

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

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
                g.setColor(new Color(0, 0, 0, 115));
                g.fillRect(0, 0, getWidth(), getHeight());
            } else {
                g.setColor(new Color(6, 10, 22));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    private final AccountRepository accountRepository = new AccountRepository();

    public RegisterFrame() {
        setTitle("ĐĂNG KÝ TÀI KHOẢN");
        setSize(460, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageBackgroundPanel root = new ImageBackgroundPanel(new BorderLayout(10, 10));
        setContentPane(root);

        JLabel lblTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(24, 10, 10, 10));
        root.add(lblTitle, BorderLayout.NORTH);

        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setOpaque(false);

        JPanel panelForm = new JPanel(new GridLayout(5, 2, 12, 15));
        panelForm.setBackground(COLOR_PANEL);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel lblHoTen = new JLabel("Họ và tên:");
        styleLabel(lblHoTen);
        panelForm.add(lblHoTen);

        txtHoTen = new JTextField();
        styleField(txtHoTen);
        panelForm.add(txtHoTen);

        JLabel lblEmail = new JLabel("Email:");
        styleLabel(lblEmail);
        panelForm.add(lblEmail);

        txtEmail = new JTextField();
        styleField(txtEmail);
        panelForm.add(txtEmail);

        JLabel lblSdt = new JLabel("Số điện thoại:");
        styleLabel(lblSdt);
        panelForm.add(lblSdt);

        txtSdt = new JTextField();
        styleField(txtSdt);
        panelForm.add(txtSdt);

        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        styleLabel(lblMatKhau);
        panelForm.add(lblMatKhau);

        txtMatKhau = new JPasswordField();
        styleField(txtMatKhau);
        panelForm.add(txtMatKhau);

        JLabel lblXacNhan = new JLabel("Xác nhận mật khẩu:");
        styleLabel(lblXacNhan);
        panelForm.add(lblXacNhan);

        txtXacNhanMatKhau = new JPasswordField();
        styleField(txtXacNhanMatKhau);
        panelForm.add(txtXacNhanMatKhau);

        formWrapper.add(panelForm);
        root.add(formWrapper, BorderLayout.CENTER);

        JPanel panelButton = new JPanel();
        panelButton.setOpaque(false);
        panelButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        JButton btnDangKy = createStyledButton("Đăng ký", COLOR_ACCENT);
        JButton btnHuy = createStyledButton("Hủy", COLOR_DANGER);

        panelButton.add(btnDangKy);
        panelButton.add(btnHuy);
        root.add(panelButton, BorderLayout.SOUTH);

        btnDangKy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dangKy();
            }
        });

        btnHuy.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }

    private void dangKy() {
        String hoTen = txtHoTen.getText().trim();
        String email = txtEmail.getText().trim();
        String sdt = txtSdt.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword()).trim();
        String xacNhan = new String(txtXacNhanMatKhau.getPassword()).trim();

        if (hoTen.isEmpty() || email.isEmpty() || sdt.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng điền đầy đủ thông tin!",
                    "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this,
                    "Email không hợp lệ!",
                    "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!sdt.matches("\\d{9,11}")) {
            JOptionPane.showMessageDialog(this,
                    "Số điện thoại không hợp lệ (chỉ nhập 9-11 chữ số)!",
                    "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (matKhau.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu phải có ít nhất 6 ký tự!",
                    "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!matKhau.equals(xacNhan)) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu xác nhận không khớp!",
                    "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (accountRepository.emailDaTonTai(email)) {
            JOptionPane.showMessageDialog(this,
                    "Email này đã được đăng ký!",
                    "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Account acc = new Account(hoTen, email, sdt, matKhau);
        boolean ok = accountRepository.them(acc);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Đăng ký thành công! Vui lòng đăng nhập để đặt phòng.",
                    "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Đăng ký thất bại, vui lòng thử lại!",
                    "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(COLOR_ACCENT);
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
            new RegisterFrame().setVisible(true);
        });
    }
}
