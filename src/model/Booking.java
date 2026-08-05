package model;

import enums.BookingStatus;
import java.time.LocalDate;

public class Booking {
    private String maDatPhong;
    private Student student;
    private Room room;
    private TimeSlot slot;
    private LocalDate ngay;
    private int soNguoi;
    private double phi;
    private BookingStatus trangThai;

    public Booking(String maDatPhong, Student student, Room room, TimeSlot slot,
                    LocalDate ngay, int soNguoi) {
        this.maDatPhong = maDatPhong;
        this.student = student;
        this.room = room;
        this.slot = slot;
        this.ngay = ngay;
        this.soNguoi = soNguoi;
        this.phi = room.tinhPhi(slot.soGio());
        this.trangThai = BookingStatus.DA_DAT;
    }

    public String getMaDatPhong() {
        return maDatPhong;
    }

    public Student getStudent() {
        return student;
    }

    public Room getRoom() {
        return room;
    }

    public TimeSlot getSlot() {
        return slot;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public int getSoNguoi() {
        return soNguoi;
    }

    public double getPhi() {
        return phi;
    }

    public double getSoGio() {
        return slot.soGio();
    }

    public BookingStatus getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(BookingStatus trangThai) {
        this.trangThai = trangThai;
    }

    public String toLine() {
        return String.join("|",
                maDatPhong,
                student.getMaSV(),
                student.getHoTen(),
                room.getMaPhong(),
                room.getLoaiPhong(),
                slot.getBatDau().toString(),
                slot.getKetThuc().toString(),
                ngay.toString(),
                String.valueOf(soNguoi),
                String.valueOf(phi),
                trangThai.name()
        );
    }

    @Override
    public String toString() {
        return maDatPhong + " | " + room.getMaPhong() + " | " + ngay + " " + slot
                + " | " + student.getHoTen() + " | " + phi + "d | " + trangThai;
    }
}
