import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import enums.BookingStatus;
import enums.RoomStatus;
import exception.BookingAlreadyCancelledException;
import exception.BookingNotFoundException;
import exception.ExceedDailyHourLimitException;
import exception.NotBookingOwnerException;
import exception.OverCapacityException;
import exception.RoomNotFoundException;
import exception.RoomUnderMaintenanceException;
import exception.TimeConflictException;
import model.Booking;
import model.Room;
import model.Student;
import model.TimeSlot;
import repository.BookingRepository;
import repository.RoomRepository;
import service.BookingService;

/**
 * Man hinh chinh cua ung dung - da duoc noi truc tiep voi lop nghiep vu
 * BookingService va cac model/repository trong package src (khong con tu
 * luu du lieu CSV rieng nhu truoc). Moi thao tac dat phong / huy lich /
 * check-in deu di qua BookingService de dam bao cac business rule bat buoc
 * (trung lich, vuot suc chua, qua 4 gio/ngay, phong bao tri...) duoc ap dung.
 */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField txtSearch, txtVoucher, txtSoNguoi, txtNgay;
    private JComboBox<String> cbRoom, cbTime;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private final Account account;
    private final boolean isAdmin;

    private final RoomRepository roomRepository = new RoomRepository();
    private final BookingRepository bookingRepository = new BookingRepository("data_booking.txt");
    private final BookingService bookingService = new BookingService(roomRepository, bookingRepository);

    // Danh sach lich dat hien dang hien thi tren bang, dong bo voi tableModel
    // theo tung dong (dung de lay lai doi tuong Booking that khi nguoi dung
    // chon 1 dong, thay vi phai parse chuoi tu bang).
    private List<Booking> danhSachDatHienTai = new ArrayList<>();
    private final Map<String, Room> roomByLabel = new LinkedHashMap<>();

    private UITheme.StatCard cardTotal, cardDaDat, cardCheckIn, cardHuy;
    private JLabel lblMoTaPhong;
    private JLabel lblAnhPhong;

    private final PointsManager pointsManager = new PointsManager();
    private final VoucherManager voucherManager = new VoucherManager();

    private final String[] TIME_LIST = {
            "07:00 - 09:00", "09:00 - 11:00", "13:00 - 15:00", "15:00 - 17:00", "17:00 - 19:00"
    };

    private final Map<String, String> ROOM_DESC = new LinkedHashMap<>();
    private final Map<String, String> ROOM_IMAGE = new LinkedHashMap<>();

    private static final DateTimeFormatter NGAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MainFrame(Account account, boolean isAdmin) {
        this.account = account;
        this.isAdmin = isAdmin;
        initRoomMeta();
        initUI();
        refreshTableFromService();
    }

    private void initRoomMeta() {
        ROOM_DESC.put("P101", "Bàn ghế cơ bản, phù hợp học tập thông thường");
        ROOM_DESC.put("P102", "Bàn ghế cơ bản, phù hợp học tập thông thường");
        ROOM_DESC.put("P103", "Phòng thường");
        ROOM_DESC.put("P201", "Có máy chiếu, phù hợp thuyết trình nhóm");
        ROOM_DESC.put("P202", "Có máy chiếu, phù hợp thuyết trình nhóm");
        ROOM_DESC.put("P301", "Phòng họp seminar, sức chứa lớn");

        ROOM_IMAGE.put("P101", "room_thuong.jpg");
        ROOM_IMAGE.put("P102", "room_thuong.jpg");
        ROOM_IMAGE.put("P103", "room_thuong.jpg");
        ROOM_IMAGE.put("P201", "room_congnghe.jpg");
        ROOM_IMAGE.put("P202", "room_congnghe.jpg");
        ROOM_IMAGE.put("P301", "room_hoitruong.jpg");
    }

    // ============================== GIAO DIỆN ==============================

    private void initUI() {
        setTitle("Hệ thống quản lý đặt phòng học — " + (isAdmin ? "Quyền Admin" : "Quyền User")
                + " — " + account.getHoTen());
        setSize(1020, 840);
        setMinimumSize(new Dimension(900, 700));
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
                UITheme.fieldLabel("Tìm trong lịch đặt", IconFactory.of(IconFactory.Type.SEARCH, UITheme.WHITE, 16)),
                BorderLayout.WEST);
        txtSearch = UITheme.roundedTextField();
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        topArea.add(searchPanel);

        UITheme.RoundedPanel inputPanel = new UITheme.RoundedPanel(
                new GridLayout(6, 2, 12, 8), 16, UITheme.GLASS_PANEL, UITheme.PRIMARY);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(),
                        "Thông tin đặt phòng",
                        0, 0, UITheme.font(Font.BOLD, 13), UITheme.PRIMARY),
                BorderFactory.createEmptyBorder(6, 16, 12, 16)));

        inputPanel.add(UITheme.fieldLabel("Phòng học", IconFactory.of(IconFactory.Type.ROOM, UITheme.PRIMARY, 15)));
        cbRoom = new JComboBox<>();
        UITheme.styleCombo(cbRoom);
        refreshRoomCombo();
        cbRoom.addActionListener(e -> capNhatMoTaPhong());
        inputPanel.add(cbRoom);

        inputPanel.add(UITheme.fieldLabel("Người đặt", IconFactory.of(IconFactory.Type.USER, UITheme.PRIMARY, 15)));
        JLabel lblNguoiDat = new JLabel(account.getHoTen() + "  (MSV: " + account.getMaSV()
                + ", Lớp: " + account.getLop() + ")");
        lblNguoiDat.setFont(UITheme.FONT_LABEL);
        lblNguoiDat.setForeground(UITheme.WHITE);
        inputPanel.add(lblNguoiDat);

        inputPanel.add(UITheme.fieldLabel("Khung giờ", IconFactory.of(IconFactory.Type.CLOCK, UITheme.PRIMARY, 15)));
        cbTime = new JComboBox<>(TIME_LIST);
        UITheme.styleCombo(cbTime);
        inputPanel.add(cbTime);

        inputPanel.add(UITheme.fieldLabel("Ngày đặt (dd/MM/yyyy)", IconFactory.of(IconFactory.Type.CLOCK, UITheme.PRIMARY, 15)));
        txtNgay = UITheme.roundedTextField();
        txtNgay.setText(LocalDate.now().format(NGAY_FORMAT));
        inputPanel.add(txtNgay);

        inputPanel.add(UITheme.fieldLabel("Số người tham gia", IconFactory.of(IconFactory.Type.ADD_USER, UITheme.PRIMARY, 15)));
        txtSoNguoi = UITheme.roundedTextField();
        inputPanel.add(txtSoNguoi);

        inputPanel.add(UITheme.fieldLabel("Mã voucher (nếu có)", IconFactory.of(IconFactory.Type.GIFT, UITheme.PRIMARY, 15)));
        txtVoucher = UITheme.roundedTextField();
        inputPanel.add(txtVoucher);

        topArea.add(inputPanel);

        lblMoTaPhong = new JLabel(" ");
        lblMoTaPhong.setFont(UITheme.font(Font.ITALIC, 12));
        lblMoTaPhong.setForeground(UITheme.PRIMARY);
        lblMoTaPhong.setBorder(BorderFactory.createEmptyBorder(8, 4, 0, 0));
        topArea.add(lblMoTaPhong);

        // Khung anh minh hoa phong dang chon
        lblAnhPhong = new JLabel();
        lblAnhPhong.setHorizontalAlignment(JLabel.CENTER);
        lblAnhPhong.setBorder(BorderFactory.createCompoundBorder(
                new UITheme.RoundedLineBorder(UITheme.PRIMARY, 12, 1.2f),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        lblAnhPhong.setPreferredSize(new Dimension(0, 150));
        topArea.add(Box.createVerticalStrut(8));
        topArea.add(lblAnhPhong);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        return topArea;
    }

    private JScrollPane buildTablePanel() {
        String[] columnNames = {"Mã ĐP", "Mã phòng", "Người đặt", "Ngày", "Khung giờ", "SL người", "Phí (đ)", "Trạng thái"};
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
        cardDaDat = new UITheme.StatCard("Đã đặt", "0", UITheme.WARNING,
                IconFactory.of(IconFactory.Type.CLOCK, UITheme.WARNING, 26));
        cardCheckIn = new UITheme.StatCard("Đã check-in", "0", UITheme.INFO,
                IconFactory.of(IconFactory.Type.DOOR, UITheme.INFO, 26));
        cardHuy = new UITheme.StatCard("Đã hủy", "0", UITheme.DANGER,
                IconFactory.of(IconFactory.Type.TRASH, UITheme.DANGER, 26));

        statsPanel.add(cardTotal);
        statsPanel.add(cardDaDat);
        statsPanel.add(cardCheckIn);
        statsPanel.add(cardHuy);
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
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        row3.setOpaque(false);
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        row4.setOpaque(false);

        JButton btnAdd = new UITheme.RoundedButton("Đặt Phòng", UITheme.PRIMARY_DARK,
                IconFactory.of(IconFactory.Type.ADD, UITheme.WHITE, 16));
        JButton btnDelete = new UITheme.RoundedButton("Hủy Lịch", UITheme.DANGER,
                IconFactory.of(IconFactory.Type.TRASH, UITheme.WHITE, 16));
        JButton btnCheckIn = new UITheme.RoundedButton("Check-in (Nhận phòng)", UITheme.INFO,
                IconFactory.of(IconFactory.Type.DOOR, UITheme.WHITE, 16));

        JButton btnLichTrong = new UITheme.RoundedButton("Xem Lịch Trống", UITheme.NEUTRAL,
                IconFactory.of(IconFactory.Type.CLOCK, UITheme.WHITE, 16));
        JButton btnDanhSachPhong = new UITheme.RoundedButton("Danh Sách Phòng", UITheme.NEUTRAL,
                IconFactory.of(IconFactory.Type.ROOM, UITheme.WHITE, 16));
        JButton btnTimPhong = new UITheme.RoundedButton("Tìm Phòng", UITheme.NEUTRAL,
                IconFactory.of(IconFactory.Type.SEARCH, UITheme.WHITE, 16));

        JButton btnDoiVoucher = new UITheme.RoundedButton("Đổi điểm lấy Voucher", UITheme.WARNING,
                IconFactory.of(IconFactory.Type.GIFT, UITheme.WHITE, 16));
        JButton btnXemDiem = new UITheme.RoundedButton("Điểm & Voucher của tôi", UITheme.NEUTRAL,
                IconFactory.of(IconFactory.Type.STAR, UITheme.WHITE, 16));
        JButton btnLogout = new UITheme.RoundedButton("Đăng Xuất", new Color(71, 85, 105),
                IconFactory.of(IconFactory.Type.LOGOUT, UITheme.WHITE, 16));

        row1.add(btnAdd);
        row1.add(btnDelete);
        row1.add(btnCheckIn);

        row2.add(btnLichTrong);
        row2.add(btnDanhSachPhong);
        row2.add(btnTimPhong);

        if (isAdmin) {
            JButton btnBaoTri = new UITheme.RoundedButton("Đổi Bảo Trì (Admin)", UITheme.ACCENT,
                    IconFactory.of(IconFactory.Type.LOCK, UITheme.WHITE, 16));
            JButton btnThongKe = new UITheme.RoundedButton("Thống kê (Admin)", UITheme.ACCENT,
                    IconFactory.of(IconFactory.Type.CHART, UITheme.WHITE, 16));
            btnBaoTri.addActionListener(e -> onDoiBaoTri());
            btnThongKe.addActionListener(e -> onThongKe());
            row3.add(btnBaoTri);
            row3.add(btnThongKe);
        }

        row4.add(btnDoiVoucher);
        row4.add(btnXemDiem);
        row4.add(btnLogout);

        buttonWrap.add(row1);
        buttonWrap.add(row2);
        if (isAdmin) {
            buttonWrap.add(row3);
        }
        buttonWrap.add(row4);

        btnAdd.addActionListener(e -> onDatPhong());
        btnDelete.addActionListener(e -> onHuyLich());
        btnCheckIn.addActionListener(e -> onCheckIn());
        btnLichTrong.addActionListener(e -> onXemLichTrong());
        btnDanhSachPhong.addActionListener(e -> onDanhSachPhong());
        btnTimPhong.addActionListener(e -> onTimPhong());
        btnDoiVoucher.addActionListener(e -> onDoiVoucher());
        btnXemDiem.addActionListener(e -> onXemDiemVaVoucher());
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

    private void refreshRoomCombo() {
        String dangChon = (String) cbRoom.getSelectedItem();
        roomByLabel.clear();
        for (Room r : roomRepository.layTatCa()) {
            String nhan = r.getMaPhong() + " - " + r.getTenPhong()
                    + " (" + tenLoaiPhong(r.getLoaiPhong()) + ")"
                    + (r.getTrangThai() == RoomStatus.BAO_TRI ? " [ĐANG BẢO TRÌ]" : "");
            roomByLabel.put(nhan, r);
        }
        cbRoom.setModel(new DefaultComboBoxModel<>(roomByLabel.keySet().toArray(new String[0])));
        if (dangChon != null && roomByLabel.containsKey(dangChon)) {
            cbRoom.setSelectedItem(dangChon);
        }
    }

    private String tenLoaiPhong(String loai) {
        if ("PHONG_THUONG".equals(loai)) return "Phòng thường";
        if ("PHONG_MAY_CHIEU".equals(loai)) return "Phòng máy chiếu";
        if ("PHONG_SEMINAR".equals(loai)) return "Phòng seminar";
        return loai;
    }

    private Room phongDangChon() {
        String nhan = (String) cbRoom.getSelectedItem();
        return nhan == null ? null : roomByLabel.get(nhan);
    }

    private void capNhatMoTaPhong() {
        Room room = phongDangChon();
        if (room == null) {
            lblMoTaPhong.setText(" ");
            lblAnhPhong.setIcon(null);
            lblAnhPhong.setText("");
            return;
        }
        String moTa = ROOM_DESC.getOrDefault(room.getMaPhong(), "");
        double donGiaGio = room.tinhPhi(1);
        String canhBaoBaoTri = room.getTrangThai() == RoomStatus.BAO_TRI
                ? " — <b><font color='#F87171'>ĐANG BẢO TRÌ, KHÔNG THỂ ĐẶT</font></b>" : "";
        lblMoTaPhong.setText("<html>" + moTa + " — Sức chứa: <b>" + room.getSucChua()
                + " người</b>, Đơn giá: <b>" + formatTien(donGiaGio) + "đ/giờ</b>" + canhBaoBaoTri + "</html>");

        String tenAnh = ROOM_IMAGE.getOrDefault(room.getMaPhong(), "");
        ImageIcon icon = taiAnhPhong(tenAnh, 500, 150);
        lblAnhPhong.setIcon(icon);
        lblAnhPhong.setText(icon == null ? "Chưa có ảnh minh họa cho phòng này" : "");
        lblAnhPhong.setForeground(UITheme.TEXT_MUTED);
    }

    // Tai anh minh hoa phong tu file, tu dong scale vua khung.
    // Tra ve null neu khong tim thay file (khong lam crash chuong trinh).
    private ImageIcon taiAnhPhong(String tenFile, int w, int h) {
        if (tenFile == null || tenFile.isEmpty()) return null;
        Image img = null;

        // Cách 1: tìm file trực tiếp theo đường dẫn tương đối với thư mục làm việc
        // hiện tại (đúng khi chạy trực tiếp từ thư mục project).
        try {
            File f = new File(tenFile);
            if (f.exists()) {
                img = javax.imageio.ImageIO.read(f);
            }
        } catch (Exception ignored) {
        }

        // Cách 2 (dự phòng): tìm trong classpath - trường hợp IDE (VS Code/IntelliJ)
        // chạy chương trình với thư mục làm việc khác thư mục chứa ảnh (ví dụ chạy
        // từ thư mục "out"/"bin" sau khi biên dịch). Đây chính là cách background.jpg
        // đang dùng để luôn hiển thị được, nên áp dụng y hệt cho ảnh phòng.
        if (img == null) {
            try {
                java.net.URL url = getClass().getClassLoader().getResource(tenFile);
                if (url != null) {
                    img = javax.imageio.ImageIO.read(url);
                }
            } catch (Exception ignored) {
            }
        }

        if (img == null) return null;
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void onDatPhong() {
        Room room = phongDangChon();
        if (room == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate ngay;
        try {
            ngay = LocalDate.parse(txtNgay.getText().trim(), NGAY_FORMAT);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày đặt không hợp lệ! Định dạng đúng: dd/MM/yyyy",
                    "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int soNguoi;
        try {
            soNguoi = Integer.parseInt(txtSoNguoi.getText().trim());
            if (soNguoi <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số người tham gia phải là số nguyên dương!",
                    "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] gio = ((String) cbTime.getSelectedItem()).split(" - ");
        TimeSlot slot;
        try {
            slot = new TimeSlot(LocalTime.parse(gio[0]), LocalTime.parse(gio[1]));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Khung giờ không hợp lệ: " + ex.getMessage(),
                    "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student student = new Student(account.getMaSV(), account.getHoTen(), account.getMaSV(), account.getLop());

        try {
            Booking booking = bookingService.datPhong(student, room.getMaPhong(), ngay, slot, soNguoi);

            double phiGoc = booking.getPhi();
            double phiThucTra = phiGoc;
            String maVoucher = txtVoucher.getText().trim();
            String ghiChuVoucher = "";
            if (!maVoucher.isEmpty()) {
                VoucherManager.Voucher v = voucherManager.timVoucherHopLe(maVoucher, account.getMaSV());
                if (v == null) {
                    ghiChuVoucher = "\n(Mã voucher không hợp lệ, không phải của bạn, hoặc đã dùng — lịch đặt vẫn được ghi nhận với phí gốc)";
                } else {
                    phiThucTra = Math.max(0, phiGoc - v.giaTriGiam);
                    voucherManager.danhDauDaDung(v);
                    ghiChuVoucher = "\nĐã áp dụng voucher " + v.maVoucher + ", còn phải trả: " + formatTien(phiThucTra) + "đ";
                }
            }

            txtVoucher.setText("");
            refreshTableFromService();
            JOptionPane.showMessageDialog(this,
                    "Đặt phòng thành công!\nMã đặt phòng: " + booking.getMaDatPhong()
                            + "\nPhí: " + formatTien(phiGoc) + "đ" + ghiChuVoucher,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (RoomNotFoundException | RoomUnderMaintenanceException | TimeConflictException
                 | OverCapacityException | ExceedDailyHourLimitException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Không thể đặt phòng", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + ex.getMessage(),
                    "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onHuyLich() {
        Booking target = bookingDuocChon();
        if (target == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch đặt trong bảng để hủy!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy lịch đặt phòng " + target.getMaDatPhong() + " không?",
                "Xác nhận hủy lịch",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        String maSVThucHien = isAdmin ? target.getStudent().getMaSV() : account.getMaSV();

        try {
            bookingService.huyPhong(target.getMaDatPhong(), maSVThucHien);
            refreshTableFromService();
            JOptionPane.showMessageDialog(this, "Đã hủy lịch thành công!");
        } catch (NotBookingOwnerException | BookingNotFoundException | BookingAlreadyCancelledException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Không thể hủy lịch", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + ex.getMessage(),
                    "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCheckIn() {
        Booking target = bookingDuocChon();
        if (target == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch trong bảng để check-in!");
            return;
        }

        if (!isAdmin && !target.getStudent().getMaSV().equals(account.getMaSV())) {
            JOptionPane.showMessageDialog(this,
                    "Bạn chỉ có thể check-in lịch đặt phòng của chính mình!",
                    "Không có quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (target.getTrangThai() == BookingStatus.DA_CHECKIN) {
            JOptionPane.showMessageDialog(this, "Lịch này đã check-in trước đó rồi!", "Đã check-in", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (target.getTrangThai() != BookingStatus.DA_DAT) {
            JOptionPane.showMessageDialog(this,
                    "Chỉ có thể check-in các lịch đang ở trạng thái \"Đã đặt\" (hiện đang: "
                            + hienThiTrangThai(target.getTrangThai()) + ")!",
                    "Chưa thể check-in", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean ok = bookingService.checkIn(target.getMaDatPhong());
            if (ok) {
                int diemCong = pointsManager.congDiem(target.getStudent().getMaSV(), target.getPhi());
                refreshTableFromService();
                JOptionPane.showMessageDialog(this,
                        "Check-in thành công!\nCộng " + diemCong + " điểm tích lũy (tổng hiện có: "
                                + pointsManager.layDiem(target.getStudent().getMaSV()) + " điểm).",
                        "Check-in thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + ex.getMessage(),
                    "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onXemLichTrong() {
        Room room = phongDangChon();
        if (room == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng để xem lịch!");
            return;
        }
        LocalDate ngay;
        try {
            ngay = LocalDate.parse(txtNgay.getText().trim(), NGAY_FORMAT);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày xem lịch không hợp lệ! Định dạng đúng: dd/MM/yyyy",
                    "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Lịch trống phòng ").append(room.getMaPhong())
                .append(" (").append(room.getTenPhong()).append(") ngày ")
                .append(txtNgay.getText().trim()).append(":\n\n");

        if (room.getTrangThai() == RoomStatus.BAO_TRI) {
            sb.append("Phòng đang BẢO TRÌ - không thể đặt bất kỳ khung giờ nào.\n");
        } else {
            for (String khungGio : TIME_LIST) {
                String[] gio = khungGio.split(" - ");
                TimeSlot slot = new TimeSlot(LocalTime.parse(gio[0]), LocalTime.parse(gio[1]));
                boolean trong;
                try {
                    trong = bookingService.coTheDat(room, ngay, slot);
                } catch (IOException ex) {
                    trong = false;
                }
                sb.append("- ").append(khungGio).append(": ")
                        .append(trong ? "Còn trống" : "Đã có người đặt").append("\n");
            }
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(UITheme.FONT_TABLE);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Lịch trống phòng", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onDanhSachPhong() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DANH SÁCH PHÒNG HỌC NHÓM ===\n\n");
        for (Room r : roomRepository.layTatCa()) {
            sb.append(r.getMaPhong()).append(" - ").append(r.getTenPhong())
                    .append(" | Tầng ").append(r.getTang())
                    .append(" | Loại: ").append(tenLoaiPhong(r.getLoaiPhong()))
                    .append(" | Sức chứa: ").append(r.getSucChua())
                    .append(" | Trạng thái: ").append(r.getTrangThai() == RoomStatus.BAO_TRI ? "Đang bảo trì" : "Đang hoạt động")
                    .append("\n");
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(UITheme.FONT_TABLE);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Danh sách phòng", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onTimPhong() {
        String tuKhoa = JOptionPane.showInputDialog(this,
                "Nhập mã phòng, loại phòng (PHONG_THUONG / PHONG_MAY_CHIEU / PHONG_SEMINAR) hoặc tên phòng:");
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) return;

        List<Room> ketQua = bookingService.timPhong(tuKhoa.trim());
        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phòng phù hợp với từ khóa: " + tuKhoa,
                    "Không có kết quả", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Kết quả tìm kiếm cho \"" + tuKhoa + "\":\n\n");
        for (Room r : ketQua) {
            sb.append(r.getMaPhong()).append(" - ").append(r.getTenPhong())
                    .append(" | ").append(tenLoaiPhong(r.getLoaiPhong()))
                    .append(" | Sức chứa ").append(r.getSucChua())
                    .append(" | ").append(r.getTrangThai() == RoomStatus.BAO_TRI ? "Đang bảo trì" : "Đang hoạt động")
                    .append("\n");
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(UITheme.FONT_TABLE);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Tìm phòng", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onDoiBaoTri() {
        if (!isAdmin) return;
        Room room = phongDangChon();
        if (room == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng cần đổi trạng thái!");
            return;
        }
        RoomStatus moi = (room.getTrangThai() == RoomStatus.BAO_TRI) ? RoomStatus.TRONG : RoomStatus.BAO_TRI;
        room.setTrangThai(moi);
        refreshRoomCombo();
        capNhatMoTaPhong();
        JOptionPane.showMessageDialog(this,
                "Phòng " + room.getMaPhong() + " hiện đang: "
                        + (moi == RoomStatus.BAO_TRI ? "Bảo trì (không thể đặt)" : "Hoạt động (có thể đặt)"),
                "Đã cập nhật trạng thái phòng", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onDoiVoucher() {
        String maSV = account.getMaSV();
        int diemHienCo = pointsManager.layDiem(maSV);
        String nhap = JOptionPane.showInputDialog(this,
                "Bạn (" + account.getHoTen() + ") đang có " + diemHienCo + " điểm.\n"
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

        VoucherManager.Voucher v = voucherManager.doiVoucher(maSV, soDiem);
        pointsManager.truDiem(maSV, soDiem);

        JOptionPane.showMessageDialog(this,
                "Đổi voucher thành công!\n\n"
                        + "Mã voucher: " + v.maVoucher + "\n"
                        + "Giá trị giảm: " + formatTien(v.giaTriGiam) + "đ\n"
                        + "Điểm còn lại: " + pointsManager.layDiem(maSV) + " điểm\n\n"
                        + "Nhập mã này vào ô \"Mã voucher\" ở lần đặt phòng tiếp theo để được giảm giá.",
                "Đổi voucher thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onXemDiemVaVoucher() {
        String maSV = account.getMaSV();

        StringBuilder sb = new StringBuilder();
        sb.append("Điểm tích lũy của ").append(account.getHoTen()).append(": ").append(pointsManager.layDiem(maSV)).append(" điểm\n\n");
        sb.append("Danh sách voucher:\n");
        List<VoucherManager.Voucher> ds = voucherManager.layVoucherCuaKhach(maSV);
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

        Map<String, Integer> soLuotTheoPhong = new LinkedHashMap<>();
        long tongDoanhThu = 0;
        int soDaDat = 0, soDaCheckIn = 0, soDaHuy = 0;

        for (Booking b : danhSachDatHienTai) {
            soLuotTheoPhong.merge(b.getRoom().getMaPhong(), 1, Integer::sum);
            if (b.getTrangThai() == BookingStatus.DA_DAT) {
                soDaDat++;
            } else if (b.getTrangThai() == BookingStatus.DA_CHECKIN) {
                soDaCheckIn++;
                tongDoanhThu += (long) b.getPhi();
            } else if (b.getTrangThai() == BookingStatus.DA_HUY) {
                soDaHuy++;
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
        sb.append("Tổng số lượt đặt: ").append(danhSachDatHienTai.size()).append("\n");
        sb.append("- Đã đặt (chưa tới dùng): ").append(soDaDat).append("\n");
        sb.append("- Đã check-in (đã dùng phòng thật): ").append(soDaCheckIn).append("\n");
        sb.append("- Đã hủy: ").append(soDaHuy).append("\n\n");
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
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text), 1, 2));
        }
    }

    private String hienThiTrangThai(BookingStatus tt) {
        if (tt == BookingStatus.DA_DAT) return "Đã đặt";
        if (tt == BookingStatus.DA_CHECKIN) return "Đã check-in";
        if (tt == BookingStatus.DA_HUY) return "Đã hủy";
        if (tt == BookingStatus.HOAN_THANH) return "Hoàn thành";
        return tt.name();
    }

    private Booking bookingDuocChon() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = table.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= danhSachDatHienTai.size()) return null;
        return danhSachDatHienTai.get(modelRow);
    }

    private void updateStats() {
        int total = danhSachDatHienTai.size();
        int daDat = 0, daCheckIn = 0, daHuy = 0;
        for (Booking b : danhSachDatHienTai) {
            if (b.getTrangThai() == BookingStatus.DA_DAT) daDat++;
            else if (b.getTrangThai() == BookingStatus.DA_CHECKIN) daCheckIn++;
            else if (b.getTrangThai() == BookingStatus.DA_HUY) daHuy++;
        }
        cardTotal.setValue(String.valueOf(total));
        cardDaDat.setValue(String.valueOf(daDat));
        cardCheckIn.setValue(String.valueOf(daCheckIn));
        cardHuy.setValue(String.valueOf(daHuy));
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

                if (column == 7 && value != null) {
                    String status = value.toString();
                    if (status.equals("Đã check-in")) {
                        c.setForeground(UITheme.INFO);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (status.equals("Đã đặt")) {
                        c.setForeground(UITheme.SUCCESS);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (status.equals("Đã hủy")) {
                        c.setForeground(UITheme.DANGER);
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

    // ============================== NẠP DỮ LIỆU TỪ BOOKINGSERVICE ==============================

    private void refreshTableFromService() {
        try {
            danhSachDatHienTai = bookingService.layLichSuDat();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Không thể đọc dữ liệu đặt phòng: " + ex.getMessage(),
                    "Lỗi đọc file", JOptionPane.ERROR_MESSAGE);
            danhSachDatHienTai = new ArrayList<>();
        } catch (RuntimeException ex) {
            // Phòng trường hợp file dữ liệu bị hỏng/sai định dạng theo cách chưa lường
            // trước - báo lỗi rõ ràng cho người dùng thay vì để cả cửa sổ crash âm thầm.
            JOptionPane.showMessageDialog(this,
                    "Dữ liệu đặt phòng (data_booking.txt) có định dạng không hợp lệ, có thể là" +
                            " file cũ từ phiên bản trước.\nHãy xóa file data_booking.txt rồi mở lại ứng dụng.\n\n"
                            + "Chi tiết lỗi: " + ex,
                    "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            danhSachDatHienTai = new ArrayList<>();
        }

        tableModel.setRowCount(0);
        for (Booking b : danhSachDatHienTai) {
            tableModel.addRow(new Object[]{
                    b.getMaDatPhong(),
                    b.getRoom().getMaPhong(),
                    b.getStudent().getHoTen(),
                    b.getNgay().format(NGAY_FORMAT),
                    b.getSlot().toString(),
                    b.getSoNguoi(),
                    formatTien(b.getPhi()),
                    hienThiTrangThai(b.getTrangThai())
            });
        }
        refreshRoomCombo();
        updateStats();
    }
}
