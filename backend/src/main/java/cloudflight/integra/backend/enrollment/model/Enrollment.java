package cloudflight.integra.backend.enrollment.model;

import java.util.UUID;

public class Enrollment {
    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private Integer semester;
    private String academicYear;

    public Enrollment(UUID id, UUID studentId, UUID courseId, Integer semester, String academicYear) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    public Enrollment() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
}
