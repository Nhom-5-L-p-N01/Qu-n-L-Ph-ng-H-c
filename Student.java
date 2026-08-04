package model;

public class Student extends User {

    private String className;

    public Student() {
    }

    public Student(String id, String fullName, String phone,
                   String email, String className) {

        super(id, fullName, phone, email);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
