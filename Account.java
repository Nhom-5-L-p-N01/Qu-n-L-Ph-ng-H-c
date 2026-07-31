public class Account {
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String matKhau;

    public Account() {}

    public Account(String hoTen, String email, String soDienThoai, String matKhau) {
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.matKhau = matKhau;
    }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String toLine() {
        return hoTen + "|" + email + "|" + soDienThoai + "|" + matKhau;
    }

    public static Account fromLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 4) return null;
        return new Account(p[0], p[1], p[2], p[3]);
    }
}
