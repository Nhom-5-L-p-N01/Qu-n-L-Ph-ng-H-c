package model;

import enums.RoomStatus;

public abstract class Room {
    protected String maPhong;
    protected String tenPhong;
    protected int tang;
    protected int sucChua;
    protected RoomStatus trangThai;

    public Room(String maPhong, String tenPhong, int tang, int sucChua) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.tang = tang;
        setSucChua(sucChua);
        this.trangThai = RoomStatus.TRONG;
    }

    public abstract double tinhPhi(double soGio);

    public abstract String getLoaiPhong();

    public String getMaPhong() {
        return maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public int getTang() {
        return tang;
    }

    public int getSucChua() {
        return sucChua;
    }

    public void setSucChua(int sucChua) {
        if (sucChua <= 0) {
            throw new IllegalArgumentException("Suc chua khong hop le");
        }
        this.sucChua = sucChua;
    }

    public RoomStatus getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(RoomStatus trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return maPhong + " (" + getLoaiPhong() + ") - Tang " + tang + " - Suc chua " + sucChua;
    }
}
