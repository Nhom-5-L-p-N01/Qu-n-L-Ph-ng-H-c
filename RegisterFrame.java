import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class RegisterFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField txtHoTen;
    private JTextField txtEmail;
    private JTextField txtSdt;
    private JTextField txtMaSV;
    private JTextField txtLop;
    private JPasswordField txtMatKhau;
    private JPasswordField txtXacNhanMatKhau;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private final AccountRepository accountRepository = new AccountRepository();

    public RegisterFrame() {
        setTitle("Đăng ký tài khoản");
        setSize(480, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        header.setBorder(BorderFactory.createEmptyBorder(26, 10, 10, 10));

        JLabel lblTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN", JLabel.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Tạo tài khoản để bắt đầu đặt phòng", JLabel.CENTER);
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
                new GridLayout(7, 1, 0, 12), 20, UITheme.GLASS_PANEL, UITheme.PRIMARY);
        card.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));
        card.setPreferredSize(new Dimension(380, 560));

        txtHoTen = UITheme.roundedTextField();
        txtEmail = UITheme.roundedTextField();
        txtSdt = UITheme.roundedTextField();
        txtMaSV = UITheme.roundedTextField();
        txtLop = UITheme.roundedTextField();
        txtMatKhau = UITheme.roundedPasswordField();
        txtXacNhanMatKhau = UITheme.roundedPasswordField();

        card.add(formRow("Họ và tên", IconFactory.Type.USER, txtHoTen));
        card.add(formRow("Email", IconFactory.Type.MAIL, txtEmail));
        card.add(formRow("Số điện thoại", IconFactory.Type.PHONE, txtSdt));
        card.add(formRow("Mã sinh viên", IconFactory.Type.ADD_USER, txtMaSV));
        card.add(formRow("Lớp", IconFactory.Type.ROOM, txtLop));
        card.add(formRow("Mật khẩu", IconFactory.Type.LOCK, txtMatKhau));
        card.add(formRow("Xác nhận mật khẩu", IconFactory.Type.LOCK, txtXacNhanMatKhau));

        return card;
    }

    private JPanel formRow(String labelText, IconFactory.Type icon, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(0, 5));
        row.setOpaque(false);
        row.add(UITheme.fieldLabel(labelText, IconFactory.of(icon, UITheme.PRIMARY, 15)), BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildFooter() {
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panelButton.setOpaque(false);
        panelButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 26, 0));

        JButton btnDangKy = new UITheme.RoundedButton("Đăng ký", UITheme.PRIMARY_DARK,
                IconFactory.of(IconFactory.Type.ADD_USER, UITheme.WHITE, 16));
        JButton btnHuy = new UITheme.RoundedButton("Hủy", UITheme.DANGER,
                IconFactory.of(IconFactory.Type.LOGOUT, UITheme.WHITE, 16));

        panelButton.add(btnDangKy);
        panelButton.add(btnHuy);

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

        return panelButton;
    }

    private void dangKy() {
        String hoTen = txtHoTen.getText().trim();
        String email = txtEmail.getText().trim();
        String sdt = txtSdt.getText().trim();
        String maSV = txtMaSV.getText().trim();
        String lop = txtLop.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword()).trim();
        String xacNhan = new String(txtXacNhanMatKhau.getPassword()).trim();

        if (hoTen.isEmpty() || email.isEmpty() || sdt.isEmpty()
                || maSV.isEmpty() || lop.isEmpty() || matKhau.isEmpty()) {
            baoLoi("Vui lòng điền đầy đủ thông tin!");
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            baoLoi("Email không hợp lệ!");
            return;
        }
        if (!sdt.matches("\\d{9,11}")) {
            baoLoi("Số điện thoại không hợp lệ (chỉ nhập 9-11 chữ số)!");
            return;
        }
        if (matKhau.length() < 6) {
            baoLoi("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }
        if (!matKhau.equals(xacNhan)) {
            baoLoi("Mật khẩu xác nhận không khớp!");
            return;
        }
        if (accountRepository.emailDaTonTai(email)) {
            baoLoi("Email này đã được đăng ký!");
            return;
        }
        if (accountRepository.maSVDaTonTai(maSV)) {
            baoLoi("Mã sinh viên này đã được đăng ký!");
            return;
        }

        Account acc = new Account(hoTen, email, sdt, matKhau, maSV, lop);
        boolean ok = accountRepository.them(acc);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Đăng ký thành công! Vui lòng đăng nhập để đặt phòng.",
                    "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginFrame().setVisible(true);
        } else {
            baoLoi("Đăng ký thất bại, vui lòng thử lại!");
        }
    }

    private void baoLoi(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}
