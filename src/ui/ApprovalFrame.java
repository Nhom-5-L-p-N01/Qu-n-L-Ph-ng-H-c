package ui;

import enums.BookingStatus;
import model.ApprovalLog;
import model.Booking;
import model.User;
import repository.ApprovalRepository;
import repository.BookingRepository;
import repository.RoomRepository;
import service.ApprovalService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ApprovalFrame extends JFrame {

    private final ApprovalService approvalService;
    private final User admin;

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Ma dat phong", "Sinh vien", "Phong", "Ngay", "Khung gio", "So nguoi", "Phi"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextArea nhatKy = new JTextArea(8, 30);

    public ApprovalFrame(User admin, ApprovalService approvalService) {
        this.admin = admin;
        this.approvalService = approvalService;

        setTitle("Duyet yeu cau dat phong - " + admin.getHoTen());
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel tieuDe = new JLabel("  Danh sach yeu cau cho duyet");
        tieuDe.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(tieuDe, BorderLayout.NORTH);

        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);

        nhatKy.setEditable(false);
        JPanel duoi = new JPanel(new BorderLayout(6, 6));
        duoi.add(new JLabel("Nhat ky xu ly:"), BorderLayout.NORTH);
        duoi.add(new JScrollPane(nhatKy), BorderLayout.CENTER);

        JButton btnDuyet = new JButton("Duyet");
        JButton btnTuChoi = new JButton("Tu choi");
        JButton btnLamMoi = new JButton("Lam moi");
        JPanel nut = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nut.add(btnLamMoi);
        nut.add(btnTuChoi);
        nut.add(btnDuyet);
        duoi.add(nut, BorderLayout.SOUTH);
        add(duoi, BorderLayout.SOUTH);

        btnLamMoi.addActionListener(e -> napDuLieu());
        btnDuyet.addActionListener(e -> xuLyDuyet());
        btnTuChoi.addActionListener(e -> xuLyTuChoi());

        napDuLieu();
    }

    private String maDangChon() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Hay chon mot yeu cau trong bang");
            return null;
        }
        return (String) model.getValueAt(row, 0);
    }

    private void xuLyDuyet() {
        String ma = maDangChon();
        if (ma == null) return;
        try {
            approvalService.duyet(ma, admin);
            JOptionPane.showMessageDialog(this, "Da duyet yeu cau " + ma);
            napDuLieu();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Khong the duyet",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyTuChoi() {
        String ma = maDangChon();
        if (ma == null) return;
        String lyDo = JOptionPane.showInputDialog(this, "Ly do tu choi:");
        if (lyDo == null) return;
        try {
            approvalService.tuChoi(ma, admin, lyDo);
            JOptionPane.showMessageDialog(this, "Da tu choi yeu cau " + ma);
            napDuLieu();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Khong the tu choi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void napDuLieu() {
        model.setRowCount(0);
        try {
            List<Booking> ds = approvalService.layDanhSachChoDuyet();
            for (Booking b : ds) {
                model.addRow(new Object[]{
                        b.getMaDatPhong(),
                        b.getStudent().getMaSV() + " - " + b.getStudent().getHoTen(),
                        b.getRoom().getMaPhong(),
                        b.getNgay(),
                        b.getSlot().toString(),
                        b.getSoNguoi(),
                        b.getPhi()
                });
            }
            StringBuilder sb = new StringBuilder();
            for (ApprovalLog log : approvalService.layNhatKy()) {
                sb.append(log).append("\n");
            }
            nhatKy.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Loi doc du lieu",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void moManHinh(User admin, RoomRepository roomRepo,
                                 BookingRepository bookingRepo, String approvalFilePath) {
        ApprovalService svc = new ApprovalService(roomRepo, bookingRepo,
                new ApprovalRepository(approvalFilePath));
        SwingUtilities.invokeLater(() -> new ApprovalFrame(admin, svc).setVisible(true));
    }

    static BookingStatus trangThaiCanXuLy() { return BookingStatus.CHO_DUYET; }
}
