package model;

/**
 * Lớp SeminarRoom kế thừa Room.
 * Đại diện cho phòng họp seminar.
 */
public class SeminarRoom extends Room {

    public SeminarRoom() {
        super();
    }

    public SeminarRoom(String roomId, String roomName,
                        int floor, int capacity,
                        String status) {

        super(roomId, roomName, floor, capacity,
                "Seminar Room", status);
    }

    @Override
    public double calculateFee(int hours) {
        return hours * 50000;
    }

    @Override
    public String toString() {
        return "SeminarRoom{" +
                "roomId='" + getRoomId() + '\'' +
                ", roomName='" + getRoomName() + '\'' +
                ", floor=" + getFloor() +
                ", capacity=" + getCapacity() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
