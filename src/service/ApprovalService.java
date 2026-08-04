package service;

import enums.BookingStatus;
import enums.RoomStatus;
import exception.*;
import model.*;
import repository.ApprovalRepository;
import repository.BookingRepository;
import repository.RoomRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApprovalService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final ApprovalRepository approvalRepository;
    private final BookingService bookingService;

    public ApprovalService(RoomRepository roomRepository,
                           BookingRepository bookingRepository,
                           ApprovalRepository approvalRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.approvalRepository = approvalRepository;
        this.bookingService = new BookingService(roomRepository, bookingRepository);
    }

    public Booking guiYeuCau(Student student, String maPhong, LocalDate ngay,
                             TimeSlot slot, int soNguoi)
            throws RoomNotFoundException, RoomUnderMaintenanceException,
            TimeConflictException, OverCapacityException,
            ExceedDailyHourLimitException, IOException {

        
        Booking b = bookingService.datPhong(student, maPhong, ngay, slot, soNguoi);

        List<Booking> ds = bookingRepository.layTatCa(roomRepository);
        for (Booking x : ds) {
            if (x.getMaDatPhong().equals(b.getMaDatPhong())) {
                x.setTrangThai(BookingStatus.CHO_DUYET);
            }
        }
        bookingRepository.luu(ds);
        b.setTrangThai(BookingStatus.CHO_DUYET);
        return b;
    }

    public List<Booking> layDanhSachChoDuyet() throws IOException {
        List<Booking> ketQua = new ArrayList<>();
        for (Booking b : bookingRepository.layTatCa(roomRepository)) {
            if (b.getTrangThai() == BookingStatus.CHO_DUYET) ketQua.add(b);
        }
        return ketQua;
    }

    public List<Booking> layYeuCauCuaSinhVien(String maSV) throws IOException {
        List<Booking> ketQua = new ArrayList<>();
        for (Booking b : bookingRepository.layTatCa(roomRepository)) {
            if (b.getStudent().getMaSV().equals(maSV)) ketQua.add(b);
        }
        return ketQua;
    }

    public Booking duyet(String maDatPhong, User nguoiXuLy)
            throws NotAdminException, BookingNotPendingException,
            RoomUnderMaintenanceException, TimeConflictException, IOException {

        kiemTraAdmin(nguoiXuLy);
        List<Booking> ds = bookingRepository.layTatCa(roomRepository);
        Booking target = tim(ds, maDatPhong);

        if (target.getRoom().getTrangThai() == RoomStatus.BAO_TRI) {
            throw new RoomUnderMaintenanceException(
                    "Phong " + target.getRoom().getMaPhong() + " dang bao tri, khong the duyet");
        }

        for (Booking b : ds) {
            if (b == target) continue;
            if (b.getRoom().getMaPhong().equals(target.getRoom().getMaPhong())
                    && b.getNgay().equals(target.getNgay())
                    && (b.getTrangThai() == BookingStatus.DA_DAT
                        || b.getTrangThai() == BookingStatus.DA_CHECKIN)
                    && b.getSlot().getBatDau().isBefore(target.getSlot().getKetThuc())
                    && target.getSlot().getBatDau().isBefore(b.getSlot().getKetThuc())) {
                throw new TimeConflictException(
                        "Khung gio da bi lich " + b.getMaDatPhong() + " chiem, khong the duyet");
            }
        }

        target.setTrangThai(BookingStatus.DA_DAT);
        bookingRepository.luu(ds);
        approvalRepository.them(new ApprovalLog(maDatPhong, moTa(nguoiXuLy),
                "DUYET", "", LocalDateTime.now()));
        return target;
    }

    public Booking tuChoi(String maDatPhong, User nguoiXuLy, String lyDo)
            throws NotAdminException, BookingNotPendingException, IOException {

        kiemTraAdmin(nguoiXuLy);
        if (lyDo == null || lyDo.trim().isEmpty()) {
            throw new IllegalArgumentException("Phai nhap ly do tu choi");
        }

        List<Booking> ds = bookingRepository.layTatCa(roomRepository);
        Booking target = tim(ds, maDatPhong);
        target.setTrangThai(BookingStatus.TU_CHOI);
        bookingRepository.luu(ds);
        approvalRepository.them(new ApprovalLog(maDatPhong, moTa(nguoiXuLy),
                "TU_CHOI", lyDo.trim(), LocalDateTime.now()));
        return target;
    }

    public List<ApprovalLog> layNhatKy() throws IOException {
        return approvalRepository.layTatCa();
    }

    public String layLyDoTuChoi(String maDatPhong) throws IOException {
        String lyDo = "";
        for (ApprovalLog log : approvalRepository.layTatCa()) {
            if (log.getMaDatPhong().equals(maDatPhong) && "TU_CHOI".equals(log.getHanhDong())) {
                lyDo = log.getLyDo();
            }
        }
        return lyDo;
    }

    private void kiemTraAdmin(User u) throws NotAdminException {
        if (u == null || !"ADMIN".equals(u.getVaiTro())) {
            throw new NotAdminException("Chi quan tri vien moi duoc duyet yeu cau dat phong");
        }
    }

    private Booking tim(List<Booking> ds, String maDatPhong) throws BookingNotPendingException {
        for (Booking b : ds) {
            if (b.getMaDatPhong().equals(maDatPhong)) {
                if (b.getTrangThai() != BookingStatus.CHO_DUYET) {
                    throw new BookingNotPendingException(
                            "Yeu cau " + maDatPhong + " khong o trang thai cho duyet (hien tai: "
                                    + b.getTrangThai() + ")");
                }
                return b;
            }
        }
        throw new BookingNotPendingException("Khong tim thay yeu cau: " + maDatPhong);
    }

    private String moTa(User u) {
        return u.getId() + " - " + u.getHoTen();
    }
}
