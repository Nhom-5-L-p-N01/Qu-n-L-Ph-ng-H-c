package model;

import java.time.LocalDateTime;

public class ApprovalLog {
    private final String maDatPhong;
    private final String nguoiXuLy;      
    private final String hanhDong;       
    private final String lyDo;
    private final LocalDateTime thoiGian;

    public ApprovalLog(String maDatPhong, String nguoiXuLy, String hanhDong,
                       String lyDo, LocalDateTime thoiGian) {
        this.maDatPhong = maDatPhong;
        this.nguoiXuLy = nguoiXuLy;
        this.hanhDong = hanhDong;
        this.lyDo = (lyDo == null) ? "" : lyDo.replace("|", "/");
        this.thoiGian = thoiGian;
    }

    public String getMaDatPhong() { return maDatPhong; }
    public String getNguoiXuLy() { return nguoiXuLy; }
    public String getHanhDong() { return hanhDong; }
    public String getLyDo() { return lyDo; }
    public LocalDateTime getThoiGian() { return thoiGian; }

    public String toLine() {
        return String.join("|", maDatPhong, nguoiXuLy, hanhDong, lyDo, thoiGian.toString());
    }

    public static ApprovalLog fromLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 5) return null;
        return new ApprovalLog(p[0], p[1], p[2], p[3], LocalDateTime.parse(p[4]));
    }

    @Override
    public String toString() {
        return thoiGian + " | " + hanhDong + " | " + maDatPhong + " | " + nguoiXuLy
                + (lyDo.isEmpty() ? "" : " | " + lyDo);
    }
}
