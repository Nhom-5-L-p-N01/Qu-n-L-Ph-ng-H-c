package model;

/**
 * Lớp Student kế thừa từ User.
 * Dùng để quản lý thông tin sinh viên đặt phòng.
 */
public class Student extends User {

    private String className;

    // Constructor mặc định
    public Student() {
        super();
    }

    // Constructor đầy đủ
    public Student(String id, String fullName, String phoneNumber,
                   String email, String className) {

        super(id, fullName, phoneNumber, email);
        this.className = className;
    }

    // Getter & Setter
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + getId() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
