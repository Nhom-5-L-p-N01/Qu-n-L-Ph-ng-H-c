package model;

public abstract class User {
    private String id;
    private String hoTen;

    public User(String id, String hoTen) {
        this.id = id;
        this.hoTen = hoTen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            throw new IllegalArgumentException("Ho ten khong duoc de trong");
        }
        this.hoTen = hoTen;
    }

    public abstract String getVaiTro();

    @Override
    public String toString() {
        return getVaiTro() + "[" + id + " - " + hoTen + "]";
    }
}
