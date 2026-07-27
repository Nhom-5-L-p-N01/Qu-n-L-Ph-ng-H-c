import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    // Các thành phần giao diện
    private JTextField txtRoomName, txtUser, txtTime;
    private JTable table;
    private DefaultTableModel tableModel;

    public MainFrame() {
        // 1. Cấu hình cửa sổ chính
        setTitle("HỆ THỐNG QUẢN LÝ ĐẶT PHÒNG HỌC");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 2. Tiêu đề (Header)
        JLabel lblTitle = new JLabel("QUẢN LÝ ĐẶT PHÒNG HỌC", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.BLUE);
        add(lblTitle, BorderLayout.NORTH);

        // 3. Khhu vực Nhập thông tin (Form Panel)
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin đặt phòng"));

        inputPanel.add(new JLabel("Tên phòng học:"));
        txtRoomName = new JTextField();
        inputPanel.add(txtRoomName);

        inputPanel.add(new JLabel("Người đặt (Mã/Tên):"));
        txtUser = new JTextField();
        inputPanel.add(txtUser);

        inputPanel.add(new JLabel("Thời gian đặt (VD: 08:00 - 10:00):"));
        txtTime = new JTextField();
        inputPanel.add(txtTime);

        // 4. Bảng hiển thị danh sách (Table Panel)
        String[] columnNames = {"Tên Phòng", "Người Đặt", "Thời Gian", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Gom Form và Bảng vào giữa
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 5. Thanh nút bấm (Button Panel)
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Đặt Phòng");
        JButton btnDelete = new JButton("Hủy Lịch");
        JButton btnClear = new JButton("Xóa Trắng Form");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN NÚT BẤM ---

        // Nút Đặt Phòng: Lấy dữ liệu từ form thêm vào Bảng
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String room = txtRoomName.getText().trim();
                String user = txtUser.getText().trim();
                String time = txtTime.getText().trim();

                if (room.isEmpty() || user.isEmpty() || time.isEmpty()) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } else {
                    tableModel.addRow(new Object[]{room, user, time, "Chờ duyệt"});
                    clearForm();
                    JOptionPane.showMessageDialog(MainFrame.this, "Đã gửi yêu cầu đặt phòng!");
                }
            }
        });

        // Nút Hủy Lịch: Xóa dòng đang chọn trên Bảng
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(MainFrame.this, "Đã hủy lịch đặt thành công!");
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "Vui lòng chọn 1 lịch trong bảng để hủy!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Nút Xóa Trắng Form
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
    }

    private void clearForm() {
        txtRoomName.setText("");
        txtUser.setText("");
        txtTime.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
