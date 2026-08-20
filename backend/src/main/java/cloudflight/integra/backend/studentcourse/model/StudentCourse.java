package cloudflight.integra.backend.studentcourse.model;

import java.util.UUID;

public class StudentCourse {

    private UUID id;
    private UUID studentId;
    private UUID courseId;

    public StudentCourse() {
    }

    public StudentCourse(UUID id, UUID studentId, UUID courseId) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }
}
