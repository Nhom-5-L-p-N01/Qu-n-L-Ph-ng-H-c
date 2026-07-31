import enums.BookingStatus;
import model.*;
import repository.*;
import service.ApprovalService;

import java.time.LocalDate;
import java.time.LocalTime;

public class ApprovalDemo {
    public static void main(String[] args) throws Exception {
        new java.io.File("bookings_demo.txt").delete();
        new java.io.File("approvals_demo.txt").delete();

        RoomRepository roomRepo = new RoomRepository();
        BookingRepository bookingRepo = new BookingRepository("bookings_demo.txt");
        ApprovalRepository approvalRepo = new ApprovalRepository("approvals_demo.txt");
        ApprovalService svc = new ApprovalService(roomRepo, bookingRepo, approvalRepo);

        Student sv = new Student("SV01", "Nguyen Van A", "SV01", "CNTT1");
        Admin admin = new Admin("AD01", "Quan tri vien");
        LocalDate ngay = LocalDate.now().plusDays(1);

        model.Booking yc1 = svc.guiYeuCau(sv, "P101", ngay,
                new TimeSlot(LocalTime.of(8, 0), LocalTime.of(10, 0)), 5);
        System.out.println("Gui yeu cau 1: " + yc1);

        model.Booking yc2 = svc.guiYeuCau(sv, "P201", ngay,
                new TimeSlot(LocalTime.of(13, 0), LocalTime.of(14, 0)), 8);
        System.out.println("Gui yeu cau 2: " + yc2);

        System.out.println("Cho duyet: " + svc.layDanhSachChoDuyet().size());

        System.out.println("Duyet 1  -> " + svc.duyet(yc1.getMaDatPhong(), admin).getTrangThai());
        System.out.println("Tu choi 2-> " + svc.tuChoi(yc2.getMaDatPhong(), admin, "Trung lich khoa").getTrangThai());
        System.out.println("Cho duyet con lai: " + svc.layDanhSachChoDuyet().size());
        System.out.println("Ly do tu choi 2: " + svc.layLyDoTuChoi(yc2.getMaDatPhong()));

        try {
            svc.duyet(yc1.getMaDatPhong(), sv);
        } catch (Exception e) {
            System.out.println("Chan dung: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        try {
            svc.duyet(yc1.getMaDatPhong(), admin);
        } catch (Exception e) {
            System.out.println("Chan dung: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("Nhat ky:");
        svc.layNhatKy().forEach(l -> System.out.println("  " + l));
        System.out.println("Trang thai moi trong enum: " + BookingStatus.CHO_DUYET + ", " + BookingStatus.TU_CHOI);
    }
}
