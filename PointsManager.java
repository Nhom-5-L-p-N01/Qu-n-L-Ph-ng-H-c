import java.io.*;
import java.util.*;


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

   
    public int tinhDiemCong(double phiThucTra) {
        return (int) Math.floor(phiThucTra / 10000.0) * DIEM_MOI_10K;
    }

   
    public int congDiem(String tenNguoiDat, double phiThucTra) {
        int diemCong = tinhDiemCong(phiThucTra);
        int diemMoi = layDiem(tenNguoiDat) + diemCong;
        diemTheoNguoiDat.put(tenNguoiDat, diemMoi);
        save();
        return diemCong;
    }

   
    public void truDiem(String tenNguoiDat, int soDiem) {
        int diemMoi = Math.max(0, layDiem(tenNguoiDat) - soDiem);
        diemTheoNguoiDat.put(tenNguoiDat, diemMoi);
        save();
    }
}
