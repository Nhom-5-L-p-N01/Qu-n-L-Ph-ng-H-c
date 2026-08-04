package model;

import java.time.LocalDateTime;

/**
 * Lớp TimeSlot lưu thông tin thời gian đặt phòng.
 */
public class TimeSlot {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Constructor mặc định
    public TimeSlot() {
    }

    // Constructor đầy đủ
    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter & Setter
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Tính số giờ sử dụng phòng.
     */
    public long getDurationHours() {
        return java.time.Duration
                .between(startTime, endTime)
                .toHours();
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
