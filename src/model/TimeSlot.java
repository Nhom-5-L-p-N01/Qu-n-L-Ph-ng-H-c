package model;

import java.time.LocalTime;
import java.time.Duration;

public class TimeSlot {
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;

    public TimeSlot(LocalTime gioBatDau, LocalTime gioKetThuc) {
        if (!gioBatDau.isBefore(gioKetThuc)) {
            throw new IllegalArgumentException("Gio bat dau phai truoc gio ket thuc");
        }
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
    }

    public LocalTime getBatDau() {
        return gioBatDau;
    }

    public LocalTime getKetThuc() {
        return gioKetThuc;
    }

    public double soGio() {
        return Duration.between(gioBatDau, gioKetThuc).toMinutes() / 60.0;
    }

    @Override
    public String toString() {
        return gioBatDau + " - " + gioKetThuc;
    }
}
