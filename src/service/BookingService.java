package service;

import model.*;
import enums.*;
import exception.*;
import repository.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BookingService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private static final double GIOI_HAN_GIO_NGAY = 4.0;

    public BookingService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Room> timPhong(String tuKhoa) {
        List<Room> ketQua = new ArrayList<>();
        for (Room r : roomRepository.layTatCa()) {
            if (r.getMaPhong().equalsIgnoreCase(tuKhoa)
                    || r.getLoaiPhong().equalsIgnoreCase(tuKhoa)
                    || r.getTenPhong().toLowerCase().contains(tuKhoa.toLowerCase())) {
                ketQua.add(r);
            }
        }
        return ketQua;
    }

    private boolean bTrung(TimeSlot a, TimeSlot b) {
        return a.getBatDau().isBefore(b.getKetThuc())
                && b.getBatDau().isBefore(a.getKetThuc());
    }

    public boolean coTheDat(Room room, LocalDate ngay, TimeSlot slot) throws IOException {
        for (Booking b : bookingRepository.layTatCa(roomRepository)) {
            if (b.getRoom().getMaPhong().equals(room.getMaPhong())
                    && b.getNgay().equals(ngay)
                    && b.getTrangThai() == BookingStatus.DA_DAT
                    && bTrung(b.getSlot(), slot)) {
                return false;
            }
        }
        return true;
    }

    public String sinhMaDatPhong(List<Booking> danhSachDat) {
        String tienTo = "BK";
        String thoiGian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        String maMoi;
        Random rd = new Random();
        do {
            int ngauNhien = rd.nextInt(900) + 100;
            maMoi = tienTo + thoiGian + ngauNhien;
        } while (daTonTai(maMoi, danhSachDat));
        return maMoi;
    }

    private boolean daTonTai(String ma, List<Booking> ds) {
        for (Booking b : ds) {
            if (b.getMaDatPhong().equals(ma)) return true;
        }
        return false;
    }

    public boolean vuotGioiHan(String maSV, LocalDate ngay, double soGioMoi) throws IOException {
        double tongGio = 0;
        for (Booking b : bookingRepository.layTatCa(roomRepository)) {
            if (b.getStudent().getMaSV().equals(maSV)
                    && b.getNgay().equals(ngay)
                    && b.getTrangThai() == BookingStatus.DA_DAT) {
                tongGio += b.getSoGio();
            }
        }
        return (tongGio + soGioMoi) > GIOI_HAN_GIO_NGAY;
    }

    public Booking datPhong(Student student, String maPhong, LocalDate ngay,
                             TimeSlot slot, int soNguoi)
            throws RoomNotFoundException, RoomUnderMaintenanceException,
            TimeConflictException, OverCapacityException,
            ExceedDailyHourLimitException, IOException {

        Room room = roomRepository.timTheoMa(maPhong);
        if (room == null) {
            throw new RoomNotFoundException("Khong tim thay phong: " + maPhong);
        }
        if (room.getTrangThai() == RoomStatus.BAO_TRI) {
            throw new RoomUnderMaintenanceException("Phong " + maPhong + " dang bao tri");
        }

        if (!coTheDat(room, ngay, slot)) {
            throw new TimeConflictException("Khung gio " + slot + " da co nguoi dat");
        }

        if (soNguoi > room.getSucChua()) {
            throw new OverCapacityException(
                    "Vuot suc chua! Phong toi da " + room.getSucChua() + " nguoi");
        }

        if (vuotGioiHan(student.getMaSV(), ngay, slot.soGio())) {
            throw new ExceedDailyHourLimitException(
                    "Ban da vuot qua gioi han " + GIOI_HAN_GIO_NGAY + " gio/ngay");
        }

        List<Booking> ds = bookingRepository.layTatCa(roomRepository);
        String maMoi = sinhMaDatPhong(ds);
        Booking booking = new Booking(maMoi, student, room, slot, ngay, soNguoi);
        ds.add(booking);
        bookingRepository.luu(ds);

        return booking;
    }

    public void huyPhong(String maDatPhong, String maSVYeuCau)
            throws NotBookingOwnerException, IOException {
        List<Booking> ds = bookingRepository.layTatCa(roomRepository);
        Booking target = null;
        for (Booking b : ds) {
            if (b.getMaDatPhong().equals(maDatPhong)) {
                target = b;
                break;
            }
        }
        if (target == null) return;

        if (!target.getStudent().getMaSV().equals(maSVYeuCau)) {
            throw new NotBookingOwnerException("Ban khong phai chu cua lich dat nay");
        }

        target.setTrangThai(BookingStatus.DA_HUY);
        bookingRepository.luu(ds);
    }

    public List<Booking> layLichSuDat() throws IOException {
        return bookingRepository.layTatCa(roomRepository);
    }
}
