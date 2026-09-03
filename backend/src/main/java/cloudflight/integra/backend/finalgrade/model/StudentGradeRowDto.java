package cloudflight.integra.backend.finalgrade.model;

import java.util.UUID;

public class StudentGradeRowDto {
    private UUID studentId;
    private String studentName;
    private String group;
    private UUID finalGradeId;
    private Integer grade;
    private Boolean provisional;
    private String completionDate;

    public StudentGradeRowDto() {}

    public StudentGradeRowDto(
        UUID studentId,
        String studentName,
        String group,
        UUID finalGradeId,
        Integer grade,
        Boolean provisional,
        String completionDate
    ) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.group = group;
        this.finalGradeId = finalGradeId;
        this.grade = grade;
        this.provisional = provisional;
        this.completionDate = completionDate;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public UUID getFinalGradeId() {
        return finalGradeId;
    }

    public void setFinalGradeId(UUID finalGradeId) {
        this.finalGradeId = finalGradeId;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Boolean getProvisional() {
        return provisional;
    }

    public void setProvisional(Boolean provisional) {
        this.provisional = provisional;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }
}
