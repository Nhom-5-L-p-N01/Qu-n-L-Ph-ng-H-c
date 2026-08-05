package repository;

import model.*;
import enums.BookingStatus;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class BookingRepository {
    private final String path;

    public BookingRepository(String path) {
        this.path = path;
    }

    public List<Booking> layTatCa(RoomRepository roomRepository) throws IOException {
        List<Booking> ds = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) return ds;

        List<String> dongLoi = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int soDong = 0;
            while ((line = br.readLine()) != null) {
                soDong++;
                if (line.trim().isEmpty()) continue;
                try {
                    String[] p = line.split("\\|");
                    Student sv = new Student(p[1], p[2], p[1], "");
                    Room room = roomRepository.timTheoMa(p[3]);
                    if (room == null) continue;
                    TimeSlot slot = new TimeSlot(LocalTime.parse(p[5]), LocalTime.parse(p[6]));

                    Booking b = new Booking(p[0], sv, room, slot, LocalDate.parse(p[7]),
                            Integer.parseInt(p[8]));
                    b.setTrangThai(BookingStatus.valueOf(p[10]));
                    ds.add(b);
                } catch (RuntimeException ex) {
                    // Dòng dữ liệu sai định dạng (ví dụ file cũ từ phiên bản trước, dùng dấu
                    // phẩy thay vì dấu | ) - bỏ qua dòng này thay vì làm sập cả ứng dụng,
                    // để các dòng hợp lệ khác vẫn nạp được bình thường.
                    dongLoi.add("Dòng " + soDong + ": " + ex.getClass().getSimpleName());
                }
            }
        }
        if (!dongLoi.isEmpty()) {
            System.err.println("[BookingRepository] Bỏ qua " + dongLoi.size()
                    + " dòng dữ liệu không đọc được trong " + path + ":");
            dongLoi.forEach(System.err::println);
        }
        return ds;
    }

    public void luu(List<Booking> ds) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (Booking b : ds) {
                bw.write(b.toLine());
                bw.newLine();
            }
        }
    }

    public void them(Booking b, RoomRepository roomRepository) throws IOException {
        List<Booking> ds = layTatCa(roomRepository);
        ds.add(b);
        luu(ds);
    }
}
