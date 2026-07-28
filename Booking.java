public class Booking {
    private String roomName;
    private String user;
    private String time;
    private String status;

    public Booking() {}

    public Booking(String roomName, String user, String time, String status) {
        this.roomName = roomName;
        this.user = user;
        this.time = time;
        this.status = status;
    }

    // Getter và Setter
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
