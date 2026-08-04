package model;

public abstract class Room {

    protected String roomId;
    protected String roomName;
    protected int floor;
    protected int capacity;
    protected String status;

    public Room(String roomId, String roomName,
                int floor, int capacity, String status) {

        this.roomId = roomId;
        this.roomName = roomName;
        this.floor = floor;
        this.capacity = capacity;
        this.status = status;
    }

    public abstract double calculateFee(int hours);
}
