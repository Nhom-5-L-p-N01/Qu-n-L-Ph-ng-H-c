import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MainFrame extends JFrame {
    private JTextField txtRoomName, txtUser, txtTime;
    private JTable table;
    private DefaultTableModel tableModel;
    private final String FILE_PATH = "data_booking.txt";
    private boolean isAdmin = false;

    // Bảng màu chủ đạo
    private final Color COLOR_PRIMARY = new Color(41, 98, 255);
    private final Color COLOR_ADMIN = new Color(220, 60, 60);
    private final Color COLOR_BG = new Color(245, 247, 252);
    private final Color COLOR_SUCCESS = new Color(46, 160, 90);
    private final Color COLOR_WARNING = new Color(235, 150, 30);
    private final Color COLOR_ROW_ALT = new Color(232, 238, 250);

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
        setSize(800, 560);
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
        lblTitle.setOpaque(true);
        lblTitle.setBackground(COLOR_BG);
        add(lblTitle, BorderLayout.NORTH);

        // ---- Form nhập liệu ----
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(themeColor, 1),
                        "📝 Thông tin đặt phòng",
                        0, 0, new Font("Segoe UI", Font.BOLD, 13), themeColor),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        inputPanel.add(styledLabel("🏫 Tên phòng học:"));
        txtRoomName = styledTextField();
        inputPanel.add(txtRoomName);

        inputPanel.add(styledLabel("👤 Người đặt:"));
        txtUser = styledTextField();
        inputPanel.add(txtUser);

        inputPanel.add(styledLabel("⏰ Thời gian (VD: 08:00 - 10:00):"));
        txtTime = styledTextField();
        inputPanel.add(txtTime);

        // ---- Bảng dữ liệu ----
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

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(COLOR_BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 235)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // ---- Thanh nút bấm ----
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(COLOR_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JButton btnAdd = createStyledButton("📤 Gửi Đặt Phòng", COLOR_PRIMARY);
        JButton btnDelete = createStyledButton("🗑️ Hủy Lịch", COLOR_ADMIN);
        JButton btnApprove = createStyledButton("✅ Duyệt Phòng (Admin)", COLOR_SUCCESS);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);

        if (isAdmin) {
            buttonPanel.add(btnApprove);
        }
        add(buttonPanel, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN (giữ nguyên logic gốc) ---

        btnAdd.addActionListener(e -> {
            String room = txtRoomName.getText().trim();
            String user = txtUser.getText().trim();
            String time = txtTime.getText().trim();

            if (room.isEmpty() || user.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            } else {
                tableModel.addRow(new Object[]{room, user, time, "Chờ duyệt"});
                saveDataToFile();
                txtRoomName.setText("");
                txtUser.setText("");
                txtTime.setText("");
                JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu đặt phòng!");
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                tableModel.removeRow(selectedRow);
                saveDataToFile();
                JOptionPane.showMessageDialog(this, "Đã hủy lịch thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng trong bảng để hủy!");
            }
        });

        btnApprove.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                tableModel.setValueAt("Đã duyệt", selectedRow, 3);
                saveDataToFile();
                JOptionPane.showMessageDialog(this, "Đã duyệt lịch đặt phòng thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch trong bảng để duyệt!");
            }
        });
    }

    // ---- Các hàm tiện ích tạo giao diện ----

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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

    // Tô màu xen kẽ dòng + tô màu theo trạng thái ở cột cuối
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

    // [PHẦN 2] Hàm lưu dữ liệu bảng vào file text (giữ nguyên gốc)
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

    // [PHẦN 2] Hàm đọc dữ liệu từ file text hiển thị lên bảng (giữ nguyên gốc)
    private void loadDataFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame(true).setVisible(true));
    }
}
 
 
