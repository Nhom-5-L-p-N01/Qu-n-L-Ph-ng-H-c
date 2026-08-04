package model;

public class Student extends User {
    private String maSV;
    private String lop;

    public Student(String id, String hoTen, String maSV, String lop) {
        super(id, hoTen);
        this.maSV = maSV;
        this.lop = lop;
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public String getLop() {
        return lop;
    }

    public void setLop(String lop) {
        this.lop = lop;
    }

    @Override
    public String getVaiTro() {
        return "SINH_VIEN";
    }
}
