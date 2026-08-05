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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {
    private JTextField txtUser, txtSearch;
    private JComboBox<String> cbRoom, cbTime;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private final String FILE_PATH = "data_booking.txt";
    private boolean isAdmin = false;
    private String currentUsername;
    //===================== LAYOUT =====================

    private CardLayout cardLayout;
    private JPanel contentPanel;

    private JPanel bookingPanel;
    private DashboardPanel dashboardPanel;

    private HistoryPanel historyPanel;
    private JPanel profilePanel;
    private JPanel statisticPanel;
    private JPanel userPanel;

//===================== HEADER =====================

    private JLabel lblTitle;
    private JLabel lblWelcome;
    private JLabel lblClock;

//===================== MENU =====================

    private JButton btnDashboard;

    private JButton btnBooking;
    private JButton btnHistory;
    private JButton btnProfile;
    private JButton btnStatistic;
    private JButton btnUser;
    private JButton btnLogout;

    private JLabel lblStatTotal, lblStatPending, lblStatApproved;

    private static final Color COLOR_ACCENT = new Color(41, 182, 246);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_PANEL = new Color(8, 14, 28, 200);
    private static final Color COLOR_TABLE_BG = new Color(8, 14, 28, 170);
    private static final Color COLOR_ROW_ALT = new Color(20, 30, 50, 170);
    private static final Color COLOR_AMBER = new Color(255, 193, 7);
    private static final Color COLOR_DANGER = new Color(230, 90, 90);
    private static final Color COLOR_SUCCESS = new Color(80, 220, 160);

    private final String[] ROOM_LIST = {"P.301", "P.302", "P.303", "P.204", "P.205", "Hội trường A"};
    private final String[] TIME_LIST = {"07:00 - 09:00", "09:00 - 11:00", "13:00 - 15:00", "15:00 - 17:00", "17:00 - 19:00"};

    // Panel vẽ ảnh nền thật, co giãn theo kích thước cửa sổ
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
                g.setColor(new Color(6, 10, 22));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public MainFrame(boolean isAdmin, String currentUsername) {

        this.isAdmin = isAdmin;
        this.currentUsername = currentUsername;
        cardLayout = new CardLayout();
        initUI();
        loadDataFromFile();
        updateStats();
        updateDashboard();
        updateHistory();
        startClock();

    }

    public MainFrame() {
        this(false, "");
    }

    private void initUI() {
        setTitle("HỆ THỐNG QUẢN LÝ ĐẶT PHÒNG HỌC - " + (isAdmin ? "QUYỀN ADMIN" : "QUYỀN USER"));
        setSize(830, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageBackgroundPanel root = new ImageBackgroundPanel(new BorderLayout());
        setContentPane(root);

        // ---- Tiêu đề ----
        root.add(createHeader(), BorderLayout.NORTH);

        // ---- Panel giữa ----
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.setOpaque(false);

        // Thanh tìm kiếm
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSearch.setForeground(Color.BLACK);
        searchPanel.add(lblSearch, BorderLayout.WEST);
        txtSearch = styledTextField();
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        topArea.add(searchPanel);

        // Form nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
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
        inputPanel.add(cbRoom);

        inputPanel.add(styledLabel("Người đặt:"));
        txtUser = styledTextField();
        if (!isAdmin) {
            txtUser.setText(currentUsername);
            txtUser.setEditable(false);
        }
        inputPanel.add(txtUser);

        inputPanel.add(styledLabel("Khung giờ:"));
        cbTime = new JComboBox<>(TIME_LIST);
        styleCombo(cbTime);
        inputPanel.add(cbTime);

        topArea.add(inputPanel);
        centerPanel.add(topArea, BorderLayout.NORTH);

        // Bảng dữ liệu
        String[] columnNames = {"Tên Phòng", "Người Đặt", "Thời Gian", "Trạng Thái"};
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

        // Thanh thống kê
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        lblStatTotal = createStatLabel("Tổng: 0", COLOR_WHITE);
        lblStatPending = createStatLabel("Chờ duyệt: 0", COLOR_AMBER);
        lblStatApproved = createStatLabel("Đã duyệt: 0", COLOR_SUCCESS);

        statsPanel.add(lblStatTotal);
        statsPanel.add(lblStatPending);
        statsPanel.add(lblStatApproved);
        centerPanel.add(statsPanel, BorderLayout.SOUTH);

        contentPanel = new JPanel(cardLayout);

        bookingPanel = centerPanel;
        historyPanel = new HistoryPanel();

        historyPanel.setCurrentUser(currentUsername);

// Luôn thêm màn hình Đặt phòng
        contentPanel.add(bookingPanel, "BOOKING");
        contentPanel.add(historyPanel, "HISTORY");


// Chỉ Admin mới có Dashboard
        if (isAdmin) {

            dashboardPanel = new DashboardPanel();

            contentPanel.add(dashboardPanel, "DASHBOARD");

            updateDashboard();
            updateHistory();

            cardLayout.show(contentPanel, "DASHBOARD");

        } else {

            cardLayout.show(contentPanel, "BOOKING");

        }

        root.add(contentPanel, BorderLayout.CENTER);
        root.add(createSidebar(), BorderLayout.WEST);

        // ---- Thanh nút bấm ----
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JButton btnAdd = createStyledButton("Gửi Đặt Phòng", COLOR_ACCENT);
        JButton btnDelete = createStyledButton("Hủy Lịch", COLOR_DANGER);
        JButton btnApprove = createStyledButton("Duyệt Phòng (Admin)", COLOR_SUCCESS);
        JButton btnLogout = createStyledButton("Đăng Xuất", new Color(100, 110, 130));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        if (isAdmin) {
            buttonPanel.add(btnApprove);
        }
        buttonPanel.add(btnLogout);
        root.add(buttonPanel, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN ---

        btnAdd.addActionListener(e -> {
            String room = (String) cbRoom.getSelectedItem();
            String user = txtUser.getText().trim();
            String time = (String) cbTime.getSelectedItem();

            if (user.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên người đặt!");
            } else {
                tableModel.addRow(new Object[]{room, user, time, "Chờ duyệt"});
                saveDataToFile();
                updateStats();
                updateDashboard();
                updateHistory();
                if(isAdmin){

                    txtUser.setText("");

                }else{

                    txtUser.setText(currentUsername);

                }
                JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu đặt phòng!");
            }
        });

        btnDelete.addActionListener(e -> {
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
                    updateDashboard();
                    updateHistory();
                    JOptionPane.showMessageDialog(this, "Đã hủy lịch thành công!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng trong bảng để hủy!");
            }
        });

        btnApprove.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow >= 0) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                tableModel.setValueAt("Đã duyệt", modelRow, 3);
                saveDataToFile();
                updateStats();
                updateDashboard();
                updateHistory();
                JOptionPane.showMessageDialog(this, "Đã duyệt lịch đặt phòng thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch trong bảng để duyệt!");
            }
        });

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
        int pending = 0, approved = 0;
        for (int i = 0; i < total; i++) {
            String status = String.valueOf(tableModel.getValueAt(i, 3));
            if (status.equals("Chờ duyệt")) pending++;
            else if (status.equals("Đã duyệt")) approved++;
        }
        lblStatTotal.setText("Tổng: " + total);
        lblStatPending.setText("Chờ duyệt: " + pending);
        lblStatApproved.setText("Đã duyệt: " + approved);
    }

    // ---- Các hàm tiện ích tạo giao diện ----

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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(COLOR_WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
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

                if (column == 3 && value != null) {
                    String status = value.toString();
                    if (status.equals("Đã duyệt")) {
                        c.setForeground(COLOR_SUCCESS);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (status.equals("Chờ duyệt")) {
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
                        tableModel.getValueAt(i, 3);
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
                if (data.length == 4) {
                    tableModel.addRow(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateStats();
    }
    private JPanel createHeader() {

        JPanel panel = new JPanel(new BorderLayout());

        panel.setPreferredSize(new Dimension(0,70));

        panel.setBackground(new Color(15,25,45));

        lblTitle = new JLabel("QUẢN LÝ PHÒNG HỌC");

        lblTitle.setFont(new Font("Segoe UI",Font.BOLD,22));

        lblTitle.setForeground(Color.WHITE);

        lblTitle.setBorder(
                BorderFactory.createEmptyBorder(
                        0,20,0,0));

        panel.add(lblTitle,BorderLayout.WEST);

        JPanel right = new JPanel(
                new GridLayout(2,1));

        right.setOpaque(false);

        lblWelcome = new JLabel(
                isAdmin ?
                        "Xin chào Admin"
                        :
                        "Xin chào Sinh viên");

        lblWelcome.setForeground(Color.WHITE);

        lblClock = new JLabel();

        lblClock.setForeground(Color.WHITE);

        right.add(lblWelcome);

        right.add(lblClock);

        panel.add(right,BorderLayout.EAST);

        return panel;

    }
    private void startClock() {

        Timer timer = new Timer(1000, e -> {

            LocalDateTime now = LocalDateTime.now();

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "dd/MM/yyyy HH:mm:ss");

            lblClock.setText(
                    now.format(formatter));

        });

        timer.start();

    }
    private JPanel createSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setPreferredSize(new Dimension(220,0));

        sidebar.setBackground(new Color(15,25,45));

        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));

        if (isAdmin) {

            btnDashboard = createMenuButton("Dashboard");

            btnDashboard.setIcon(
                    UIManager.getIcon("FileView.directoryIcon"));

            btnDashboard.addActionListener(e -> {

                cardLayout.show(contentPanel, "DASHBOARD");

            });

        }



        btnBooking = createMenuButton("Đặt phòng");
        btnBooking.setIcon(UIManager.getIcon("FileChooser.newFolderIcon"));
        btnBooking.addActionListener(e -> {
            cardLayout.show(contentPanel, "BOOKING");
        });

//===================== HISTORY =====================

        btnHistory = createMenuButton("Lịch sử");
        btnHistory.setIcon(UIManager.getIcon("FileView.fileIcon"));

        btnHistory.addActionListener(e -> {
            cardLayout.show(contentPanel, "HISTORY");
        });

        if (isAdmin) {

            btnUser = createMenuButton("Sinh viên");

            btnUser.setIcon(
                    UIManager.getIcon("FileChooser.detailsViewIcon"));

        }

        btnProfile = createMenuButton("Hồ sơ");
        btnProfile.setIcon(UIManager.getIcon("FileChooser.homeFolderIcon"));

        if (isAdmin) {

            btnStatistic = createMenuButton("Thống kê");

            btnStatistic.setIcon(
                    UIManager.getIcon("Tree.closedIcon"));

        }

        btnLogout = createMenuButton("Đăng xuất");
        btnLogout.setIcon(UIManager.getIcon("InternalFrame.closeIcon"));

        if(isAdmin){
            sidebar.add(btnDashboard);
        }

        sidebar.add(btnBooking);

        sidebar.add(btnHistory);

        if(isAdmin){
            sidebar.add(btnUser);
        }

        sidebar.add(btnProfile);

        if(isAdmin){
            sidebar.add(btnStatistic);
        }

        sidebar.add(Box.createVerticalGlue());

        sidebar.add(btnLogout);
        System.out.println(UIManager.getIcon("FileView.directoryIcon"));

        return sidebar;
    }
    private JButton createMenuButton(String text) {

        JButton btn = new JButton(text);

        btn.setIconTextGap(12);

        btn.setMaximumSize(new Dimension(200,45));

        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.setBackground(new Color(15,25,45));

        btn.setForeground(Color.WHITE);

        btn.setFont(new Font("Segoe UI",Font.BOLD,15));

        btn.setFocusPainted(false);

        btn.setBorder(BorderFactory.createEmptyBorder(10,20,10,10));

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }
    private void updateDashboard() {

        if (!isAdmin || dashboardPanel == null) {
            return;
        }

        int totalRoom = ROOM_LIST.length;

        int totalBooking = tableModel.getRowCount();

        int pending = 0;

        int approved = 0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {

            String status = tableModel.getValueAt(i,3).toString();

            if(status.equals("Chờ duyệt")){

                pending++;

            }else if(status.equals("Đã duyệt")){

                approved++;

            }

        }

        dashboardPanel.updateDashboard(
                totalRoom,
                totalBooking,
                pending,
                approved);
        dashboardPanel.updateRecentBooking(tableModel);

    }
    private void updateHistory(){

        if(historyPanel!=null){

            historyPanel.loadHistory(
                    tableModel,
                    isAdmin
            );

        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new MainFrame(true, "admin").setVisible(true));
    }

}
