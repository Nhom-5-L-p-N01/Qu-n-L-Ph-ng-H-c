import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class VoucherManager {
    private static final String FILE_PATH = "data_vouchers.txt";

    public static final int DIEM_TOI_THIEU = 50;
    public static final int BOI_SO_DOI = 10;
    public static final int GIA_TRI_MOI_DIEM = 1000; // 1 điểm = 1.000đ giảm giá

    public static class Voucher {
        public String maVoucher;
        public String chuSoHuu;
        public int giaTriGiam;
        public boolean daDung;

        Voucher(String maVoucher, String chuSoHuu, int giaTriGiam, boolean daDung) {
            this.maVoucher = maVoucher;
            this.chuSoHuu = chuSoHuu;
            this.giaTriGiam = giaTriGiam;
            this.daDung = daDung;
        }

        String toLine() {
            return maVoucher + "|" + chuSoHuu + "|" + giaTriGiam + "|" + daDung;
        }
    }

    private final List<Voucher> danhSach = new ArrayList<>();

    public VoucherManager() {
        load();
    }

    private void load() {
        File f = new File(FILE_PATH);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                if (p.length == 4) {
                    danhSach.add(new Voucher(p[0], p[1], Integer.parseInt(p[2]), Boolean.parseBoolean(p[3])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Voucher v : danhSach) {
                bw.write(v.toLine());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Voucher doiVoucher(String chuSoHuu, int soDiem) {
        String ma = "PH-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"))
                + new Random().nextInt(90 + 10);
        Voucher v = new Voucher(ma, chuSoHuu, soDiem * GIA_TRI_MOI_DIEM, false);
        danhSach.add(v);
        save();
        return v;
    }

    public Voucher timVoucherHopLe(String maVoucher, String chuSoHuu) {
        for (Voucher v : danhSach) {
            if (v.maVoucher.equalsIgnoreCase(maVoucher.trim())
                    && v.chuSoHuu.equalsIgnoreCase(chuSoHuu.trim())
                    && !v.daDung) {
                return v;
            }
        }
        return null;
    }

    public void danhDauDaDung(Voucher v) {
        v.daDung = true;
        save();
    }

    public List<Voucher> layVoucherCuaKhach(String chuSoHuu) {
        List<Voucher> ketQua = new ArrayList<>();
        for (Voucher v : danhSach) {
            if (v.chuSoHuu.equalsIgnoreCase(chuSoHuu.trim())) {
                ketQua.add(v);
            }
        }
        return ketQua;
    }
}
