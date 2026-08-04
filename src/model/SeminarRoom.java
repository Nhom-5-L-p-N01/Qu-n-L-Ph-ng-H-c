package model;

public class SeminarRoom extends Room {
    private static final double DON_GIA = 50000;

    public SeminarRoom(String maPhong, String tenPhong, int tang, int sucChua) {
        super(maPhong, tenPhong, tang, sucChua);
    }

    @Override
    public double tinhPhi(double soGio) {
        return soGio * DON_GIA;
    }

    @Override
    public String getLoaiPhong() {
        return "PHONG_SEMINAR";
    }
}
