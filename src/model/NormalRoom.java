package model;

public class NormalRoom extends Room {
    public NormalRoom(String maPhong, String tenPhong, int tang, int sucChua) {
        super(maPhong, tenPhong, tang, sucChua);
    }

    @Override
    public double tinhPhi(double soGio) {
        return 0;
    }

    @Override
    public String getLoaiPhong() {
        return "PHONG_THUONG";
    }
}
