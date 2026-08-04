package model;

/**
 * Lớp ProjectorRoom kế thừa Room.
 * Đại diện cho phòng có máy chiếu.
 */
public class ProjectorRoom extends Room {

    // Constructor mặc định
    public ProjectorRoom() {
        super();
    }

    // Constructor đầy đủ
    public ProjectorRoom(String roomId, String roomName,
                         int floor, int capacity,
                         String status) {

        super(roomId, roomName, floor, capacity,
                "Projector Room", status);
    }

   
    @Override
    public double calculateFee(int hours) {
        return hours * 20000;
    }

    @Override
    public String toString() {
        return "ProjectorRoom{" +
                "roomId='" + getRoomId() + '\'' +
                ", roomName='" + getRoomName() + '\'' +
                ", floor=" + getFloor() +
                ", capacity=" + getCapacity() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
