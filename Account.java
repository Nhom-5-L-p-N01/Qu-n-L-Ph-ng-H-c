public class Account {
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String matKhau;
    private String maSV;
    private String lop;

    public Account() {}

    public Account(String hoTen, String email, String soDienThoai, String matKhau,
                    String maSV, String lop) {
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.matKhau = matKhau;
        this.maSV = maSV;
        this.lop = lop;
    }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getMaSV() { return maSV; }
    public void setMaSV(String maSV) { this.maSV = maSV; }

    public String getLop() { return lop; }
    public void setLop(String lop) { this.lop = lop; }

    // Chuyển thành 1 dòng để lưu file (ho ten|email|sdt|mat khau|ma sv|lop)
    public String toLine() {
        return hoTen + "|" + email + "|" + soDienThoai + "|" + matKhau + "|" + maSV + "|" + lop;
    }

    public static Account fromLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length >= 6) {
            return new Account(p[0], p[1], p[2], p[3], p[4], p[5]);
        }
        if (p.length == 4) {
            // Du lieu tai khoan tao truoc khi co them truong Ma sinh vien/Lop.
            // Van cho dang nhap duoc binh thuong; dung email lam ma SV tam thoi
            // de moi tai khoan cu van co 1 dinh danh rieng biet (khong bi trung "").
            return new Account(p[0], p[1], p[2], p[3], p[1], "");
        }
        return null;
    }
}
