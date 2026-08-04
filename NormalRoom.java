package model;


public class NormalRoom extends Room {

    // Constructor mặc định
    public NormalRoom() {
        super();
    }

    // Constructor đầy đủ
    public NormalRoom(String roomId, String roomName, int floor,
                      int capacity, String status) {

        super(roomId, roomName, floor, capacity,
                "Normal Room", status);
    }

    
    @Override
    public double calculateFee(int hours) {
        return 0;
    }

    @Override
    public String toString() {
        return "NormalRoom{" +
                "roomId='" + getRoomId() + '\'' +
                ", roomName='" + getRoomName() + '\'' +
                ", floor=" + getFloor() +
                ", capacity=" + getCapacity() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
