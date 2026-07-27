import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        // Thiết lập tiêu đề và kích thước cửa sổ
        setTitle("HỆ THỐNG QUẢN LÝ PHÒNG HỌC");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Căn giữa màn hình

        // Tạo giao diện đơn giản
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Màn Hình Quản Lý Đặt Phòng");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton btnBooking = new JButton("Đặt phòng");
        JButton btnCancel = new JButton("Hủy phòng");

        panel.add(label);
        panel.add(btnBooking);
        panel.add(btnCancel);

        add(panel);
    }

    public static void main(String[] args) {
        // Chạy chương trình
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
