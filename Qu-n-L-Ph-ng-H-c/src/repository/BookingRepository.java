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

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                Student sv = new Student(p[1], p[2], p[1], "");
                Room room = roomRepository.timTheoMa(p[3]);
                if (room == null) continue;
                TimeSlot slot = new TimeSlot(LocalTime.parse(p[5]), LocalTime.parse(p[6]));

                Booking b = new Booking(p[0], sv, room, slot, LocalDate.parse(p[7]),
                        Integer.parseInt(p[8]));
                b.setTrangThai(BookingStatus.valueOf(p[10]));
                ds.add(b);
            }
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
