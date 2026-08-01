import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Màn hình chính: quản lý đặt phòng học, duyệt/check-in, điểm & voucher,
 * thống kê (Admin).
 */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField txtUser, txtSearch, txtVoucher;
    private JComboBox<String> cbRoom, cbTime;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private final String FILE_PATH = "data_booking.txt";
    private boolean isAdmin = false;

    private UITheme.StatCard cardTotal, cardPending, cardApproved, cardDone;
    private JLabel lblMoTaPhong;

    private final PointsManager pointsManager = new PointsManager();
    private final VoucherManager voucherManager = new VoucherManager();

    private final String[] ROOM_LIST = {
            "P.101 (Thường)", "P.102 (Thường)",
            "P.201 (Sáng tạo)", "P.202 (Sáng tạo)",
            "P.301 (Công nghệ)",
            "P.401 (Nhóm nhỏ)", "P.402 (Nhóm nhỏ)",
            "Hội trường A"
    };

    private final String[] TIME_LIST = {
            "07:00 - 09:00", "09:00 - 11:00", "13:00 - 15:00", "15:00 - 17:00", "17:00 - 19:00"
    };

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

    public MainFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;
        initUI();
        loadDataFromFile();
    }

    public MainFrame() {
        this(false);
    }

    // ============================== GIAO DIỆN ==============================

    private void initUI() {
        setTitle("Hệ thống quản lý đặt phòng học — " + (isAdmin ? "Quyền Admin" : "Quyền User"));
        setSize(980, 760);
        setMinimumSize(new Dimension(860, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        UITheme.BackgroundPanel root = new UITheme.BackgroundPanel(new BorderLayout(10, 10));
        setContentPane(root);

        root.add(buildTitleBar(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 12));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        centerPanel.add(buildTopArea(), BorderLayout.NORTH);
        centerPanel.add(buildTablePanel(), BorderLayout.CENTER);
        centerPanel.add(buildStatsPanel(), BorderLayout.SOUTH);

        root.add(centerPanel, BorderLayout.CENTER);
        root.add(buildButtonBar(), BorderLayout.SOUTH);

        capNhatMoTaPhong();
    }

    private JLabel buildTitleBar() {
        JLabel lblTitle = new JLabel("QUẢN LÝ ĐẶT PHÒNG HỌC", JLabel.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(16, 10, 10, 10));
        return lblTitle;
    }

    private JPanel buildTopArea() {
        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.setOpaque(false);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        searchPanel.add(
                UITheme.fieldLabel("Tìm kiếm", IconFactory.of(IconFactory.Type.SEARCH, UITheme.WHITE, 16)),
                BorderLayout.WEST);
        txtSearch = UITheme.roundedTextField();
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        topArea.add(searchPanel);

        UITheme.RoundedPanel inputPanel = new UITheme.RoundedPanel(
                new GridLayout(4, 2, 12, 10), 16, UITheme.GLASS_PANEL, UITheme.PRIMARY);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(),
                        "Thông tin đặt phòng",
                        0, 0, UITheme.font(Font.BOLD, 13), UITheme.PRIMARY),
                BorderFactory.createEmptyBorder(6, 16, 12, 16)));

        inputPanel.add(UITheme.fieldLabel("Phòng học", IconFactory.of(IconFactory.Type.ROOM, UITheme.PRIMARY, 15)));
        cbRoom = new JComboBox<>(ROOM_LIST);
        UITheme.styleCombo(cbRoom);
        cbRoom.addActionListener(e -> capNhatMoTaPhong());
        inputPanel.add(cbRoom);

        inputPanel.add(UITheme.fieldLabel("Người đặt", IconFactory.of(IconFactory.Type.USER, UITheme.PRIMARY, 15)));
        txtUser = UITheme.roundedTextField();
        inputPanel.add(txtUser);

        inputPanel.add(UITheme.fieldLabel("Khung giờ", IconFactory.of(IconFactory.Type.CLOCK, UITheme.PRIMARY, 15)));
        cbTime = new JComboBox<>(TIME_LIST);
        UITheme.styleCombo(cbTime);
        inputPanel.add(cbTime);

        inputPanel.add(UITheme.fieldLabel("Mã voucher (nếu có)", IconFactory.of(IconFactory.Type.GIFT, UITheme.PRIMARY, 15)));
        txtVoucher = UITheme.roundedTextField();
        inputPanel.add(txtVoucher);

        topArea.add(inputPanel);

        lblMoTaPhong = new JLabel(" ");
        lblMoTaPhong.setFont(UITheme.font(Font.ITALIC, 12));
        lblMoTaPhong.setForeground(UITheme.PRIMARY);
        lblMoTaPhong.setBorder(BorderFactory.createEmptyBorder(8, 4, 0, 0));
        topArea.add(lblMoTaPhong);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        return topArea;
    }

    private JScrollPane buildTablePanel() {
        String[] columnNames = {"Tên Phòng", "Người Đặt", "Thời Gian", "Phí", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(UITheme.FONT_TABLE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setForeground(UITheme.WHITE);
        table.setOpaque(false);
        table.setSelectionBackground(UITheme.PRIMARY);
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new StatusRowRenderer());

        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.FONT_TABLE_HEADER);
        header.setBackground(UITheme.PRIMARY_DARK);
        header.setForeground(UITheme.WHITE);
        header.setPreferredSize(new Dimension(0, 34));
        header.setReorderingAllowed(false);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new UITheme.RoundedLineBorder(UITheme.PRIMARY, 14, 1.4f));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel buildStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));

        cardTotal = new UITheme.StatCard("Tổng lượt đặt", "0", UITheme.WHITE,
                IconFactory.of(IconFactory.Type.CHART, UITheme.WHITE, 26));
        cardPending = new UITheme.StatCard("Chờ duyệt", "0", UITheme.WARNING,
                IconFactory.of(IconFactory.Type.CLOCK, UITheme.WARNING, 26));
        cardApproved = new UITheme.StatCard("Đã duyệt", "0", UITheme.SUCCESS,
                IconFactory.of(IconFactory.Type.CHECK, UITheme.SUCCESS, 26));
        cardDone = new UITheme.StatCard("Đã check-in", "0", UITheme.INFO,
                IconFactory.of(IconFactory.Type.DOOR, UITheme.INFO, 26));

        statsPanel.add(cardTotal);
        statsPanel.add(cardPending);
        statsPanel.add(cardApproved);
        statsPanel.add(cardDone);
        return statsPanel;
    }

    private JPanel buildButtonBar() {
        JPanel buttonWrap = new JPanel();
        buttonWrap.setLayout(new BoxLayout(buttonWrap, BoxLayout.Y_AXIS));
        buttonWrap.setOpaque(false);
        buttonWrap.setBorder(BorderFactory.createEmptyBorder(6, 0, 14, 0));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        row1.setOpaque(false);
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        row2.setOpaque(false);

        JButton btnAdd = new UITheme.RoundedButton("Gửi Đặt Phòng", UITheme.PRIMARY_DARK,
                IconFactory.of(IconFactory.Type.ADD, UITheme.WHITE, 16));
        JButton btnDelete = new UITheme.RoundedButton("Hủy Lịch", UITheme.DANGER,
                IconFactory.of(IconFactory.Type.TRASH, UITheme.WHITE, 16));
        JButton btnApprove = new UITheme.RoundedButton("Duyệt Phòng (Admin)", UITheme.SUCCESS,
                IconFactory.of(IconFactory.Type.CHECK, UITheme.WHITE, 16));
        JButton btnCheckIn = new UITheme.RoundedButton("Check-in (Nhận phòng)", UITheme.INFO,
                IconFactory.of(IconFactory.Type.DOOR, UITheme.WHITE, 16));
        JButton btnDoiVoucher = new UITheme.RoundedButton("Đổi điểm lấy Voucher", UITheme.WARNING,
                IconFactory.of(IconFactory.Type.GIFT, UITheme.WHITE, 16));
        JButton btnXemDiem = new UITheme.RoundedButton("Điểm & Voucher của tôi", UITheme.NEUTRAL,
                IconFactory.of(IconFactory.Type.STAR, UITheme.WHITE, 16));
        JButton btnThongKe = new UITheme.RoundedButton("Thống kê (Admin)", UITheme.ACCENT,
                IconFactory.of(IconFactory.Type.CHART, UITheme.WHITE, 16));
        JButton btnLogout = new UITheme.RoundedButton("Đăng Xuất", new Color(71, 85, 105),
                IconFactory.of(IconFactory.Type.LOGOUT, UITheme.WHITE, 16));

        row1.add(btnAdd);
        row1.add(btnDelete);
        if (isAdmin) {
            row1.add(btnApprove);
        }
        row1.add(btnCheckIn);

        row2.add(btnDoiVoucher);
        row2.add(btnXemDiem);
        if (isAdmin) {
            row2.add(btnThongKe);
        }
        row2.add(btnLogout);

        buttonWrap.add(row1);
        buttonWrap.add(row2);

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

        return buttonWrap;
    }

    // ============================== HÀNH ĐỘNG ==============================

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
                    "Áp dụng voucher " + v.maVoucher + " thành công!\n"
                            + "Phí gốc: " + formatTien(phiGoc) + "đ - Giảm: " + formatTien(v.giaTriGiam)
                            + "đ => Còn phải trả: " + formatTien(phiThucTra) + "đ");
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
                "Check-in thành công cho " + tenNguoiDat + "!\n"
                        + "Cộng " + diemCong + " điểm tích lũy (tổng hiện có: " + pointsManager.layDiem(tenNguoiDat) + " điểm).",
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
                "Bạn (" + ten + ") đang có " + diemHienCo + " điểm.\n"
                        + "Đổi tối thiểu " + VoucherManager.DIEM_TOI_THIEU + " điểm, theo bội số " + VoucherManager.BOI_SO_DOI + ".\n"
                        + "Tỉ lệ: 1 điểm = " + formatTien(VoucherManager.GIA_TRI_MOI_DIEM) + "đ giảm giá.\n\n"
                        + "Nhập số điểm muốn đổi:", "100");
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
                "Đổi voucher thành công!\n\n"
                        + "Mã voucher: " + v.maVoucher + "\n"
                        + "Giá trị giảm: " + formatTien(v.giaTriGiam) + "đ\n"
                        + "Điểm còn lại: " + pointsManager.layDiem(ten) + " điểm\n\n"
                        + "Nhập mã này vào ô \"Mã voucher\" ở lần đặt phòng tiếp theo để được giảm giá.",
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
        area.setFont(UITheme.FONT_TABLE);
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

            if (trangThai.equals(TT_CHO_DUYET)) {
                soChoDuyet++;
            } else if (trangThai.equals(TT_DA_DUYET)) {
                soDaDuyet++;
            } else if (trangThai.equals(TT_DA_CHECKIN)) {
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
        area.setFont(UITheme.FONT_TABLE);
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
        cardTotal.setValue(String.valueOf(total));
        cardPending.setValue(String.valueOf(pending));
        cardApproved.setValue(String.valueOf(approved));
        cardDone.setValue(String.valueOf(done));
    }

    // ============================== TRÌNH VẼ BẢNG ==============================

    private class StatusRowRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? UITheme.TABLE_ROW_1 : UITheme.TABLE_ROW_2);
                c.setForeground(UITheme.WHITE);

                if (column == 4 && value != null) {
                    String status = value.toString();
                    if (status.equals(TT_DA_CHECKIN)) {
                        c.setForeground(UITheme.INFO);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (status.equals(TT_DA_DUYET)) {
                        c.setForeground(UITheme.SUCCESS);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (status.equals(TT_CHO_DUYET)) {
                        c.setForeground(UITheme.WARNING);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                } else {
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
            }
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            ((JComponent) c).setOpaque(true);
            return c;
        }
    }

    // ============================== LƯU / NẠP DỮ LIỆU ==============================

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
