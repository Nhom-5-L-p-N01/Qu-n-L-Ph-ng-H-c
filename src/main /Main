package main;

import model.Booking;
import model.Room;
import model.Student;
import model.TimeSlot;
import repository.BookingRepository;
import repository.RoomRepository;
import service.BookingService;
import exception.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Class Main - demo nhanh luong nghiep vu cot loi (dat phong / huy phong)
 * bang console, khong phu thuoc giao dien Swing. Dung de kiem tra nhanh
 * cac business rule va exception cua he thong.
 *
 * Giao dien chinh de nguoi dung su dung thuc te la LoginFrame (Swing),
 * chay o thu muc goc cua project.
 */
public class Main {

    public static void main(String[] args) {
        RoomRepository roomRepository = new RoomRepository();
        BookingRepository bookingRepository = new BookingRepository("data_booking_demo.txt");
        BookingService bookingService = new BookingService(roomRepository, bookingRepository);

        Student sv1 = new Student("SV001", "Nguyen Van A", "SV001", "CNTT01");

        System.out.println("=== DANH SACH PHONG ===");
        for (Room r : roomRepository.layTatCa()) {
            System.out.println(r);
        }

        System.out.println("\n=== TEST 1: Dat phong thanh cong ===");
        try {
            Booking b1 = bookingService.datPhong(
                    sv1, "P201", LocalDate.now().plusDays(1),
                    new TimeSlot(LocalTime.of(7, 0), LocalTime.of(9, 0)), 8);
            System.out.println("Dat phong thanh cong: " + b1);
        } catch (RoomNotFoundException | RoomUnderMaintenanceException | TimeConflictException
                 | OverCapacityException | ExceedDailyHourLimitException | IOException ex) {
            System.out.println("Loi: " + ex.getMessage());
        }

        System.out.println("\n=== TEST 2: Dat phong dang bao tri (P103) - phai bao loi ===");
        try {
            bookingService.datPhong(
                    sv1, "P103", LocalDate.now().plusDays(1),
                    new TimeSlot(LocalTime.of(9, 0), LocalTime.of(11, 0)), 4);
        } catch (RoomUnderMaintenanceException ex) {
            System.out.println("Bao loi dung nhu mong doi: " + ex.getMessage());
        } catch (RoomNotFoundException | TimeConflictException | OverCapacityException
                 | ExceedDailyHourLimitException | IOException ex) {
            System.out.println("Loi khac: " + ex.getMessage());
        }

        System.out.println("\n=== TEST 3: Dat phong trung lich voi TEST 1 - phai bao loi ===");
        try {
            bookingService.datPhong(
                    sv1, "P201", LocalDate.now().plusDays(1),
                    new TimeSlot(LocalTime.of(8, 0), LocalTime.of(10, 0)), 5);
        } catch (TimeConflictException ex) {
            System.out.println("Bao loi dung nhu mong doi: " + ex.getMessage());
        } catch (RoomNotFoundException | RoomUnderMaintenanceException | OverCapacityException
                 | ExceedDailyHourLimitException | IOException ex) {
            System.out.println("Loi khac: " + ex.getMessage());
        }

        System.out.println("\n=== TEST 4: Dat phong vuot suc chua - phai bao loi ===");
        try {
            bookingService.datPhong(
                    sv1, "P301", LocalDate.now().plusDays(1),
                    new TimeSlot(LocalTime.of(13, 0), LocalTime.of(15, 0)), 999);
        } catch (OverCapacityException ex) {
            System.out.println("Bao loi dung nhu mong doi: " + ex.getMessage());
        } catch (RoomNotFoundException | RoomUnderMaintenanceException | TimeConflictException
                 | ExceedDailyHourLimitException | IOException ex) {
            System.out.println("Loi khac: " + ex.getMessage());
        }

        System.out.println("\n=== TEST 5: Huy lich vua dat o TEST 1 ===");
        try {
            java.util.List<Booking> ds = bookingService.layLichSuDat();
            if (!ds.isEmpty()) {
                String maDatPhong = ds.get(0).getMaDatPhong();
                bookingService.huyPhong(maDatPhong, "SV001");
                System.out.println("Huy thanh cong lich " + maDatPhong);

                System.out.println("\n=== TEST 6: Huy lai lan nua - phai bao loi da huy truoc do ===");
                bookingService.huyPhong(maDatPhong, "SV001");
            }
        } catch (BookingAlreadyCancelledException ex) {
            System.out.println("Bao loi dung nhu mong doi: " + ex.getMessage());
        } catch (BookingNotFoundException | NotBookingOwnerException | IOException ex) {
            System.out.println("Loi khac: " + ex.getMessage());
        }
    }
}
