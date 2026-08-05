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

    // ===== THUAT TOAN 1: Tim kiem phong (slide 14) - O(n) =====
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

    // ===== THUAT TOAN 2: Kiem tra trung lich (slide 15) - Interval Overlap =====
    private boolean bTrung(TimeSlot a, TimeSlot b) {
        return a.getBatDau().isBefore(b.getKetThuc())
                && b.getBatDau().isBefore(a.getKetThuc());
    }

    public boolean coTheDat(Room room, LocalDate ngay, TimeSlot slot) throws IOException {
        for (Booking b : bookingRepository.layTatCa(roomRepository)) {
            if (b.getRoom().getMaPhong().equals(room.getMaPhong())
                    && b.getNgay().equals(ngay)
                    && (b.getTrangThai() == BookingStatus.DA_DAT || b.getTrangThai() == BookingStatus.DA_CHECKIN)
                    && bTrung(b.getSlot(), slot)) {
                return false;
            }
        }
        return true;
    }

    // ===== THUAT TOAN 3: Tinh phi (slide 16) - Strategy qua da hinh =====
    // Khong can viet rieng: goi truc tiep room.tinhPhi(soGio) vi da hinh da xu ly (xem Booking constructor)

    // ===== THUAT TOAN 4: Sinh ma dat phong duy nhat (slide 17) =====
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

    // ===== THUAT TOAN 5: Kiem tra gioi han 4 gio/ngay (slide 18) =====
    public boolean vuotGioiHan(String maSV, LocalDate ngay, double soGioMoi) throws IOException {
        double tongGio = 0;
        for (Booking b : bookingRepository.layTatCa(roomRepository)) {
            if (b.getStudent().getMaSV().equals(maSV)
                    && b.getNgay().equals(ngay)
                    && (b.getTrangThai() == BookingStatus.DA_DAT || b.getTrangThai() == BookingStatus.DA_CHECKIN)) {
                tongGio += b.getSoGio();
            }
        }
        return (tongGio + soGioMoi) > GIOI_HAN_GIO_NGAY;
    }

    // ===== LUONG DAT PHONG TONG HOP (slide 19) - gom tat ca buoc kiem tra =====
    public Booking datPhong(Student student, String maPhong, LocalDate ngay,
                             TimeSlot slot, int soNguoi)
            throws RoomNotFoundException, RoomUnderMaintenanceException,
            TimeConflictException, OverCapacityException,
            ExceedDailyHourLimitException, IOException {

        // Buoc 1+2: Tim & kiem tra ton tai / bao tri
        Room room = roomRepository.timTheoMa(maPhong);
        if (room == null) {
            throw new RoomNotFoundException("Khong tim thay phong: " + maPhong);
        }
        if (room.getTrangThai() == RoomStatus.BAO_TRI) {
            throw new RoomUnderMaintenanceException("Phong " + maPhong + " dang bao tri");
        }

        // Buoc 3: Kiem tra trung lich
        if (!coTheDat(room, ngay, slot)) {
            throw new TimeConflictException("Khung gio " + slot + " da co nguoi dat");
        }

        // Buoc 4: Kiem tra suc chua
        if (soNguoi > room.getSucChua()) {
            throw new OverCapacityException(
                    "Vuot suc chua! Phong toi da " + room.getSucChua() + " nguoi");
        }

        // Buoc 5: Kiem tra gioi han 4h/ngay
        if (vuotGioiHan(student.getMaSV(), ngay, slot.soGio())) {
            throw new ExceedDailyHourLimitException(
                    "Ban da vuot qua gioi han " + GIOI_HAN_GIO_NGAY + " gio/ngay");
        }

        // Buoc 6: Sinh ma & luu (phi tinh tu dong theo da hinh trong Booking constructor)
        List<Booking> ds = bookingRepository.layTatCa(roomRepository);
        String maMoi = sinhMaDatPhong(ds);
        Booking booking = new Booking(maMoi, student, room, slot, ngay, soNguoi);
        ds.add(booking);
        bookingRepository.luu(ds);

        return booking;
    }

    // ===== HUY LICH (kiem tra quyen huy - slide 20) =====
    // Ap dung du 3 dieu kien bat buoc trong de bai: ma dat phong khong ton tai,
    // lich da bi huy truoc do, va sinh vien khong phai chu cua lich dat.
    public void huyPhong(String maDatPhong, String maSVYeuCau)
            throws BookingNotFoundException, NotBookingOwnerException,
            BookingAlreadyCancelledException, IOException {
        List<Booking> ds = bookingRepository.layTatCa(roomRepository);
        Booking target = null;
        for (Booking b : ds) {
            if (b.getMaDatPhong().equals(maDatPhong)) {
                target = b;
                break;
            }
        }
        if (target == null) {
            throw new BookingNotFoundException("Khong tim thay lich dat voi ma: " + maDatPhong);
        }

        if (!target.getStudent().getMaSV().equals(maSVYeuCau)) {
            throw new NotBookingOwnerException("Ban khong phai chu cua lich dat nay");
        }

        if (target.getTrangThai() == BookingStatus.DA_HUY) {
            throw new BookingAlreadyCancelledException("Lich dat nay da bi huy truoc do");
        }

        target.setTrangThai(BookingStatus.DA_HUY);
        bookingRepository.luu(ds);
    }

    // ===== CHECK-IN: xac nhan sinh vien da thuc su den dung phong =====
    // Chi khi goi ham nay xong (tra ve true), moi duoc phep cong diem tich luy
    public boolean checkIn(String maDatPhong) throws IOException {
        List<Booking> ds = bookingRepository.layTatCa(roomRepository);
        for (Booking b : ds) {
            if (b.getMaDatPhong().equals(maDatPhong) && b.getTrangThai() == BookingStatus.DA_DAT) {
                b.setTrangThai(BookingStatus.DA_CHECKIN);
                bookingRepository.luu(ds);
                return true;
            }
        }
        return false; // khong tim thay hoac booking khong o trang thai cho check-in
    }

    public List<Booking> layLichSuDat() throws IOException {
        return bookingRepository.layTatCa(roomRepository);
    }
}
