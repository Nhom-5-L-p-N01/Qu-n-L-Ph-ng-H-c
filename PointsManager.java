import java.io.*;
import java.util.*;

/**
 * Quản lý điểm tích lũy theo tên người đặt (do hệ thống hiện tại chưa có
 * tài khoản sinh viên thật, dùng "Người đặt" làm khóa - giống cách toàn bộ
 * MainFrame đang quản lý dữ liệu).
 *
 * Quy tắc cộng điểm: mô phỏng theo hệ thống Rạp Chiếu Phim Mặt Trời Nhỏ -
 * 1 điểm cho mỗi 10.000đ phí phòng đã thanh toán thực tế (sau khi trừ voucher),
 * chỉ cộng điểm khi phòng ĐÃ ĐƯỢC CHECK-IN THẬT (không cộng khi mới "Chờ duyệt"
 * hay "Đã duyệt" nhưng chưa tới dùng phòng - tránh gian lận đặt-rồi-hủy để ăn điểm).
 */
public class PointsManager {
    private static final String FILE_PATH = "data_points.txt";
    public static final int DIEM_MOI_10K = 1;

    private final Map<String, Integer> diemTheoNguoiDat = new LinkedHashMap<>();

    public PointsManager() {
        load();
    }

    private void load() {
        File f = new File(FILE_PATH);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", 2);
                if (p.length == 2) {
                    diemTheoNguoiDat.put(p[0], Integer.parseInt(p[1].trim()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<String, Integer> e : diemTheoNguoiDat.entrySet()) {
                bw.write(e.getKey() + "|" + e.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int layDiem(String tenNguoiDat) {
        return diemTheoNguoiDat.getOrDefault(tenNguoiDat, 0);
    }

    /** Tính số điểm cộng thêm dựa trên phí thực trả, KHÔNG lưu ngay. */
    public int tinhDiemCong(double phiThucTra) {
        return (int) Math.floor(phiThucTra / 10000.0) * DIEM_MOI_10K;
    }

    /** Cộng điểm cho người đặt (gọi khi check-in thành công) và lưu file. */
    public int congDiem(String tenNguoiDat, double phiThucTra) {
        int diemCong = tinhDiemCong(phiThucTra);
        int diemMoi = layDiem(tenNguoiDat) + diemCong;
        diemTheoNguoiDat.put(tenNguoiDat, diemMoi);
        save();
        return diemCong;
    }

    /** Trừ điểm khi đổi voucher (gọi sau khi đã kiểm tra đủ điều kiện). */
    public void truDiem(String tenNguoiDat, int soDiem) {
        int diemMoi = Math.max(0, layDiem(tenNguoiDat) - soDiem);
        diemTheoNguoiDat.put(tenNguoiDat, diemMoi);
        save();
    }
}
