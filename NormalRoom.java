package model;

public class NormalRoom extends Room {

    public NormalRoom(String roomId,
                      String roomName,
                      int floor,
                      int capacity,
                      String status) {

        super(roomId, roomName, floor, capacity, status);
    }

    @Override
    public double calculateFee(int hours) {
        return 0;
    }
}
