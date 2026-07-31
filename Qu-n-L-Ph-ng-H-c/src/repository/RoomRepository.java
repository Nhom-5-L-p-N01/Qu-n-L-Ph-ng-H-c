package repository;

import model.*;
import java.util.*;

public class RoomRepository {
    private final List<Room> danhSachPhong = new ArrayList<>();

    public RoomRepository() {
        danhSachPhong.add(new NormalRoom("P101", "Phong thuong 1", 1, 6));
        danhSachPhong.add(new NormalRoom("P102", "Phong thuong 2", 1, 8));
        danhSachPhong.add(new ProjectorRoom("P201", "Phong may chieu 1", 2, 10));
        danhSachPhong.add(new ProjectorRoom("P202", "Phong may chieu 2", 2, 12));
        danhSachPhong.add(new SeminarRoom("P301", "Phong seminar 1", 3, 20));
    }

    public List<Room> layTatCa() {
        return danhSachPhong;
    }

    public Room timTheoMa(String maPhong) {
        for (Room r : danhSachPhong) {
            if (r.getMaPhong().equalsIgnoreCase(maPhong)) return r;
        }
        return null;
    }

    public void them(Room room) {
        danhSachPhong.add(room);
    }
}
