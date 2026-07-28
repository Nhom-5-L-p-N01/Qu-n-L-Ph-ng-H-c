import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.geom.GeneralPath;
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

    private static final Color COLOR_BG = new Color(12, 14, 12);
    private static final Color COLOR_PANEL = new Color(22, 26, 22);
    private static final Color COLOR_GREEN = new Color(0, 230, 118);
    private static final Color COLOR_GREEN_DIM = new Color(0, 140, 70);
    private static final Color COLOR_AMBER = new Color(235, 180, 40);
    private static final Color COLOR_DANGER = new Color(210, 80, 80);
    private static final Color COLOR_ROW_ALT = new Color(18, 22, 18);

    private final String[] ROOM_LIST = {"P.301", "P.302", "P.303", "P.204", "P.205", "Hội trường A"};
    private final String[] TIME_LIST = {"07:00 - 09:00", "09:00 - 11:00", "13:00 - 15:00", "15:00 - 17:00", "17:00 - 19:00"};

    // Panel nền đen có vẽ họa tiết quyển sách mờ ở góc
    static class BookBackgroundPanel extends JPanel {
        BookBackgroundPanel(LayoutManager lm) {
            super(lm);
            setOpaque(true);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_BG);
            g2.fillRect(0, 0, getWidth(), getHeight());

            int w = 160, h = 80;
            int cx = getWidth() - w / 2 - 20;
            int cy = h / 2 + 20;

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
            g2.setColor(COLOR_GREEN);
            g2.setStroke(new BasicStroke(2f));

            GeneralPath left = new GeneralPath();
            left.moveTo(cx, cy - h / 2);
            left.quadTo(cx - w / 2, cy - h / 2 - 8, cx - w / 2, cy);
            left.quadTo(cx - w / 2, cy + h / 2, cx, cy + h / 2 - 6);
            left.closePath();

            GeneralPath right = new GeneralPath();
            right.moveTo(cx, cy - h / 2);
            right.quadTo(cx + w / 2, cy - h / 2 - 8, cx + w / 2, cy);
            right.quadTo(cx + w / 2, cy + h / 2, cx, cy + h / 2 - 6);
            right.closePath();

            g2.draw(left);
            g2.draw(right);
            g2.drawLine(cx, cy - h / 2, cx, cy + h / 2 - 6);
            g2.dispose();
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
        setSize(830, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BookBackgroundPanel root = new BookBackgroundPanel(new BorderLayout(10, 10));
        setContentPane(root);

        // ---- Tiêu đề ----
        JLabel lblTitle = new JLabel("QUẢN LÝ ĐẶT PHÒNG HỌC", JLabel.CENTER);
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 22));
        lblTitle.setForeground(COLOR_GREEN);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        root.add(lblTitle, BorderLayout.NORTH);

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
        lblSearch.setFont(new Font("Consolas", Font.PLAIN, 13));
        lblSearch.setForeground(COLOR_GREEN);
        searchPanel.add(lblSearch, BorderLayout.WEST);
        txtSearch = styledTextField();
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        topArea.add(searchPanel);

        // Form nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(COLOR_PANEL);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COLOR_GREEN_DIM, 1),
                        "Thông tin đặt phòng",
                        0, 0, new Font("Consolas", Font.BOLD, 13), COLOR_GREEN),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        inputPanel.add(styledLabel("Phòng học:"));
        cbRoom = new JComboBox<>(ROOM_LIST);
        styleCombo(cbRoom);
        inputPanel.add(cbRoom);

        inputPanel.add(styledLabel("Người đặt:"));
        txtUser = styledTextField();
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
        table.setFont(new Font("Consolas", Font.PLAIN, 13));
        table.setGridColor(new Color(35, 45, 35));
        table.setBackground(COLOR_BG);
        table.setForeground(COLOR_GREEN);
        table.setSelectionBackground(COLOR_GREEN_DIM);
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Consolas", Font.BOLD, 13));
        table.getTableHeader().setBackground(COLOR_PANEL);
        table.getTableHeader().setForeground(COLOR_GREEN);
        table.setDefaultRenderer(Object.class, new StatusRowRenderer());

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_GREEN_DIM));
        scrollPane.getViewport().setBackground(COLOR_BG);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Thanh thống kê
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        lblStatTotal = createStatLabel("Tổng: 0", COLOR_GREEN);
        lblStatPending = createStatLabel("Chờ duyệt: 0", COLOR_AMBER);
        lblStatApproved = createStatLabel("Đã duyệt: 0", COLOR_GREEN);

        statsPanel.add(lblStatTotal);
        statsPanel.add(lblStatPending);
        statsPanel.add(lblStatApproved);
        centerPanel.add(statsPanel, BorderLayout.SOUTH);

        root.add(centerPanel, BorderLayout.CENTER);

        // ---- Thanh nút bấm ----
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JButton btnAdd = createStyledButton("Gửi Đặt Phòng", COLOR_GREEN);
        JButton btnDelete = createStyledButton("Hủy Lịch", COLOR_DANGER);
        JButton btnApprove = createStyledButton("Duyệt Phòng (Admin)", COLOR_GREEN);
        JButton btnLogout = createStyledButton("Đăng Xuất", COLOR_GREEN_DIM);

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
        lbl.setFont(new Font("Consolas", Font.PLAIN, 13));
        lbl.setForeground(COLOR_GREEN);
        return lbl;
    }

    private JLabel createStatLabel(String text, Color color) {
        JLabel lbl = new JLabel(text, JLabel.CENTER);
        lbl.setFont(new Font("Consolas", Font.BOLD, 13));
        lbl.setForeground(color);
        lbl.setOpaque(true);
        lbl.setBackground(COLOR_PANEL);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_GREEN_DIM),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return lbl;
    }

    private JTextField styledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Consolas", Font.PLAIN, 13));
        tf.setBackground(COLOR_PANEL);
        tf.setForeground(COLOR_GREEN);
        tf.setCaretColor(COLOR_GREEN);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_GREEN_DIM, 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        return tf;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Consolas", Font.PLAIN, 13));
        combo.setBackground(COLOR_PANEL);
        combo.setForeground(COLOR_GREEN);
    }

    private JButton createStyledButton(String text, Color accent) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.BOLD, 13));
        btn.setForeground(accent);
        btn.setBackground(COLOR_PANEL);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private class StatusRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? COLOR_BG : COLOR_ROW_ALT);
                c.setForeground(COLOR_GREEN);

                if (column == 3 && value != null) {
                    String status = value.toString();
                    if (status.equals("Đã duyệt")) {
                        c.setForeground(COLOR_GREEN);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame(true).setVisible(true));
    }
}
