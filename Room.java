package model;

/**
 * Lớp trừu tượng Room
 * Đại diện cho phòng học nhóm trong hệ thống.
 */
public abstract class Room {

    private String roomId;
    private String roomName;
    private int floor;
    private int capacity;
    private String roomType;
    private String status;

    // Constructor mặc định
    public Room() {
    }

    // Constructor đầy đủ
    public Room(String roomId, String roomName, int floor,
                int capacity, String roomType, String status) {

        this.roomId = roomId;
        this.roomName = roomName;
        this.floor = floor;
        this.capacity = capacity;
        this.roomType = roomType;
        this.status = status;
    }

    // Getter & Setter

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Phương thức tính phí.
     * Mỗi loại phòng sẽ tính phí khác nhau.
     */
    public abstract double calculateFee(int hours);

    @Override
    public String toString() {
        return "Room{" +
                "roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", floor=" + floor +
                ", capacity=" + capacity +
                ", roomType='" + roomType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
