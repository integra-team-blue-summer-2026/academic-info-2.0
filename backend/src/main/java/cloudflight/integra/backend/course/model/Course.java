package cloudflight.integra.backend.course.model;

import java.util.UUID;

public class Course {

    private UUID id;
    private UUID teacherId;
    private String courseName;
    private String syllabus;
    private int credits;
    private String description;

    public Course() {
    }

    public Course(UUID id, UUID teacherId, String courseName,
                  String syllabus, int credits, String description) {
        this.id = id;
        this.teacherId = teacherId;
        this.courseName = courseName;
        this.syllabus = syllabus;
        this.credits = credits;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(UUID teacherId) {
        this.teacherId = teacherId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
