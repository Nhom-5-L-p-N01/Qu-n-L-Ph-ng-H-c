import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.*;
import java.util.List;
import java.util.*;

public class MainFrame extends JFrame {
    private JTextField txtUser, txtSearch, txtVoucher;
    private JComboBox<String> cbRoom, cbTime;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private final String FILE_PATH = "data_booking.txt";
    private boolean isAdmin = false;

    private JLabel lblStatTotal, lblStatPending, lblStatApproved, lblStatDone;
    private JLabel lblMoTaPhong;

    private final PointsManager pointsManager = new PointsManager();
    private final VoucherManager voucherManager = new VoucherManager();

    private static final Color COLOR_ACCENT = new Color(41, 182, 246);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_PANEL = new Color(8, 14, 28, 200);
    private static final Color COLOR_TABLE_BG = new Color(8, 14, 28, 170);
    private static final Color COLOR_ROW_ALT = new Color(20, 30, 50, 170);
    private static final Color COLOR_AMBER = new Color(255, 193, 7);
    private static final Color COLOR_DANGER = new Color(230, 90, 90);
    private static final Color COLOR_SUCCESS = new Color(80, 220, 160);
    private static final Color COLOR_INFO = new Color(140, 160, 255);

    private final String[] ROOM_LIST = {
            "P.101 (Thường)", "P.102 (Thường)",
            "P.201 (Sáng tạo)", "P.202 (Sáng tạo)",
            "P.301 (Công nghệ)",
            "P.401 (Nhóm nhỏ)", "P.402 (Nhóm nhỏ)",
            "Hội trường A"
    };

    private final String[] TIME_LIST = {"07:00 - 09:00", "09:00 - 11:00", "13:00 - 15:00", "15:00 - 17:00", "17:00 - 19:00"};

    private final Map<String, Integer> ROOM_FEE = new LinkedHashMap<String, Integer>() {{
        put("P.101 (Thường)", 30000);
        put("P.102 (Thường)", 30000);
        put("P.201 (Sáng tạo)", 60000);
        put("P.202 (Sáng tạo)", 60000);
        put("P.301 (Công nghệ)", 100000);
        put("P.401 (Nhóm nhỏ)", 50000);
        put("P.402 (Nhóm nhỏ)", 50000);
        put("Hội trường A", 150000);
    }};

    private final Map<String, String> ROOM_DESC = new LinkedHashMap<String, String>() {{
        put("P.101 (Thường)", "Bàn ghế cơ bản, có máy chiếu, phù hợp học tập thông thường");
        put("P.102 (Thường)", "Bàn ghế cơ bản, có máy chiếu, phù hợp học tập thông thường");
        put("P.201 (Sáng tạo)", "Ghế linh hoạt nhiều màu, không gian mở, phù hợp thảo luận sáng tạo");
        put("P.202 (Sáng tạo)", "Ghế linh hoạt nhiều màu, không gian mở, phù hợp thảo luận sáng tạo");
        put("P.301 (Công nghệ)", "Bàn có bánh xe, ổ điện tích hợp, màn hình tương tác");
        put("P.401 (Nhóm nhỏ)", "Bàn tròn/vuông cỡ nhỏ, phù hợp làm việc nhóm 4-6 người");
        put("P.402 (Nhóm nhỏ)", "Bàn tròn/vuông cỡ nhỏ, phù hợp làm việc nhóm 4-6 người");
        put("Hội trường A", "Sức chứa lớn, dùng cho hội thảo, thuyết trình, sự kiện");
    }};

    private static final String TT_CHO_DUYET = "Chờ duyệt";
    private static final String TT_DA_DUYET = "Đã duyệt";
    private static final String TT_DA_CHECKIN = "Đã check-in";

