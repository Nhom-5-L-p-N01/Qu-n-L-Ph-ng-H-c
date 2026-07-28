import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MainFrame extends JFrame {
    private JTextField txtUser, txtSearch;
    private JComboBox<String> cbRoom, cbTime;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private final String FILE_PATH = "data_booking.txt";
    private boolean isAdmin = false;

    private JLabel lblStatTotal, lblStatPending, lblStatApproved;

    private final Color COLOR_PRIMARY = new Color(41, 98, 255);
    private final Color COLOR_ADMIN = new Color(220, 60, 60);
    private final Color COLOR_BG = new Color(245, 247, 252);
    private final Color COLOR_SUCCESS = new Color(46, 160, 90);
    private final Color COLOR_WARNING = new Color(235, 150, 30);
    private final Color COLOR_ROW_ALT = new Color(232, 238, 250);

    private final String[] ROOM_LIST = {"P.301", "P.302", "P.303", "P.204", "P.205", "Hội trường A"};
    private final String[] TIME_LIST = {"07:00 - 09:00", "09:00 - 11:00", "13:00 - 15:00", "15:00 - 17:00", "17:00 - 19:00"};

    public MainFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;
        initUI();
        loadDataFromFile();
    }

    public MainFrame() {
        this(false);
    }

    private void initUI() {
        Color themeColor = isAdmin ? COLOR_ADMIN : COLOR_PRIMARY;

        setTitle("HỆ THỐNG QUẢN LÝ ĐẶT PHÒNG HỌC - " + (isAdmin ? "QUYỀN ADMIN" : "QUYỀN USER"));
        setSize(830, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COLOR_BG);

        // ---- Tiêu đề ----
        String icon = isAdmin ? "🛡️" : "🎓";
        JLabel lblTitle = new JLabel(icon + "  QUẢN LÝ ĐẶT PHÒNG HỌC", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(themeColor);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(lblTitle, BorderLayout.NORTH);

        // ---- Panel giữa: tìm kiếm + form + bảng + thống kê ----
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(COLOR_BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.setBackground(COLOR_BG);

        // Thanh tìm kiếm
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(COLOR_BG);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel lblSearch = new JLabel("🔍 Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(lblSearch, BorderLayout.WEST);
        txtSearch = styledTextField();
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        topArea.add(searchPanel);

        // Form nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(themeColor, 1),
                        "📝 Thông tin đặt phòng",
                        0, 0, new Font("Segoe UI", Font.BOLD, 13), themeColor),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        inputPanel.add(styledLabel("🏫 Phòng học:"));
        cbRoom = new JComboBox<>(ROOM_LIST);
        cbRoom.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputPanel.add(cbRoom);

        inputPanel.add(styledLabel("👤 Người đặt:"));
        txtUser = styledTextField();
        inputPanel.add(txtUser);

        inputPanel.add(styledLabel("⏰ Khung giờ:"));
        cbTime = new JComboBox<>(TIME_LIST);
        cbTime.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputPanel.add(cbTime);

        topArea.add(inputPanel);
        centerPanel.add(topArea, BorderLayout.NORTH);

        // Bảng dữ liệu
        String[] columnNames = {"Tên Phòng", "Người Đặt", "Thời Gian", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(220, 224, 235));
        table.setSelectionBackground(themeColor);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(themeColor);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setDefaultRenderer(Object.class, new StatusRowRenderer());

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 235)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Thanh thống kê
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setBackground(COLOR_BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        lblStatTotal = createStatLabel("📊 Tổng: 0", new Color(90, 90, 90));
        lblStatPending = createStatLabel("⏳ Chờ duyệt: 0", COLOR_WARNING);
        lblStatApproved = createStatLabel("✅ Đã duyệt: 0", COLOR_SUCCESS);

        statsPanel.add(lblStatTotal);
        statsPanel.add(lblStatPending);
        statsPanel.add(lblStatApproved);
        centerPanel.add(statsPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // ---- Thanh nút bấm ----
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(COLOR_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JButton btnAdd = createStyledButton("📤 Gửi Đặt Phòng", COLOR_PRIMARY);
        JButton btnDelete = createStyledButton("🗑️ Hủy Lịch", COLOR_ADMIN);
        JButton btnApprove = createStyledButton("✅ Duyệt Phòng (Admin)", COLOR_SUCCESS);
        JButton btnLogout = createStyledButton("🚪 Đăng Xuất", new Color(110, 110, 110));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        if (isAdmin) {
            buttonPanel.add(btnApprove);
        }
        buttonPanel.add(btnLogout);
        add(buttonPanel, BorderLayout.SOUTH);

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
                txtUser.setText("");
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

        // Tìm kiếm trực tiếp khi gõ
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });
    }

    // Lọc bảng theo tên phòng hoặc người đặt
    private void filterTable() {
        String text = txtSearch.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text), 0, 1));
        }
    }

    // Cập nhật thanh thống kê
    private void updateStats() {
        int total = tableModel.getRowCount();
        int pending = 0, approved = 0;
        for (int i = 0; i < total; i++) {
            String status = String.valueOf(tableModel.getValueAt(i, 3));
            if (status.equals("Chờ duyệt")) pending++;
            else if (status.equals("Đã duyệt")) approved++;
        }
        lblStatTotal.setText("📊 Tổng: " + total);
        lblStatPending.setText("⏳ Chờ duyệt: " + pending);
        lblStatApproved.setText("✅ Đã duyệt: " + approved);
    }

    // ---- Các hàm tiện ích tạo giao diện ----

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
    }

    private JLabel createStatLabel(String text, Color color) {
        JLabel lbl = new JLabel(text, JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(color);
        lbl.setOpaque(true);
        lbl.setBackground(Color.WHITE);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 235)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return lbl;
    }

    private JTextField styledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 220), 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        return tf;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
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
                c.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_ROW_ALT);
                c.setForeground(Color.BLACK);

                if (column == 3 && value != null) {
                    String status = value.toString();
                    if (status.equals("Đã duyệt")) {
                        c.setForeground(COLOR_SUCCESS);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (status.equals("Chờ duyệt")) {
                        c.setForeground(COLOR_WARNING);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                } else {
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
            }
            return c;
        }
    }

    // [PHẦN 2] Hàm lưu dữ liệu bảng vào file text
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

    // [PHẦN 2] Hàm đọc dữ liệu từ file text hiển thị lên bảng
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame(true).setVisible(true));
    }
}
