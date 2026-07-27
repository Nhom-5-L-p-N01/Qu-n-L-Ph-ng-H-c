
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MainFrame extends JFrame {
    private JTextField txtRoomName, txtUser, txtTime;
    private JTable table;
    private DefaultTableModel tableModel;
    private final String FILE_PATH = "data_booking.txt";
    private boolean isAdmin = false;

    // Constructor nhận vào trạng thái có phải Admin không (gửi từ LoginFrame sang)
    public MainFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;
        initUI();
        loadDataFromFile(); // Tự động đọc dữ liệu cũ từ file lên bảng khi mở màn hình
    }

    // Constructor mặc định (chạy test độc lập)
    public MainFrame() {
        this(false);
    }

    private void initUI() {
        setTitle("HỆ THỐNG QUẢN LÝ ĐẶT PHÒNG HỌC - " + (isAdmin ? "QUYỀN ADMIN" : "QUYỀN USER"));
        setSize(750, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Tiêu đề màn hình
        JLabel lblTitle = new JLabel("QUẢN LÝ ĐẶT PHÒNG HỌC", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(isAdmin ? Color.RED : Color.BLUE);
        add(lblTitle, BorderLayout.NORTH);

        // 2. Form nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin đặt phòng"));

        inputPanel.add(new JLabel("Tên phòng học:"));
        txtRoomName = new JTextField();
        inputPanel.add(txtRoomName);

        inputPanel.add(new JLabel("Người đặt:"));
        txtUser = new JTextField();
        inputPanel.add(txtUser);

        inputPanel.add(new JLabel("Thời gian (VD: 08:00 - 10:00):"));
        txtTime = new JTextField();
        inputPanel.add(txtTime);

        // 3. Bảng dữ liệu
        String[] columnNames = {"Tên Phòng", "Người Đặt", "Thời Gian", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 4. Thanh nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Gửi Đặt Phòng");
        JButton btnDelete = new JButton("Hủy Lịch");
        JButton btnApprove = new JButton("Duyệt Phòng (Admin)");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        
        // Chỉ hiện nút Duyệt nếu đăng nhập bằng tài khoản Admin
        if (isAdmin) {
            buttonPanel.add(btnApprove);
        }
        add(buttonPanel, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN ---

        // Nút Thêm (Gửi Đặt Phòng)
        btnAdd.addActionListener(e -> {
            String room = txtRoomName.getText().trim();
            String user = txtUser.getText().trim();
            String time = txtTime.getText().trim();

            if (room.isEmpty() || user.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            } else {
                tableModel.addRow(new Object[]{room, user, time, "Chờ duyệt"});
                saveDataToFile(); // Lưu ngay dòng mới vào file
                txtRoomName.setText("");
                txtUser.setText("");
                txtTime.setText("");
                JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu đặt phòng!");
            }
        });

        // Nút Hủy Lịch
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                tableModel.removeRow(selectedRow);
                saveDataToFile(); // Cập nhật lại file sau khi xóa
                JOptionPane.showMessageDialog(this, "Đã hủy lịch thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng trong bảng để hủy!");
            }
        });

        // Nút Duyệt Phòng (Dành cho Admin)
        btnApprove.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                tableModel.setValueAt("Đã duyệt", selectedRow, 3);
                saveDataToFile(); // Cập nhật lại file sau khi duyệt
                JOptionPane.showMessageDialog(this, "Đã duyệt lịch đặt phòng thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch trong bảng để duyệt!");
            }
        });
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
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            tableModel.setRowCount(0); // Làm sạch bảng trước khi nạp
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