    // Panel vẽ ảnh nền thật, co giãn theo kích thước cửa sổ + phủ mờ đen để bảng/chữ dễ đọc
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
                // Lớp phủ đen mờ giúp bảng dữ liệu và chữ dễ đọc hơn trên ảnh nền
                g.setColor(new Color(0, 0, 0, 130));
                g.fillRect(0, 0, getWidth(), getHeight());
            } else {
                g.setColor(new Color(6, 10, 22));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public MainFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;
        initUI();
        loadDataFromFile();
    }

    public MainFrame() {
        this(false);
    }

    private void initUI() {
        setTitle("HỆ THỐNG QUẢN LÝ ĐẶT PHÒNG HỌC - " + (isAdmin ? "QUYỀN ADMIN" : "QUYỀN USER"));
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageBackgroundPanel root = new ImageBackgroundPanel(new BorderLayout(10, 10));
        setContentPane(root);

        JLabel lblTitle = new JLabel("QUẢN LÝ ĐẶT PHÒNG HỌC", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(COLOR_WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        root.add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.setOpaque(false);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSearch.setForeground(COLOR_WHITE);
        searchPanel.add(lblSearch, BorderLayout.WEST);
        txtSearch = styledTextField();
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        topArea.add(searchPanel);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBackground(COLOR_PANEL);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COLOR_ACCENT, 1),
                        "Thông tin đặt phòng",
                        0, 0, new Font("Segoe UI", Font.BOLD, 13), COLOR_ACCENT),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        inputPanel.add(styledLabel("Phòng học:"));
        cbRoom = new JComboBox<>(ROOM_LIST);
        styleCombo(cbRoom);
        cbRoom.addActionListener(e -> capNhatMoTaPhong());
        inputPanel.add(cbRoom);

        inputPanel.add(styledLabel("Người đặt:"));
        txtUser = styledTextField();
        inputPanel.add(txtUser);

        inputPanel.add(styledLabel("Khung giờ:"));
        cbTime = new JComboBox<>(TIME_LIST);
        styleCombo(cbTime);
        inputPanel.add(cbTime);

        inputPanel.add(styledLabel("Mã voucher (nếu có):"));
        txtVoucher = styledTextField();
        inputPanel.add(txtVoucher);

        topArea.add(inputPanel);

        lblMoTaPhong = new JLabel(" ");
        lblMoTaPhong.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblMoTaPhong.setForeground(COLOR_ACCENT);
        lblMoTaPhong.setBorder(BorderFactory.createEmptyBorder(6, 4, 0, 0));
        topArea.add(lblMoTaPhong);

        centerPanel.add(topArea, BorderLayout.NORTH);

        String[] columnNames = {"Tên Phòng", "Người Đặt", "Thời Gian", "Phí", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(60, 90, 130));
        table.setForeground(COLOR_WHITE);
        table.setOpaque(false);
        table.setSelectionBackground(COLOR_ACCENT);
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(10, 20, 40));
        table.getTableHeader().setForeground(COLOR_WHITE);
        table.setDefaultRenderer(Object.class, new StatusRowRenderer());

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        lblStatTotal = createStatLabel("Tổng: 0", COLOR_WHITE);
        lblStatPending = createStatLabel("Chờ duyệt: 0", COLOR_AMBER);
        lblStatApproved = createStatLabel("Đã duyệt: 0", COLOR_SUCCESS);
        lblStatDone = createStatLabel("Đã check-in: 0", COLOR_INFO);

        statsPanel.add(lblStatTotal);
        statsPanel.add(lblStatPending);
        statsPanel.add(lblStatApproved);
        statsPanel.add(lblStatDone);
        centerPanel.add(statsPanel, BorderLayout.SOUTH);

        root.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonWrap = new JPanel();
        buttonWrap.setLayout(new BoxLayout(buttonWrap, BoxLayout.Y_AXIS));
        buttonWrap.setOpaque(false);

        JPanel buttonPanelRow1 = new JPanel(new FlowLayout());
        buttonPanelRow1.setOpaque(false);
        JPanel buttonPanelRow2 = new JPanel(new FlowLayout());
        buttonPanelRow2.setOpaque(false);
        buttonPanelRow2.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JButton btnAdd = createStyledButton("Gửi Đặt Phòng", COLOR_ACCENT);
        JButton btnDelete = createStyledButton("Hủy Lịch", COLOR_DANGER);
        JButton btnApprove = createStyledButton("Duyệt Phòng (Admin)", COLOR_SUCCESS);
        JButton btnCheckIn = createStyledButton("Check-in (Nhận phòng)", COLOR_INFO);
        JButton btnDoiVoucher = createStyledButton("Đổi điểm lấy Voucher", COLOR_AMBER);
        JButton btnXemDiem = createStyledButton("Điểm & Voucher của tôi", new Color(120, 130, 150));
        JButton btnThongKe = createStyledButton("Thống kê (Admin)", new Color(160, 100, 220));
        JButton btnLogout = createStyledButton("Đăng Xuất", new Color(100, 110, 130));

        buttonPanelRow1.add(btnAdd);
        buttonPanelRow1.add(btnDelete);
        if (isAdmin) {
            buttonPanelRow1.add(btnApprove);
        }
        buttonPanelRow1.add(btnCheckIn);

        buttonPanelRow2.add(btnDoiVoucher);
        buttonPanelRow2.add(btnXemDiem);
        if (isAdmin) {
            buttonPanelRow2.add(btnThongKe);
        }
        buttonPanelRow2.add(btnLogout);

        buttonWrap.add(buttonPanelRow1);
        buttonWrap.add(buttonPanelRow2);
        root.add(buttonWrap, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> onDatPhong());
        btnDelete.addActionListener(e -> onHuyLich());
        btnApprove.addActionListener(e -> onDuyet());
        btnCheckIn.addActionListener(e -> onCheckIn());
        btnDoiVoucher.addActionListener(e -> onDoiVoucher());
        btnXemDiem.addActionListener(e -> onXemDiemVaVoucher());
        btnThongKe.addActionListener(e -> onThongKe());

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất không?",
                    "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        capNhatMoTaPhong();
    }

    private void capNhatMoTaPhong() {
        String room = (String) cbRoom.getSelectedItem();
        int phi = ROOM_FEE.getOrDefault(room, 0);
        String moTa = ROOM_DESC.getOrDefault(room, "");
        lblMoTaPhong.setText("<html>" + moTa + " — <b>Giá: " + formatTien(phi) + "đ</b></html>");
    }

    private void onDatPhong() {
        String room = (String) cbRoom.getSelectedItem();
        String user = txtUser.getText().trim();
        String time = (String) cbTime.getSelectedItem();
        String maVoucher = txtVoucher.getText().trim();

        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên người đặt!");
            return;
        }

        int phiGoc = ROOM_FEE.getOrDefault(room, 30000);
        int phiThucTra = phiGoc;

        if (!maVoucher.isEmpty()) {
            VoucherManager.Voucher v = voucherManager.timVoucherHopLe(maVoucher, user);
            if (v == null) {
                JOptionPane.showMessageDialog(this,
                        "Mã voucher không hợp lệ, không phải của bạn, hoặc đã được sử dụng!",
                        "Voucher không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            phiThucTra = Math.max(0, phiGoc - v.giaTriGiam);
            voucherManager.danhDauDaDung(v);
            JOptionPane.showMessageDialog(this,
                    "Áp dụng voucher " + v.maVoucher + " thành công!\n" +
                            "Phí gốc: " + formatTien(phiGoc) + "đ - Giảm: " + formatTien(v.giaTriGiam) +
                            "đ => Còn phải trả: " + formatTien(phiThucTra) + "đ");
        }

        tableModel.addRow(new Object[]{room, user, time, phiThucTra, TT_CHO_DUYET});
        saveDataToFile();
        updateStats();
        txtUser.setText("");
        txtVoucher.setText("");
        JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu đặt phòng! Phí: " + formatTien(phiThucTra) + "đ");
    }

    private void onHuyLich() {
        int viewRow = table.getSelectedRow();
        if (viewRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn hủy lịch đặt phòng này không?",
                    "Xác nhận hủy lịch",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                tableModel.removeRow(modelRow);
                saveDataToFile();
                updateStats();
                JOptionPane.showMessageDialog(this, "Đã hủy lịch thành công!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng trong bảng để hủy!");
        }
    }

    private void onDuyet() {
        int viewRow = table.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            String trangThaiHienTai = String.valueOf(tableModel.getValueAt(modelRow, 4));
            if (!trangThaiHienTai.equals(TT_CHO_DUYET)) {
                JOptionPane.showMessageDialog(this, "Chỉ có thể duyệt các đơn đang \"Chờ duyệt\"!");
                return;
            }
            tableModel.setValueAt(TT_DA_DUYET, modelRow, 4);
            saveDataToFile();
            updateStats();
            JOptionPane.showMessageDialog(this, "Đã duyệt lịch đặt phòng thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch trong bảng để duyệt!");
        }
    }

    private void onCheckIn() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch trong bảng để check-in!");
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        String trangThaiHienTai = String.valueOf(tableModel.getValueAt(modelRow, 4));

        if (trangThaiHienTai.equals(TT_DA_CHECKIN)) {
            JOptionPane.showMessageDialog(this, "Lịch này đã check-in trước đó rồi!", "Đã check-in", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!trangThaiHienTai.equals(TT_DA_DUYET)) {
            JOptionPane.showMessageDialog(this,
                    "Chỉ có thể check-in các đơn ĐÃ ĐƯỢC DUYỆT (hiện đang: " + trangThaiHienTai + ")!",
                    "Chưa thể check-in", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tenNguoiDat = String.valueOf(tableModel.getValueAt(modelRow, 1));
        double phi = Double.parseDouble(String.valueOf(tableModel.getValueAt(modelRow, 3)));

        tableModel.setValueAt(TT_DA_CHECKIN, modelRow, 4);
        int diemCong = pointsManager.congDiem(tenNguoiDat, phi);
        saveDataToFile();
        updateStats();

        JOptionPane.showMessageDialog(this,
                "Check-in thành công cho " + tenNguoiDat + "!\n" +
                        "Cộng " + diemCong + " điểm tích lũy (tổng hiện có: " + pointsManager.layDiem(tenNguoiDat) + " điểm).",
                "Check-in thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onDoiVoucher() {
        String tenMacDinh = txtUser.getText().trim();
        String ten = JOptionPane.showInputDialog(this,
                "Nhập tên người đặt (đúng như khi đặt phòng) để đổi điểm:", tenMacDinh);
        if (ten == null || ten.trim().isEmpty()) return;
        ten = ten.trim();

        int diemHienCo = pointsManager.layDiem(ten);
        String nhap = JOptionPane.showInputDialog(this,
                "Bạn (" + ten + ") đang có " + diemHienCo + " điểm.\n" +
                        "Đổi tối thiểu " + VoucherManager.DIEM_TOI_THIEU + " điểm, theo bội số " + VoucherManager.BOI_SO_DOI + ".\n" +
                        "Tỉ lệ: 1 điểm = " + formatTien(VoucherManager.GIA_TRI_MOI_DIEM) + "đ giảm giá.\n\n" +
                        "Nhập số điểm muốn đổi:", "100");
        if (nhap == null || nhap.trim().isEmpty()) return;

        int soDiem;
        try {
            soDiem = Integer.parseInt(nhap.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số điểm không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (soDiem < VoucherManager.DIEM_TOI_THIEU) {
            JOptionPane.showMessageDialog(this,
                    "Phải đổi tối thiểu " + VoucherManager.DIEM_TOI_THIEU + " điểm!", "Không đủ điều kiện", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (soDiem % VoucherManager.BOI_SO_DOI != 0) {
            JOptionPane.showMessageDialog(this,
                    "Số điểm đổi phải là bội số của " + VoucherManager.BOI_SO_DOI + "!", "Không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (soDiem > diemHienCo) {
            JOptionPane.showMessageDialog(this,
                    "Bạn không đủ điểm! Hiện có " + diemHienCo + " điểm.", "Không đủ điểm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        VoucherManager.Voucher v = voucherManager.doiVoucher(ten, soDiem);
        pointsManager.truDiem(ten, soDiem);

        JOptionPane.showMessageDialog(this,
                "Đổi voucher thành công!\n\n" +
                        "Mã voucher: " + v.maVoucher + "\n" +
                        "Giá trị giảm: " + formatTien(v.giaTriGiam) + "đ\n" +
                        "Điểm còn lại: " + pointsManager.layDiem(ten) + " điểm\n\n" +
                        "Nhập mã này vào ô \"Mã voucher\" ở lần đặt phòng tiếp theo để được giảm giá.",
                "Đổi voucher thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onXemDiemVaVoucher() {
        String tenMacDinh = txtUser.getText().trim();
        String ten = JOptionPane.showInputDialog(this, "Nhập tên người đặt để xem điểm & voucher:", tenMacDinh);
        if (ten == null || ten.trim().isEmpty()) return;
        ten = ten.trim();

        StringBuilder sb = new StringBuilder();
        sb.append("Điểm tích lũy của ").append(ten).append(": ").append(pointsManager.layDiem(ten)).append(" điểm\n\n");
        sb.append("Danh sách voucher:\n");
        List<VoucherManager.Voucher> ds = voucherManager.layVoucherCuaKhach(ten);
        if (ds.isEmpty()) {
            sb.append("(chưa có voucher nào)");
        } else {
            for (VoucherManager.Voucher v : ds) {
                sb.append("- ").append(v.maVoucher)
                        .append(" | giảm ").append(formatTien(v.giaTriGiam)).append("đ")
                        .append(" | ").append(v.daDung ? "Đã dùng" : "Chưa dùng").append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Điểm & Voucher của bạn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onThongKe() {
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "Chỉ Admin mới xem được thống kê!", "Không có quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int total = tableModel.getRowCount();
        Map<String, Integer> soLuotTheoPhong = new LinkedHashMap<>();
        long tongDoanhThu = 0;
        int soChoDuyet = 0, soDaDuyet = 0, soDaCheckIn = 0;

        for (int i = 0; i < total; i++) {
            String phong = String.valueOf(tableModel.getValueAt(i, 0));
            String trangThai = String.valueOf(tableModel.getValueAt(i, 4));
            double phi = Double.parseDouble(String.valueOf(tableModel.getValueAt(i, 3)));

            soLuotTheoPhong.merge(phong, 1, Integer::sum);

            if (trangThai.equals(TT_CHO_DUYET)) soChoDuyet++;
            else if (trangThai.equals(TT_DA_DUYET)) soDaDuyet++;
            else if (trangThai.equals(TT_DA_CHECKIN)) {
                soDaCheckIn++;
                tongDoanhThu += (long) phi;
            }
        }

        String phongDatNhieuNhat = "—";
        int soLuotNhieuNhat = 0;
        for (Map.Entry<String, Integer> e : soLuotTheoPhong.entrySet()) {
            if (e.getValue() > soLuotNhieuNhat) {
                soLuotNhieuNhat = e.getValue();
                phongDatNhieuNhat = e.getKey();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== THỐNG KÊ TỔNG QUAN ===\n\n");
        sb.append("Tổng số lượt đặt: ").append(total).append("\n");
        sb.append("- Chờ duyệt: ").append(soChoDuyet).append("\n");
        sb.append("- Đã duyệt (chưa tới dùng): ").append(soDaDuyet).append("\n");
        sb.append("- Đã check-in (đã dùng phòng thật): ").append(soDaCheckIn).append("\n\n");
        sb.append("Phòng được đặt nhiều nhất: ").append(phongDatNhieuNhat)
                .append(" (").append(soLuotNhieuNhat).append(" lượt)\n\n");
        sb.append("Tổng doanh thu (chỉ tính đơn đã check-in): ").append(formatTien(tongDoanhThu)).append("đ\n\n");
        sb.append("Chi tiết lượt đặt theo từng phòng:\n");
        for (Map.Entry<String, Integer> e : soLuotTheoPhong.entrySet()) {
            sb.append("- ").append(e.getKey()).append(": ").append(e.getValue()).append(" lượt\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setRows(18);
        area.setColumns(40);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Thống kê (Admin)", JOptionPane.INFORMATION_MESSAGE);
    }

    private String formatTien(double soTien) {
        return String.format("%,.0f", soTien).replace(",", ".");
    }

    private void filterTable() {
        String text = txtSearch.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text), 0, 1));
        }
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        int pending = 0, approved = 0, done = 0;
        for (int i = 0; i < total; i++) {
            String status = String.valueOf(tableModel.getValueAt(i, 4));
            if (status.equals(TT_CHO_DUYET)) pending++;
            else if (status.equals(TT_DA_DUYET)) approved++;
            else if (status.equals(TT_DA_CHECKIN)) done++;
        }
        lblStatTotal.setText("Tổng: " + total);
        lblStatPending.setText("Chờ duyệt: " + pending);
        lblStatApproved.setText("Đã duyệt: " + approved);
        lblStatDone.setText("Đã check-in: " + done);
    }

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(COLOR_ACCENT);
        return lbl;
    }

    private JLabel createStatLabel(String text, Color color) {
        JLabel lbl = new JLabel(text, JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(color);
        lbl.setOpaque(true);
        lbl.setBackground(COLOR_PANEL);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return lbl;
    }

    private JTextField styledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(new Color(255, 255, 255, 230));
        tf.setForeground(new Color(10, 20, 40));
        tf.setCaretColor(COLOR_ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        return tf;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(new Color(255, 255, 255, 230));
        combo.setForeground(new Color(10, 20, 40));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(COLOR_WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private class StatusRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? COLOR_TABLE_BG : COLOR_ROW_ALT);
                c.setForeground(COLOR_WHITE);

                if (column == 4 && value != null) {
                    String status = value.toString();
                    if (status.equals(TT_DA_CHECKIN)) {
                        c.setForeground(COLOR_INFO);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (status.equals(TT_DA_DUYET)) {
                        c.setForeground(COLOR_SUCCESS);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (status.equals(TT_CHO_DUYET)) {
                        c.setForeground(COLOR_AMBER);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                } else {
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
            }
            ((JComponent) c).setOpaque(true);
            return c;
        }
    }

    private void saveDataToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String line = tableModel.getValueAt(i, 0) + "," +
                        tableModel.getValueAt(i, 1) + "," +
                        tableModel.getValueAt(i, 2) + "," +
                        tableModel.getValueAt(i, 3) + "," +
                        tableModel.getValueAt(i, 4);
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDataFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            updateStats();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            tableModel.setRowCount(0);
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    tableModel.addRow(data);
                } else if (data.length == 4) {
                    int phiMacDinh = ROOM_FEE.getOrDefault(data[0], 30000);
                    tableModel.addRow(new Object[]{data[0], data[1], data[2], phiMacDinh, data[3]});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateStats();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame(true).setVisible(true));
    }
}
