package cloudflight.integra.backend.finalgrade.model;

import cloudflight.integra.backend.course.model.Course;
import cloudflight.integra.backend.student.model.Student;

import java.util.UUID;

public class FinalGrade {
    private UUID id;
    private UUID courseId;
    private UUID studentId;
    private Integer semester;
    private Integer grade;
    private Boolean provisional;
    private String completionDate;

    public FinalGrade() {}

    public FinalGrade(UUID id, UUID courseId, UUID studentId, Integer semester, Boolean provisional, Integer grade, String completionDate) {
        this.id = id;
        this.courseId = courseId;
        this.studentId = studentId;
        this.semester = semester;
        this.provisional = provisional;
        this.grade = grade;
        this.completionDate = completionDate;
    }

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

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public Boolean getProvisional() {
        return provisional;
    }

    public void setProvisional(Boolean provisional) {
        this.provisional = provisional;
    }
}
