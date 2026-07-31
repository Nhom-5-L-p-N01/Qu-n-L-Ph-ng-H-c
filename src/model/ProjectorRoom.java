package model;

public class ProjectorRoom extends Room {
    private static final double DON_GIA = 20000;

    public ProjectorRoom(String maPhong, String tenPhong, int tang, int sucChua) {
        super(maPhong, tenPhong, tang, sucChua);
    }

    @Override
    public double tinhPhi(double soGio) {
        return soGio * DON_GIA;
    }

    @Override
    public String getLoaiPhong() {
        return "PHONG_MAY_CHIEU";
    }
}
