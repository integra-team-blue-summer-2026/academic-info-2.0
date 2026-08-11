package cloudflight.integra.backend.teacher.model;

import java.util.UUID;

public class Teacher {
    private UUID id;
    private String firstName;
    private String lastName;
    private String title;
    private String department;
    private String passwordHash;

    public Teacher() {
    }

    public Teacher(UUID id, String firstName, String lastName, String title, String department) {
        this(id, firstName, lastName, title, department, null);
    }

    public Teacher(UUID id, String firstName, String lastName, String title, String department, String passwordHash) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.department = department;
        this.passwordHash = passwordHash;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
